package org.dorum.automation.common.utils.rest;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Headers;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.ProxySpecification;
import org.dorum.automation.common.utils.ConfigProperties;
import org.dorum.automation.common.utils.Log;
import org.dorum.automation.common.utils.enums.ProjectConfig;
import org.json.JSONObject;

import java.util.Map;

public class RestAssuredUtils {

    private static final String CONNECTION_TIMEOUT = "http.connection.timeout";
    private static final int CONNECTION_TIMEOUT_VALUE = 60000;
    private static final RestAssuredConfig CONFIG = RestAssuredConfig.config()
            .httpClient(HttpClientConfig.httpClientConfig().setParam(CONNECTION_TIMEOUT, CONNECTION_TIMEOUT_VALUE));
    private static final ProxySpecification PROXY_NA = ProxySpecification
            .host(ConfigProperties.getProperty(ProjectConfig.PROXY_HOST_NA))
            .withPort(Integer.parseInt(ConfigProperties.getProperty(ProjectConfig.PROXY_PORT_NA)))
            .withAuth(
                    ConfigProperties.getProperty(ProjectConfig.PROXY_USER_NA),
                    ConfigProperties.getProperty(ProjectConfig.PROXY_PASS_NA));
    private static final ProxySpecification PROXY_DC = ProxySpecification
            .host(ConfigProperties.getProperty(ProjectConfig.PROXY_HOST_DC))
            .withPort(Integer.parseInt(ConfigProperties.getProperty(ProjectConfig.PROXY_PORT_DC)));
    private static ProxySpecification proxySpecification;
    private static final String PROXY_HOST = "proxyHost", PROXY_PORT = "proxyPort", HTTPS_SCHEMA = "https.", HTTP_SCHEMA = "http.";

    public static void setupRestAssured() {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.config = RestAssured.config().logConfig(Log.LOG_CONFIG);
        proxySpecification = PROXY_DC;
    }

    public static void setGlobalProxy() {
        Log.info("Setting global proxy");
        System.getProperties().put(HTTPS_SCHEMA + PROXY_HOST, ConfigProperties.getProperty(ProjectConfig.PROXY_HOST_DC));
        System.getProperties().put(HTTPS_SCHEMA + PROXY_PORT, ConfigProperties.getProperty(ProjectConfig.PROXY_PORT_DC));
    }

    public static void clearGlobalProxy() {
        Log.info("Cleaning global proxy");
        System.clearProperty(HTTP_SCHEMA + PROXY_HOST);
        System.clearProperty(HTTP_SCHEMA + PROXY_PORT);
        System.clearProperty(HTTPS_SCHEMA + PROXY_HOST);
        System.clearProperty(HTTPS_SCHEMA + PROXY_PORT);
    }

    public static Response get(String url, Headers headers) {
        return get(url, headers, false);
    }

    public static Response get(String url, Headers headers, boolean isProxy) { // Perfecto API
        reportUrl(url, HttpMethod.GET);
        if (isProxy) {
            RestAssured.proxy = proxySpecification;
        }
        reportProxyData();
        Response response = RestAssured.given()
                .filter(new CustomLogging())
                .config(CONFIG)
                .headers(headers)
                .and()
                .get(url)
                .then().log().ifError()
                .extract().response();
        reportResponse(response);
        RestAssured.reset();
        return response;
    }

    public static Response get(String url, Headers headers, Map<String, ?> query) {
        return get(url, headers, query, false);
    }

    public static Response get(String url, Headers headers, Map<String, ?> query, boolean isProxy) { // Not in external usage yet
        reportUrl(url, HttpMethod.GET);
        if (isProxy) {
            RestAssured.proxy = proxySpecification;
        }
        reportProxyData();
        Response response = RestAssured.given()
                .filter(new CustomLogging())
                .config(CONFIG)
                .queryParams(query)
                .headers(headers)
                .get(url)
                .then().log().ifError()
                .extract().response();
        reportResponse(response);
        RestAssured.reset();
        return response;
    }

    public static Response put(String url, Headers headers, JSONObject body) {
        return put(url, headers, body, false);
    }

    public static Response put(String url, Headers headers, JSONObject body, boolean isProxy) { // Not in external usage yet
        reportUrl(url, HttpMethod.PUT, body);
        if (isProxy) {
            RestAssured.proxy = proxySpecification;
        }
        reportProxyData();
        Response response = RestAssured.given()
                .filter(new CustomLogging())
                .config(CONFIG)
                .headers(headers)
                .and()
                .body(body.toString())
                .put(url)
                .then().log().ifError()
                .extract().response();
        reportResponse(response);
        RestAssured.reset();
        return response;
    }

    public static Response put(String url, Headers headers) {
        return put(url, headers, false);
    }

    public static Response put(String url, Headers headers, boolean isProxy) { // Not in external usage yet
        reportUrl(url, HttpMethod.PUT);
        if (isProxy) {
            RestAssured.proxy = proxySpecification;
        }
        reportProxyData();
        Response response = RestAssured.given()
                .filter(new CustomLogging())
                .config(CONFIG)
                .headers(headers)
                .and()
                .put(url)
                .then().log().ifError()
                .extract().response();
        reportResponse(response);
        RestAssured.reset();
        return response;
    }

