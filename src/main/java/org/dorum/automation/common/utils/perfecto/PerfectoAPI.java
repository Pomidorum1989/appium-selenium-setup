package org.dorum.automation.common.utils.perfecto;

import io.appium.java_client.remote.MobilePlatform;
import io.qameta.allure.Step;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.dorum.automation.common.consts.CapabilityName;
import org.dorum.automation.common.consts.HeaderParam;
import org.dorum.automation.common.driver.AbstractDriverManager;
import org.dorum.automation.common.utils.ConfigProperties;
import org.dorum.automation.common.utils.DateUtils;
import org.dorum.automation.common.utils.Log;
import org.dorum.automation.common.utils.TextUtils;
import org.dorum.automation.common.utils.enums.GlobalVariables;
import org.dorum.automation.common.utils.enums.ProjectConfig;
import org.dorum.automation.common.utils.enums.User;
import org.dorum.automation.common.utils.rest.RestAssuredUtils;
import org.json.JSONObject;

import java.util.*;

import static org.dorum.automation.common.utils.perfecto.PerfectoCommands.getSysProperty;

public class PerfectoAPI {

    public static String artifactName, availableDevice, reservationID;
    public static User user = User.getByID(getSysProperty(GlobalVariables.SYSTEM_USER_NAME.getValue()));
    public static final List<String> DEVICE_BLACK_LIST = new ArrayList<>(List.of());

    public static final List<String> PREFERRED_DEVICE_LIST = new ArrayList<>(List.of(
            "R5CR7165EYJ", "R3CN20SK7FF", "RFCN90KJZ2D", "R3CR20A2BCY"
    ));

