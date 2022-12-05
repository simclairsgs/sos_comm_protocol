package com.sgs.sos.scp;

import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;

import java.util.Arrays;
import java.util.logging.Logger;

public class ScpMessageUnit {

    private byte[] message;
    private byte length;
    public byte messageType;
    Logger scplogger = ScpLogger.getScpLogger();

    public ScpMessageUnit() {

    }

    public byte[] getMessage() {
        return message;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public void setMessage(byte[] message) {
        this.message = message.clone();
        setLength((byte)this.message.length);
    }

    public ScpMessageUnit(byte messageType)
    {
        this.messageType = messageType;
    }

    public byte getLength() {
        return length;
    }

    public void setLength(byte length) {
        this.length = length;
    }

    public byte[] getMessaage()
    {
        byte[] msg = new byte[]{getMessageType(), getLength()};
        return Util.addBytesToArray(msg, this.message);
    }

    public ScpMessageUnit parseMessageUnit(byte[] data)
    {
        try
        {
            ScpMessageUnit msgUnit = new ScpMessageUnit();
            msgUnit.setMessageType(data[0]);
            msgUnit.setLength(data[1]);
            msgUnit.setMessage(Arrays.copyOfRange(data,2,data.length-2));
            return msgUnit;
        }
        catch (Exception e)
        {
            scplogger.severe("Exception in MsgUnitParsing " + e.getMessage());
        }
        return null;
    }

    @Override
    public String toString() {
        return "ScpMessageUnit{" +
                "message=" + Arrays.toString(message) +
                ", length=" + length +
                ", type=" + messageType +
                '}';
    }

}
