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
        this.scpData = scpData;
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

    public static ScpData parseScpData(byte[] data)
    {
        ScpData scpData = new ScpData();
        scpData.header = ScpHeader.parseScpHeader(Arrays.copyOfRange(data,0,32));
        scpData.setPayload(Arrays.copyOfRange(data,32,32+scpData.header.getPayloadLength()));
        scpData.parseMessageUnits();
        return scpData;
    }

    private void parseMessageUnits() {
        Util.print(Arrays.toString(payload));
        messageUnits = new LinkedList<ScpMessageUnit>();
        if(payload.length<2)
        {
            return;
        }
        int pos = 0, mLength = 0;
        try
        {
            for(int i=0; i< header.getScpUnitCount(); i++)
            {
                 mLength = payload[pos+1] + 2;
                 Util.print(Arrays.toString(Arrays.copyOfRange(payload,pos,pos+mLength)));
                 Util.print(pos+"-"+mLength);
                 pos += mLength;
            }

        }
        catch (Exception e)
        {
            scplogger.info("Exception in parsing messageUnits | "+e.getMessage());
        }
    }
}
