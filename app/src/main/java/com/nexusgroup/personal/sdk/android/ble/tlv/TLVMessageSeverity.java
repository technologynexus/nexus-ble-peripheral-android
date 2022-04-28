package com.nexusgroup.personal.sdk.android.ble.tlv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nexusgroup.personal.sdk.android.ble.BLEErrorSeverity;
import com.nexusgroup.personal.sdk.android.ble.tlv.util.TLVConvertUtil;

public enum TLVMessageSeverity {
    DEBUG(0x00, "DEBUG"),
    INFO(0x01, "INFO"),
    WARNING(0x02, "WARNING"),
    ERROR(0x03, "ERROR");

    private int value;
    private final String name;

    @Nullable
    public static TLVMessageSeverity fromInteger(@Nullable Integer integer) {
        if (integer == null) {
            return null;
        }
        for (TLVMessageSeverity code : values()) {
            if (code.getValue() == integer) {
                return code;
            }
        }
        return null;
    }

    TLVMessageSeverity(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public byte[] getBytes() {
        return TLVConvertUtil.intToBytes(getValue());
    }

    public BLEErrorSeverity convert() {
        switch (this) {
            case DEBUG:
                return BLEErrorSeverity.DEBUG;
            case INFO:
                return BLEErrorSeverity.INFO;
            case WARNING:
                return BLEErrorSeverity.WARNING;
            case ERROR:
                return BLEErrorSeverity.ERROR;
        }
        return BLEErrorSeverity.ERROR;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
