package com.sgs.sos.io;

import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.scp.ScpData;
import com.sgs.sos.scp.ScpHeader;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Logger;

public class ScpInputHandler {

    private static Logger scplogger = ScpLogger.getScpLogger();

    public ScpInputHandler(InetAddress clientAddress, int port , byte[] rawdata)
    {
        try
        {
            scplogger.info("REQUEST_IN -> address_in=" + (clientAddress.getHostAddress()) + ":" + port);
            ScpData scpData = ScpData.parseScpData(rawdata);
            scplogger.info(scpData.toString());
        }
        catch (Exception e)
        {
            scplogger.severe(e.getMessage());
        }
    }

    public void process()
    {

    }
}
