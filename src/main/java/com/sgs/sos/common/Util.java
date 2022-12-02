package com.sgs.sos.common;

import org.apache.commons.lang3.ArrayUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

public class Util {

    public static void print(Object o)
    {
       System.out.println(o);
    }

    public static String byteArrayToIpString(byte[] ip) throws UnknownHostException {
        print(Arrays.toString(ip));
        return (InetAddress.getByAddress(ip)).getHostAddress();
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

    public static int formIntFromBytes(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }
}
