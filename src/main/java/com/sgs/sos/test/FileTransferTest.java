package com.sgs.sos.test;

import com.sgs.sos.common.Util;
import com.sgs.sos.scp.ScpConstants;
import com.sgs.sos.scp.ScpData;
import com.sgs.sos.scp.ScpMessageUnit;
import com.sgs.sos.server.ScpSocketHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class FileTransferTest
{
	 public static void init()
	 {
		 try 
		 {
			 long ssid = Util.generateSsid();
			 TestMain.testActive(ssid);
			 Thread.sleep(2000);
			 sendMetaInfo(ssid, "Testr.jpg");
			 Thread.sleep(2000);
			 sendFile("src/main/resources/test.jpg", ssid);
			 Thread.sleep(2000);
			 TestMain.testClose(ssid);
		 }
		 catch (Exception e)
		 {
			 throw new RuntimeException(e);
		 }
	 }

	private static void sendFile(String s, long ssid)
	{
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(s);
		byte[] buffer = new byte[256];
		int bytesRead = -1;

		while (inputStream.read(buffer) != -1) {
			Util.print(Arrays.toString(buffer));
			sendData(ssid, buffer);
		}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void sendData(long ssid, byte[] bytes)
	{
		ScpData scpData1 = new ScpData();
		scpData1.initData(TestMain.DESTINATION_IP, ScpConstants.HIGH, ScpConstants.SOCKET, ssid);
		ScpMessageUnit mu = new ScpMessageUnit(ScpConstants.APP_DATA);
		mu.setMessage(bytes);
		scpData1.addMessage(mu);
		byte[] data = scpData1.getFullScpDataArray();
		ScpSocketHandler.DownstreamResponder.sendResponse(TestMain.address, 8085, data);
	}

	public static void sendMetaInfo(long ssid, String filename)
	{
		ScpData scpData1 = new ScpData();
		scpData1.initData(TestMain.DESTINATION_IP, ScpConstants.HIGH, ScpConstants.SOCKET, ssid);
		ScpMessageUnit mu = new ScpMessageUnit(ScpConstants.FILE_TRANSFER);
		mu.setMessage(filename.getBytes());
		scpData1.addMessage(mu);
		byte[] data = scpData1.getFullScpDataArray();
		ScpSocketHandler.DownstreamResponder.sendResponse(TestMain.address, 8085, data);
	}
}
