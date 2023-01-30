package com.sgs.sos.test;

import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;
import com.sgs.sos.scp.FileTransferUtil;
import com.sgs.sos.scp.ScpConstants;
import com.sgs.sos.scp.ScpData;
import com.sgs.sos.scp.ScpMessageUnit;
import com.sgs.sos.server.ScpSocketHandler;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class HostMain extends FileTransferUtil
{
	public void init()
	{
		try
		{
			long ssid = Util.generateSsid();
			TestMain.testActive(ssid);
			Thread.sleep(5000);
			super.init(ssid, "test.jpg");
			Thread.sleep(2000);
			super.startTransfer();
			TestMain.testClose(ssid);
		}
		catch (Exception e)
		{
			ScpLogger.getScpLogger().severe("EXCEPTION IN HOST "+ e.getMessage());
		}
	}

	private static void testSendFile(long ssid)
	{
		Path path = Paths.get("src/main/resources/test.jpg");
		try {
			File file = new File(path.toUri());
			FileInputStream is = new FileInputStream(file);
			byte[] chunk = new byte[80];
			int chunkLen = 0;
			int cnt = 0;
			while ((chunkLen = is.read(chunk)) != -1) {
				ScpLogger.getScpLogger().info(" T "+ ++cnt +" - >" + Arrays.toString(chunk));
				testMessage(ssid, new String(chunk));
			}
		} catch (FileNotFoundException fnfE) {
			// file not found, handle case
		} catch (IOException ioE) {
			// problem reading, handle case
		}
	}

	public static void testMessage(long ssid, String message)
	{
		ScpData scpData1 = new ScpData();
		scpData1.initData(TestMain.DESTINATION_IP, ScpConstants.LOW, ScpConstants.SOCKET, ssid);
		ScpMessageUnit mu = new ScpMessageUnit(ScpConstants.APP_DATA);
		mu.setMessage(message.getBytes());
		scpData1.addMessage(mu);
		byte[] data = scpData1.getFullScpDataArray();
		ScpSocketHandler.DownstreamResponder.sendResponse(TestMain.address, 8085, data);
	}

	public  void sendMetaInfo(long ssid, String filename)
	{
		ScpData scpData1 = new ScpData();
		scpData1.initData(TestMain.DESTINATION_IP, ScpConstants.HIGH, ScpConstants.SOCKET, ssid);
		ScpMessageUnit mu = new ScpMessageUnit(ScpConstants.FILE_NAME_CMD);
		mu.setMessage(filename.getBytes());
		scpData1.addMessage(mu);
		byte[] data = scpData1.getFullScpDataArray();
		ScpSocketHandler.DownstreamResponder.sendResponse(TestMain.address, 8085, data);
	}

	@Override
	public void sendData(long ssid, byte[] data)
	{
		ScpSocketHandler.DownstreamResponder.sendResponse(TestMain.address, 8085, data);
	}
}
