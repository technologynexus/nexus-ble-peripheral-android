package com.nexusgroup.personal.sdk.android.ble;

import android.content.Context;
import android.util.Log;

import com.nexusgroup.personal.sdk.android.ble.tlv.EnvelopeTag;
import com.nexusgroup.personal.sdk.android.ble.tlv.RecordTag;
import com.nexusgroup.personal.sdk.android.ble.tlv.TLVResultCode;
import com.nexusgroup.personal.sdk.android.ble.tlv.util.TLVParserBytesMissingException;
import com.nexusgroup.personal.sdk.android.ble.tlv.util.TLVRecord;
import com.nexusgroup.personal.sdk.android.ble.tlv.util.TlvParser;
import com.nexusgroup.personal.sdk.android.ble.tlv.util.TlvParserException;

import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class BLEDeviceSession {
    private final static String TAG = "ble-device-session";
    private byte[] unparsedBytes = null;
    private final TlvParser tlvParser = new TlvParser();
    private Crypto crypto;

    // A better solution is to generate random one for each GENERATE_SESSION_PIN request
    private byte[] sessionPin = new byte[]{1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8};

    public BLEDeviceSession(Context ctx) {
        crypto = new Crypto(ctx);
    }

    private TLVRecord handleTLV(TLVRecord envelopeRecord) throws Exception {
        Log.println(Log.INFO, TAG, "received envelope tag " + envelopeRecord.getEnvelopeTag().getName());
        EnvelopeTag envelopeTag = envelopeRecord.getEnvelopeTag();
        TLVRecord response = new TLVRecord(envelopeTag);
        if (envelopeTag == EnvelopeTag.PROFILE_IDS) {
            // First step, send back profile ID
            // This sample app only has one profile with hardcoded ID
            // ID must be 16 bytes
            TLVRecord dataRecord = new TLVRecord(RecordTag.PROFILE_ID, new byte[]{1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8});
            response.addSubRecord(dataRecord);
        } else if (envelopeTag == EnvelopeTag.PROFILE_INFO) {
            TLVRecord dataRecord = new TLVRecord(RecordTag.PROFILE_NAME, "nexus-ble-peripheral-android".getBytes(StandardCharsets.UTF_8));
            response.addSubRecord(dataRecord);
        } else if (envelopeTag == EnvelopeTag.PROFILE_CERTS) {
            TLVRecord dataRecord = new TLVRecord(RecordTag.CERT_HASH, crypto.certificateSha256());
            response.addSubRecord(dataRecord);
        } else if (envelopeTag == EnvelopeTag.CERTIFICATE) {
            TLVRecord dataRecord = new TLVRecord(RecordTag.CERT_BYTES, crypto.encodedCertificate());
            response.addSubRecord(dataRecord);
        } else if (envelopeTag == EnvelopeTag.GENERATE_SESSION_PIN) {
            TLVRecord dataRecord = new TLVRecord(RecordTag.SESSION_PIN, sessionPin);
            response.addSubRecord(dataRecord);
            TLVRecord dataRecord2 = new TLVRecord(RecordTag.EXPIRES_TIME, new byte[]{11,22});
            response.addSubRecord(dataRecord2);
        } else if (envelopeTag == EnvelopeTag.SIGN_DATA) {
            TLVRecord signTBSRecord = envelopeRecord.getRecordForTag(RecordTag.SIGN_TBS);
            Log.println(Log.INFO, TAG, "signing " + signTBSRecord.getValue().length + " bytes");
            byte[] signature = crypto.sign(signTBSRecord.getValue());
            //ArrayUtils.reverse(signature);
            TLVRecord dataRecord = new TLVRecord(RecordTag.SIGNED_BYTES, signature);
            response.addSubRecord(dataRecord);
        }
        response.addSubRecord(envelopeRecord.getRecordForTag(RecordTag.REQUEST_ID));
        response.addSubRecord(new TLVRecord(RecordTag.RESULT_CODE, TLVResultCode.OK.getBytes()));
        return response;
    }

    public byte[] handleData(byte[] incoming) {
        byte[] reply = null;
        byte[] data = unparsedBytes != null ? ArrayUtils.addAll(unparsedBytes, incoming) : incoming;
        unparsedBytes = null;

        List<TLVRecord> tlvRecords;
        try {
            tlvRecords = tlvParser.parseAndGetRootTLVRecords(data, true);
            for (TLVRecord record : tlvRecords) {
                TLVRecord response = handleTLV(record);
                if (response == null) {
                    continue;
                }
                if (reply == null) {
                    reply = new byte[]{};
                }
                reply = ArrayUtils.addAll(reply, response.toByteArray());
            }
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
        } catch (TlvParserException e) {
            String message = "unable to parse received TLV: " + SDKHex.encode(data);
            Log.println(Log.ERROR, TAG, message);
        } catch (Exception e) {
            Log.println(Log.ERROR, TAG, "some other exception " + e.getMessage());
        }
        return reply;
    }
}