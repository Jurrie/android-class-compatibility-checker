package org.jurr.java.android.classcompatibilitychecker;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

import com.tngtech.archunit.core.domain.AccessTarget;
import com.tngtech.archunit.core.domain.AccessTarget.CodeUnitAccessTarget;
import com.tngtech.archunit.core.domain.AccessTarget.ConstructorCallTarget;
import com.tngtech.archunit.core.domain.AccessTarget.ConstructorReferenceTarget;
import com.tngtech.archunit.core.domain.AccessTarget.FieldAccessTarget;
import com.tngtech.archunit.core.domain.AccessTarget.MethodCallTarget;
import com.tngtech.archunit.core.domain.AccessTarget.MethodReferenceTarget;
import com.tngtech.archunit.core.domain.JavaAccess;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaCodeUnit;
import com.tngtech.archunit.core.domain.JavaConstructor;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaStaticInitializer;
import com.tngtech.archunit.core.domain.JavaType;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;
import com.tngtech.archunit.core.importer.Locations;

public class AndroidClassCompatibilityChecker
{
	static final String NO_DESUGARING = "no desugaring";

	private final int androidVersion;
	private final Path tempDirectory;
	private final String desugaringJarMavenVersion;
	private final boolean includeTestClasses;
	private final JavaClasses classpath;
	private final ClassFileImporter classFileImporter;

	protected AndroidClassCompatibilityChecker(final int androidVersion, final Path tempDirectory, final String desugaringJarMavenVersion, final boolean includeTestClasses) throws IOException
	{
		this.androidVersion = androidVersion;
		this.tempDirectory = tempDirectory;
		this.desugaringJarMavenVersion = desugaringJarMavenVersion;
		this.includeTestClasses = includeTestClasses;

		classFileImporter = getClassFileImporter(includeTestClasses);
		classpath = getClassesAvailableOnAndroidClasspath();
	}

	public int getAndroidVersion()
	{
		return androidVersion;
	}

	public Path getTempDirectory()
	{
		return tempDirectory;
	}

	public String getDesugaringJarMavenVersion()
	{
		return desugaringJarMavenVersion;
	}

	public boolean isIncludeTestClasses()
	{
		return includeTestClasses;
	}

	private static ClassFileImporter getClassFileImporter(final boolean includeTestClasses)
	{
		ClassFileImporter classFileImporter = new ClassFileImporter();

		if (!includeTestClasses)
		{
			classFileImporter = classFileImporter.withImportOption(new ImportOption.DoNotIncludeTests());
		}

		return classFileImporter;
	}

	/**
	 * Test the specified package (and subpackages) for Android compatibility.
	 *
	 * @param packageName The package to test.
	 * @return The test results.
	 */
	public TestResults testPackage(final String packageName)
	{
		return testPackages(packageName);
	}

	/**
	 * Test the specified packages (and subpackages) for Android compatibility.
	 *
	 * @param packageNames The packages to test.
	 * @return The test results.
	 */
	public TestResults testPackages(final String... packageNames)
	{
		return testPackages(Arrays.asList(packageNames));
	}

	/**
	 * Test the specified packages (and subpackages) for Android compatibility.
	 *
	 * @param packageNames The packages to test.
	 * @return The test results.
	 */
	public TestResults testPackages(final Collection<String> packageNames)
	{
		final JavaClasses classesToCheck = classFileImporter.importPackages(packageNames);
		return test(classesToCheck);
	}

	/**
	 * Test the specified class for Android compatibility.
	 *
	 * @param className The class to test.
	 * @return The test results.
	 */
	public TestResults testClass(final Class<?> className)
	{
		return testClasses(className);
	}

	/**
	 * Test the specified classes for Android compatibility.
	 *
	 * @param className The classes to test.
	 * @return The test results.
	 */
	public TestResults testClasses(final Class<?>... className)
	{
		return testClasses(Arrays.asList(className));
	}

	/**
	 * Test the specified classes for Android compatibility.
	 *
	 * @param className The classes to test.
	 * @return The test results.
	 */
	public TestResults testClasses(final Collection<Class<?>> className)
	{
		final JavaClasses classesToCheck = classFileImporter.importClasses(className);
		return test(classesToCheck);
	}

