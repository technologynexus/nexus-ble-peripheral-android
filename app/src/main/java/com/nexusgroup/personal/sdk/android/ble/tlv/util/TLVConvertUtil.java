package com.nexusgroup.personal.sdk.android.ble.tlv.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class with conversion methods
 */
public class TLVConvertUtil {
    private TLVConvertUtil() {
    }

    /**
     * Convert a byte array to int value. Length should be less than 8 (for long)
     *
     * @param bytes the byte array to process
     * @return int value representing the byte array
     * @throws Exception
     */
    public static int intFromBytes(byte[] bytes) throws Exception {
        if (bytes == null || bytes.length > 4) {
            throw new Exception("bytes are too many to fit into an int");
        }

        int result = 0;
        for (byte aByte : bytes) {
            result = (result << 8) | aByte & 0xff;
        }

        return result;
    }

    /**
     * Convert a int to byte array value.
     *
     * @param integer the integer to process
     * @return byte array representing the integer
     */
    public static byte[] intToBytes(int integer) {
        int value = integer;
        List<Byte> bytes = new ArrayList<>();
        do {
            bytes.add(0, (byte) (value & 0xff));
            value = value >> 8;
        } while (value > 0);

        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }
        return result;
    }
}