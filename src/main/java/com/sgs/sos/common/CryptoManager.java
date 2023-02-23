package com.sgs.sos.common;

import com.sgs.sos.common.fpe.FF3_1;

import javax.crypto.Cipher;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Hashtable;

import static com.sgs.sos.session.SessionManager.scplogger;

public class CryptoManager
{
    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    private static Hashtable<String,PublicKey> keymap = new Hashtable<String, PublicKey>();

    public static void init() {
        try
        {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            keygen.initialize(AppConf.CRYPTO_KEY_SIZE);
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

    public static boolean isIPAddressInKeymap(InetAddress address)
    {
        return keymap.containsKey(address.getHostAddress());
    }

    public static PublicKey getPublicKeyofSrc(String address)
    {
        return keymap.get(address);
    }

    public static PrivateKey getPrivateKey() {
        return privateKey;
    }

    public static PublicKey getPublicKey() {
        return publicKey;
    }

    public static void setPublicKeyOfSource(InetAddress srcAddress, byte[] publicKey) {
        X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKey);
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey pub = kf.generatePublic(ks);
            keymap.put(srcAddress.getHostName(), pub);
        } catch (Exception e) {
            scplogger.severe("EXCEPTION in setting SRC KEY"+ e.getLocalizedMessage()+ " | "+ srcAddress);
        }
    }

    public static byte[] encrypt(String ip, byte[] data)
    {
        try
        {
            PublicKey key = keymap.get(ip);
            Cipher encrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            encrypt.init(Cipher.ENCRYPT_MODE, key);
            return encrypt.doFinal(data);
        }
        catch (Exception e)
        {
            scplogger.severe("EXCEPTION IN Encryption "+ e.getLocalizedMessage());
        }
        return null;
    }

    public static byte[] decrypt(byte[] data)
    {
        try
        {
            scplogger.warning("size = "+data.length);
            Cipher decrypt=Cipher.getInstance("RSA/ECB/PKCS1Padding");
            decrypt.init(Cipher.DECRYPT_MODE, privateKey);
            return decrypt.doFinal(data);
        }
        catch (Exception e)
        {
            Util.print(e.getCause()+"\n"+Arrays.toString(e.getStackTrace()));
            scplogger.severe("EXCEPTION IN Decryption "+ e.getLocalizedMessage());
        }
        return null;
    }
}
