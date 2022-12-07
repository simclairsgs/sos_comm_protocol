package com.sgs.sos.io;

import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;
import com.sgs.sos.scp.ScpData;
import com.sgs.sos.scp.ScpParser;

import java.net.InetAddress;
import java.util.logging.Logger;

public class ScpInputHandler {

    private static Logger scplogger = ScpLogger.getScpLogger();

    public static void handle(InetAddress clientAddress, int port , byte[] rawdata)
    {
        try
        {
            scplogger.info(("REQUEST_IN -> address_in=" + (clientAddress.getHostAddress()) + ":" + port));
            rawdata = Util.removeTrailingZeros(rawdata);
            ScpData scpData = ScpParser.parseScpData(rawdata);
            scplogger.info((scpData.toString()));
            process(scpData);
        }
        catch (Exception e)
        {
            scplogger.severe(e.getMessage());
        }
    }
    public static void process(ScpData scpData)
    {
        ScpProcessor.addProcess(scpData);
    }
}
