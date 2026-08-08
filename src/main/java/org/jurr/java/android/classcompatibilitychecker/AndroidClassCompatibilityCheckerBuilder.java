package org.jurr.java.android.classcompatibilitychecker;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A builder class for creating instances of {@link AndroidClassCompatibilityChecker}.
 * It allows for a method chaining style, setter methods, static methods and "just use the constructor" style of setting the parameters. Of course <em>way</em> too elaborate, but hey - it's <em>my</em> project so I can do what I want ;)
 */
public class AndroidClassCompatibilityCheckerBuilder
{
	private int androidVersion;
	private Path tempDirectory = Paths.get(System.getProperty("java.io.tmpdir"));
	private String desugaringJarMavenVersion = AndroidClassCompatibilityChecker.NO_DESUGARING;
	private boolean includeTestClasses = false;

	public AndroidClassCompatibilityCheckerBuilder(final int androidVersion)
	{
		this.androidVersion = androidVersion;
	}

	public AndroidClassCompatibilityCheckerBuilder(final int androidVersion, final String desugaringVersion)
	{
		this.androidVersion = androidVersion;
		desugaringJarMavenVersion = desugaringVersion;
	}

	public AndroidClassCompatibilityCheckerBuilder(final int androidVersion, final Path tempDirectory)
	{
		this.androidVersion = androidVersion;
		this.tempDirectory = tempDirectory;
	}

	public AndroidClassCompatibilityCheckerBuilder(final int androidVersion, final boolean includeTestClasses)
	{
		this.androidVersion = androidVersion;
		this.includeTestClasses = includeTestClasses;
	}

	public AndroidClassCompatibilityCheckerBuilder(final int androidVersion, final String desugaringJarMavenVersion, final Path tempDirectory)
	{
		this.androidVersion = androidVersion;
		this.tempDirectory = tempDirectory;
		this.desugaringJarMavenVersion = desugaringJarMavenVersion;
	}

	public AndroidClassCompatibilityCheckerBuilder(final int androidVersion, final String desugaringJarMavenVersion, final boolean includeTestClasses)
	{
		this.androidVersion = androidVersion;
		this.desugaringJarMavenVersion = desugaringJarMavenVersion;
		this.includeTestClasses = includeTestClasses;
	}

	public AndroidClassCompatibilityCheckerBuilder(final int androidVersion, final Path tempDirectory, final boolean includeTestClasses)
	{
		this.androidVersion = androidVersion;
		this.tempDirectory = tempDirectory;
		this.includeTestClasses = includeTestClasses;
	}

	// If you want to use all parameters, you might be better of using the static build(int, String, Path, boolean) method.
	public AndroidClassCompatibilityCheckerBuilder(final int androidVersion, final String desugaringJarMavenVersion, final Path tempDirectory, final boolean includeTestClasses)
	{
		this.androidVersion = androidVersion;
		this.tempDirectory = tempDirectory;
		this.desugaringJarMavenVersion = desugaringJarMavenVersion;
		this.includeTestClasses = includeTestClasses;
	}

	public static AndroidClassCompatibilityChecker build(final int androidVersion) throws IOException
	{
		return new AndroidClassCompatibilityCheckerBuilder(androidVersion).build();
	}

	public static AndroidClassCompatibilityChecker build(final int androidVersion, final String desugaringJarMavenVersion) throws IOException
	{
		return new AndroidClassCompatibilityCheckerBuilder(androidVersion, desugaringJarMavenVersion).build();
	}

	public static AndroidClassCompatibilityChecker build(final int androidVersion, final Path tempDirectory) throws IOException
	{
		return new AndroidClassCompatibilityCheckerBuilder(androidVersion, tempDirectory).build();
	}

	public static AndroidClassCompatibilityChecker build(final int androidVersion, final boolean includeTestClasses) throws IOException
	{
		return new AndroidClassCompatibilityCheckerBuilder(androidVersion, includeTestClasses).build();
	}

	public static AndroidClassCompatibilityChecker build(final int androidVersion, final String desugaringJarMavenVersion, final Path tempDirectory) throws IOException
	{
		return new AndroidClassCompatibilityCheckerBuilder(androidVersion, desugaringJarMavenVersion, tempDirectory).build();
	}

