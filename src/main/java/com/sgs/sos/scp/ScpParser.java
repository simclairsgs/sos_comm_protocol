package com.sgs.sos.scp;

import java.util.Arrays;
import java.util.LinkedList;

import static com.sgs.sos.scp.ScpHeader.scplogger;

public class ScpParser
{

    public static ScpData parseScpData(byte[] data)
    {
        try
        {
            ScpData scpData = new ScpData();
            scpData.setScpData(data);
            scpData.setHeader(ScpHeader.parseScpHeader(Arrays.copyOfRange(data, 0, 32)));
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
