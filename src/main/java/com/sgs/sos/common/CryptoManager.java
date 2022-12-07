package com.sgs.sos.common;

import java.io.FileOutputStream;
import java.security.*;

public class CryptoManager
{
    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    public static void init() {
        try
        {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            keygen.initialize(1024);
            KeyPair keyPair = keygen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

            FileOutputStream out = new FileOutputStream( AppConf.getKeyLocation() + "rsa1024.key");
            out.write(privateKey.getEncoded());
            out.close();

            out = new FileOutputStream(AppConf.getKeyLocation() + "rsa1024.pub");
            out.write(publicKey.getEncoded());
            out.close();
            ScpLogger.getScpLogger().config(("crypto Key generated"));
        }
        catch (Exception e)
        {
            ScpLogger.getScpLogger().severe("Crypto Exception in keygen" + e.getMessage());
        }
    }

    public static PrivateKey getPrivateKey() {
        return privateKey;
    }

    public static PublicKey getPublicKey() {
        return publicKey;
    }
}
