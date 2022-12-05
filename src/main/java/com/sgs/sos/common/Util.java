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


    public static void printByteArray(byte[] b)
    {
        int c = 0;
        System.out.print("[");
        for (int i = 0; i < b.length - 1; i++) {
            System.out.print(b[i] + ", ");
            c++;
            if(c>=4)
            {
                c=0;
                System.out.println();
            }
        }
            System.out.println(b[b.length - 1] + "]");
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
