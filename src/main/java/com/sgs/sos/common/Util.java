package com.sgs.sos.common;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.logging.Logger;

public class Util {

    public static void printByteArray(byte[] b)
    {
        System.out.print("[");
        for (int i = 0; i < b.length - 1; i++) {
            System.out.print(b[i] + ", ");
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
}
