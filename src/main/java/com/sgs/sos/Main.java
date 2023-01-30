package com.sgs.sos;

import com.sgs.sos.client.ClientMain;
import com.sgs.sos.common.AppConf;
import com.sgs.sos.common.CryptoManager;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.server.ServerMain;
import com.sgs.sos.test.FileTransferTest;
import com.sgs.sos.test.TestMain;

public class Main {

    public static void main(String[] args)
    {
        //init block
        AppConf.init();
        ScpLogger.init();
        CryptoManager.init();
        ServerMain.start();

        //Test
        //TestMain.test();
        //ClientMain.main();
        FileTransferTest.init();
    }
}