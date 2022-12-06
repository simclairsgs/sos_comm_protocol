package com.sgs.sos.test;

import com.sgs.sos.common.AppConf;
import com.sgs.sos.scp.ScpConstants;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.scp.ScpData;
import com.sgs.sos.scp.ScpMessageUnit;
import com.sgs.sos.server.ScpSocketHandler;

import java.net.InetAddress;
import java.util.logging.Logger;

public class TestMain {
    public static void test()
    {
        Logger scplogger = ScpLogger.getScpLogger();
        scplogger.info("IP address : " + AppConf.getIpAddress());
       /* ScpHeader header = new ScpHeader("127.0.0.1", (byte) 0x1, (byte) 0x2);
        byte data[] = header.getHeader();
        InetAddress address = new InetSocketAddress("127.0.0.1",8085).getAddress();
        ScpSocketHandler.DownstreamResponder.sendResponse(address, 8085, data);*/
        try {
            ScpData scpData = new ScpData();
            scpData.initData("127.0.0.1", (byte) 0x1, (byte) 0x2);
            ScpMessageUnit m1 = new ScpMessageUnit(ScpConstants.INIT_CONN);
            m1.setMessage("Test".getBytes());
            ScpMessageUnit m2 = new ScpMessageUnit(ScpConstants.APP_DATA);
            m2.setMessage("Hello World".getBytes());
            ScpMessageUnit m3 = new ScpMessageUnit(ScpConstants.APP_DATA);
            m3.setMessage("Test Hello World".getBytes());
            scpData.addMessage(m1);
            scpData.addMessage(m2);
            scpData.addMessage(m3);
            byte[] data = scpData.getScpDataArray();
            scplogger.severe(scpData.toString());
            ScpSocketHandler.DownstreamResponder.sendResponse(InetAddress.getByName("192.168.0.109"), 8085, data);

        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
