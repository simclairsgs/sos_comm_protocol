package com.sgs.sos.io;

import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;
import com.sgs.sos.scp.ScpHeader;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class ScpInputHandler {

    private static Logger scplogger = ScpLogger.getScpLogger();

    public ScpInputHandler(InetAddress clientAddress, int port , byte[] rawdata)
    {
        try
        {
            scplogger.info("REQUEST_IN -> address_in=" + (clientAddress.getHostAddress()) + ":" + port);
            scplogger.info("Data : " + Arrays.toString(rawdata));
            ScpHeader header = ScpHeader.parseScpHeader(Arrays.copyOfRange(rawdata, 0, 31));
            scplogger.info("hmac = " + header.getHMAC() +
                    " src=" + Util.byteArrayToIpString(header.getSrcAddress())
            +" Dest="+Util.byteArrayToIpString(header.getDestAddress()));
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
