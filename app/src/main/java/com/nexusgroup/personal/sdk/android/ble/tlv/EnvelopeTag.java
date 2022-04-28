package com.nexusgroup.personal.sdk.android.ble.tlv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nexusgroup.personal.sdk.android.ble.tlv.util.TLVConvertUtil;

/*
envelope_tags: (class 'content specific', type 'constructed', tag = 0b101xxxxx)
	mainchar:
		raw				hex		tag	name						request_tags	response_tags
		(0b10100001)	0xA1	1	profile_ids					[7]				[2 * n, 7, 16]
		(0b10100010)	0xA2	2	profile_certs				[2, 7]			[2, 4 * n, 7, 16]
		(0b10100011)	0xA3	3	profile_info				[2, 7]			[2, 3, 7, 16]
		(0b10100100)	0xA4	4	cert						[4, 7]			[5, 7, 16]
		(0b10100101)	0xA5	5	generate_session_pin		[2, 7, 8]		[2, 6, 7, 16]
		(0b10100110)	0xA6	6	authenticate_session_pin	[2, 6, 7]		[2, 7, 16]
		(0b10100111)	0xA7	7	sign_data					[4, 7, 9, 10]	[2?, 7, 11, 16]
		(0b10101000)	0xA8	8	decrypt_data				[4, 7, 12, 13]	[2?, 7, 14, 16]
		(0b10101001)	0xA9	9	error_from_peripheral		[7, 15, 16, 17]	[7, 16]

	pingchar:
		(0b10110001)	0xB1	17	version						[1, 7, 17]		[1, 7, 16, 17]
		(0b10110010)	0xB2	18	ping						[7]				[7, 16]
		(0b10110011)	0xB3	19	refresh_profile_ids			[7]				[7, 16]
		(0b10110011)	0xB4	20	rssi_low					[7]				[7, 16]
		(0b10110100)	0xB5	21	rssi_high					[7]				[7, 16]
		(0b10110101)	0xB6	22	error_from_central			[7, 15, 16, 17]	[7, 16]

		TODO make this prettier
	RESERVERED FOR INTERNAL SERVICE COMMUNICATION, DO NOT USE!!
	                    0xB7
	                    0xB8
	                    0xB9
 */
public enum EnvelopeTag {
    //Main characteristic
    PROFILE_IDS(0xA1, "PROFILE_IDS"),
    PROFILE_CERTS(0xA2, "PROFILE_CERTS"),
    PROFILE_INFO(0xA3, "PROFILE_INFO"),
    CERTIFICATE(0xA4, "CERTIFICATE"),
    GENERATE_SESSION_PIN(0xA5, "GENERATE_SESSION_PIN"),
    // AUTHENTICATE (0xA6) is only in service now
    SIGN_DATA(0xA7, "SIGN_DATA"),
    DECRYPT_DATA(0xA8, "DECRYPT_DATA"),
    ERROR_FROM_PERIPHERAL(0xA9, "ERROR_FROM_PERIPHERAL"),

    //Ping characteristic
    VERSION(0xB1, "VERSION"),
    PING(0xB2, "PING"),
    REFRESH_PROFILE_IDS(0xB3, "REFRESH_PROFILE_IDS"),
    RSSI_LOW(0xB4, "RSSI_LOW"),
    RSSI_HIGH(0xB5, "RSSI_HIGH"),
    ERROR_FROM_CENTRAL(0xB6, "ERROR_FROM_CENTRAL"),
    ;

    private final int value;
    private final String name;


    @Nullable
    public static EnvelopeTag fromInteger(@Nullable Integer integer) {
        if (integer == null) {
            return null;
        }
        for (EnvelopeTag code : values()) {
            if (code.getValue() == integer) {
                return code;
            }
        }
        return null;
    }

    EnvelopeTag(int value, String name) {
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
