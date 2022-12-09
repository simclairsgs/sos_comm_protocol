package com.sgs.sos.scp;

public class ScpConstants {
    //MessageType
    public static final byte TERMINATE_CONN = 0x0;
    public static final byte INIT_CONN = 0x1;
    public static final byte INIT_ACK = 0x2;
    public static final byte SRC_KEY = 0x3;
    public static final byte KEY_ACK = 0x4;
    public static final byte BYE = 0x5;
    public static final byte APP_DATA = 0x6;
    public static final byte SINGLE_SHOT = 0x7;
    public static final byte REQ_DATA = 0x08;
    public static final byte REQ_CON = 0x09;


    public static final byte SCP_PDU = 0x50;

    // CONN-TYPE
    public static final byte SOCKET = 0x01;
    public static final byte HTTP = 0x2;

    // PRIORITY
    public static final byte HIGH = 0x01;
    public static final byte LOW = 0x00;

    public class SESSION_STATE
    {
        //Session-State
        public static final byte NEW = 0x00;
        public static final byte ACTIVE = 0x01;
        public static final byte WAITING = 0x02;
        public static final byte CLOSED = 0x03;
    }

}
