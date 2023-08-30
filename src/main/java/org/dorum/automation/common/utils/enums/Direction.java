package org.dorum.automation.common.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Direction {

    UP      ("UP",    "40%,20%",  "40%,80%"),
    DOWN    ("DOWN",  "40%,80%",  "40%,20%"),
    LEFT    ("LEFT",  "15%,60%",  "20%,40%"),
    RIGHT   ("RIGHT", "20%,40%",  "15%,60%");

    private final String direction, start, end;
}
