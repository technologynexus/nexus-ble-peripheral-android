package com.nexusgroup.personal.sdk.android.ble.tlv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nexusgroup.personal.sdk.android.ble.tlv.util.TLVConvertUtil;

/*
record_tags: (class 'private', type 'primitive', tag = 0b110xxxxx)
	(0b11000001)	0xC1	1	version_code	3 bytes typical, major.minor.patch, 0xFF reserved, means two more bytes for that position (ex 0x02FF02040B -> 2.516.11)
	(0b11000010)	0xC2	2	profile_id		16 bytes UUID
	(0b11000011)	0xC3	3	profile_name	n > 0 bytes, UTF8 string
	(0b11000100)	0xC4	4	cert_hash		32 bytes (sha256)
	(0b11000101)	0xC5	5	cert_bytes		n > 0 bytes, DER formatted x509
	(0b11000110)	0xC6	6	session_pin		n > 10 bytes, ALPHANUMERIC string
	(0b11000111)	0xC7	7	request_id		n > 0 bytes, typically increasing integer, return in response if present
	(0b11001000)	0xC8	8	expires_time	4? bytes integer, lifetime in ms, big endian (high order bits first)
	(0b11001001)	0xC9	9	sign_algorithm	1? byte integer, need to define values in separate enum (sign_algo)
	(0b11001010)	0xCA	10	sign_tbs		n > 0 bytes, as specified by algorithm, always accompanied by algorithm
	(0b11001011)	0xCB	11	signed_bytes	n > 0 bytes, result of sign operation
	(0b11001100)	0xCC	12	decr_algorithm	1? byte integer, need to define values in separate enum (decr_algo)
	(0b11001101)	0xCD	13	decr_data		n > 0 bytes, as specified by algorithm, always accompanied by algorithm
	(0b11001110)	0xCE	14	decrypted_bytes	n > 0 bytes, result of decrypt operation
	(0b11001111)	0xCF	15	msg_severity	1? byte integer, need to be defined in separate enum (msg_severity)
	(0b11010000)	0xD0	16	result_code		n > 0 bytes integer, need to be defined in separate enum (result_code)
	(0b11010001)	0xD1	17	message			n > 0 bytes, UTF8 string
    (0b11010100)	0xD4	20	process_name	n > 0 bytes, UTF8 string
 */
public enum RecordTag {
    VERSION_CODE(0xC1, "VERSION_CODE"),
    PROFILE_ID(0xC2, "PROFILE_ID"),
    PROFILE_NAME(0xC3, "PROFILE_NAME"),
    CERT_HASH(0xC4, "CERT_HASH"),
    CERT_BYTES(0xC5, "CERT_BYTES"),
    SESSION_PIN(0xC6, "SESSION_PIN"),
    REQUEST_ID(0xC7, "REQUEST_ID"),
    EXPIRES_TIME(0xC8, "EXPIRES_TIME"),

    SIGN_TBS(0xCA, "SIGN_TBS"),
    SIGNED_BYTES(0xCB, "SIGNED_BYTES"),

    DECRYPT_DATA(0xCD, "DECRYPT_DATA"),
    DECRYPTED_BYTES(0xCE, "DECRYPTED_BYTES"),
    MESSAGE_SEVERITY(0xCF, "MESSAGE_SEVERITY"),
    RESULT_CODE(0xD0, "RESULT_CODE"),
    MESSAGE(0xD1, "MESSAGE"),


    PROCESS_NAME(0xD4, "PROCESS_NAME"),

    PROCESS_ID(0xD6, "PROCESS_ID"),
    FLAGS(0xD7, "FLAGS");

    private int value;
    private final String name;

    @Nullable
    public static RecordTag fromInteger(@Nullable Integer integer) {
        if (integer == null) {
            return null;
        }
        for (RecordTag code : values()) {
            if (code.getValue() == integer) {
                return code;
            }
        }
        return null;
    }

    RecordTag(int value, String name) {
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
