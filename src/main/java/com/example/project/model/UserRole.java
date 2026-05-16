package com.example.project.model;

public enum UserRole {
    ADMIN("Admin"),
    CUSTOMER_SUPPORT("Customer Support"),
    CUSTOMER("Customer");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
