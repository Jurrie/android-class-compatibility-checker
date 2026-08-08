package org.jurr.java.android.classcompatibilitychecker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;

class AndroidClassCompatibilityCheckerTest
{
	private static final AndroidClassCompatibilityChecker ANDROID_23_CHECKER;
	private static final AndroidClassCompatibilityChecker ANDROID_23_WITH_DESUGARING_CHECKER;

	static
	{
		try
		{
			final AndroidClassCompatibilityCheckerBuilder builder = new AndroidClassCompatibilityCheckerBuilder(23)
					.withTempDirectory(Paths.get("target/")) // Be a good Maven citizen
					.withIncludeTestClasses(true); // We need to include test classes because our own inner classes are test classes
			ANDROID_23_CHECKER = builder.build();
			ANDROID_23_WITH_DESUGARING_CHECKER = builder.withDesugaringJarMavenVersion("2.1.5").build();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to initialize AndroidClassCompatibilityChecker instances", e);
		}
	}

	@Test
	void testUsingUnavailableClassConstructor_noDesugaring()
	{
		final TestResults testResults = ANDROID_23_CHECKER.testClass(UsingUnavailableClassConstructor.class);
		assertFalse(testResults.isSuccessful(), "Android compatibility test did not fail as expected");

		assertEquals(1, testResults.getDirectDependenciesOnUnavailableClass().size(), "Expected 1 direct dependency on unavailable class");
		testResults.getDirectDependenciesOnUnavailableClass().forEach(d -> {
			assertEquals(UsingUnavailableClassConstructor.class.getName(), d.getSourceClass(), "Expected tested class name to match");
			assertEquals("java.time.format.DateTimeFormatterBuilder", d.getTargetClass(), "Expected target class name to match");
		});

		assertEquals(0, testResults.getAccessesToUnavailableMember().size(), "Expected 0 accesses to unavailable member");
	}

	@Test
	void testUsingUnavailableClassConstructor_withDesugaring()
	{
		final TestResults testResults = ANDROID_23_WITH_DESUGARING_CHECKER.testClass(UsingUnavailableClassConstructor.class);
		assertTrue(testResults.isSuccessful(), "Android compatibility test failed: " + testResults.toErrorDescription());

		assertEquals(0, testResults.getDirectDependenciesOnUnavailableClass().size(), "Expected 0 direct dependencies on unavailable class");

		assertEquals(0, testResults.getAccessesToUnavailableMember().size(), "Expected 0 accesses to unavailable member");
	}

	@Test
	void testUsingUnavailableClassStaticMethod_noDesugaring()
	{
		final TestResults testResults = ANDROID_23_CHECKER.testClass(UsingUnavailableClassStaticMethod.class);
		assertFalse(testResults.isSuccessful(), "Android compatibility test did not fail as expected");

		assertEquals(1, testResults.getDirectDependenciesOnUnavailableClass().size(), "Expected 1 direct dependency on unavailable class");
		testResults.getDirectDependenciesOnUnavailableClass().forEach(d -> {
			assertEquals(UsingUnavailableClassStaticMethod.class.getName(), d.getSourceClass(), "Expected tested class name to match");
			assertEquals("java.time.Clock", d.getTargetClass(), "Expected target class name to match");
		});

		assertEquals(0, testResults.getAccessesToUnavailableMember().size(), "Expected 0 accesses to unavailable member");
	}

	@Test
	void testUsingUnavailableClassStaticMethod_withDesugaring()
	{
		final TestResults testResults = ANDROID_23_WITH_DESUGARING_CHECKER.testClass(UsingUnavailableClassStaticMethod.class);
		assertTrue(testResults.isSuccessful(), "Android compatibility test failed: " + testResults.toErrorDescription());

		assertEquals(0, testResults.getDirectDependenciesOnUnavailableClass().size(), "Expected 0 direct dependencies on unavailable class");

		assertEquals(0, testResults.getAccessesToUnavailableMember().size(), "Expected 0 accesses to unavailable member");
	}

	@Test
	void testUsingUnavailableClassAsField_noDesugaring()
	{
		final TestResults testResults = ANDROID_23_CHECKER.testClass(UsingUnavailableClassAsField.class);
		assertFalse(testResults.isSuccessful(), "Android compatibility test did not fail as expected");

		assertEquals(1, testResults.getDirectDependenciesOnUnavailableClass().size(), "Expected 1 direct dependency on unavailable class");
		testResults.getDirectDependenciesOnUnavailableClass().forEach(d -> {
			assertEquals(UsingUnavailableClassAsField.class.getName(), d.getSourceClass(), "Expected tested class name to match");
			assertEquals("java.time.Clock", d.getTargetClass(), "Expected target class name to match");
		});

		assertEquals(0, testResults.getAccessesToUnavailableMember().size(), "Expected 0 accesses to unavailable member");
	}