    @Step("Step >> Perfecto API: Get latest artifact")
    public static String getLatestBuildVersion(boolean isSpecificVersion) {
        Log.info("Perfecto API: getting latest artifact");
        RestAssuredUtils.setupRestAssured();
        String artifactLatestUploaded = "";
        String artifactBrandType = "".
                replace(ConfigProperties.getProperty(ProjectConfig.APPLICATION_VERSION), "").replace("-%s", "");
        String artifactName = String.format("",
                ConfigProperties.getProperty(ProjectConfig.APPLICATION_VERSION));
        String query = "artifacts.findAll {it.artifactMetadata.artifactName =~ '%s' "
                + "&& it.artifactMetadata.artifactSize != 0 && it.artifactMetadata.information.value =~ '%s'}"
                + ".max {it.usageMetadata.creationTime}.%s";
        Map<String, String> map = new HashMap<>();
        String informationValue;
        if (AbstractDriverManager.isAndroid()) {
          informationValue =
                        String.format("%s - Validation develop", ConfigProperties.getProperty(ProjectConfig.APPLICATION_VERSION));

            map.put("skip", "6600");
            map.put("artifactType", MobilePlatform.ANDROID);
        } else {
            informationValue =
                    String.format("%s", ConfigProperties.getProperty(ProjectConfig.APPLICATION_VERSION));
            map.put("skip", "5445");
            map.put("artifactType", MobilePlatform.IOS);
        }
        Response response = RestAssuredUtils.get(PerfectoLink.ARTIFACTS.getUrl(),
                getPerfectoHeaders(PerfectoCommands.getPerfectoToken()), map);
        if ((response != null) && (response.getStatusCode() == 200)) {
            PerfectoAPI.artifactName = response.jsonPath().getString(
                    String.format(query, artifactName,
                            informationValue, "artifactLocator"));
            artifactLatestUploaded = response.jsonPath().getString(
                    String.format(query, artifactBrandType,
                            ConfigProperties.getProperty(ProjectConfig.APPLICATION_VERSION).charAt(0), "artifactLocator"));
            String artifactSize = response.jsonPath().getString(
                    String.format(query, artifactName, ConfigProperties.getProperty(ProjectConfig.APPLICATION_VERSION),
                            "artifactMetadata.artifactSize"));
            double artifactSizeInMB = (double) (Long.parseLong(artifactSize) / 1024 / 1024);
            String artifactType = response.jsonPath().getString(
                    String.format(query, artifactName,
                            ConfigProperties.getProperty(ProjectConfig.APPLICATION_VERSION), "artifactMetadata.artifactType"));
            String artifactVersion = response.jsonPath().getString(
                            String.format(query, artifactName,
                                    ConfigProperties.getProperty(ProjectConfig.APPLICATION_VERSION), "artifactMetadata.information.value"))
                    .replace("[", "").replace("]", "");
            List<String> artifactTags = response.jsonPath().getList(
                    String.format(query, artifactName,
                            ConfigProperties.getProperty(ProjectConfig.APPLICATION_VERSION), "artifactMetadata.tags"));
            String artifactCreationTime = response.jsonPath().getString(
                    String.format(query, artifactName,
                            ConfigProperties.getProperty(ProjectConfig.APPLICATION_VERSION), "usageMetadata.creationTime"));
            artifactCreationTime = DateUtils.getFormattedDateAsString(DateUtils.PATTERN_DMY_HM,
                    DateUtils.getDateFromEpoch(Long.parseLong(artifactCreationTime)));
            String artifactUploaderName = response.jsonPath().getString(
                    String.format(query, artifactName, ConfigProperties.getProperty(ProjectConfig.APPLICATION_VERSION), "username"));
            Log.info("Artifact details:\nArtifact repository location: %s\nArtifact size: %s MB\n"
                            + "Artifact type: %s\nArtifact version: %s\nArtifact creation time: %s\n"
                            + "Artifact uploader name: %s\nArtifact tags: %s",
                    PerfectoAPI.artifactName, artifactSizeInMB, artifactType, artifactVersion, artifactCreationTime,
                    artifactUploaderName, artifactTags);
            Log.info("Latest available version in Perfecto repository is %s", artifactLatestUploaded);
        } else {
            String message = "";
            try {
                if (response != null) message = response.jsonPath().prettify();
            } catch (Exception e) {
                message = StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : "FAILED - parse the JSON document";
            }
            assert response != null;
            Log.warn("FAILED - unable to get latest build.\n Response code: %s\n Status code: %s\n Message: %s",
                    response.getStatusCode(), response.getStatusLine(), message);
        }
        String exactVersion = ConfigProperties.getProperty(ProjectConfig.EXACT_VERSION);
        if (!exactVersion.isEmpty()
                && (TextUtils.getArtifactVersion(PerfectoAPI.artifactName).compareTo(exactVersion) > 0)
                || (PerfectoAPI.artifactName.compareTo(artifactLatestUploaded) > 0)) {
            Log.warn("High probability of application downgrade");
        }
        if (isSpecificVersion) {
            PerfectoAPI.artifactName = ConfigProperties.getProperty(ProjectConfig.EXACT_VERSION);
            Log.warn("Explicitly set application version to: %s", PerfectoAPI.artifactName);
        }
        return PerfectoAPI.artifactName;
    }

