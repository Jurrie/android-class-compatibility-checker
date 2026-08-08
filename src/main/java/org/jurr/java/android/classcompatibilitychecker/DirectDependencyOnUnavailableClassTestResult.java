package org.jurr.java.android.classcompatibilitychecker;

import com.tngtech.archunit.core.domain.Dependency;

public class DirectDependencyOnUnavailableClassTestResult implements TestResult
{
	private final Dependency dependency;

	DirectDependencyOnUnavailableClassTestResult(final Dependency dependency)
	{
		this.dependency = dependency;
	}

	@Override
	public String getSourceClass()
	{
		return dependency.getOriginClass().getName();
	}

	@Override
	public String getTargetClass()
	{
		return dependency.getTargetClass().getName();
	}

	@Override
	public String getSourceFileName()
	{
		return dependency.getSourceCodeLocation().getSourceFileName();
	}

	@Override
	public int getSourceLineNumber()
	{
		return dependency.getSourceCodeLocation().getLineNumber();
	}

	@Override
	public String toErrorDescription()
	{
		return getSourceClass() + " → " + getTargetClass() + " (at " + getSourceFileName() + ":" + getSourceLineNumber() + ")";
	}
}
