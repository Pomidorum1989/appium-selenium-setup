package org.dorum.automation.common.utils.perfecto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PerfectoFailureReasons {

    APPLICATION_CRASHED         ("ApplicationCrashed-1544380704"),
    ASSERTION_FAILURE           ("zWZamQJYqt"),
    CLASS_NOT_FOUND             ("ClassNotFound-1640249805"),
    ELEMENT_NOT_INTERACTABLE    ("ElementNotInteractable-1640249805"),
    ILLEGAL_ARGUMENT            ("IllegalArgument-1640249805"),
    INDEX_OUT_OF_BOUNDS         ("IndexOutOfBounds-1640249805"),
    INVALID_EXPRESSION          ("InvalidExpression-1586069055"),
    INVALID_SESSION             ("InvalidSession-1640249805"),
    INVALID_XPATH_SYNTAX        ("InvalidXPathSyntax-1542806868"),
    JSON_ERROR                  ("JSONError-1640249805"),
    MULTIPLE_ELEMENTS_FOUND     ("MultipleElementsFound-1542806869"),
    NAVIGATED_PAGE_NOT_LOADED   ("OTiy5yqlzt"),
    PERFECTO_FAILURE_REASONS    ("ElementNotFound-1542806867"),
    POPUP_HANDLING              ("PopupHandling-1542806870"),
    PRIOR_TO_LOGIN              ("q5vxH1gX3A"),
    SESSION_INACTIVITY          ("SessionInactivity-1631442364000"),
    UNABLE_TO_CONNECT           ("lq0aDnqZkf"),
    UNABLE_TO_LOGOUT            ("NMBFgTsJdJ"),
    UNABLE_TO_SETUP_APP         ("M9gKmX1DT7"),
    UNSUPPORTED_COMMAND         ("UnsupportedCommand-1640249805");

    private final String failure;
}
