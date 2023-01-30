package com.sgs.sos.scp;

import com.google.common.primitives.Longs;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;

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
			byte[] chunk = new byte[256];
			int chunkLen = 0;
			int cnt = 0;
			while ((chunkLen = is.read(chunk)) != -1) {
				ScpLogger.getScpLogger().info(" T "+ ++cnt +" - >" + Arrays.toString(chunk));
				sendData(ssid, Util.addByteArrays(header, chunk));
			}
		} catch (FileNotFoundException fnfE) {
			// file not found, handle case
			ScpLogger.getScpLogger().severe("FILE NOT FOUND ");
		} catch (IOException ioE) {
			// problem reading, handle case
			ScpLogger.getScpLogger().severe("FILE IO EXCEPTION ");
		}
	}
}
