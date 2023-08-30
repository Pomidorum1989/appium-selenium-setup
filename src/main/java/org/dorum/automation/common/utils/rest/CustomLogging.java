package org.dorum.automation.common.utils.rest;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.dorum.automation.common.utils.Log;

public class CustomLogging implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext filterContext) {
        Log.info("++++++++++++++++++++++++++++++ REQUEST - START +++++++++++++++++++++++++++++++++");
        Log.info("Request URI: %s", requestSpec.getURI());
        Log.info("Request Method: %s", requestSpec.getMethod());
        requestSpec.getHeaders().forEach(header -> {
            if (header.getName().equals("Perfecto-Authorization") ||
                    header.getName().equals("Authorization")) {
                Log.info("Header: %s=hidden", header.getName());
            } else {
                Log.info("Header: %s=%s", header.getName(), header.getValue());
            }
        });
        if (requestSpec.getCookies().size() > 0) {
            Log.info("Request cookies: %s", requestSpec.getCookies());
        }
        if (requestSpec.getMultiPartParams().size() > 0) {
            Log.info("Request multi parts: %s", requestSpec.getMultiPartParams());
        }
        if (requestSpec.getProxySpecification() != null) {
            Log.info("Request proxy: %s", requestSpec.getProxySpecification());
        }
        if (requestSpec.getRequestParams().size() > 0) {
            Log.info("Request params: %s", requestSpec.getRequestParams());
        }
        if (requestSpec.getQueryParams().size() > 0) {
            Log.info("Request query params: %s", requestSpec.getQueryParams());
        }
        if (requestSpec.getFormParams().size() > 0) {
            Log.info("Request form params: %s", requestSpec.getFormParams());
        }
//        if (requestSpec.getBasePath() != null) {
//            Log.info("Request Base Path: %s", requestSpec.getBasePath());
//        }
//        if (requestSpec.getBaseUri() != null) {
//            Log.info("Request Base URI: %s", requestSpec.getBaseUri());
//        }
        if (requestSpec.getPathParams().size() > 0) {
            Log.info("Request path params: %s", requestSpec.getPathParams());
        }
        if (requestSpec.getContentType() != null) {
            Log.info("Request content type: %s", requestSpec.getContentType());
        }
        if (requestSpec.getBody() != null && requestSpec.getBody().toString().length() <= 1000) {
            Log.info("Request body: " + requestSpec.getBody()); // Body is <T> - no simple convert to String
        } else {
            Log.info("Request body: too big for printing");
            Log.debug(requestSpec.getBody());
        }
        Log.info("++++++++++++++++++++++++++++++ REQUEST - END +++++++++++++++++++++++++++++++++++");
        Response response = filterContext.next(requestSpec, responseSpec);
        Log.info("++++++++++++++++++++++++++++++ RESPONSE - START ++++++++++++++++++++++++++++++++");
        Log.info("Status-Code=%s", response.statusCode());
        Headers header = response.headers();
        if (header != null) {
            header.forEach(head -> Log.info(head.getName() + "=" + head.getValue()));
        }
        try {
            if ((response.getBody() != null) && (requestSpec.getBody().toString().length() <= 1000)) {
                Log.info("Response body: %s", response.getBody().prettyPrint());
            } else {
                Log.info("Response body: too big for printing");
                Log.debug(requestSpec.getBody());
            }
        } catch (Exception ignored) {
        }
        Log.info("++++++++++++++++++++++++++++++ RESPONSE - END ++++++++++++++++++++++++++++++++++");
        return response;
    }
}

