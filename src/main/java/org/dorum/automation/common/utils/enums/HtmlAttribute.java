package org.dorum.automation.common.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HtmlAttribute {

    CHECKED         ("checked"),
    INNER_HTML      ("innerHTML"),
    OUTER_HTML      ("outerHTML"),
    STYLE           ("style"),
    TEXT_CONTENT    ("textContent"),
    TITLE           ("title"),
    VALUE           ("value");

    private final String content;
}
