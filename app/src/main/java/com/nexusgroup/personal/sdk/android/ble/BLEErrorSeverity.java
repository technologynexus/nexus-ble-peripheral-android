package com.nexusgroup.personal.sdk.android.ble;

import androidx.annotation.NonNull;

public enum BLEErrorSeverity {
    DEBUG("DEBUG"),
    INFO("INFO"),
    WARNING("WARNING"),
    ERROR("ERROR");

    private final String name;

    BLEErrorSeverity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