	public static AndroidClassCompatibilityChecker build(final int androidVersion, final String desugaringJarMavenVersion, final boolean includeTestClasses) throws IOException
	{
		return new AndroidClassCompatibilityCheckerBuilder(androidVersion, desugaringJarMavenVersion, includeTestClasses).build();
	}

	public static AndroidClassCompatibilityChecker build(final int androidVersion, final Path tempDirectory, final boolean includeTestClasses) throws IOException
	{
		return new AndroidClassCompatibilityCheckerBuilder(androidVersion, tempDirectory, includeTestClasses).build();
	}

	public static AndroidClassCompatibilityChecker build(final int androidVersion, final String desugaringJarMavenVersion, final Path tempDirectory, final boolean includeTestClasses) throws IOException
	{
		return new AndroidClassCompatibilityCheckerBuilder(androidVersion, desugaringJarMavenVersion, tempDirectory, includeTestClasses).build();
	}

	/**
	 * Returns the Android API level to check against. For example <code>19</code> targets Android 4.4 "KitKat" and <code>33</code> targets Android 13 "Tiramisu". For a full list of API levels, check the <a href="https://developer.android.com/guide/topics/manifest/uses-sdk-element#ApiLevels">official documentation</a>.
	 *
	 * @return The Android API level to check against.
	 * @see #setAndroidVersion(int)
	 */
	public int getAndroidVersion()
	{
		return androidVersion;
	}

	/**
	 * Sets the Android API level to check against. For example <code>19</code> targets Android 4.4 "KitKat" and <code>33</code> targets Android 13 "Tiramisu". For a full list of API levels, check the <a href="https://developer.android.com/guide/topics/manifest/uses-sdk-element#ApiLevels">official documentation</a>.
	 *
	 * @param androidVersion The Android API level to check against.
	 * @see #getAndroidVersion()
	 */
	public void setAndroidVersion(final int androidVersion)
	{
		this.androidVersion = androidVersion;
	}

	/**
	 * Returns the temporary directory used for downloading artifacts. Defaults to the system temporary directory (property "java.io.tmpdir").
	 *
	 * @return The temporary directory used for downloading artifacts.
	 * @see #setTempDirectory(Path)
	 */
	public Path getTempDirectory()
	{
		return tempDirectory;
	}

	/**
	 * Sets the temporary directory to use for downloading artifacts. Defaults to the system temporary directory (property "java.io.tmpdir"). s
	 * For example, when you use Maven, you could use <code>withTempDirectory(Paths.get("target/"))</code> to use the `target/` directory of your Maven project.
	 *
	 * @param tempDirectory The temporary directory to use for downloading artifacts.
	 * @see #getTempDirectory()
	 */
	public void setTempDirectory(final Path tempDirectory)
	{
		this.tempDirectory = tempDirectory;
	}

	/**
	 * Syntactic sugar for {@link #setTempDirectory(Path)} while using a method chaining.
	 *
	 * @param tempDirectory The temporary directory to use for downloading artifacts.
	 * @return The current instance of {@link AndroidClassCompatibilityCheckerBuilder} for method chaining.
	 */
	public AndroidClassCompatibilityCheckerBuilder withTempDirectory(final Path tempDirectory)
	{
		this.tempDirectory = tempDirectory;
		return this;
	}

	/**
	 * Returns whether desugaring is enabled. Desugaring is enabled if the Maven version of the desugaring library is set to a value other than {@link AndroidClassCompatibilityChecker#NO_DESUGARING}.
	 *
	 * @return True if desugaring is enabled, false otherwise.
	 * @see #setDesugaringJarMavenVersion(String)
	 */
	public boolean isDesugaring()
	{
		return !AndroidClassCompatibilityChecker.NO_DESUGARING.equals(desugaringJarMavenVersion);
	}

	/**
	 * Returns the Maven version of the desugaring library to use. For example, <code>"2.0.3"</code> would use the library "com.android.tools:desugar_jdk_libs:2.0.3".
	 *
	 * @return The Maven version of the desugaring library to use, or {@link AndroidClassCompatibilityChecker#NO_DESUGARING} if desugaring is not enabled.
	 */
	public String getDesugaringJarMavenVersion()
	{
		return desugaringJarMavenVersion;
	}