	private TestResults test(final JavaClasses classesToCheck)
	{
		final TestResults testResults = new TestResults();
		testResults.addAll(testDirectDependencies(classesToCheck, classpath));
		testResults.addAll(testAccesses(classesToCheck, classpath));

		return testResults;
	}

	private TestResults testDirectDependencies(final JavaClasses classesToCheck, final JavaClasses classpath)
	{
		final TestResults testResults = new TestResults();
		final ArrayList<JavaClass> targetClassesAlreadyChecked = new ArrayList<>();
		classesToCheck.forEach(classToCheck -> {
			classToCheck.getDirectDependenciesFromSelf().forEach(dependencyToCheck -> {
				final JavaClass targetClass = dependencyToCheck.getTargetClass().getBaseComponentType();

				// Did we already check this target class? If so, skip it.
				if (targetClassesAlreadyChecked.contains(targetClass))
				{
					return;
				}

				if (targetClass.isPrimitive())
				{
					return;
				}

				if (!targetClassAvailableOnClasspath(targetClass, classpath)) // Is the target class available on the Android classpath?
				{
					testResults.addDirectDependencyOnUnavailableClass(new DirectDependencyOnUnavailableClassTestResult(dependencyToCheck));
				}

				targetClassesAlreadyChecked.add(targetClass);
			});
		});
		return testResults;
	}

	private TestResults testAccesses(final JavaClasses classesToCheck, final JavaClasses classpath)
	{
		final TestResults testResults = new TestResults();
		final ArrayList<AccessTarget> codeUnitAccessTargetsAlreadyChecked = new ArrayList<>();
		classesToCheck.forEach(classToCheck -> {
			classToCheck.getAccessesFromSelf().forEach(methodCallToCheck -> {
				final AccessTarget targetMethodCall = methodCallToCheck.getTarget();

				// Did we already check this target method call? If so, skip it.
				if (codeUnitAccessTargetsAlreadyChecked.contains(targetMethodCall))
				{
					return;
				}

				final JavaClass targetClass = methodCallToCheck.getTargetOwner().getBaseComponentType();

				if (targetClass.isPrimitive())
				{
					// Does not seem to work...
					return;
				}

				if (!targetClassAvailableOnClasspath(targetClass, classpath)) // Is the target class available on the Android classpath?
				{
					// Target class is not available on the Android classpath. This situation is already covered by the direct dependency test, so we can skip it here.
					return;
				}
				else if (!targetAccessAvailableInTargetClass(methodCallToCheck, classpath)) // Is the target method available in the target class on the Android classpath?
				{
					testResults.addAccessToUnavailableMember(new AccessToUnavailableMemberTestResult(methodCallToCheck));
				}

				codeUnitAccessTargetsAlreadyChecked.add(targetMethodCall);
			});
		});
		return testResults;
	}

	private List<Location> getClasspathWithoutJREArtifacts()
	{
		final String javaHome = System.getProperty("java.home");

		final ArrayList<Location> result = new ArrayList<>();
		for (final Location location : Locations.inClassPath())
		{
			final String locationAsString = location.asURI().toString();
			if (!locationAsString.startsWith("jrt:") && // Java 9+ modules
					!locationAsString.startsWith("file:" + javaHome) && // Java classes
					!locationAsString.startsWith("jar:file:" + javaHome)) // Java classes in JAR files
			{
				result.add(location);
			}
		}

		return result;
	}

	private List<Location> getAndroidClasspath() throws IOException
	{
		final Path androidClasspathJar = Paths.get(tempDirectory + "/android-" + androidVersion + ".jar");

		try
		{
			final URI androidClasspathRemoteLocation = new URI("https://github.com/Jurrie/android-platforms/raw/refs/heads/master/android-" + androidVersion + "/android.jar");
			ArtifactDownloader.downloadArtifactToTempDirectoryIfNotExits(androidClasspathRemoteLocation, androidClasspathJar);
		}
		catch (final URISyntaxException e)
		{
			throw new IOException("Failed to create URI for Android classpath JAR", e);
		}

		final Location androidClassesLocation = Location.of(new JarFile(androidClasspathJar.toFile()));
		return Collections.singletonList(androidClassesLocation);
	}

