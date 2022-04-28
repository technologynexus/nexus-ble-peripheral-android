package com.nexusgroup.personal.sdk.android.ble.tlv.util;

import java.util.List;

public class TLVParserBytesMissingException extends Exception {
    private List<TLVRecord> tlvEntries;

    public TLVParserBytesMissingException(String message, List<TLVRecord> tlvEntries) {
        super(message);
        this.tlvEntries = tlvEntries;
    }

    public List<TLVRecord> getTlvEntries() {
        return tlvEntries;
    }
}