	/**
	 * Sets the Maven version of the desugaring library to use. For example, <code>"2.0.3"</code> would use the library "com.android.tools:desugar_jdk_libs:2.0.3".
	 * If not set, desugaring will not be used.
	 *
	 * @param desugaringJarMavenVersion The Maven version of the desugaring library to use.
	 */
	public void setDesugaringJarMavenVersion(final String desugaringJarMavenVersion)
	{
		this.desugaringJarMavenVersion = desugaringJarMavenVersion;
	}

	/**
	 * Do not use desugaring. This is the default behavior.
	 */
	public void setNoDesugaring()
	{
		desugaringJarMavenVersion = AndroidClassCompatibilityChecker.NO_DESUGARING;
	}

	/**
	 * Syntactic sugar for {@link #setDesugaringJarMavenVersion(String)} while using a method chaining.
	 *
	 * @param desugaringJarMavenVersion The Maven version of the desugaring library used.
	 * @return The current instance of {@link AndroidClassCompatibilityCheckerBuilder} for method chaining.
	 */
	public AndroidClassCompatibilityCheckerBuilder withDesugaringJarMavenVersion(final String desugaringJarMavenVersion)
	{
		this.desugaringJarMavenVersion = desugaringJarMavenVersion;
		return this;
	}

	/**
	 * Syntactic sugar for {@link #setNoDesugaring()} while using a method chaining.
	 *
	 * @return The current instance of {@link AndroidClassCompatibilityCheckerBuilder} for method chaining.
	 */
	public AndroidClassCompatibilityCheckerBuilder withNoDesugaring()
	{
		desugaringJarMavenVersion = AndroidClassCompatibilityChecker.NO_DESUGARING;
		return this;
	}

	/**
	 * Returns whether we should include test classes in the check. If true, test classes will be included; if false, they will be excluded.
	 *
	 * @return True if test classes should be included, false otherwise.
	 * @see #setIncludeTestClasses(boolean)
	 */
	public boolean isIncludeTestClasses()
	{
		return includeTestClasses;
	}

	/**
	 * Sets whether to include test classes in the check. If true, test classes will be included; if false, they will be excluded.
	 *
	 * @param includeTestClasses Whether to include test classes in the check. If true, test classes will be included; if false, they will be excluded.
	 * @see #isIncludeTestClasses()
	 */
	public void setIncludeTestClasses(final boolean includeTestClasses)
	{
		this.includeTestClasses = includeTestClasses;
	}

	/**
	 * Syntactic sugar for {@link #setIncludeTestClasses(boolean)} while using a method chaining.
	 *
	 * @param includeTestClasses Whether to include test classes in the check. If true, test classes will be included; if false, they will be excluded.
	 * @return The current instance of {@link AndroidClassCompatibilityCheckerBuilder} for method chaining.
	 */
	public AndroidClassCompatibilityCheckerBuilder withIncludeTestClasses(final boolean includeTestClasses)
	{
		this.includeTestClasses = includeTestClasses;
		return this;
	}

	/**
	 * Syntactic sugar for {@link #setIncludeTestClasses(boolean)} with parameter <code>true</code> while using a method chaining.
	 *
	 * @return The current instance of {@link AndroidClassCompatibilityCheckerBuilder} for method chaining.
	 */
	public AndroidClassCompatibilityCheckerBuilder withIncludeTestClasses()
	{
		includeTestClasses = true;
		return this;
	}

	/**
	 * Syntactic sugar for {@link #setIncludeTestClasses(boolean)} with parameter <code>false</code> while using a method chaining.
	 *
	 * @return The current instance of {@link AndroidClassCompatibilityCheckerBuilder} for method chaining.
	 */
	public AndroidClassCompatibilityCheckerBuilder withoutIncludeTestClasses()
	{
		includeTestClasses = false;
		return this;
	}

	/**
	 * Build the actual {@link AndroidClassCompatibilityChecker} instance with the parameters set in this builder.
	 *
	 * @return The {@link AndroidClassCompatibilityChecker} instance.
	 * @throws IOException If an I/O error occurs while creating the instance.
	 */
	public AndroidClassCompatibilityChecker build() throws IOException
	{
		return new AndroidClassCompatibilityChecker(androidVersion, tempDirectory, desugaringJarMavenVersion, includeTestClasses);
	}
}