	@Test
	void testUsingUnavailableClassAsField_withDesugaring()
	{
		final TestResults testResults = ANDROID_23_WITH_DESUGARING_CHECKER.testClass(UsingUnavailableClassAsField.class);
		assertTrue(testResults.isSuccessful(), "Android compatibility test failed: " + testResults.toErrorDescription());

		assertEquals(0, testResults.getDirectDependenciesOnUnavailableClass().size(), "Expected 0 direct dependencies on unavailable class");

		assertEquals(0, testResults.getAccessesToUnavailableMember().size(), "Expected 0 accesses to unavailable member");
	}

	@Test
	void testUsingUsingUnavailableMethod()
	{
		final TestResults testResults = ANDROID_23_CHECKER.testClass(UsingUnavailableMethod.class);
		assertFalse(testResults.isSuccessful(), "Android compatibility test did not fail as expected");

		assertEquals(0, testResults.getDirectDependenciesOnUnavailableClass().size(), "Expected 0 direct dependencies on unavailable class");

		assertEquals(1, testResults.getAccessesToUnavailableMember().size(), "Expected 2 accesses to unavailable member");
		testResults.getAccessesToUnavailableMember().forEach(d -> {
			assertEquals(UsingUnavailableMethod.class.getName(), d.getSourceClass(), "Expected tested class name to match");
			assertEquals("java.util.ArrayList", d.getTargetClass(), "Expected target class name to match");
			assertEquals("forEach", d.getTargetCodeUnit(), "Expected target code unit name to match");
		});
	}

	@Test
	void testUsingUnavailableMethodInStaticBlock()
	{
		final TestResults testResults = ANDROID_23_CHECKER.testClass(UsingUnavailableMethodInStaticBlock.class);
		assertFalse(testResults.isSuccessful(), "Android compatibility test did not fail as expected");

		assertEquals(0, testResults.getDirectDependenciesOnUnavailableClass().size(), "Expected 0 direct dependencies on unavailable class");

		assertEquals(1, testResults.getAccessesToUnavailableMember().size(), "Expected 1 access to unavailable member");
		testResults.getAccessesToUnavailableMember().forEach(d -> {
			assertEquals(UsingUnavailableMethodInStaticBlock.class.getName(), d.getSourceClass(), "Expected tested class name to match");
			assertEquals("java.util.ArrayList", d.getTargetClass(), "Expected target class name to match");
			assertEquals("forEach", d.getTargetCodeUnit(), "Expected target code unit name to match");
		});
	}

	@Test
	void testUsingUnavailableMethodReference_noDesugaring()
	{
		final TestResults testResults = ANDROID_23_CHECKER.testClass(UsingUnavailableMethodReference.class);
		assertFalse(testResults.isSuccessful(), "Android compatibility test did not fail as expected");

		assertEquals(0, testResults.getDirectDependenciesOnUnavailableClass().size(), "Expected 0 direct dependencies on unavailable class");

		assertEquals(1, testResults.getAccessesToUnavailableMember().size(), "Expected 1 access to unavailable member");
		testResults.getAccessesToUnavailableMember().forEach(d -> {
			assertEquals(UsingUnavailableMethodReference.class.getName(), d.getSourceClass(), "Expected tested class name to match");
			assertEquals("java.util.Collection", d.getTargetClass(), "Expected target class name to match");
			assertEquals("stream", d.getTargetCodeUnit(), "Expected target code unit name to match");
		});
	}

	@Test
	void testUsingUnavailableMethodReference_withDesugaring()
	{
		final TestResults testResults = ANDROID_23_WITH_DESUGARING_CHECKER.testClass(UsingUnavailableMethodReference.class);
		assertTrue(testResults.isSuccessful(), "Android compatibility test failed: " + testResults.toErrorDescription());

		assertEquals(0, testResults.getDirectDependenciesOnUnavailableClass().size(), "Expected 0 direct dependencies on unavailable class");

		assertEquals(0, testResults.getAccessesToUnavailableMember().size(), "Expected 0 accesses to unavailable member");
	}

	static class UsingUnavailableClassConstructor
	{
		// java.time.Clock is not available on Android API level 23, but is available after desugaring
		public final Object dateTimeFormatterBuilder = new java.time.format.DateTimeFormatterBuilder();
	}

	static class UsingUnavailableClassStaticMethod
	{
		// java.time.Clock is not available on Android API level 23, but is available after desugaring
		public final Object clock = java.time.Clock.systemUTC();
	}

	static class UsingUnavailableClassAsField
	{
		// java.time.Clock is not available on Android API level 23, but is available after desugaring
		public final java.time.Clock clock = null;
	}

	static class UsingUnavailableMethod
	{
		public void dummy()
		{
			// java.util.ArrayList is available on Android API level 23, but forEach is not
			new ArrayList<>().forEach(System.out::println);
		}
	}

	static class UsingUnavailableMethodInStaticBlock
	{
		static
		{
			// java.util.ArrayList is available on Android API level 23, but forEach is not
			new ArrayList<>().forEach(System.out::println);
		}
	}

	static class UsingUnavailableMethodReference
	{
		interface ObjectSupplier
		{
			Object get();
		}

		public ObjectSupplier dummy()
		{
			Collection<String> collection = new ArrayList<>();
			// java.util.Collection is available on Android API level 23, but method stream() is not. It is available after desugaring.
			return collection::stream;
		}
	}
}