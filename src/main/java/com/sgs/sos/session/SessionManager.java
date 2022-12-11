package com.sgs.sos.session;

import com.sgs.sos.common.CryptoManager;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;
import com.sgs.sos.scp.ScpConstants;
import com.sgs.sos.scp.ScpData;
import com.sgs.sos.scp.ScpMessageUnit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.logging.Logger;

public class SessionManager
{
    public static Logger scplogger = ScpLogger.getScpLogger();
    public static Hashtable<Long, SessionDetails> ssidMap = new Hashtable<Long, SessionDetails>();

    public static boolean isExistingSsid(long ssid)
    {
        return ssidMap.containsKey(ssid);
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
                    if(session.getCurrentState() == ScpConstants.SESSION_STATE.ACTIVE)
                    session.closeSession();
                    ssidMap.remove(ssid);
                    break;
                default:

            }
        }
    }

    public static void processDataUnits(long ssid, ScpData scpData)
    {
        byte[] msg = scpData.getPayload();
        switch (msg[0])
        {
            case ScpConstants.SRC_KEY:
                try {
                    setSourceEncryptionKey(InetAddress.getByAddress(scpData.getHeader().getSrcAddress()) ,Arrays.copyOfRange(msg,5, msg.length));
                } catch (UnknownHostException e) {
                    scplogger.severe("EXCEPTION IN SRC_KEY_SET " + e.getLocalizedMessage());
                }
                break;
        }
    }

    private static void setSourceEncryptionKey(InetAddress srcAddress, byte[] publicKey)
    {
        scplogger.info("SETTING SRC KEY "+ srcAddress.getHostAddress());
        CryptoManager.setPublicKeyOfSource(srcAddress, publicKey);
    }

    public static boolean isActiveSession(long ssid)
    {
        scplogger.warning(ssidMap.containsKey(ssid) +" "+ ssidMap.get(ssid).getCurrentState() +" "+ ScpConstants.SESSION_STATE.ACTIVE);
        return ssidMap.containsKey(ssid) && ssidMap.get(ssid).getCurrentState() == ScpConstants.SESSION_STATE.ACTIVE;
    }

    public static Hashtable<Long, SessionDetails> getSsidMap() {
        return ssidMap;
    }
}
