package com.sgs.sos;

import com.sgs.sos.common.AppConf;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;
import com.sgs.sos.scp.ScpHeader;
import com.sgs.sos.server.ServerMain;
import com.sgs.sos.test.TestMain;

import java.util.logging.Logger;

public class Main {

    public static void main(String[] args)
    {
        //init block
        AppConf.init();
        ScpLogger.init();

        ServerMain.start();

        //Test
        TestMain.test();
    }
}