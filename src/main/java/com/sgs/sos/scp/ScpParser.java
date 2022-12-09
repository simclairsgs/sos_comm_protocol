package com.sgs.sos.scp;

import com.sgs.sos.common.Util;

import java.util.Arrays;
import java.util.LinkedList;

import static com.sgs.sos.scp.ScpHeader.scplogger;

public class ScpParser
{
    public static ScpHeader parseScpHeader(byte[] data)
    {
        try
        {
            ScpHeader header = new ScpHeader();
            header.setHeader(data);
            header.setSrcAddress(Arrays.copyOfRange(data, 8, 12));
            header.setDestAddress(Arrays.copyOfRange(data, 12, 16));
            header.setHMAC(Util.bytesToInt(Arrays.copyOfRange(data, 4, 8)));
            header.setPriorityMode(data[1]);
            header.setReserved(data[0]);
            header.setPayloadLength(data[2]);
            header.setPadding((byte)((data[3] & 0xF0)>>4));
            header.setScpUnitCount((byte)(data[3] & 0xF));
            if(header.getPayloadLength()==0 && header.getScpUnitCount()==0)
                header.setPdu(true);
            header.setTimestamp(Util.bytesToLong(Arrays.copyOfRange(data,16,24)));
            header.setSsid(Util.bytesToLong(Arrays.copyOfRange(data,24,32)));
            return header;
        }
        catch (Exception e)
        {
            scplogger.severe(" Exception in parsing ScpHeader "+e.getStackTrace());
        }
        return null;
    }

    public static ScpData parseScpData(byte[] data)
    {
        try
        {
            ScpData scpData = new ScpData();
            scpData.setScpData(data);
            scpData.setHeader(parseScpHeader(Arrays.copyOfRange(data, 0, 32)));
            if(scpData.getHeader().isPdu() && data[32]==80)
            {
                scpData.setPayload(Arrays.copyOfRange(data,32,data.length));
                return parseScpPDU(scpData);
            }
            scpData.setPayload(Arrays.copyOfRange(data, 32, 32 + scpData.getHeader().getPayloadLength()));
            ScpParser.parseMessageUnits(scpData);
            return scpData;
        }
        catch (Exception e)
        {
            scplogger.severe("Exception parsing scpData" + e.getMessage());
        }
        return null;
    }

    public static ScpData parseScpPDU(ScpData scpData) {
        try
        {
            byte[] data = scpData.getPayload();
            scpData.setPdu(new ScpDataUnit((byte)0x50));
            scpData.getPdu().setPayloadLength(Util.bytesToInt(Arrays.copyOfRange(data,1,5)));
            scpData.getPdu().setPayload(Arrays.copyOfRange(data,5,5 + scpData.getPdu().getPayloadLength()));
            return scpData;
        }
        catch (Exception e)
        {
            scplogger.severe("Exception in parsing SCP-PDU "+ e.getLocalizedMessage());
        }
        return null;
    }

    public static void parseMessageUnits(ScpData scpData) {
        scpData.setMessageUnits(new LinkedList<ScpMessageUnit>());
        if (scpData.getPayload().length < 2) {
            return;
        }
        int pos = 0, mLength = 0;
        try {
            for (int i = 0; i < scpData.getHeader().getScpUnitCount(); i++) {
                mLength = scpData.getPayload()[pos + 1] + 2;
                ScpMessageUnit msg = ScpParser.parseMessageUnit(Arrays.copyOfRange(scpData.getPayload(), pos, pos + mLength));
                scpData.addMessage(msg);
                pos += mLength;
            }

        } catch (Exception e) {
            scplogger.severe("Exception in parsing messageUnits | " + e.getMessage());
        }
    }

    public static ScpMessageUnit parseMessageUnit(byte[] data)
    {
        try
        {
            ScpMessageUnit msgUnit = new ScpMessageUnit();
            msgUnit.setMessageType(data[0]);
            msgUnit.setLength(data[1]);
            msgUnit.setMessage(Arrays.copyOfRange(data,2,data.length));
            return msgUnit;
        }
        catch (Exception e)
        {
            scplogger.severe("Exception in MsgUnitParsing " + e.getMessage());
        }
        return null;
    }
}
