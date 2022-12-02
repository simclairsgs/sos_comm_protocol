package com.sgs.sos.io;

import com.sgs.sos.common.ScpLogger;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Logger;

public class ScpInputHandler {

    private static Logger scplogger = ScpLogger.getScpLogger();

    public ScpInputHandler(InetAddress clientAddress, int port , byte[] rawdata)
    {
        scplogger.info("REQUEST_IN -> address_in="+ (clientAddress.getHostAddress()) +":"+port);
        scplogger.info("Data : "+ Arrays.toString(rawdata));
    }

    public void process()
    {

    }
}
