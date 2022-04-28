package com.nexusgroup.personal.sdk.android.ble.tlv.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nexusgroup.personal.sdk.android.ble.SDKHex;
import com.nexusgroup.personal.sdk.android.ble.tlv.EnvelopeTag;
import com.nexusgroup.personal.sdk.android.ble.tlv.RecordTag;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TLVRecord {

    private final static String TAG = "tlv-record";

    public enum TagClass {
        UNIVERSAL("UNIVERSAL"),
        APPLICATION("APPLICATION"),
        CONTEXT_SPECIFIC("CONTEXT_SPECIFIC"),
        PRIVATE("PRIVATE");

        private final String name;

        TagClass(String name) {
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

    private byte[] tag;
    private int valueLen;
    private byte[] value;
    private List<TLVRecord> subrecords = new ArrayList<>();

    /**
     * Constructs a <code>TLVRecord</code> object based on a tag.
     *
     * @param tag
     */
    public TLVRecord(byte[] tag) {
        this.tag = tag;
        this.valueLen = 0;
        this.value = new byte[0];
    }

    /**
     * Constructs a <code>TLVRecord</code> object based on a tag.
     *
     * @param tag
     */
    public TLVRecord(EnvelopeTag tag) {
        this.tag = tag.getBytes();
        this.valueLen = 0;
        this.value = new byte[0];
    }

    /**
     * Constructs a <code>TLVRecord</code> object based on a tag and value.
     *
     * @param tag
     * @param value
     */
    public TLVRecord(byte[] tag, byte[] value) {
        this.tag = tag;
        this.valueLen = value.length;
        this.value = new byte[valueLen];
        System.arraycopy(value, 0, this.value, 0, this.valueLen);
    }

    /**
     * Constructs a <code>TLVRecord</code> object based on a tag and value.
     *
     * @param tag
     * @param value
     */
    public TLVRecord(RecordTag tag, byte[] value) {
        this.tag = tag.getBytes();
        this.valueLen = value.length;
        this.value = new byte[valueLen];
        System.arraycopy(value, 0, this.value, 0, this.valueLen);
    }

    @Nullable
    public TagClass getTagClass() {
        int header = tag[0] & 0b11000000;
        switch (header) {
            case 0b00000000:
                return TagClass.UNIVERSAL;
            case 0b01000000:
                return TagClass.APPLICATION;
            case 0b10000000:
                return TagClass.CONTEXT_SPECIFIC;
            case 0b11000000:
                return TagClass.PRIVATE;
        }
        return null;
    }

    public boolean isPrimitive() {
        return (tag[0] & 0b00100000) == 0;
    }

    /**
     * Add a subrecord to the end of this <code>TLVRecord</code>'s subrecord list. Skipping nulls.
     *
     * @param tlv
     */
    public void addSubRecord(TLVRecord tlv) {
        if (tlv != null) {
            subrecords.add(tlv);
        }
    }

    /**
     * Remove a specific subrecord referenced by its tag.
     *
     * @param tag
     */
    public void removeSubRecordWithTag(byte[] tag) {
        TLVRecord removeMeTlv = null;
        for (TLVRecord entry : getSubRecords()) {
            if (Arrays.equals(tag, entry.getTag())) {
                removeMeTlv = entry;
                break;
            }
        }
        if (removeMeTlv != null) {
            subrecords.remove(removeMeTlv);
        }
    }

    @Nullable
    public TLVRecord getRecordForTag(RecordTag recordTag) {
        List<TLVRecord> result = getRecordsWithTag(recordTag);
        int size = result.size();
        if (size <= 0) {
            return null;
        } else if (result.size() > 1) {
            Log.println(Log.ERROR, TAG, "found more records than expected with tag: " + recordTag + ", number: " + size);
        }
        return result.get(0);
    }

    public List<TLVRecord> getRecordsWithTag(RecordTag recordTag) {
        List<TLVRecord> records = new ArrayList<>();
        for (TLVRecord record : getSubRecords()) {
            if (record.getRecordTag() == recordTag) {
                records.add(record);
            }
        }
        return records;
    }

    /**
     * @return list of subrecords for this <code>TLVRecord</code>.
     */
    public List<TLVRecord> getSubRecords() {
        return subrecords;
    }

    /**
     * Is the record of a specific tag?
     *
     * @return true or false
     */
    public boolean hasTag(byte[] tag) {
        return Arrays.equals(getTag(), tag);
    }

    /**
     * @return the tag value of this <code>TLVRecord</code>.
     */
    @Nullable
    public byte[] getTag() {
        return tag;
    }

    @Nullable
    private Integer getTagInt() {
        try {
            return TLVConvertUtil.intFromBytes(tag);
        } catch (Exception e) {
            Log.println(Log.ERROR, TAG, "tag " + SDKHex.encode(tag) + " not convertible to int...");
            return null;
        }
    }

    @Nullable
    public EnvelopeTag getEnvelopeTag() {
        return EnvelopeTag.fromInteger(getTagInt());
    }

    @Nullable
    public RecordTag getRecordTag() {
        return RecordTag.fromInteger(getTagInt());
    }

    /**
     * This method returns the value (no tag or length bytes) of this TLV in
     * byte form. If the TLV is at root level (containing no value itself), the
     * method will iterate through its subrecords and return all subrecords in
     * byte form.
     *
     * @return value field of this <code>TLVRecord</code>
     */
    public byte[] getValue() {
        if (value.length == 0) {
            byte[] entries = new byte[getValueLength()];
            int offset = 0;
            byte[] tlvBytes;

            for (TLVRecord tlv : subrecords) {
                tlvBytes = tlv.toByteArray();
                System.arraycopy(tlvBytes, 0, entries, offset, tlvBytes.length);
                offset += tlvBytes.length;
            }
            return entries;
        } else {
            return value;
        }
    }

    /**
     * This method returns the value (no tag or length bytes) of a specific
     * subrecord within this TLV, referenced through it's tag. If the tag passed
     * is the same as the tag for the calling TLV, this method returns
     * <code>getValue()</code>. If you want the entire TLV data, you should use
     * toByteArray() or getByteArrayForTag() instead
     *
     * @return value field of a specific subrecord of this <code>TLVRecord</code>
     * @see TLVRecord#getValue()
     */
    @Nullable
    private byte[] getValueForTag(byte[] tag) {
        if (Arrays.equals(tag, this.tag)) {
            return getValue();
        } else {
            for (TLVRecord record : subrecords) {
                byte[] tmp = record.getValueForTag(tag);
                if (tmp != null) {
                    return tmp;
                }
            }
            return null;
        }
    }

    @Nullable
    public byte[] getValueForTag(RecordTag recordTag) {
        return getValueForTag(recordTag.getBytes());
    }

    @Nullable
    public Integer getIntValueForTag(RecordTag recordTag) {
        try {
            return TLVConvertUtil.intFromBytes(getValueForTag(recordTag));
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public String getStringValueForTag(RecordTag recordTag) {
        byte[] value = getValueForTag(recordTag);
        if (value == null) {
            return null;
        } else {
            return new String(value, StandardCharsets.UTF_8);
        }
    }

    /**
     * @return full size of this <code>TLVRecord</code>.
     */
    public int getSize() {
        int length = getValueLength();

        if (length <= 0x7F) {
            return tag.length + length + 1;
        } else if (length <= 0xFF) {
            return tag.length + length + 2;
        } else if (length <= 0xFFFF) {
            return tag.length + length + 3;
        } else if (length <= 0xFFFFFF) {
            return tag.length + length + 4;
        } else {
            return tag.length + length + 5;
        }
    }

    /**
     * @return the length of this <code>TLVRecord</code>'s value.
     */
    private int getValueLength() {
        if (subrecords.isEmpty()) {
            return value.length;
        } else {
            int length = 0;

            for (TLVRecord tlv : subrecords) {
                long sum = (long) length + tlv.getSize();
                if (sum > Integer.MAX_VALUE) {
                    Log.println(Log.ERROR, TAG, "bytes are too many to fit into an int");
                    //TODO: better error handling here
                    return length;
                }
                length += tlv.getSize();
            }
            return length;
        }
    }

    /**
     * @return returns a complete representation of this <code>TLVRecord</code>
     * in byte form.
     */
    public byte[] toByteArray() {
        byte[] completeTlvBytes = new byte[getSize()];
        int offset = 0;

        System.arraycopy(tag, 0, completeTlvBytes, offset, tag.length);
        offset += tag.length;
        offset = addTlvLength(completeTlvBytes, offset);
        byte[] tlvValue = getValue();
        System.arraycopy(tlvValue, 0, completeTlvBytes, offset, tlvValue.length);

        return completeTlvBytes;
    }

    /**
     * Helper method used for adding correct length indicator (if needed) when
     * calling <code>toByteArray()</code>.
     *
     * @param finalData
     * @param offset
     * @return
     */
    private int addTlvLength(byte[] finalData, int offset) {
        int length = getValueLength();

        if (length <= 0x7F) {
            finalData[offset++] = (byte) length;
        } else if (length <= 0xFF) {
            finalData[offset++] = (byte) 0x81;
            finalData[offset++] = (byte) (length & 0xFF);
        } else if (length <= 0xFFFF) {
            finalData[offset++] = (byte) 0x82;
            finalData[offset++] = (byte) ((length >> 8) & 0xFF);
            finalData[offset++] = (byte) (length & 0xFF);
        } else if (length <= 0xFFFFFF) {
            finalData[offset++] = (byte) 0x83;
            finalData[offset++] = (byte) ((length >> 16) & 0xFF);
            finalData[offset++] = (byte) ((length >> 8) & 0xFF);
            finalData[offset++] = (byte) (length & 0xFF);
        } else {
            finalData[offset++] = (byte) 0x84;
            finalData[offset++] = (byte) ((length >> 24) & 0xFF);
            finalData[offset++] = (byte) ((length >> 16) & 0xFF);
            finalData[offset++] = (byte) ((length >> 8) & 0xFF);
            finalData[offset++] = (byte) (length & 0xFF);
        }
        return offset;
    }
}