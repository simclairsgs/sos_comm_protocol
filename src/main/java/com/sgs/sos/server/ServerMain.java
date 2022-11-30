package com.sgs.sos.server;

import com.sgs.sos.common.ScpLogger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

public class ServerMain {

    static Logger scplogger = ScpLogger.getScpLogger();
    static ServerThread serverThread;

    static class ServerThread extends Thread
    {
        public static int SERVER_PORT = 8080;

        @Override
        public void run()
        {
            try {
                ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
                ServerSocket serverSocket = new ServerSocket(SERVER_PORT);				// listen to port 80800
                scplogger.info("Web+Scp : Server Started listening on port "+ SERVER_PORT);
                while(true) {
                    Socket socket = serverSocket.accept();						// client
                    executor.execute(new ResourceServerThread(socket));			// serve client
                }
            } catch (Exception e) {
                scplogger.severe(e.getLocalizedMessage());
            }
        }
    }

    public static void start()
    {
        serverThread  = new ServerThread();
        serverThread.start();
    }

    public static void stop()
    {
        serverThread.stop();
    }
}
