package com.nexusgroup.personal.sdk.android.ble;

import androidx.annotation.NonNull;

public enum BLEErrorCode {
    /*
    OK(0x00),
    NO_PROFILES_FOUND(0x01),
    NO_NAME_FOUND_FOR_PROFILE(0x02),
    UNABLE_TO_FIND_CERT(0x03),
    MISSING_PARAMETER(0x04),
    UNABLE_TO_HANDLE(0x05),
    SESSION_PIN_MISMATCH(0x06),
    BAD_CERT_HASH(0x07),
    BYTE_LENGTH_MISMATCH(0x08),
    UNSUPPORTED_ALGORITHM(0x09),
    SESSION_EXPIRED(0x0A),
    UNKNOWN_ERROR(0x0B),
    UNSUPPORTED_VERSION(0x0C),
    UNSUPPORTED_TAG(0x0D),
    END_USER_REJECTED(0x0E),
    VERSION_ERROR(0x0F),
    //TODO: fixme! need ta at least handle all cases from TLV object i think?
     */
    VERSION_ERROR("VERSION_ERROR"),
    DRIVER_ERROR("DRIVER_ERROR"),
    GENERAL_ERROR("GENERAL_ERROR"),
    ;

    private final String name;

    BLEErrorCode(String name) {
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
