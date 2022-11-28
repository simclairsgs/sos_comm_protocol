package com.sgs.sos.common;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class AppConf {
    private static String ipAddress;
    private static byte[] ipAddressBytes;
    public static void init()
    {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80));
            ipAddress = String.valueOf(socket.getLocalAddress()).replace("/","");
            ipAddressBytes = socket.getLocalAddress().getAddress();
            socket.close();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getIpAddress()
    {
        return ipAddress;
    }
    public static byte[] getIpAddressAsBytes()
    {
        return ipAddressBytes;
    }
}
