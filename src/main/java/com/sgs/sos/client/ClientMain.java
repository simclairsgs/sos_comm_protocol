package com.sgs.sos.client;

import com.sgs.sos.common.CryptoManager;
import com.sgs.sos.common.Util;
import com.sgs.sos.io.ScpOutputHandler;
import com.sgs.sos.scp.ScpConstants;
import com.sgs.sos.scp.ScpData;
import com.sgs.sos.scp.ScpDataUnit;
import com.sgs.sos.scp.ScpMessageUnit;
import com.sgs.sos.server.ScpSocketHandler;
import com.sgs.sos.session.SessionManager;
import sun.reflect.generics.tree.ArrayTypeSignature;

import java.util.Arrays;

import static com.sgs.sos.test.TestMain.DESTINATION_IP;

public class ClientMain
{
    public static ScpData scpData;
    public static ScpMessageUnit smu;
    public static ScpDataUnit pdu;
    public static void main() {
        try
        {
            long ssid = Util.generateSsid();
            scpData = new ScpData();
            scpData.initData(DESTINATION_IP, ScpConstants.HIGH, ScpConstants.SOCKET, ssid);
            smu = new ScpMessageUnit(ScpConstants.INIT_CONN);
            scpData.addMessage(smu);
            ScpOutputHandler.sendData(DESTINATION_IP, scpData.getFullScpDataArray());

            Thread.sleep(2000);
            Util.print(SessionManager.getSsidMap());

            /*
            byte[] d = scpData.getFullScpDataArray();
            Util.print("BEFORE = "+ Arrays.toString(d));
            d = CryptoManager.encrypt(d);
            Util.print("IN =     "+ Arrays.toString(d) + " "+d.length);
            d = CryptoManager.decrypt(d);
            Util.print("AFTER =  "+ Arrays.toString(d));
            /**/


           /* Thread.sleep(10000);
            scpData = new ScpData();
            scpData.initData(DESTINATION_IP, ScpConstants.HIGH, ScpConstants.SOCKET, ssid);
            smu = new ScpMessageUnit(ScpConstants.APP_DATA);
            scpData.addMessage(smu);
            ScpOutputHandler.sendData(DESTINATION_IP, CryptoManager.encrypt(DESTINATION_IP, scpData.getFullScpDataArray()));

            Thread.sleep(1000);
            Util.print(SessionManager.getSsidMap());*/
        }
        catch (Exception e)
        {
            Util.print("CLIENT EXCEPTION "+ e.getLocalizedMessage());
        }
    }
}
