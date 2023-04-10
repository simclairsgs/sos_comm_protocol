package com.sgs.sos.test;

import com.sgs.sos.common.AppConf;
import com.sgs.sos.common.CryptoManager;
import com.sgs.sos.common.Util;
import com.sgs.sos.scp.ScpConstants;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.scp.ScpData;
import com.sgs.sos.scp.ScpDataUnit;
import com.sgs.sos.scp.ScpMessageUnit;
import com.sgs.sos.server.ScpSocketHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.logging.Logger;

public class TestMain {
    public static String DESTINATION_IP = "192.168.59.1";
    public static InetAddress address = new InetSocketAddress(DESTINATION_IP, AppConf.getUsServerPort()).getAddress();
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
            long ssid = Util.generateSsid();
            testActive(ssid);
            sgsWait(3000);
            testMessage(ssid);
            sgsWait(5000);
            testMessage(ssid);
            sgsWait(5000);
            testClose(ssid);
            
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void sgsWait(int i) throws Exception
    {
        Thread.sleep(1000);
        scplogger.config("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    public static void testActive(long ssid)
    {
        ScpData scpData1 = new ScpData();
        scpData1.initData(DESTINATION_IP, ScpConstants.LOW, ScpConstants.SOCKET, ssid);
        ScpMessageUnit mu = new ScpMessageUnit(ScpConstants.INIT_CONN);
        mu.setMessage("Test".getBytes());
        scpData1.addMessage(mu);
        byte[] data = scpData1.getFullScpDataArray();
        ScpSocketHandler.DownstreamResponder.sendResponse(address, 8085, data);
    }

    public static void testClose(long ssid)
    {
        ScpData scpData1 = new ScpData();
        scpData1.initData(DESTINATION_IP, ScpConstants.LOW, ScpConstants.SOCKET, ssid);
        ScpMessageUnit mu = new ScpMessageUnit(ScpConstants.TERMINATE_CONN);
        mu.setMessage("Test".getBytes());
        scpData1.addMessage(mu);
        byte[] data = scpData1.getFullScpDataArray();
        ScpSocketHandler.DownstreamResponder.sendResponse(address, 8085, data);
    }

    public static void testEncrypedClose(long ssid)
    {
        ScpData scpData1 = new ScpData();
        scpData1.initData(DESTINATION_IP, ScpConstants.LOW, ScpConstants.SOCKET, ssid);
        ScpMessageUnit mu = new ScpMessageUnit(ScpConstants.TERMINATE_CONN);
        mu.setMessage("Test".getBytes());
        scpData1.addMessage(mu);
        byte[] data = scpData1.getFullScpDataArray();
        ScpSocketHandler.DownstreamResponder.sendResponse(address, 8085, CryptoManager.encrypt(DESTINATION_IP, data));
    }

    public static void testSrcKey(long ssid)
    {
        ScpData scpData1 = new ScpData();
        scpData1.initData(DESTINATION_IP, ScpConstants.LOW, ScpConstants.SOCKET, ssid);
        byte[] data = scpData1.getFullScpDataArray(true, CryptoManager.getPublicKey().getEncoded(), ScpConstants.SRC_KEY);
        ScpSocketHandler.DownstreamResponder.sendResponse(address, 8085, data);
    }
    
    public static void testMessage(long ssid)
    {
        ScpData scpData1 = new ScpData();
        scpData1.initData(DESTINATION_IP, ScpConstants.LOW, ScpConstants.SOCKET, ssid);
        ScpMessageUnit mu = new ScpMessageUnit(ScpConstants.APP_DATA);
        mu.setMessage("This is a test".getBytes());
        scpData1.addMessage(mu);
        byte[] data = scpData1.getFullScpDataArray();
        ScpSocketHandler.DownstreamResponder.sendResponse(address, 8085, data);
    }

    public static void testPDU(long ssid) throws Exception {
        ScpData scpData = new ScpData();
        scpData.initData(DESTINATION_IP, ScpConstants.HIGH, ScpConstants.SOCKET, ssid);
        byte[] data = scpData.getFullScpDataArray(true, "Test data".getBytes(), (byte) 9);
        scplogger.info("PREP " + (scpData.toString()));
        scplogger.info("PREP " + Arrays.toString(Arrays.copyOfRange(data,32,data.length)));
        ScpSocketHandler.DownstreamResponder.sendResponse(InetAddress.getByName(DESTINATION_IP), 8085, data);
    }

    public static void testHTTPWithPDU() throws Exception {
        ScpData scpData = new ScpData();
        scpData.initData(DESTINATION_IP, ScpConstants.HIGH, ScpConstants.HTTP, Util.generateSsid());
        byte[] data = scpData.getFullScpDataArray(true, CryptoManager.getPublicKey().getEncoded(), (byte) 2);
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
        mu = new ScpMessageUnit(ScpConstants.APP_DATA);
        mu.setMessage("App data test".getBytes());
        scpData1.addMessage(mu);
        byte[] data1 = scpData1.getFullScpDataArray();
        scplogger.info("PREP " + (scpData1.toString()));
        scplogger.info("PREP " + Arrays.toString(Arrays.copyOfRange(data1,32,data1.length)));
        ScpSocketHandler.DownstreamResponder.sendResponse(InetAddress.getByName(DESTINATION_IP), 8085, data1);

    }
}