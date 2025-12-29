package ru.binarysimple.auth.model;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum Roles {
    USER("user"),
    MANAGER("manager"),
    ADMIN("admin"),
    SUPERADMIN("superadmin");

    private final String role;

    Roles(String role) {
        this.role = role;
    }
}