	private List<Location> getDesugarClasspath() throws IOException
	{
		if (NO_DESUGARING.equals(desugaringJarMavenVersion))
		{
			return Collections.emptyList();
		}

		final Path desugaredClasspathJar = Paths.get(tempDirectory + "/desugar_jdk_libs-" + desugaringJarMavenVersion + ".jar");

		try
		{
			final URI desugaredClasspathRemoteLocation = new URI("https://maven.google.com/com/android/tools/desugar_jdk_libs/" + desugaringJarMavenVersion + "/desugar_jdk_libs-" + desugaringJarMavenVersion + ".jar");
			ArtifactDownloader.downloadArtifactToTempDirectoryIfNotExits(desugaredClasspathRemoteLocation, desugaredClasspathJar);
		}
		catch (final URISyntaxException e)
		{
			throw new IOException("Failed to create URI for desugared classpath JAR", e);
		}

		final Location desugaredClassesLocation = Location.of(new JarFile(desugaredClasspathJar.toFile()));
		return Collections.singletonList(desugaredClassesLocation);
	}

	private JavaClasses getClassesAvailableOnAndroidClasspath() throws IOException
	{
		final List<Location> androidClasspath = getAndroidClasspath();
		final List<Location> desugarClasspath = getDesugarClasspath();
		final List<Location> classpathWithoutJREArtifacts = getClasspathWithoutJREArtifacts();

		final ArrayList<Location> allClassesLocations = new ArrayList<>(androidClasspath.size() + desugarClasspath.size() + classpathWithoutJREArtifacts.size());
		allClassesLocations.addAll(desugarClasspath); // Order matters here: desugaring classes should be checked first, then Android classes.
		allClassesLocations.addAll(androidClasspath);
		allClassesLocations.addAll(classpathWithoutJREArtifacts);

		return classFileImporter.importLocations(allClassesLocations);
	}

	private boolean targetClassAvailableOnClasspath(final JavaClass classToCheck, final JavaClasses classpath)
	{
		return classpath.contain(classToCheck.getName());
	}

	private boolean targetAccessAvailableInTargetClass(final JavaAccess<?> javaAccessToCheck, final JavaClasses classpath)
	{
		final AccessTarget targetAccess = javaAccessToCheck.getTarget();
		final JavaClass targetClass = javaAccessToCheck.getTargetOwner().getBaseComponentType();
		final JavaClass classpathClass = classpath.get(targetClass.getName());

		if (targetAccess instanceof CodeUnitAccessTarget)
		{
			final Set<JavaCodeUnit> allMethods = getAllCodeUnits(classpathClass, classpath);
			for (final JavaCodeUnit possibleTargetMethod : allMethods)
			{
				if (isSameCodeUnit((CodeUnitAccessTarget) targetAccess, possibleTargetMethod))
				{
					return true;
				}
			}
		}
		else if (targetAccess instanceof FieldAccessTarget) // TODO: We do not yet have a unit test for this case. So the field should not be available on Android, but the containing class should be available.
		{
			final Set<JavaField> allMethods = classpathClass.getAllFields();
			for (final JavaField possibleTargetMethod : allMethods)
			{
				if (isSameField((FieldAccessTarget) targetAccess, possibleTargetMethod))
				{
					return true;
				}
			}
		}

		return false;
	}

	private Set<JavaCodeUnit> getAllCodeUnits(final JavaClass javaClass, final JavaClasses classpath)
	{
		final Set<JavaCodeUnit> result = new HashSet<>();
		result.addAll(javaClass.getCodeUnits());
		for (JavaClass superClass : javaClass.getAllRawSuperclasses())
		{
			result.addAll(superClass.getCodeUnits());
		}
		for (JavaClass superClass : javaClass.getAllRawInterfaces())
		{
			result.addAll(superClass.getCodeUnits());
		}
		if (javaClass.isInterface())
		{
			// Interfaces do not inherit from Object, but they do "inherit the methods of Object". So we need to add those methods as well.
			// http://docs.oracle.com/javase/specs/jls/se7/html/jls-9.html#jls-9.2
			result.addAll(classpath.get(Object.class).getCodeUnits());
		}
		return result;
	}

