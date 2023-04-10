package com.sgs.sos.scp;

import com.sgs.sos.common.Util;

import java.util.Arrays;

public class ScpDataUnit
{
    private byte[] payload;
    private byte pduType;
    private int payloadLength;

    public ScpDataUnit(byte b) {
        setPduType(b);
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public void setPayloadLength(int payloadLength) {
        this.payloadLength = payloadLength;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload.clone();
        setPayloadLength(this.payload.length);
    }

    public byte[] getPDUPayload()
    {
        return Util.addByteArrays(new byte[]{getPduType()},Util.intToByteArray(getPayloadLength()),getPayload());
    }

    public byte getPduType() {
        return pduType;
    }

    public void setPduType(byte pduType) {
        this.pduType = pduType;
    }

    @Override
    public String toString() {
        return "ScpDataUnit{" +
                "payload=" + Arrays.toString(payload) +
                ", pduType=" + pduType +
                ", payloadLength=" + payloadLength +
                '}';
    }
}
