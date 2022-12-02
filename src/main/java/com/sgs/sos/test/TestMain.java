package com.sgs.sos.test;

import com.sgs.sos.common.Util;
import com.sgs.sos.scp.ScpHeader;
import com.sgs.sos.server.ScpSocketHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class TestMain {
    public static void test()
    {
        ScpHeader header = new ScpHeader("127.0.0.1", (byte) 0x1, (byte) 0x2);
        byte data[] = header.getHeader();
        InetAddress address = new InetSocketAddress("127.0.0.1",8085).getAddress();
        ScpSocketHandler.DownstreamResponder.sendResponse(address, 8085, data);
    }
}
