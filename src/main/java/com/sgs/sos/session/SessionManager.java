package com.sgs.sos.session;

import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;
import com.sgs.sos.scp.ScpConstants;
import com.sgs.sos.scp.ScpData;
import com.sgs.sos.scp.ScpMessageUnit;

import java.util.Hashtable;
import java.util.logging.Logger;

public class SessionManager
{
    public static Logger scplogger = ScpLogger.getScpLogger();
    private static Hashtable<Long, SessionDetails> ssidMap = new Hashtable<Long, SessionDetails>();

    public static boolean isExistingSsid(long ssid)
    {
        return ssidMap.contains(ssid);
    }

    public static void createSession(long ssid, ScpData scpData)
    {
        SessionDetails session = new SessionDetails(ssid, ScpConstants.SESSION_STATE.NEW,
                scpData.getHeader().getTimestamp(), scpData.getHeader().getMode(),
                scpData.getHeader().getDestAddress(), scpData.getHeader().getSrcAddress());
        ssidMap.put(ssid, session);
    }

    public static void processMessageUnits(long ssid, ScpMessageUnit msg)
    {
        scplogger.warning("ssid="+ssid+" => "+ msg.toString());
        SessionDetails session = ssidMap.get(ssid);
        if(msg.getMessageType()==ScpConstants.APP_DATA)
        {
            Util.print("PROCESS APP DATA");
        }
        else
        {
            switch (msg.getMessageType())
            {
                case ScpConstants.INIT_CONN:
                    session.setActiveSession();
                    break;

                case ScpConstants.TERMINATE_CONN:

                    break;
                default:

            }
        }
    }
}
