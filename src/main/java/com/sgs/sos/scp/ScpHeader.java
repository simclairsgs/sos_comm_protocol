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
import java.util.logging.Logger;

public class ScpHeader implements Serializable {

    static Logger scplogger = ScpLogger.getScpLogger();
    private byte reserved = 0x3;
    private byte[] destAddress;
    private byte[] srcAddress;
    private byte payloadLength = 0;
    private long timestamp;
    private long ssid;
    private byte padding = 0;
    private byte priorityMode;
    private int HMAC;
    private byte scpUnitCount = 0;

    private byte[] header;

    public ScpHeader(String destAddress, byte priority, byte mode) {
        try {
            InetAddress ip = InetAddress.getByName(destAddress);
            setDestAddress(ip.getAddress());
            srcAddress = (AppConf.getIpAddressAsBytes().clone());
            setPriorityMode((byte) (priority | mode));
            setTimestamp(System.currentTimeMillis());
            setSsid(Util.generateSsid());
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
            setSsid(Util.generateSsid());
        } catch (Exception e) {
            scplogger.severe(e.getLocalizedMessage());
        }
    }

    public ScpHeader() {

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
        this.destAddress = destAddress.clone();
    }

    public byte[] getSrcAddress() {
        return srcAddress;
    }

    public void setSrcAddress(byte[] srcAddress) {
        this.srcAddress = srcAddress.clone();
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


    public void setHeader(byte[] header) {
        this.header = header;
    }

    public byte[] getHeader(boolean isObj)
    {
        return this.header;
    }

    public void incrementScpUnitCount()
    {
        this.scpUnitCount++;
    }

    @Override
    public String toString() {
        return "ScpHeader{" +
                "reserved=" + reserved +
                ", destAddress=" + Arrays.toString(destAddress) +
                ", srcAddress=" + Arrays.toString(srcAddress) +
                ", payloadLength=" + payloadLength +
                ", timestamp=" + timestamp +
                ", ssid=" + ssid +
                ", padding=" + padding +
                ", priorityMode=" + priorityMode +
                ", HMAC=" + HMAC +
                ", scpUnitCount=" + scpUnitCount +
                ", header=" + Arrays.toString(header) +
                '}';
    }

    public byte[] getHeader()
    {
        byte[] head = new byte[1];
        head[0] = getReserved();
        byte[] ssidBytes = Longs.toByteArray(getSsid());
        byte[] timestampBytes = Longs.toByteArray(getTimestamp());
        byte[] hmacBytes = Ints.toByteArray((int) getHMAC());
        head = Util.addByteArrays(head, new byte[]{getPriorityMode(),payloadLength, (byte)(padding | scpUnitCount)},
                hmacBytes, getSrcAddress(), getDestAddress(), timestampBytes, ssidBytes);
        setHeader(head);
        return head;
    }

    public static ScpHeader parseScpHeader(byte[] data)
    {
        try
        {
            ScpHeader header = new ScpHeader();
            header.setHeader(data);
            header.setSrcAddress(Arrays.copyOfRange(data, 8, 12));
            header.setDestAddress(Arrays.copyOfRange(data, 12, 16));
            header.setHMAC(Util.bytesToInt(Arrays.copyOfRange(data, 4, 8)));
            header.setPriorityMode(data[1]);
            header.setReserved(data[0]);
            header.setPayloadLength(data[2]);
            header.setPadding((byte)((data[3] & 0xF0)>>4));
            header.setScpUnitCount((byte)(data[3] & 0xF));
            header.setTimestamp(Util.bytesToLong(Arrays.copyOfRange(data,16,24)));
            header.setSsid(Util.bytesToLong(Arrays.copyOfRange(data,24,32)));
            return header;
        }
        catch (Exception e)
        {
            scplogger.severe(" Exception in parsing ScpHeader "+e.getStackTrace());
        }
        return null;
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