    @Step("Step >> Perfecto API: Get available devices")
    public static String getAvailableDevice() {
        Log.info("Perfecto API: getting available devices");
        if (availableDevice == null) {
            if (Boolean.parseBoolean(ConfigProperties.getProperty(ProjectConfig.IS_EXACT_DEVICE))) {
                if (AbstractDriverManager.isAndroid()) {
                    availableDevice = ConfigProperties.getProperty(ProjectConfig.ANDROID_DEVICE_ID);
                } else {
                    availableDevice = ConfigProperties.getProperty(ProjectConfig.IOS_DEVICE_ID);
                }
                verifyExactDeviceAvailability(availableDevice);
                return availableDevice;
            }
            RestAssuredUtils.setupRestAssured();
            Map<String, Object> query = new HashMap<>();
            query.put("admin", false);
            JSONObject device = new JSONObject();
            JSONObject body = new JSONObject();
            if (AbstractDriverManager.isAndroid()) {
                body.put(CapabilityName.MANUFACTURER, "Samsung|Google");
                body.put("os", "Android");
                body.put("osVersion", "10|11|12|13");
                body.put(CapabilityName.MODEL, "Galaxy S.*|Galaxy N.*|Galaxy Z.*|Pixel");
            } else {
                body.put(CapabilityName.MANUFACTURER, "Apple");
                body.put("os", "iOS");
                body.put("osVersion", "14.*|15.*|16.*");
                body.put(CapabilityName.MODEL, "iPhone-1.*(?!Mini)");
            }
            body.put("availableTo", user.getEmail());
            body.put("status", "Connected");
            body.put("inUse", false);
            body.put("language", "English");
            device.put("device", body);
            Response response = RestAssuredUtils.post(
                    PerfectoLink.DEVICE_LIST.getUrl(),
                    getPerfectoHeaders(PerfectoCommands.getPerfectoToken()),
                    device,
                    query,
                    false);
            if ((response == null) || (response.getBody() == null) || (response.getBody().jsonPath() == null)) {
                Log.exception("FAILED - invalid REST Response/body/JSON path: NULL");
            }
            List<String> availableDevices = response.getBody().jsonPath().getList("handsets.handset.deviceId");
            if ((availableDevices == null) || availableDevices.isEmpty()) {
                Log.exception("FAILED - invalid available devices list: NULL/EMPTY");
            }

            availableDevice = getDevice(availableDevices, response);
            if (availableDevice == null) {
                Log.exception("FAILED - not able to find available device from available devices list");
            }
            String jPath = "handsets.handset.findAll {it.deviceId =~ '" + availableDevice + "'}.%s";
            JsonPath path = response.getBody().jsonPath();
            String reserved = path.getString(String.format(jPath, "reserved"));
            String model = path.getString(String.format(jPath, "model"));
            String cradleID = path.getString(String.format(jPath, "cradleId"));
            String lastUsedBy = path.getString(String.format(jPath, "lastUsedBy"));
            String nativeImei = path.getString(String.format(jPath, "nativeImei"));
            String resolution = path.getString(String.format(jPath, "resolution"));
            String phoneNumber = path.getString(String.format(jPath, "phoneNumber"));
            String firmware = path.getString(String.format(jPath, "firmware"));
            String location = path.getString(String.format(jPath, "location"));
            String available = path.getString(String.format(jPath, "available"));
            String inUse = path.getString(String.format(jPath, "inUse"));
            String description = path.getString(String.format(jPath, "description"));
            String logMessage = String.format(
                    "\nDevice ID: %s\nCradle ID: %s\nLast used: %s\nNative IMEI: %s\nResolution: %s\nPhone number: %s\n"
                            + "Firmware: %s\nLocation: %s\nModel: %s\nAvailability: %s\nReserved: %s\nDescription: %s\nInUse: %s",
                    availableDevice, cradleID, lastUsedBy, nativeImei, resolution, phoneNumber, firmware, location,
                    model, available, reserved, description, inUse);
            Log.info(logMessage.replaceAll("[\\[\\]]", ""));
        }
        return availableDevice;
    }

    public static String getDeviceParameter(String deviceId, PerfectoDeviceParam param) {
        Map<PerfectoDeviceParam, String> params = getDeviceParameters(deviceId, param);
        if (params.isEmpty()) {
            return "";
        }
        return params.get(param);
    }

    public static Map<PerfectoDeviceParam, String> getDeviceParameters(String deviceId, PerfectoDeviceParam... params) {
        Log.info("Perfecto API: getting device %s parameters %s", deviceId, params);
        RestAssuredUtils.setupRestAssured();
        Map<String, String> query = new HashMap<>();
        query.put("operation", "info");
        query.put("securityToken", PerfectoCommands.getPerfectoToken());
        Response response = RestAssuredUtils.get(
                String.format(PerfectoLink.DEVICE_PARAMETER.getUrl(), deviceId),
                new Headers(List.of(new Header(HeaderParam.CONTENT_TYPE, "text/html"))), query, true);
        Map<PerfectoDeviceParam, String> parameters = new HashMap<>();
        for (PerfectoDeviceParam param : params) {
            String parameter = "";
            if ((response != null) && (response.getStatusCode() == 200)) {
                parameter = response.xmlPath().getString(param.getParameter());
                Log.info("%s: '%s'", param.getComment(), parameter);
            } else {
                Log.warn("FAILED - unable to get %s", param.getParameter());
            }
            parameters.put(param, parameter);
        }
        return parameters;
    }

