package com.sgs.sos.session;

import com.sgs.sos.common.AppConf;
import com.sgs.sos.common.CryptoManager;
import com.sgs.sos.common.ScpLogger;
import com.sgs.sos.io.ScpOutputHandler;
import com.sgs.sos.scp.ActionId;
import com.sgs.sos.scp.ScpConstants;
import com.sgs.sos.scp.ScpData;
import com.sgs.sos.scp.ScpMessageUnit;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Logger;

public class SessionDetails
{
    Logger scplogger = ScpLogger.getScpLogger();
    private long ssid = 0;
    private byte currentState = 0;
    private boolean encrypted = false;
    private long lastPacketIn = 0;
    private byte connectionType = 0;

    private byte[] destAddress = null;
    private byte[] srcAddress = null;
    
    private OutputStream writer = null;

    public ActionId lastActionId = ActionId.NULL_ACTION;

    public long getSsid() {
        return ssid;
    }

    public void setSsid(long ssid) {
        this.ssid = ssid;
    }

    public byte getCurrentState() {
        return currentState;
    }

    public void setCurrentState(byte currentState) {
        this.currentState = currentState;
    }


    public long getLastPacketIn() {
        return lastPacketIn;
    }

    public void setLastPacketIn(long lastPacketIn) {
        this.lastPacketIn = lastPacketIn;
    }

    public byte getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(byte connectionType) {
        this.connectionType = connectionType;
    }

    public byte[] getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(byte[] destAddress) {
        this.destAddress = destAddress.clone();
    }

    public byte[] getSrcAddress() {
        return srcAddress.clone();
    }

    public void setSrcAddress(byte[] srcAddress) {
        this.srcAddress = srcAddress.clone();
    }

    public ActionId getLastActionId() {
        return lastActionId;
    }

    public void setLastActionId(ActionId actionId) {
        this.lastActionId = actionId;
    }
    
    public void setWriter(String file)
    {
        try {
            OutputStream outputStream = new FileOutputStream("src/main/resources/"+file);
            this.writer = outputStream;
            scplogger.info("File writer active => "+ this);
        } catch (Exception e) {
            scplogger.severe(e.getMessage());
        }
    }
    
    public boolean isActiveFileSharingSession()
    {
        return (writer != null);
    }

    public void writeData(byte[] data)
    {
        if(writer == null)
        {
            scplogger.severe(" WRITER NOT INIT BUT DATA WRITE CALLED "+ this);
            return;
        }
        try {
            writer.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void closeWriter()
    {
        try {
            writer.close();
            writer = null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SessionDetails(long ssid, byte currentState, long lastPacketIn, byte connectionType, byte[] destAddress, byte[] srcAddress) {
        this.ssid = ssid;
        this.currentState = currentState;
        this.lastPacketIn = lastPacketIn;
        this.connectionType = connectionType;
        this.destAddress = destAddress.clone();
        this.srcAddress = srcAddress.clone();
        try {
            if(CryptoManager.isIPAddressInKeymap(InetAddress.getByAddress(srcAddress)))
            {
                this.encrypted = true;
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "SessionDetails{" +
                "ssid=" + ssid +
                ", currentState=" + currentState +
                ", encrypted=" + encrypted +
                ", lastPacketIn=" + lastPacketIn +
                ", connectionType=" + connectionType +
                ", destAddress=" + Arrays.toString(destAddress) +
                ", srcAddress=" + Arrays.toString(srcAddress) +
                ", lastActionId=" + lastActionId +
                '}';
    }

    public void setActiveSession() {
        this.currentState = ScpConstants.SESSION_STATE.ACTIVE;
        this.encrypted = false;
        scplogger.info(" SESSION_ACTIVE : "+ this.toString());
        sendInitAck();
    }

    private void sendInitAck()
    {
        try
        {

            ScpData scpData = new ScpData();
            if (connectionType == ScpConstants.SOCKET) {
                scpData.initData(getSrcAddress(), ScpConstants.HIGH, ScpConstants.SOCKET, getSsid());
                scpData.addMessage(new ScpMessageUnit(ScpConstants.INIT_ACK));
                if(AppConf.isAckEnabled())
                ScpOutputHandler.sendData(getSrcAddress(), scpData.getFullScpDataArray());
            }
        }
        catch (Exception e)
        {
            scplogger.severe("EXCEPTION IN SENDING ACK_INIT "+e.getLocalizedMessage());
        }
    }

    public void closeSession()
    {
        this.currentState = ScpConstants.SESSION_STATE.CLOSED;
        scplogger.info(" SESSION_CLOSED : "+ this.toString());
        try
        {
            ScpData scpData = new ScpData();
            if (connectionType == ScpConstants.SOCKET) {
                scpData.initData(getSrcAddress(), ScpConstants.HIGH, ScpConstants.SOCKET, getSsid());
                scpData.addMessage(new ScpMessageUnit(ScpConstants.BYE));
                if(AppConf.isAckEnabled())
                    ScpOutputHandler.sendData(getSrcAddress(), scpData.getFullScpDataArray());
            }
        }
        catch (Exception e)
        {
            scplogger.severe("EXCEPTION IN SENDING BYE "+e.getLocalizedMessage());
        }
    }

    public void setEncrypted(boolean ipAddressInKeymap)
    {
        this.encrypted = ipAddressInKeymap;
    }
}
