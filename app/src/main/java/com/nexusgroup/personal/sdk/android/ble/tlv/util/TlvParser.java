package com.nexusgroup.personal.sdk.android.ble.tlv.util;

import android.util.Log;

import com.nexusgroup.personal.sdk.android.ble.SDKHex;
import com.nexusgroup.personal.sdk.android.ble.tlv.EnvelopeTag;
import com.nexusgroup.personal.sdk.android.ble.tlv.RecordTag;

import java.util.ArrayList;
import java.util.List;

public class TlvParser {
    private final static String TAG = "tlv-parser";

    private final int TAG_CONSTRUCTED_BIT = 0x20;
    private final int TAG_VALUE_MASK = 0x1F;
    private final int MULTI_BYTE_TAG = 0x1F;
    private final int TAG_LAST_BYTE_BIT = 0x80;
    private final int MULTI_BYTE_LENGTH_BIT = 0x80;
    private final int RESERVED_LENGTH = 0x80;
    private final int RESERVED_INDEFINITE_LENGTH = 0xFF;

    public TlvParser() {
    }

    public List<TLVRecord> parseAndGetRootTLVRecords(byte[] tlv, boolean logErrors) throws TlvParserException, TLVParserBytesMissingException {
        List<TLVRecord> tlvList = parseTlvList(tlv, logErrors);
        if (tlvList.isEmpty()) {
            throw new TlvParserException("parsed result was empty");
        }
        return tlvList;
    }

    private List<TLVRecord> parseTlvList(byte[] tlv, boolean logErrors) throws TlvParserException, TLVParserBytesMissingException {
        if (tlv == null || tlv.length == 0) {
            throw new TlvParserException(new NullPointerException("tlv bytes was null or empty"));
        }

        List<TLVRecord> tlvEntries = new ArrayList<>();
        int offset = 0;
        int dataLength;

        while (tlv.length > offset) {
            byte[] tag;

            if (tlv.length < offset + 2) {
                String message = "TLV has not enough bytes for tag and length";
                throw new TLVParserBytesMissingException(message, tlvEntries);
            }

            if ((tlv[offset] & TAG_VALUE_MASK) < MULTI_BYTE_TAG) {
                tag = new byte[1];
                tag[0] = tlv[offset++];
            } else if ((tlv[offset + 1] & TAG_LAST_BYTE_BIT) == 0) {
                tag = new byte[2];
                tag[0] = tlv[offset++];
                tag[1] = tlv[offset++];
            } else {
                //TODO: support larger tags?
                throw new TlvParserException("found tag larger than 2 bytes, aborting!");
            }

            if (tlv.length < offset + 1) {
                String message = "TLV has not enough bytes for first length byte";
                throw new TLVParserBytesMissingException(message, tlvEntries);
            }

            int len = tlv[offset++] & 0xFF;
            if (len < MULTI_BYTE_LENGTH_BIT) {
                dataLength = len;
            } else if (len == RESERVED_LENGTH || len == RESERVED_INDEFINITE_LENGTH) {
                if (logErrors) {
                    Log.println(Log.ERROR,TAG, "Unsupported TLV length encoding: " + len + ". Length values of 0x80 and 0xFF are reserved.");
                }
                throw new TlvParserException("Unsupported TLV length encoding: " + len);
            } else {
                int numBytes = len & 0x7F;

                if (numBytes > 4) {
                    throw new TlvParserException("length larger than 4 bytes, aborting");
                }

                if (tlv.length < offset + numBytes) {
                    String message = "TLV has not enough bytes for length bytes";
                    throw new TLVParserBytesMissingException(message, tlvEntries);
                }

                byte[] lenBytes = new byte[numBytes];
                System.arraycopy(tlv, offset, lenBytes, 0, numBytes);
                offset += numBytes;
                try {
                    dataLength = TLVConvertUtil.intFromBytes(lenBytes);
                } catch (Exception e) {
                    throw new TlvParserException("unable to parse bytes to int for length");
                }
            }

            if (tlv.length < offset + dataLength) {
                String message = "TLV has not enough bytes for value lenght";
                throw new TLVParserBytesMissingException(message, tlvEntries);
            }

            byte[] data = new byte[dataLength];
            try {
                System.arraycopy(tlv, offset, data, 0, dataLength);
                offset += dataLength;
            } catch (ArrayIndexOutOfBoundsException e) {
                if (logErrors) {
                    Log.println(Log.ERROR, TAG, "Invalid TLV structure found at TAG " + SDKHex.encode(tag));
                }
                for (TLVRecord t : tlvEntries) {
                    if (logErrors) {
                        Log.println(Log.ERROR, TAG, t.toString());
                    }
                }

                String message = "Failed to copy tlv bytes for value: " + e.getMessage();
                throw new TLVParserBytesMissingException(message, tlvEntries);
            }

            TLVRecord tlvEntry;

            int tagInt;
            try {
                tagInt = TLVConvertUtil.intFromBytes(tag);
            } catch (Exception e) {
                throw new TlvParserException("invalid tag size: " + tag.length);
            }

            if ((tag[0] & TAG_CONSTRUCTED_BIT) != 0) {
                EnvelopeTag envelopeTag = EnvelopeTag.fromInteger(tagInt);
                if (envelopeTag == null) {
                    Log.println(Log.ERROR, TAG, "unknown envelope tag received: " + tagInt);
                    tlvEntry = new TLVRecord(tag);
                } else {
                    tlvEntry = new TLVRecord(envelopeTag);
                }
                List<TLVRecord> subEntries = parseTlvList(data, logErrors);

                for (TLVRecord se : subEntries) {
                    tlvEntry.addSubRecord(se);
                }
            } else {
                RecordTag recordTag = RecordTag.fromInteger(tagInt);
                if (recordTag == null) {
                    Log.println(Log.ERROR, TAG, "unknown record tag received: " + tagInt);
                    tlvEntry = new TLVRecord(tag, data);
                } else {
                    tlvEntry = new TLVRecord(recordTag, data);
                }
            }

            tlvEntries.add(tlvEntry);
        }

        return tlvEntries;
    }
}