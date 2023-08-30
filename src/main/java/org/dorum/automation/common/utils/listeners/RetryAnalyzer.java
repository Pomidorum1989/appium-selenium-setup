package org.dorum.automation.common.utils.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int count = 0;
    private static final int MAX_TRY = 3;
    public static boolean isRetryFailed = false;

    @Override
    public boolean retry(ITestResult iTestResult) {
        String status = "[Run FAILED]";
        if (!iTestResult.isSuccess()) {
            iTestResult.setStatus(ITestResult.FAILURE);
            System.out.printf("%s RetryAnalyzer (count=%s | method=%s)\n",
                    status, count, iTestResult.getMethod().getMethodName());
            if (iTestResult.getMethod().getMethodName().contains("Setup")) {
                if (count < MAX_TRY) {
                    count++;
                    System.out.println("Continue re-try");
                    return true; // Continue re-try
                }
                isRetryFailed = true;
            }
        } else {
            status = "[Run PASS]";
            iTestResult.setStatus(ITestResult.SUCCESS);
            isRetryFailed = false;
        }
        System.out.printf("%s RetryAnalyzer - final (count=%s | method=%s)\n",
                status, count, iTestResult.getMethod().getMethodName());
        count = 0;
        return false; // Stop re-try
    }
}
