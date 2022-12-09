package com.sgs.sos.test;

import com.sgs.sos.common.AppConf;
import com.sgs.sos.common.CryptoManager;
import com.sgs.sos.scp.ScpConstants;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.scp.ScpData;
import com.sgs.sos.scp.ScpMessageUnit;
import com.sgs.sos.server.ScpSocketHandler;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Logger;

public class TestMain {
    public static String DESTINATION_IP = "192.168.59.229";
    public static void test()
    {
        Logger scplogger = ScpLogger.getScpLogger();
        scplogger.fine("Current Local IP address : " + AppConf.getIpAddress());
       /* ScpHeader header = new ScpHeader("127.0.0.1", (byte) 0x1, (byte) 0x2);
        byte data[] = header.getHeader();
        InetAddress address = new InetSocketAddress("127.0.0.1",8085).getAddress();
        ScpSocketHandler.DownstreamResponder.sendResponse(address, 8085, data);*/
        try {
            ScpData scpData = new ScpData();
            scpData.initData(DESTINATION_IP, (byte) 0x3, (byte) 0x2);
            byte[] data = scpData.getFullScpDataArray(true, CryptoManager.getPublicKey().getEncoded());
            scplogger.info("PREP " + (scpData.toString()));
            scplogger.info("PREP " + Arrays.toString(Arrays.copyOfRange(data,32,data.length)));
            System.out.println(data.length);
            ScpSocketHandler.DownstreamResponder.sendResponse(InetAddress.getByName(DESTINATION_IP), 8085, data);

        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
