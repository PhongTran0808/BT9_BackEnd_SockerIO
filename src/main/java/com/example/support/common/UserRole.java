package com.example.support.common;

public enum UserRole {
    CUSTOMER("CUSTOMER"),
    MANAGER("MANAGER");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}