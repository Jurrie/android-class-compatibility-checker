package org.jurr.java.android.classcompatibilitychecker;

import java.util.ArrayList;
import java.util.List;

public class TestResults
{
	private final List<TestResult> directDependenciesOnUnavailableClass;
	private final List<AccessToUnavailableMemberTestResult> accessesToUnavailableMember;

	TestResults()
	{
		directDependenciesOnUnavailableClass = new ArrayList<>();
		accessesToUnavailableMember = new ArrayList<>();
	}

	void addAll(final TestResults otherTestResults)
	{
		directDependenciesOnUnavailableClass.addAll(otherTestResults.getDirectDependenciesOnUnavailableClass());
		accessesToUnavailableMember.addAll(otherTestResults.getAccessesToUnavailableMember());
	}

	void addDirectDependencyOnUnavailableClass(final TestResult testResult)
	{
		directDependenciesOnUnavailableClass.add(testResult);
	}

	public List<TestResult> getDirectDependenciesOnUnavailableClass()
	{
		return directDependenciesOnUnavailableClass;
	}

	void addAccessToUnavailableMember(final AccessToUnavailableMemberTestResult testResult)
	{
		accessesToUnavailableMember.add(testResult);
	}

	public List<AccessToUnavailableMemberTestResult> getAccessesToUnavailableMember()
	{
		return accessesToUnavailableMember;
	}

	public boolean isSuccessful()
	{
		return directDependenciesOnUnavailableClass.isEmpty() && accessesToUnavailableMember.isEmpty();
	}

	public String toErrorDescription()
	{
		final StringBuilder errorDescription = new StringBuilder();
		if (!directDependenciesOnUnavailableClass.isEmpty())
		{
			errorDescription.append("There are ").append(directDependenciesOnUnavailableClass.size()).append(" direct dependencies on classes that are not available on the Android classpath:\n");
			directDependenciesOnUnavailableClass.forEach(testResult -> errorDescription.append("- ").append(testResult.toErrorDescription()).append("\n"));
		}
		if (!accessesToUnavailableMember.isEmpty())
		{
			errorDescription.append("There are ").append(accessesToUnavailableMember.size()).append(" members accessed that are not available on the Android classpath:\n");
			accessesToUnavailableMember.forEach(testResult -> errorDescription.append("- ").append(testResult.toErrorDescription()).append("\n"));
		}
		return errorDescription.toString();
	}
}
