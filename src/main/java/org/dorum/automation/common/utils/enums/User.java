package org.dorum.automation.common.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum User {

    DORUM("1234",   "valentinedorum");

    private final String id, email;

    public String getEmail() {
        return this.email + "@gmail.com";
    }

    public static User getByID(String gmId) {
        return Arrays.stream(User.values()).filter(user -> user.getId().equals(gmId)).findFirst().orElse(DORUM);
    }
}
