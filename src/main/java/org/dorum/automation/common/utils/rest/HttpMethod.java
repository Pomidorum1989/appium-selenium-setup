package org.dorum.automation.common.utils.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HttpMethod {

    DELETE  ("delete"),
    GET     ("get"),
    PATCH   ("patch"),
    POST    ("post"),
    PUT     ("put");

    private final String value;
}
