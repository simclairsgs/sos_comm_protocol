package com.sgs.sos.scp;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.sgs.sos.common.AppConf;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.common.Util;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
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
    private long HMAC;
    private byte scpUnitCount = 1;

    public ScpHeader(String destAddress, byte priority, byte mode) {
        try {
            InetAddress ip = InetAddress.getByName(destAddress);
            setDestAddress(ip.getAddress());
            srcAddress = (AppConf.getIpAddressAsBytes().clone());
            setPriorityMode((byte) (priority | mode));
            setTimestamp(System.currentTimeMillis());
            setSsid(new Random().nextLong());
            setHMAC(new Random().nextLong());
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
            setHMAC(new Random().nextLong());
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

    public void setHMAC(long HMAC) {
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
        head = Util.addByteArrays(head,getSrcAddress(), getDestAddress(), ssidBytes, new byte[]{getPriorityMode()},
                timestampBytes,hmacBytes, new byte[]{padding,payloadLength,scpUnitCount});
        scplogger.info(" Length header = "+ head.length);
        return head;
    }
}
