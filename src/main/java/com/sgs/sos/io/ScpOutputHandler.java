package com.sgs.sos.io;

import com.sgs.sos.common.AppConf;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.server.ScpSocketHandler;

import java.net.InetAddress;

public class ScpOutputHandler
{
    public static void sendData(byte[] ip, byte[] data)
    {
        try {
            ScpSocketHandler.DownstreamResponder.sendResponse(InetAddress.getByAddress(ip), AppConf.getUsServerPort(), data);
        }
        catch (Exception e)
        {
            ScpLogger.getScpLogger().severe("SEND DATA EXCEPTION = "+ e.getLocalizedMessage());
        }
    }

    public static void sendData(String ip, byte[] data)
    {
        try {
            ScpSocketHandler.DownstreamResponder.sendResponse(InetAddress.getByName(ip), AppConf.getUsServerPort(), data);
        }
        catch (Exception e)
        {
            ScpLogger.getScpLogger().severe("SEND DATA EXCEPTION = "+ e.getLocalizedMessage());
        }
    }
}
