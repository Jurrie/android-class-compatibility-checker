package org.jurr.java.android.classcompatibilitychecker;

public interface TestResult
{
	String getSourceClass();

	String getTargetClass();

	String getSourceFileName();

	int getSourceLineNumber();

	String toErrorDescription();
}