	private boolean isSameCodeUnit(final CodeUnitAccessTarget codeUnitAccessTarget, final JavaCodeUnit candidateCodeUnit)
	{
		if (candidateCodeUnit instanceof JavaMethod && (codeUnitAccessTarget instanceof MethodCallTarget || codeUnitAccessTarget instanceof MethodReferenceTarget))
		{
			return isSameCodeUnit(codeUnitAccessTarget, (JavaMethod) candidateCodeUnit);
		}
		else if (candidateCodeUnit instanceof JavaConstructor && (codeUnitAccessTarget instanceof ConstructorCallTarget || codeUnitAccessTarget instanceof ConstructorReferenceTarget))
		{
			return isSameCodeUnit(codeUnitAccessTarget, (JavaConstructor) candidateCodeUnit);
		}
		else if (candidateCodeUnit instanceof JavaStaticInitializer)
		{
			return true; // This is probably ok
		}
		else
		{
			return false;
		}
	}

	private boolean isSameCodeUnit(final CodeUnitAccessTarget methodCallOrReferenceTarget, final JavaMethod candidateMethod)
	{
		// Check if the method names are the same.
		if (!methodCallOrReferenceTarget.getName().equals(candidateMethod.getName()))
		{
			return false;
		}

		// Check if the return types are compatible.
		final JavaClass methodCallTargetReturnType = methodCallOrReferenceTarget.getReturnType().toErasure();
		final JavaClass candidateMethodReturnType = candidateMethod.getReturnType().toErasure();
		if (!methodCallTargetReturnType.isAssignableTo(candidateMethodReturnType.getName()))
		{
			return false;
		}

		// Check if the parameter types are compatible.
		final List<JavaType> methodCallTargetParameters = methodCallOrReferenceTarget.getParameterTypes();
		final List<JavaType> candidateMethodParameters = candidateMethod.getParameterTypes();
		if (methodCallTargetParameters.size() != candidateMethodParameters.size())
		{
			return false;
		}
		for (int i = 0; i < methodCallTargetParameters.size(); i++)
		{
			final JavaType methodCallTargetParameter = methodCallTargetParameters.get(i);
			final JavaType candidateMethodParameter = candidateMethodParameters.get(i);
			if (!methodCallTargetParameter.toErasure().isAssignableTo(candidateMethodParameter.toErasure().getName()))
			{
				return false;
			}
		}

		return true;
	}

	private boolean isSameCodeUnit(final CodeUnitAccessTarget constructorCallOrReferenceTarget, final JavaConstructor candidateConstructor)
	{
		// Check if the parameter types are compatible.
		final List<JavaType> constructorCallTargetParameters = constructorCallOrReferenceTarget.getParameterTypes();
		final List<JavaType> candidateConstructorParameters = candidateConstructor.getParameterTypes();
		if (constructorCallTargetParameters.size() != candidateConstructorParameters.size())
		{
			return false;
		}
		for (int i = 0; i < constructorCallTargetParameters.size(); i++)
		{
			final JavaType constructorCallTargetParameter = constructorCallTargetParameters.get(i);
			final JavaType candidateConstructorParameter = candidateConstructorParameters.get(i);
			if (!constructorCallTargetParameter.toErasure().isAssignableTo(candidateConstructorParameter.toErasure().getName()))
			{
				return false;
			}
		}

		return true;
	}

	private boolean isSameField(final FieldAccessTarget fieldAccessTarget, final JavaField candidateField)
	{
		// Check if the field names are the same.
		if (!fieldAccessTarget.getName().equals(candidateField.getName()))
		{
			return false;
		}

		// Check if the field types are compatible.
		final JavaClass fieldAccessTargetType = fieldAccessTarget.getRawType().toErasure();
		final JavaClass candidateFieldType = candidateField.getRawType().toErasure();
		if (!fieldAccessTargetType.isAssignableTo(candidateFieldType.getName()))
		{
			return false;
		}

		return true;
	}
}
