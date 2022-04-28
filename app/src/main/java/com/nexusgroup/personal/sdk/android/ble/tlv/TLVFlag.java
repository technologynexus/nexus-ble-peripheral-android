package com.nexusgroup.personal.sdk.android.ble.tlv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nexusgroup.personal.sdk.android.ble.tlv.util.TLVConvertUtil;

import java.util.ArrayList;
import java.util.List;

public enum TLVFlag {
    NONE(0x00, "NONE"),
    IS_WINLOGON(0x01, "IS_WINLOGON");

    private int value;
    private final String name;

    public static List<TLVFlag> fromInteger(@Nullable Integer integer) {
        List<TLVFlag> result = new ArrayList<>();
        if (integer == null) {
            result.add(NONE);
            return result;
        }

        for (TLVFlag code : values()) {
            if ((code.getValue() & integer) != 0) {
                result.add(code);
            }
        }

        if (TLVFlag.NONE.getValue() == integer || result.size() <= 0) {
            result.add(NONE);
            return result;
        } else {
            return result;
        }
    }

    TLVFlag(int value, String name) {
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

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
