package com.sgs.sos.scp;


import java.io.Serializable;

public class ScpData implements Serializable {
    public ScpHeader header;

    public ScpData()
    {
        //header = new ScpHeader("182.168.0.0", "182.168.0.1", System.currentTimeMillis(), 8679464887684l, (byte)0x50);
    }

    public ScpData(String s, String s1, long currentTimeMillis, long l, byte b) {
       // header = new ScpHeader(s, s1, currentTimeMillis, l, b);
    }

    public void initData()
    {
        header.setHMAC(45484518);
    }
    public ScpData getScpData()
    {
        return this;
    }
}
