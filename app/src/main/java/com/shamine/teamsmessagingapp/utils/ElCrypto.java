package com.shamine.teamsmessagingapp.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

class ElCrypto {
    private static SecretKeySpec setKey(String sT, String sS, String sR)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");

        String string = "peCpC+zJ8Q4Vnyui7Pau1rItMN9OkCxIiOBKOiF3kBw";
        String builder = sS + string + sT + string + sR;

        byte[] key = sha.digest(builder.getBytes());

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_256");
        KeySpec spec = new PBEKeySpec(Arrays.toString(key).toCharArray(), string.getBytes(), 104, 256);

        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    static String crypto(String subject, String sT, String sS, String sR, boolean encryption) {
        try {
            IvParameterSpec ivParSpec = new IvParameterSpec(new byte[16]);
            SecretKeySpec spec = setKey(sT, sS, sR);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            if (encryption) {
                cipher.init(Cipher.ENCRYPT_MODE, spec, ivParSpec);
                return Base64.getEncoder().encodeToString(cipher.doFinal(subject.getBytes(StandardCharsets.UTF_8)));
            } else {
                cipher.init(Cipher.DECRYPT_MODE, spec, ivParSpec);
                return new String(cipher.doFinal(Base64.getDecoder().decode(subject)));
            }
        } catch (Exception e) {
            return (encryption ? "Encryption Error: " : "Decryption Error: ") + e.toString();
        }
    }

    static String authCrypto(String subject, String sT, boolean encryption) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");

            String myString = "vPysRPQ6wgFF5la/PlWLQxd2Fiklv+BwAbW0CVIeX7g=";

            String builder = sT + myString + sT + myString;
            byte[] key = sha.digest(builder.getBytes());

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(Arrays.toString(key).toCharArray(), myString.getBytes(), 1000, 256);
            SecretKeySpec authKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

            IvParameterSpec IVPar1 = new IvParameterSpec(new byte[16]);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            if (encryption) {
                cipher.init(Cipher.ENCRYPT_MODE, authKey, IVPar1);
                return Base64.getEncoder().encodeToString(cipher.doFinal(subject.getBytes(StandardCharsets.UTF_8)));
            } else {
                cipher.init(Cipher.DECRYPT_MODE, authKey, IVPar1);
                return new String(cipher.doFinal(Base64.getDecoder().decode(subject)));
            }
        } catch (Exception e) {
            return (encryption ? "Encryption Error: " : "Decryption Error: ") + e.toString();
        }
    }

}