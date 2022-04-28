package com.nexusgroup.personal.sdk.android.ble.tlv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nexusgroup.personal.sdk.android.ble.BLEErrorCode;
import com.nexusgroup.personal.sdk.android.ble.tlv.util.TLVConvertUtil;

public enum TLVResultCode {
    OK(0x00, "OK"),
    NO_PROFILES_FOUND(0x01, "NO_PROFILES_FOUND"),
    NO_NAME_FOUND_FOR_PROFILE(0x02, "NO_NAME_FOUND_FOR_PROFILE"),
    UNABLE_TO_FIND_CERT(0x03, "UNABLE_TO_FIND_CERT"),
    MISSING_PARAMETER(0x04, "MISSING_PARAMETER"),
    UNABLE_TO_HANDLE(0x05, "UNABLE_TO_HANDLE"),
    SESSION_PIN_MISMATCH(0x06, "SESSION_PIN_MISMATCH"),
    BAD_CERT_HASH(0x07, "BAD_CERT_HASH"),
    BYTE_LENGTH_MISMATCH(0x08, "BYTE_LENGTH_MISMATCH"),
    UNSUPPORTED_ALGORITHM(0x09, "UNSUPPORTED_ALGORITHM"),
    SESSION_EXPIRED(0x0A, "SESSION_EXPIRED"),
    UNKNOWN_ERROR(0x0B, "UNKNOWN_ERROR"),
    UNSUPPORTED_VERSION(0x0C, "UNSUPPORTED_VERSION"),
    UNSUPPORTED_TAG(0x0D, "UNSUPPORTED_TAG"),
    END_USER_REJECTED(0x0E, "END_USER_REJECTED"),
    DRIVER_ERROR(0x0F, "DRIVER_ERROR"),
    GENERAL_ERROR(0x10, "GENERAL_ERROR"),
    MINIDRIVER_ERROR(0x11, "MINIDRIVER_ERROR"),
    REQUEST_NEW_PIN(0x12, "REQUEST_NEW_PIN"),
    UNKNOWN_PROFILE_ID(0x13, "UNKNOWN_PROFILE_ID"),
    PROCESS_NAME_MISSING(0x14, "PROCESS_NAME_MISSING"),
    PROCESS_ID_MISSING(0x15, "PROCESS_ID_MISSING"),
    BLE_TURNED_OFF(0x16, "BLE_TURNED_OFF");

    private int value;
    private final String name;

    @Nullable
    public static TLVResultCode fromInteger(@Nullable Integer integer) {
        if (integer == null) {
            return null;
        }
        for (TLVResultCode code : values()) {
            if (code.getValue() == integer) {
                return code;
            }
        }
        return null;
    }

    TLVResultCode(int value, String name) {
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

    public BLEErrorCode convert() {
        switch (this) {
            case OK:
                //TODO: handle?
                break;
            case GENERAL_ERROR:
            case NO_PROFILES_FOUND:
            case NO_NAME_FOUND_FOR_PROFILE:
            case UNABLE_TO_FIND_CERT:
            case MISSING_PARAMETER:
            case UNABLE_TO_HANDLE:
            case SESSION_PIN_MISMATCH:
            case BAD_CERT_HASH:
            case BYTE_LENGTH_MISMATCH:
            case UNSUPPORTED_ALGORITHM:
            case SESSION_EXPIRED:
            case UNKNOWN_ERROR:
            case UNSUPPORTED_TAG:
            case END_USER_REJECTED:
                return BLEErrorCode.GENERAL_ERROR;
            case UNSUPPORTED_VERSION:
                return BLEErrorCode.VERSION_ERROR;
            case DRIVER_ERROR:
                return BLEErrorCode.DRIVER_ERROR;
        }
        //TODO: fixme!
        return BLEErrorCode.GENERAL_ERROR;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
