package com.sgs.sos.test;

import com.sgs.sos.common.AppConf;
import com.sgs.sos.common.CryptoManager;
import com.sgs.sos.common.Util;
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
    static Logger scplogger;

    public static void test()
    {
        scplogger = ScpLogger.getScpLogger();
        scplogger.warning("Current Local IP address : " + AppConf.getIpAddress());
       /* ScpHeader header = new ScpHeader("127.0.0.1", (byte) 0x1, (byte) 0x2);
        byte data[] = header.getHeader();
        InetAddress address = new InetSocketAddress("127.0.0.1",8085).getAddress();
        ScpSocketHandler.DownstreamResponder.sendResponse(address, 8085, data);*/
        try {
            testPDU();
            Thread.sleep(5000);
            testPMU();
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void testPDU() throws Exception {
        ScpData scpData = new ScpData();
        scpData.initData(DESTINATION_IP, ScpConstants.HIGH, ScpConstants.SOCKET, Util.generateSsid());
        byte[] data = scpData.getFullScpDataArray(true, CryptoManager.getPublicKey().getEncoded());
        scplogger.info("PREP " + (scpData.toString()));
        scplogger.info("PREP " + Arrays.toString(Arrays.copyOfRange(data,32,data.length)));
        ScpSocketHandler.DownstreamResponder.sendResponse(InetAddress.getByName(DESTINATION_IP), 8085, data);
    }

    public static void testHTTPWithPDU() throws Exception {
        ScpData scpData = new ScpData();
        scpData.initData(DESTINATION_IP, ScpConstants.HIGH, ScpConstants.HTTP, Util.generateSsid());
        byte[] data = scpData.getFullScpDataArray(true, CryptoManager.getPublicKey().getEncoded());
        scplogger.info("PREP " + (scpData.toString()));
        scplogger.info("PREP " + Arrays.toString(Arrays.copyOfRange(data,32,data.length)));
        ScpSocketHandler.DownstreamResponder.sendResponse(InetAddress.getByName(DESTINATION_IP), 8085, data);
    }

    public static void testPMU() throws Exception
    {
        ScpData scpData1 = new ScpData();
        scpData1.initData(DESTINATION_IP, ScpConstants.LOW, ScpConstants.SOCKET, Util.generateSsid());
        ScpMessageUnit mu = new ScpMessageUnit(ScpConstants.INIT_CONN);
        mu.setMessage("Test".getBytes());
        scpData1.addMessage(mu);
        byte[] data1 = scpData1.getFullScpDataArray();
        scplogger.info("PREP " + (scpData1.toString()));
        scplogger.info("PREP " + Arrays.toString(Arrays.copyOfRange(data1,32,data1.length)));
        ScpSocketHandler.DownstreamResponder.sendResponse(InetAddress.getByName(DESTINATION_IP), 8085, data1);

    }
}