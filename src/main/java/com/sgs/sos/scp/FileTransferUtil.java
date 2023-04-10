package com.sgs.sos.scp;

import com.google.common.primitives.Longs;
import com.sgs.sos.common.AppConf;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;
import com.sgs.sos.server.ScpSocketHandler;
import com.sgs.sos.test.TestMain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public abstract class FileTransferUtil
{
	abstract public void sendMetaInfo(long ssid, String filename);
	abstract public void sendData(long ssid, byte[] data);
	
	long ssid = 0l;
	String filename;
	byte[] header = null;
	
	public void init(long _ssid, String filename)
	{
		ssid = _ssid;
		this.filename = filename;
		sendMetaInfo(ssid, filename);

		byte[] type = {ScpConstants.FILE_DATA};
		byte[] ss = Longs.toByteArray(ssid);
		header = Util.addByteArrays(type,ss);
	}
	
	public void startTransfer()
	{
		Path path = Paths.get("src/main/resources/"+filename);
		try {
			File file = new File(path.toUri());
			FileInputStream is = new FileInputStream(file);
			byte[] chunk = new byte[AppConf.FTR_BUFFER_SIZE];
			int chunkLen = 0;
			int cnt = 0;
			while ((chunkLen = is.read(chunk)) != -1) {
				ScpLogger.getScpLogger().info(" T "+ ++cnt +" - >" + Arrays.toString(chunk));
				Thread.sleep(20);
				sendData(ssid, Util.addByteArrays(header, chunk));
			}
			sendEndTransfer(ssid);
		} catch (FileNotFoundException fnfE) {
			// file not found, handle case
			ScpLogger.getScpLogger().severe("FILE NOT FOUND ");
		} catch (IOException ioE) {
			// problem reading, handle case
			ScpLogger.getScpLogger().severe("FILE IO EXCEPTION ");
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void sendEndTransfer(long ssid)
	{
		ScpData scpData1 = new ScpData();
		scpData1.initData(TestMain.DESTINATION_IP, ScpConstants.LOW, ScpConstants.SOCKET, ssid);
		ScpMessageUnit mu = new ScpMessageUnit(ScpConstants.F_CLOSE);
		mu.setMessage("".getBytes());
		scpData1.addMessage(mu);
		byte[] data = scpData1.getFullScpDataArray();
		ScpSocketHandler.DownstreamResponder.sendResponse(TestMain.address, 8085, data);
	}
}
