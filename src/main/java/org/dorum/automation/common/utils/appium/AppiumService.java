package org.dorum.automation.common.utils.appium;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.dorum.automation.common.utils.Log;

import java.io.File;
import java.net.ServerSocket;

public class AppiumService {

    private static AppiumDriverLocalService service;

    public AppiumDriverLocalService startService(int port) {
        if (!checkIfServerIsRunning(port)) {
            Log.info("Starting Appium Server on port: %s", port);
            service = AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
                    .withIPAddress("127.0.0.1")
                    .usingPort(port)
                    .withArgument(GeneralServerFlag.RELAXED_SECURITY)
                    .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                    .withArgument(GeneralServerFlag.ALLOW_INSECURE, Boolean.TRUE.toString())
                    .withArgument(GeneralServerFlag.LOG_LEVEL, "debug:debug")
                    .withArgument(GeneralServerFlag.LOG_TIMESTAMP)
                    .withArgument(GeneralServerFlag.DEBUG_LOG_SPACING)
                    .withAppiumJS(new File("C:\\Users\\SI_NAM_176226_D_aut\\AppData\\Roaming\\npm\\node_modules" +
                            "\\appium\\build\\lib\\main.js")));
            service.clearOutPutStreams();
            service.start();
        }
        return service;
    }

    public boolean checkIfServerIsRunning(int port) {
        boolean isServerRunning = false;
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.close();
        } catch (Exception e) {
            Log.warn("FAILED - check Server running\n%s", e);
            //If control comes here, then it means that the port is in use
            isServerRunning = true;
        } finally {
            serverSocket = null;
        }
        return isServerRunning;
    }
}
