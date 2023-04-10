package com.sgs.sos.server;

import com.sgs.sos.common.AppConf;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;
import com.sgs.sos.test.TestMain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class HttpResourceServerThread implements Runnable{
    private final Socket socket;
    private final String LOCATION = AppConf.getWebappLocation();

    HttpResourceServerThread(Socket socket){								// receiver socket from main thread
        this.socket = socket;
    }

    public void run() {

        try {
            Logger scplogger = ScpLogger.getScpLogger();
            InputStreamReader bir = new InputStreamReader(socket.getInputStream());
            BufferedReader br = new BufferedReader(bir);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            String reqData= br.readLine().split(" ")[1];
            scplogger.info((" Web Req received :"+reqData));
            String fileName= "/index.html";
            if(reqData.equals("/test"))
            {
                TestMain.testHTTPWithPDU();
                printWriter.println("HTTP/1.1 200 OK");							// set HTTP - Headers
                printWriter.println("Content-Type: text/html");
                printWriter.println("Content-Length: " + new File(LOCATION+fileName).length());
                printWriter.println("\r\n");
                printWriter.println("<html> Ok Success </html>");
                printWriter.close();
                return;
            }
            if (reqData.contains("scp"))
            {
                scplogger.warning("TEST SCP HTTP IN/IP => " + reqData.replace("/scp?data=web%2Bscp%3A",""));
                try {
                    FileReader file = new FileReader(LOCATION+fileName);
                    BufferedReader bfr = new BufferedReader(file);
                    printWriter.println("HTTP/1.1 200 OK");							// set HTTP - Headers
                    printWriter.println("Content-Type: text/html");
                    printWriter.println("Content-Length: " + new File(LOCATION+fileName).length());
                    printWriter.println("\r\n");
                    String line = bfr.readLine();
                    while (line != null)
                    {
                        printWriter.println(line);
                        line = bfr.readLine();
                    }
                    bfr.close();
                    bfr.close();
                    printWriter.close();
                }
                catch(IOException e) {
                    PrintWriter error404 = new PrintWriter(socket.getOutputStream());
                    error404.println("HTTP/1.1 404 NOT FOUND");							// set HTTP - Headers
                    error404.println("Content-Type: text/html");
                    error404.println("\r\n");
                    error404.write("<center><h2>404 NOT FOUND ERROR</h2><br>REQUESTED RESOURCE IS NOT FOUND IN THE SERVER</center>");
                    error404.close();
                }
                
                return;
            }
            if(reqData.equals("/"));
            else fileName = reqData;
            try {
                FileReader file = new FileReader(LOCATION+fileName);
                BufferedReader bfr = new BufferedReader(file);
                printWriter.println("HTTP/1.1 200 OK");							// set HTTP - Headers
                printWriter.println("Content-Type: text/html");
                printWriter.println("Content-Length: " + new File(LOCATION+fileName).length());
                printWriter.println("\r\n");
                String line = bfr.readLine();
                while (line != null)
                {
                    printWriter.println(line);
                    line = bfr.readLine();
                }
                bfr.close();
                bfr.close();
                printWriter.close();
            }
            catch(IOException e) {
                PrintWriter error404 = new PrintWriter(socket.getOutputStream());
                error404.println("HTTP/1.1 404 NOT FOUND");							// set HTTP - Headers
                error404.println("Content-Type: text/html");
                error404.println("\r\n");
                error404.write("<center><h2>404 NOT FOUND ERROR</h2><br>REQUESTED RESOURCE IS NOT FOUND IN THE SERVER</center>");
                error404.close();
            }

        }catch(Exception e) {
            PrintWriter error500 = null;
            try {
                error500 = new PrintWriter(socket.getOutputStream());
                error500.println("HTTP/1.1 500 INTERNAL SERVER ERROR");							// set HTTP - Headers
                error500.println("Content-Type: text/html");
                error500.println("\r\n");
                error500.write("<center><h2>500 INTERNAL SERVER ERROR</h2>Contact Administrator or simclair.sgs@gmail.com</center>");
                error500.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}