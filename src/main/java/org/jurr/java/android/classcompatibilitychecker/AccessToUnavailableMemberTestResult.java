package org.jurr.java.android.classcompatibilitychecker;

import com.tngtech.archunit.core.domain.JavaAccess;

public class AccessToUnavailableMemberTestResult implements TestResult
{
	private final JavaAccess<?> accessToUnavailableMember;

	AccessToUnavailableMemberTestResult(final JavaAccess<?> accessToUnavailableMember)
	{
		this.accessToUnavailableMember = accessToUnavailableMember;
	}

	@Override
	public String getSourceClass()
	{
		return accessToUnavailableMember.getOriginOwner().getName();
	}

	public String getSourceCodeUnit()
	{
		return accessToUnavailableMember.getOrigin().getName();
	}

	@Override
	public String getTargetClass()
	{
		return accessToUnavailableMember.getTargetOwner().getName();
	}

	public String getTargetCodeUnit()
	{
		return accessToUnavailableMember.getTarget().getName();
	}

	@Override
	public String getSourceFileName()
	{
		return accessToUnavailableMember.getSourceCodeLocation().getSourceFileName();
	}

	@Override
	public int getSourceLineNumber()
	{
		return accessToUnavailableMember.getSourceCodeLocation().getLineNumber();
	}

	@Override
	public String toErrorDescription()
	{
		return getSourceClass() + "." + getSourceCodeUnit() + " → " + getTargetClass() + "." + getTargetCodeUnit() + " (at " + getSourceFileName() + ":" + getSourceLineNumber() + ")";
	}
}
