package com.sgs.sos.server;

import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.io.ScpInputHandler;

import java.io.IOException;
import java.net.*;
import java.util.logging.Logger;

public class ScpSocketHandler
{
    static Logger scplogger = ScpLogger.getScpLogger();
    static UpstreamListener upstreamListener;

    static class DownstreamResponder
    {
        static int DOWNSTREAM_PORT = 8086;
        static DatagramSocket socket;

        static void init()
        {
            try {
                socket = new DatagramSocket(DOWNSTREAM_PORT);
            } catch (SocketException e) {
               scplogger.severe(e.getMessage());
            }
            if(!socket.isClosed())
                scplogger.info("Scp-ds: D/S responder open at port : "+DOWNSTREAM_PORT);
        }

        static void sendResponse(InetAddress destination, int port, byte[] data) {
            try {
                socket.connect(new InetSocketAddress(destination,port));
                byte buffer[] = new byte[256];
                System.arraycopy(data, 0, buffer, 0, data.length);
                DatagramPacket packet  = new DatagramPacket(buffer,256);
                socket.send(packet);
                socket.disconnect();

            } catch (SocketException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        static void close()
        {
            if(socket != null)
                socket.close();
        }
    }

    static class UpstreamListener extends Thread
    {
        static int UPSTREAM_PORT = 8085;

        public void run()
        {
            try
            {
                DatagramSocket socket = new DatagramSocket(UPSTREAM_PORT);
                byte buffer[] = new byte[256];
                DatagramPacket packet  = new DatagramPacket(buffer,256);
                scplogger.info("Scp Listener started at port :"+UPSTREAM_PORT);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                ScpInputHandler wcpin = new ScpInputHandler(address, port, buffer);
                wcpin.process();
                wcpin = null;
                socket.close();
            } catch (Exception e) {
                scplogger.severe(e.getLocalizedMessage());
            }
        }
    }

    public static void start()
    {
        upstreamListener = new UpstreamListener();
        upstreamListener.start();
        DownstreamResponder.init();
    }

    public static void stop()
    {
        upstreamListener.stop();
    }
}
