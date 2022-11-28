package com.sgs.sos;

import com.sgs.sos.common.AppConf;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;
import com.sgs.sos.scp.ScpHeader;

public class Main {
    public static void main(String[] args)
    {
        //init block
        AppConf.init();
        ScpLogger.init();

        //Test
        ScpHeader header = new ScpHeader("192.168.2.11", (byte) 0x1, (byte) 0x2);
        Util.printByteArray(header.getHeader());
    }
}