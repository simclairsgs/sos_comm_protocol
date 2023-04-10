package com.sgs.sos.scp;


import com.sgs.sos.common.Util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

public class ScpData implements Serializable {
    private ScpHeader header;
    private LinkedList<ScpMessageUnit> messageUnits;
    private byte[] scpData;
    private byte[] payload;
    private ScpDataUnit pdu;

    public ScpData()
    {

    }

    public void initData(String destination, byte priority, byte mode, long ssid)
    {
       header = new ScpHeader(destination, priority, mode, ssid);
       messageUnits = new LinkedList<ScpMessageUnit>();
    }

    public void initData(byte[] destination, byte priority, byte mode, long ssid)
    {
        header = new ScpHeader(destination, priority, mode, ssid);
        messageUnits = new LinkedList<ScpMessageUnit>();
    }

    public void addMessage(ScpMessageUnit msg)
    {
        messageUnits.add(msg);
    }

    public byte[] getFullScpDataArray(boolean isPDU, byte[] data, byte type)
    {
        pdu = new ScpDataUnit(type);
        pdu.setPayload(data);
        byte[] payload = pdu.getPDUPayload();
        header.setPayloadLength((byte)0);
        header.setPdu(true);
        header.setHMAC(Util.calculateCRC(payload));
        setPayload(payload);
        setScpData(Util.addByteArrays(header.getHeader(), this.payload));
        return getScpData();
    }

    public byte[] getFullScpDataArray()
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

    public ScpDataUnit getPdu() {
        return pdu;
    }

    public void setPdu(ScpDataUnit pdu) {
        this.pdu = pdu;
    }

    @Override
    public String toString() {
        return "ScpData{" +
                "header=" + header +
                ", messageUnits=" + messageUnits +
                ", scpData=" + Arrays.toString(scpData) +
                ", payload=" + Arrays.toString(payload) +
                ", pdu=" + pdu +
                '}';
    }
}
