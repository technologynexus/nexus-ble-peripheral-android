package com.nexusgroup.personal.sdk.android.ble;

import android.util.Log;
import com.nexusgroup.personal.sdk.android.ble.tlv.util.TLVParserBytesMissingException;
import com.nexusgroup.personal.sdk.android.ble.tlv.util.TLVRecord;
import com.nexusgroup.personal.sdk.android.ble.tlv.util.TlvParser;
import com.nexusgroup.personal.sdk.android.ble.tlv.util.TlvParserException;

import org.apache.commons.lang3.ArrayUtils;
import java.util.List;

public class BLEDeviceSession {
    private final static String TAG = "ble-device-session";
    private byte[] unparsedBytes = null;
    private final TlvParser tlvParser = new TlvParser();

    public void dataReceived(byte[] incoming) {
        byte[] data = unparsedBytes != null ? ArrayUtils.addAll(unparsedBytes, incoming) : incoming;
        unparsedBytes = null;

        try {
            List<TLVRecord> tlvRecords;
            try {
                tlvRecords = tlvParser.parseAndGetRootTLVRecords(data, true);
            } catch (TLVParserBytesMissingException e) {
                tlvRecords = e.getTlvEntries();

                int unused = data.length;
                for (TLVRecord record : tlvRecords) {
                    unused -= record.toByteArray().length;
                }

                if (unused <= 0) {
                    Log.println(Log.ERROR, TAG, "unused bytes are 0, should never happen if we got this exception");
                } else {
                    int offset = data.length - unused;
                    byte[] buffer = new byte[unused];
                    try {
                        System.arraycopy(data, offset, buffer, 0, unused);
                        unparsedBytes = buffer;
                        Log.println(Log.ERROR, TAG, "stored incomplete chunk of data of length: " + buffer.length);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        Log.println(Log.ERROR, TAG, "unable to copy unused bytes: " + ex.getMessage());
                    }
                }

                if (tlvRecords.size() <= 0) {
                    Log.println(Log.INFO, TAG, "got incomplete TLV, wait for the rest");
                    return;
                } else {
                    Log.println(Log.DEBUG, TAG, "got partially complete TLV, handle the working part");
                }
            }

            int recordListSize = tlvRecords.size();
            if (recordListSize == 0) {
                // cannot happen since parser throws this as error, but still...
                String message = "did not find any TLVs inside data: " + SDKHex.encode(data);
                Log.println(Log.ERROR, TAG, message);
            } else if (recordListSize > 1) {
                Log.println(Log.DEBUG, TAG, "unexpectedly found: [" + recordListSize + "] TLVRecords, expected: 1");
            }

            for (TLVRecord record : tlvRecords) {
                Log.println(Log.INFO, TAG, "onCharacteristicUpdate() (" + incoming.length + ") + (" + record.getEnvelopeTag() + ")");
            }
        } catch (TlvParserException e) {
            String message = "unable to parse received TLV: " + SDKHex.encode(data);
            Log.println(Log.ERROR, TAG, message);
        }
    }
}