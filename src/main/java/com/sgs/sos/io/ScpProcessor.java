package com.sgs.sos.io;

import com.sgs.sos.common.CryptoManager;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;
import com.sgs.sos.scp.ScpConstants;
import com.sgs.sos.scp.ScpData;
import com.sgs.sos.scp.ScpMessageUnit;
import com.sgs.sos.session.SessionDetails;
import com.sgs.sos.session.SessionManager;
import com.sun.org.apache.bcel.internal.generic.SWITCH;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class ScpProcessor {
    private static ConcurrentLinkedQueue<ScpData> queue = new ConcurrentLinkedQueue<ScpData>();
    private static Logger scplogger= ScpLogger.getScpLogger();

    public static void addProcess(ScpData scpData)
    {
        queue.add(scpData);
        process();
    }

    private static void process()
    {
        if(queue.size()<1)
        {
            return;
        }
        ScpData data = queue.remove();
        scplogger.info(("Processing ScpData => ssid="+data.getHeader().getSsid()));

        try
        {
            if(data.getHeader().isPdu())
            {
                processPDU(data);
            }
            else
            {
                processSMU(data);
            }
        }
        catch (Exception e)
        {
            scplogger.severe("Exception in processing data "+ data.toString());
        }
        if(queue.size()>0)
        {
            process();
        }
    }

    private static void processSMU(ScpData data)
    {
        long ssid = data.getHeader().getSsid();
        if(!SessionManager.isExistingSsid(ssid))
            SessionManager.createSession(ssid, data);
        SessionDetails session = SessionManager.getSsidMap().get(ssid);
        session.setLastPacketIn(data.getHeader().getTimestamp());
        try {
            session.setEncrypted(CryptoManager.isIPAddressInKeymap(InetAddress.getByAddress(data.getHeader().getSrcAddress())));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        for(ScpMessageUnit msg : data.getMessageUnits())
        {
            SessionManager.processMessageUnits(ssid,msg);
        }
    }

    private static void processPDU(ScpData data)
    {
        long ssid = data.getHeader().getSsid();
       if(SessionManager.isActiveSession(ssid))
           SessionManager.processDataUnits(ssid, data);
    }
}
