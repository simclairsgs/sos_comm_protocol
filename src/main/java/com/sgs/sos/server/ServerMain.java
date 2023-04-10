package com.sgs.sos.server;

import com.sgs.sos.common.AppConf;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;

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
        public static int SERVER_PORT = AppConf.getHttpServerPort();

        @Override
        public void run()
        {
            try {
                ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
                ServerSocket serverSocket = new ServerSocket(SERVER_PORT);				// listen to port 80800
                scplogger.config(("Web+Scp : Server Started listening on port "+ SERVER_PORT));
                scplogger.config(("------------------------- SERVER STARTED SUCCESSFULLY -------------------------------"));
                while(true) {
                    Socket socket = serverSocket.accept();						// client
                    executor.execute(new HttpResourceServerThread(socket));			// serve client
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

        //WCP Sockets
        ScpSocketHandler.start();
    }

    public static void stop()
    {
        serverThread.stop();
    }
}