    public static String startExecution() {
        Log.info("Perfecto API: starting execution");
        Map<String, String> query = new HashMap<>();
        query.put("operation", "start");
        query.put("securityToken", PerfectoCommands.getPerfectoToken());
        query.put("responseFormat", "json");
        query.put("output.video", Boolean.FALSE.toString());
        query.put("output.report", Boolean.FALSE.toString());
        Response response = RestAssuredUtils.post(PerfectoLink.EXECUTION.getUrl(), query, false);
        String executionID = response.getBody().jsonPath().getString("executionId");
        Log.info("Execution ID: " + executionID);
        return executionID;
    }

    public static void stopExecution(String executionID) {
        Log.info("Perfecto API: stopping execution");
        Map<String, String> query = new HashMap<>();
        query.put("operation", "end");
        query.put("securityToken", PerfectoCommands.getPerfectoToken());
        query.put("responseFormat", "json");
        Response response = RestAssuredUtils.post(
                String.format(PerfectoLink.EXECUTIONS.getUrl(), executionID), query, false);
        String status = response.getBody().jsonPath().getString("status");
        if (status.equals("Success")) {
            Log.info("Execution stop status: " + status);
        } else {
            Log.error("Execution stop status: " + status);
        }
    }

    public static void deleteApplication(String deviceID, String executionID, String packageName) {
        Log.info("Perfecto API: deleting  application %s from device %s", packageName, deviceID);
        Map<String, String> query = new HashMap<>();
        query.put("operation", "command");
        query.put("securityToken", PerfectoCommands.getPerfectoToken());
        query.put("command", "application");
        query.put("subcommand", "uninstall");
        query.put("param.deviceId", deviceID);
        query.put("param.name", packageName);
        if (AbstractDriverManager.isAndroid()) {
            query.put("param.identifier", packageName);
        } else {
            query.put("param.identifier", packageName);
        }
        Response response = RestAssuredUtils.post(
                String.format(PerfectoLink.EXECUTIONS.getUrl(), executionID), query, false);
        String result = response.getBody().jsonPath().getString("flowEndCode");
        if (result.equals("FAILED")) {
            Log.error("Application removal status: " + result);
            Log.warn("Description: " + response.getBody().jsonPath().getString("description"));
        } else {
            Log.info("Application removal status: " + result);
        }
    }

    public static void openCloseDevice(String deviceID, String executionID, boolean isOpen) {
        Log.info("Perfecto API: opening close device %s", deviceID);
        Map<String, String> query = new HashMap<>();
        query.put("operation", "command");
        query.put("securityToken", PerfectoCommands.getPerfectoToken());
        query.put("command", "device");
        query.put("param.deviceId", deviceID);
        if (isOpen) {
            query.put("subcommand", "open");
            query.put("param.video", "norecord");
            query.put("param.audio", "noaudio");
        } else {
            query.put("subcommand", "close");
        }
        Response response = RestAssuredUtils.post(
                String.format(PerfectoLink.EXECUTIONS.getUrl(), executionID), query, false);
        String result = response.getBody().jsonPath().getString("flowEndCode");
        String timer = response.getBody().jsonPath().getString("'timer.elapsed'");
        if (result.equals("SUCCEEDED")) {
            Log.info("Open/close device status: " + result);
        } else {
            Log.error("Open/close device status: " + result);
        }
        Log.info("Timer: " + timer);
    }

    @Step("Step >> Perfecto API: Remove application")
    public static void clearApplication(String deviceID, String packageName) {
        if (Boolean.parseBoolean(ConfigProperties.getProperty(ProjectConfig.IS_CLEAR_APP))) {
            Log.info("Perfecto API: clearing application %s from device %s", packageName, deviceID);
            RestAssuredUtils.setupRestAssured();
            String executionID = startExecution();
            openCloseDevice(deviceID, executionID, true);
            deleteApplication(deviceID, executionID, packageName);
            openCloseDevice(deviceID, executionID, false);
            stopExecution(executionID);
        }
    }

