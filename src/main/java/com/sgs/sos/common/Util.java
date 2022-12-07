package com.sgs.sos.common;

import org.apache.commons.lang3.ArrayUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

public class Util {

    private static final String HMAC_SHA512 = "HmacSHA512";

    public static void print(Object o)
    {
       System.out.println(o);
    }

    public static String byteArrayToIpString(byte[] ip) throws UnknownHostException {
        print(Arrays.toString(ip));
        return (InetAddress.getByAddress(ip)).getHostAddress();
    }

    public static long generateSsid()
    {
        return new Random().nextLong();
    }

    public static int calculateCRC(byte[] data)
    {
        CRC32 dat = new CRC32();
        dat.update(data);
        return (int)(dat.getValue());
    }

    public static byte[] removeTrailingZeros(byte[] data)
    {
        if(data[2]==0)
        {
            int i = data.length - 1;
            while(data[i] == 0)
            {
                --i;
            }
            return Arrays.copyOfRange(data,0, i+1);
        }
        int len = data[2];
        return Arrays.copyOfRange(data,0,32+len);
    }

    public static byte[] addBytesToArray(byte[] b, byte... elem)
    {
        return ArrayUtils.addAll(b, elem);
    }

    public static byte[] addByteArrays(byte[] b, byte[] ... elem)
    {
        for(byte[] e : elem)
        {
            b = ArrayUtils.addAll(b,e);
        }
        return b;
    }

    public static int bytesToInt(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }
}
