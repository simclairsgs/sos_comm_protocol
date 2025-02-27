package com.sgs.sos.server;

import com.sgs.sos.common.AppConf;
import com.sgs.sos.common.CryptoManager;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;
import com.sgs.sos.io.ScpInputHandler;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.logging.Logger;

public class ScpSocketHandler
{
    static Logger scplogger = ScpLogger.getScpLogger();
    static UpstreamListener upstreamListener;
    static int BUFFER_SIZE = AppConf.getBufferSize();

    public static class DownstreamResponder
    {
        static int DOWNSTREAM_PORT = AppConf.getDsServerPort();
        static DatagramSocket socket;

        static void init()
        {
            try {
                socket = new DatagramSocket(DOWNSTREAM_PORT);
            } catch (SocketException e) {
               scplogger.severe(e.getMessage());
            }
            if(!socket.isClosed())
                scplogger.config(("Scp-ds: D/S responder open at port : "+DOWNSTREAM_PORT));
        }

        public static void sendResponse(InetAddress destination, int port, byte[] data) {
            try {
                socket.connect(new InetSocketAddress(destination,port));
                byte buffer[] = new byte[BUFFER_SIZE];
                System.arraycopy(data, 0, buffer, 0, data.length);
                DatagramPacket packet  = new DatagramPacket(buffer,BUFFER_SIZE);
                socket.send(packet);
                scplogger.info(("DATA_OUT -> destination="+destination.getHostAddress()+":"+port));
                scplogger.warning("BEFORE SEND = "+ data.length +" - "+ Arrays.toString(data));
                socket.disconnect();

            } catch (SocketException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static void close()
        {
            if(socket != null)
                socket.close();
        }
    }

    public static class UpstreamListener extends Thread
    {
        static int UPSTREAM_PORT = AppConf.getUsServerPort();

        public void run()
        {
            try
            {
                DatagramSocket socket = new DatagramSocket(UPSTREAM_PORT);
                byte buffer[] = new byte[BUFFER_SIZE];
                DatagramPacket packet  = new DatagramPacket(buffer,BUFFER_SIZE);
                scplogger.config(("Scp Listener started at port :"+UPSTREAM_PORT));
               boolean stayalive = true;
                while(stayalive)
                {
                    socket.receive(packet);
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    if(CryptoManager.isIPAddressInKeymap(address))
                    {
                        buffer = Arrays.copyOfRange(buffer, 0, 256);
                        scplogger.warning("AFTER -"+buffer.length+ " - "+ Arrays.toString(buffer));
                        buffer = CryptoManager.decrypt(buffer);
                    }
                    ScpInputHandler.handle(address, port, buffer);
                }
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
                scplogger.severe(" U/S Listener"+ e.getMessage());
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
