package com.nexusgroup.personal.sdk.android.ble;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Enumeration;

public class Crypto {
    private final static String TAG = "crypto";
    PrivateKey privateKey;
    Certificate certificate;
    public Crypto(Context ctx) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream ins = ctx.getResources().openRawResource(
                    ctx.getResources().getIdentifier("mary_shaw_omni_soft_2025_pin1234",
                            "raw", ctx.getPackageName()));
            char[] pwd = "1234".toCharArray();
            keyStore.load(ins, pwd);
            Enumeration<String> aliases = keyStore.aliases();
            for (; aliases.hasMoreElements(); ) {
                String alias = aliases.nextElement();
                privateKey = (PrivateKey)keyStore.getKey(alias, pwd);
                certificate = keyStore.getCertificate(alias);
                break;
            }
        } catch (Exception e) {
            Log.println(Log.ERROR, TAG, e.getMessage());
        }
    }

    public byte[] encodedCertificate() throws Exception {
        return certificate.getEncoded();
    }

    public byte[] certificateSha256() throws Exception {
        return MessageDigest.getInstance("SHA-256").digest(encodedCertificate());
    }

    public byte[] sign(byte[] message) throws Exception {
        Signature signature = Signature.getInstance("NONEwithRSA");
        signature.initSign(privateKey);
        signature.update(message);
        byte[] digitalSignature = signature.sign();
        return digitalSignature;
    }
}
