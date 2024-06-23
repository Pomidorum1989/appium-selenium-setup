package org.dorum.automation.common.utils.listeners;

import lombok.extern.log4j.Log4j2;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

@Log4j2
public class Retry implements IRetryAnalyzer {

    private int retryCount = 0;

    public boolean retry(ITestResult result) {
        int maxRetryCount = 0;
        if (retryCount < maxRetryCount) {
            retryCount++;
            log.info("Retry #{} for test: {}", retryCount, result.getMethod().getMethodName());
            return true;
        }
        return false;
    }
}
