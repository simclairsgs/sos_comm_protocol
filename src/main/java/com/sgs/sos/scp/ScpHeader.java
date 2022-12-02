package com.sgs.sos.scp;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.sgs.sos.common.AppConf;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.logging.Logger;

public class ScpHeader implements Serializable {

    Logger scplogger = ScpLogger.getScpLogger();
    private byte reserved = 0x3;
    private byte[] destAddress;
    private byte[] srcAddress;
    private byte payloadLength = 1;
    private long timestamp;
    private long ssid;
    private byte padding = 0;
    private byte priorityMode;
    private int HMAC;
    private byte scpUnitCount = 1;

    public ScpHeader(String destAddress, byte priority, byte mode) {
        try {
            InetAddress ip = InetAddress.getByName(destAddress);
            setDestAddress(ip.getAddress());
            srcAddress = (AppConf.getIpAddressAsBytes().clone());
            setPriorityMode((byte) (priority | mode));
            setTimestamp(System.currentTimeMillis());
            setSsid(new Random().nextLong());
            setHMAC((new byte[]{12,43,56,78,-2,34,56}).hashCode());
        } catch (UnknownHostException e) {
            scplogger.severe(e.getLocalizedMessage());
        }
    }

    public ScpHeader(byte[] destAddress, byte priority, byte mode) {
        try {
            setDestAddress(destAddress);
            srcAddress = (AppConf.getIpAddressAsBytes().clone());
            setPriorityMode((byte) (priority | mode));
            setTimestamp(System.currentTimeMillis());
            setSsid(new Random().nextLong());
            setHMAC((new byte[]{12,43,56,78,-2,34,56}).hashCode());
        } catch (Exception e) {
            scplogger.severe(e.getLocalizedMessage());
        }
    }

    public byte getReserved() {
        return reserved;
    }

    public void setReserved(byte reserved) {
        this.reserved = reserved;
    }

    public byte[] getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(byte[] destAddress) {
        this.destAddress = destAddress;
    }

    public byte[] getSrcAddress() {
        return srcAddress;
    }

    public void setSrcAddress(byte[] srcAddress) {
        srcAddress = srcAddress;
    }

    public byte getPayloadLength() {
        return payloadLength;
    }

    public void setPayloadLength(byte payloadLength) {
        this.payloadLength = payloadLength;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getSsid() {
        return ssid;
    }

    public void setSsid(long ssid) {
        this.ssid = ssid;
    }

    public byte getPadding() {
        return padding;
    }

    public void setPadding(byte padding) {
        this.padding = padding;
    }

    public byte getPriorityMode() {
        return priorityMode;
    }

    public void setPriorityMode(byte priorityMode) {
        this.priorityMode = priorityMode;
    }

    public long getHMAC() {
        return HMAC;
    }

    public void setHMAC(int HMAC) {
        this.HMAC = HMAC;
    }

    public byte getScpUnitCount() {
        return scpUnitCount;
    }

    public void setScpUnitCount(byte scpUnitCount) {
        this.scpUnitCount = scpUnitCount;
    }

    public byte[] getHeader()
    {
        byte[] head = new byte[1];
        head[0] = getReserved();
        byte[] ssidBytes = Longs.toByteArray(getSsid());
        byte[] timestampBytes = Longs.toByteArray(getTimestamp());
        byte[] hmacBytes = Ints.toByteArray((int) getHMAC());
        head = Util.addByteArrays(head, new byte[]{getPriorityMode(),payloadLength, (byte)(padding | scpUnitCount)},
                hmacBytes, getSrcAddress(), getDestAddress(), ssidBytes,
                timestampBytes);
        scplogger.info(" Length header = "+ head.length);
        return head;
    }

    /*
                                            SCP HEADER FORMAT - 32 bytes

    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    | Reserved | priority | length | padding|scpCount |                   HMAC  4 bytes               |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |  source address  4 byte                         |               destination address 4 byte      |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |                                       Timestamp   8 bytes                                       |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |                                      Source Sync ID 8 bytes                                     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

    * */
}
