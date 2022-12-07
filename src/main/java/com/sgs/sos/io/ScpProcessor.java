package com.sgs.sos.io;

import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;
import com.sgs.sos.scp.ScpData;

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
}
