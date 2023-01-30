package com.sgs.sos.session;

import com.sgs.sos.common.CryptoManager;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;
import com.sgs.sos.scp.*;

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
            if( ! SessionManager.isActiveSession(ssid))
            {
                scplogger.severe(" APP DATA RECEIVED FOR INACTIVE SESSION "+ ssid);
                return;
            }
            Util.print("PROCESS APP DATA");
            Util.print(new String(msg.getMessage()));
        }
        else
        {
            switch (msg.getMessageType())
            {
                case ScpConstants.INIT_CONN:
                    session.setActiveSession();
                    break;
                    
                case ScpConstants.FILE_NAME_CMD: case ScpConstants.FILE_TRANSFER:
                {
                    session.setLastActionId(ActionId.FILE_TRANSFER);
                    String file = new String(msg.getMessage());
                    scplogger.warning(" INCOMING FILE NAME = "+file);
                    session.setWriter(file);
                }
                break;

                case ScpConstants.F_CLOSE:
                {
                    session.closeWriter();
                    session.lastActionId = ActionId.NULL_ACTION;
                }
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
                
            case 9:
            {
                scplogger.info("SESSION ="+ getSession(ssid));
                scplogger.info(" PDU_IN  = "+ new String(Arrays.copyOfRange(msg, 5 , msg.length)));
            }
            break;
        }
    }

    public static SessionDetails getSession(long ssid)
    {
        return ssidMap.get(ssid);
    }
    
    private static void setSourceEncryptionKey(InetAddress srcAddress, byte[] publicKey)
    {
        scplogger.info("SETTING SRC KEY "+ srcAddress.getHostAddress());
        CryptoManager.setPublicKeyOfSource(srcAddress, publicKey);
    }

    public static boolean isActiveSession(long ssid)
    {
        return ssidMap.containsKey(ssid) && ssidMap.get(ssid).getCurrentState() == ScpConstants.SESSION_STATE.ACTIVE;
    }

    public static Hashtable<Long, SessionDetails> getSsidMap() {
        return ssidMap;
    }
}
