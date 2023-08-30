package org.dorum.automation.common.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Color {

    GREY        ("Grey"),
    ORANGE      ("Orange"),
    RED         ("Red"),
    TURQUOISE   ("turquoise"),
    YELLOW      ("Yellow");

    private final String value;
}
