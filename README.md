# android-class-compatibility-checker

android-class-compatibility-checker helps you verify that code written for a regular JVM will also work on Android.

It is useful when you build a Java library that should run on both:

- Android devices and Android-based runtimes
- regular JVMs, such as desktop or server JVMs

A typical problem is that code compiles fine on the JVM but uses classes, methods, or fields that are not present on the Android runtime you target. This tool catches those incompatibilities before you ship your library.

## Why use it?

If you maintain a shared Java library that targets both Android and the JVM, you can easily introduce runtime failures that are hard to spot.

Examples:

- a class exists on the JVM but is missing from the Android API level you target
- a method is present on the JVM but not on the Android runtime
- a newer Java API is available only after desugaring, while your Android target does not include it

This project checks your classes against an Android classpath and reports the problematic dependencies. Note that this project does not test on language features (e.g. when you use Java 21 things while Android supports up to Java 17).

This project does not mandate the use of Android Studio or Gradle. You are free to choose any IDE and build tool you want.

## Dependency coordinates

The library is published with:

- GroupId: `org.jurr.java.android.classcompatibilitychecker`
- ArtifactId: `android-class-compatibility-checker`

### Maven

```xml
<dependency>
  <groupId>org.jurr.java.android.classcompatibilitychecker</groupId>
  <artifactId>android-class-compatibility-checker</artifactId>
  <version>[INSERT LATEST VERSION]</version>
  <scope>test</scope>
</dependency>
```

### Gradle

```groovy
dependencies {
  testImplementation 'org.jurr.java.android.classcompatibilitychecker:android-class-compatibility-checker:[INSERT LATEST VERSION]'
}
```

# How to use

To use the checker, choose the Android API level that your library should support. For example, `19` targets Android 4.4 "KitKat" and `33` targets Android 13 "Tiramisu". For a full list of API levels, check the [official documentation](https://developer.android.com/guide/topics/manifest/uses-sdk-element#ApiLevels). The checker then compares your classes against the Android platform jar for that API level.

If you also want to model Java APIs that are made available through desugaring, pass a desugaring library version when you create the checker. [Desugaring](https://developer.android.com/studio/write/java8-support#library-desugaring) is the process of making newer Java APIs available on older Android versions by rewriting or backporting them. This project can optionally add the `desugar_jdk_libs` jar to the checked classpath so that compatibility is tested against the desugared API surface.

Desugaring for Java 11+ APIs comes in three flavors: `minimal`, `default` (includes `minimal`) and `nio` (includes `default`). When you use desugaring, it is assumed that you use `nio`.

## Using it as a unit test

The most common way to use this project is from a unit test. For example, when using JUnit, create a JUnit test, instantiate an `AndroidClassCompatibilityChecker`, and assert that the test results are successful.

```java
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.jurr.java.android.classcompatibilitychecker.AndroidClassCompatibilityChecker;
import org.jurr.java.android.classcompatibilitychecker.AndroidClassCompatibilityCheckerBuilder;
import org.jurr.java.android.classcompatibilitychecker.TestResults;

class MyLibraryCompatibilityTest
{
    private final AndroidClassCompatibilityChecker checker = new AndroidClassCompatibilityCheckerBuilder(23).build();

    @Test
    void myLibraryIsAndroidCompatible() throws IOException
    {
        TestResults results = checker.testPackage("com.example.mylibrary");
        assertTrue(results.isSuccessful(), results.toErrorDescription());
    }
}
```

If your project has test classes that should also be checked, use `.withIncludeTestClasses(true)` when building the checker.

## Using it as a stand-alone tool

The project was meant to be used in a unit test, but could also be used as a stand-alone tool. For example, you could write a class like this:

```java
import java.io.IOException;
import java.nio.file.Paths;

import org.jurr.java.android.classcompatibilitychecker.AndroidClassCompatibilityChecker;
import org.jurr.java.android.classcompatibilitychecker.AndroidClassCompatibilityCheckerBuilder;
import org.jurr.java.android.classcompatibilitychecker.TestResults;

public class AndroidCompatibilityCheckExample
{
    public static void main(String[] args) throws IOException
    {
        AndroidClassCompatibilityChecker checker = new AndroidClassCompatibilityCheckerBuilder(23).build();

        TestResults results = checker.testClass(MyLibraryClass.class);

        if (!results.isSuccessful())
        {
            System.out.println(results.toErrorDescription());
        }
    }
}
```

You can also check whole packages (including subpackages):

```java
TestResults results = checker.testPackage("com.example.mylibrary");
```

# Building this project

This project uses [SDKMAN!](https://sdkman.io/). If you use it too, then as soon as you change to the root directory, you'll get the correct Java and Maven version for building it. If you don't use it, please see the `.sdkmanrc` file for the correct Java and Maven versions to use.

The project is built with Maven. If you are not familiar with it, just give the following command from the repository root:

```bash
mvn clean verify
```

This builds and tests the jar and places it in the `target/` directory. You do not need Maven to use the library in your own code; the jar can be added to any Java classpath manually if you prefer.
