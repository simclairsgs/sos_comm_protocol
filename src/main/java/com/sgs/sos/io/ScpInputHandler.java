package com.sgs.sos.io;

import com.sgs.sos.common.AppConf;
import com.sgs.sos.common.CryptoManager;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;
import com.sgs.sos.scp.ActionId;
import com.sgs.sos.scp.ScpConstants;
import com.sgs.sos.scp.ScpData;
import com.sgs.sos.scp.ScpParser;
import com.sgs.sos.session.SessionManager;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Logger;

public class ScpInputHandler {

    private static Logger scplogger = ScpLogger.getScpLogger();

    public static void handle(InetAddress clientAddress, int port , byte[] rawdata)
    {
        try
        {
            scplogger.info(("REQUEST_IN -> address_in=" + (clientAddress.getHostAddress()) + ":" + port));
            if(rawdata[0] == ScpConstants.FILE_DATA)
            {
                long ssid = Util.bytesToLong(Arrays.copyOfRange(rawdata, 1, 9));
                if(SessionManager.isActiveSession(ssid) && SessionManager.getSession(ssid).getLastActionId()== ActionId.FILE_TRANSFER)
                {
                    scplogger.warning("FILE_DATA_IN "+  rawdata.length +" - "+Arrays.toString(Arrays.copyOfRange(rawdata, 9, 9+AppConf.FTR_BUFFER_SIZE)));
                    SessionManager.getSession(ssid).writeData(Arrays.copyOfRange(rawdata, 9, 9+ AppConf.FTR_BUFFER_SIZE));
                    return;
                }
                else if(SessionManager.isExistingSsid(ssid))
                {
                    scplogger.severe(" FILE_IN_FOR_INACTIVE_SESSION");
                }
            }
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
