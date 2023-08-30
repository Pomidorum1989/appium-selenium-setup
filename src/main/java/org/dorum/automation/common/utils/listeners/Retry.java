package org.dorum.automation.common.utils.listeners;

import org.dorum.automation.common.utils.Log;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {

    private int retryCount = 0;

    public boolean retry(ITestResult result) {
        int maxRetryCount = 0;
        if (retryCount < maxRetryCount) {
            retryCount++;
            return Log.info("Retry #%s for test: %s", retryCount, result.getMethod().getMethodName());
        }
        return false;
    }
}
