package com.nexusgroup.personal.sdk.android.ble.tlv.util;

public class TlvParserException extends Exception {

    private static final long serialVersionUID = -4769459741687997835L;

    public TlvParserException(String message) {
        super(message);
    }

    public TlvParserException(Exception e) {
        super(e);
    }
}