package com.sgs.sos.scp;


import com.sgs.sos.common.Util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

import static com.sgs.sos.scp.ScpHeader.scplogger;

public class ScpData implements Serializable {
    private ScpHeader header;
    private LinkedList<ScpMessageUnit> messageUnits;
    private byte[] scpData;
    private byte[] payload;

    public ScpData()
    {

    }

    public void initData(String destination, byte priority, byte mode)
    {
       header = new ScpHeader(destination, priority, mode);
       messageUnits = new LinkedList<ScpMessageUnit>();
    }

    public void addMessage(ScpMessageUnit msg)
    {
        messageUnits.add(msg);
    }

    public byte[] getScpDataArray()
    {
        byte[] payload = new byte[0];
        for(ScpMessageUnit msg : messageUnits)
        {
            payload = Util.addByteArrays(payload, msg.getMessaage());
            header.incrementScpUnitCount();
        }
        header.setPayloadLength((byte)payload.length);
        header.setHMAC(Util.calculateCRC(payload));
        setPayload(payload);
        setScpData(Util.addByteArrays(header.getHeader(), this.payload));
        return getScpData();
    }

    public ScpHeader getHeader() {
        return header;
    }

    public void setHeader(ScpHeader header) {
        this.header = header;
    }

    public LinkedList<ScpMessageUnit> getMessageUnits() {
        return messageUnits;
    }

    public void setMessageUnits(LinkedList<ScpMessageUnit> messageUnits) {
        this.messageUnits = messageUnits;
    }

    public byte[] getScpData() {
        return scpData;
    }

    public void setScpData(byte[] scpData) {
        this.scpData = scpData.clone();
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "ScpData{" +
                "header=" + header +
                ", messageUnits=" + messageUnits +
                ", scpData=" + Arrays.toString(scpData) +
                ", payload=" + Arrays.toString(payload) +
                '}';
    }
}