    public static List<String> reserveDevice(String deviceId, int minutes) {
        Log.info("Perfecto API: reserve device %s for %s minutes", deviceId, minutes);
        long roundedEpochMillis = DateUtils.getCurrentEpochTime();
        Map<String, Object> query = new HashMap<>();
        query.put("admin", Boolean.TRUE.toString());
        JSONObject body = new JSONObject();
        body.put("resourceIds", new String[]{deviceId});
        body.put("startTime", roundedEpochMillis);
        body.put("endTime", roundedEpochMillis + (minutes * 60000L));
        body.put("reservedTo", user.getEmail());
        body.put("description", "Reservation for CCMS group");
        Response response = RestAssuredUtils.post(
                PerfectoLink.RESERVATION.getUrl(),
                getPerfectoHeaders(PerfectoCommands.getPerfectoToken()),
                body,
                query,
                false);
        List<String> reservations = response.getBody().jsonPath().getList("reservationIds");
        reservationID = reservations.get(0);
        Log.info("Device reservation:");
        reservations.forEach(Log::info);
        return reservations;
    }

    public static void removeReservation(String reservationId) {
        Log.info("Perfecto API: remove reservation %s", reservationId);
        Map<String, String> query = new HashMap<>();
        query.put("admin", Boolean.FALSE.toString());
        query.put("scope", "remaining");
        Response response = RestAssuredUtils.delete(
                String.format(PerfectoLink.RESERVATION_REMOVE.getUrl(), reservationId),
                getPerfectoHeaders(PerfectoCommands.getPerfectoToken()),
                query,
                false);
        String status = response.getBody().jsonPath().getString("status");
        if (status.equals("Success")) {
            Log.info("%s reservation removed", reservationId);
        }
    }

    //--------------- Private Methods ---------------

    private static Headers getPerfectoHeaders(String token) {
        return new Headers(Arrays.asList(
                new Header(HeaderParam.AUTHORIZATION_PERFECTO, token),
                new Header(HeaderParam.CONTENT_TYPE, HeaderParam.APP_JSON)));
    }

    private static void verifyExactDeviceAvailability(String selectedDevice) {
        Map<PerfectoDeviceParam, String> parameters = getDeviceParameters(selectedDevice,
                PerfectoDeviceParam.AVAILABILITY, PerfectoDeviceParam.RESERVED, PerfectoDeviceParam.RESERVED_TO);
        for (Map.Entry<PerfectoDeviceParam, String> parameter : parameters.entrySet()) {
            if (parameter.getKey().equals(PerfectoDeviceParam.AVAILABILITY)) {
                if (parameter.getValue().isEmpty() || parameter.getValue().contains(Boolean.FALSE.toString())) {
                    Log.exception("%s device is not available", selectedDevice);
                }
            }
            if (parameter.getKey().equals(PerfectoDeviceParam.RESERVED)) {
                if (parameter.getValue().isEmpty() || parameter.getValue().contains(Boolean.TRUE.toString())) {
                    String reservedTo = parameters.get(PerfectoDeviceParam.RESERVED_TO);
                    if (reservedTo.isEmpty() || !reservedTo.equals(user.getEmail())) {
                        Log.exception("%s device is reserved", selectedDevice);
                    }
                }
            }
        }
    }

    private static String getDevice(List<String> availableDevices, Response response) {
        for (String preferredDevice : PREFERRED_DEVICE_LIST) {
            if (availableDevices.contains(preferredDevice)) {
                Log.info("Preferred device %s is available", preferredDevice);
                return preferredDevice;
            }
        }
        Log.info("All preferred device are not available");
        availableDevices.removeAll(DEVICE_BLACK_LIST);
        if (availableDevices.isEmpty()) {
            Log.exception("No available device that is not on black list");
        }
        Random rand = new Random();
        return availableDevices.get(rand.nextInt(availableDevices.size()));

    }
}
