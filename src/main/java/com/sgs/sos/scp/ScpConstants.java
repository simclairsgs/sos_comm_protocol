package com.sgs.sos.scp;

public class ScpConstants {
    //MessageType
    public static final byte INIT_CONN = 0x1;
    public static final byte TERMINATE_CONN = 0x0;
    public static final byte INIT_ACK = 0x2;
    public static final byte SRC_KEY = 0x3;
    public static final byte BYE = 0x4;
    public static final byte APP_DATA = 0x5;
    public static final byte SINGLE_SHOT = 0x6;
    public static final byte SCP_PDU = 0x50;

    // CONN-TYPE
    public static final byte SOCKET = 0x01;
    public static final byte HTTP = 0x2;

    // PRIORITY
    public static final byte HIGH = 0x01;
    public static final byte LOW = 0x00;
}
