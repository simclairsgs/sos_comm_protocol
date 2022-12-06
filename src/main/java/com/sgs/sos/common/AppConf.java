package com.sgs.sos.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class AppConf {
    private static String ipAddress;
    private static byte[] ipAddressBytes;
    private static int US_SERVER_PORT = 8085;
    private static int DS_SERVER_PORT = 8086;
    private static int HTTP_SERVER_PORT = 8080;
    private static int BUFFER_SIZE = 256;

    public static int getBufferSize() {
        return BUFFER_SIZE;
    }

    public static void setBufferSize(int bufferSize) {
        BUFFER_SIZE = bufferSize;
    }

    public static int getUsServerPort() {
        return US_SERVER_PORT;
    }

    public static void setUsServerPort(int usServerPort) {
        US_SERVER_PORT = usServerPort;
    }

    public static int getDsServerPort() {
        return DS_SERVER_PORT;
    }

    public static void setDsServerPort(int dsServerPort) {
        DS_SERVER_PORT = dsServerPort;
    }

    public static int getHttpServerPort() {
        return HTTP_SERVER_PORT;
    }

    public static void setHttpServerPort(int httpServerPort) {
        HTTP_SERVER_PORT = httpServerPort;
    }

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