    public static Response post(String url, Headers headers, JSONObject body) {
        return post(url, headers, body, false);
    }

    public static Response post(String url, Headers headers, JSONObject body, boolean isProxy) { // Not in external usage yet
        reportUrl(url, HttpMethod.POST, body);
        if (isProxy) {
            RestAssured.proxy = proxySpecification;
        }
        reportProxyData();
        Response response = RestAssured.given()
                .filter(new CustomLogging())
                .config(CONFIG)
                .headers(headers)
                .and()
                .body(body.toString())
                .post(url)
                .then().log().ifError()
                .extract().response();
        reportResponse(response);
        RestAssured.reset();
        return response;
    }

    public static Response post(String url, Headers headers, JSONObject body, Map<String, Object> query, boolean isProxy) {
        reportUrl(url, HttpMethod.POST, body);
        if (isProxy) {
            RestAssured.proxy = proxySpecification;
        }
        reportProxyData();
        Response response = RestAssured.given()
                .filter(new CustomLogging())
                .config(CONFIG)
                .headers(headers)
                .queryParams(query)
                .body(body.toString())
                .post(url)
                .then().log().ifError()
                .extract().response();
        reportResponse(response);
        RestAssured.reset();
        return response;
    }

    public static Response post(String url, Map<String, String> query, boolean isProxy) {
        reportUrl(url, HttpMethod.POST, null);
        if (isProxy) {
            RestAssured.proxy = proxySpecification;
        }
        reportProxyData();
        Response response = RestAssured.given()
                .filter(new CustomLogging())
                .config(CONFIG)
                .queryParams(query)
                .post(url)
                .then().log().ifError()
                .extract().response();
        reportResponse(response);
        RestAssured.reset();
        return response;
    }

    public static Response patch(String url, Headers headers, JSONObject body) {
        return patch(url, headers, body, false);
    }

    public static Response patch(String url, Headers headers, JSONObject body, boolean isProxy) { // Not in external usage yet
        reportUrl(url, HttpMethod.PATCH, body);
        if (isProxy) {
            RestAssured.proxy = proxySpecification;
        }
        reportProxyData();
        Response response = RestAssured.given()
                .filter(new CustomLogging())
                .config(CONFIG)
                .headers(headers)
                .and()
                .body(body.toString())
                .patch(url)
                .then().log().ifError()
                .extract().response();
        reportResponse(response);
        RestAssured.reset();
        return response;
    }

    public static Response delete(String url, Headers headers, Map<String, String> query, boolean isProxy) {
        reportUrl(url, HttpMethod.DELETE, null);
        if (isProxy) {
            RestAssured.proxy = proxySpecification;
        }
        reportProxyData();
        Response response = RestAssured.given()
                .filter(new CustomLogging())
                .config(CONFIG)
                .headers(headers)
                .queryParams(query)
                .delete(url)
                .then().log().ifError()
                .extract().response();
        reportResponse(response);
        RestAssured.reset();
        return response;
    }


    public static Response call(HttpMethod httpMethod, String url, JSONObject body, Headers headers) {
        Response response;
        clearGlobalProxy();
        switch (httpMethod) {
            case POST:
                response = post(url, headers, body);
                break;
            case PATCH:
                response = patch(url, headers, body);
                break;
            case PUT:
                response = put(url, headers, body);
                break;
            default:
                response = null;
        }
        setGlobalProxy();
        if (response == null) {
            Log.exception("FAILED - invalid Response: NULL");
        }
        return response;
    }

    //--------------- Private Methods ---------------

    private static void reportUrl(String url, HttpMethod httpMethod) {
        reportUrl(url, httpMethod, null);
    }

    private static void reportUrl(String url, HttpMethod httpMethod, JSONObject body) {
        if (url.contains("securityToken=")) {
            Log.info("Execute REST call (%s), URL: %s", httpMethod,
                     url.replaceAll("(?<=securityToken=).*", "*****"));
        }
        if (body != null) {
            Log.info("Execute REST call (%s), body: %s", httpMethod, body.toString());
        }
    }

    private static void reportResponse(Response response) {
        if (response == null) {
            Log.warn("Invalid Response - NULL");
        } else {
            int responseStatus = response.getStatusCode();
            String status = String.format("Response Status: %s (%s)", responseStatus, response.getStatusLine().trim());
            if (responseStatus == 200) {
                Log.info(status);
            } else {
                Log.warn(status);
            }
        }
    }

    private static void reportProxyData() {
        if (RestAssured.proxy != null) {
            Log.info("RestAssured proxy details - host: %s | port: %s | username: %s | password: %s",
                     RestAssured.proxy.getHost(),
                     RestAssured.proxy.getPort(),
                     RestAssured.proxy.getUsername(),
                     (RestAssured.proxy.getPassword() != null) ? "*****" : "null"); // Password is hidden
        }
    }
}
