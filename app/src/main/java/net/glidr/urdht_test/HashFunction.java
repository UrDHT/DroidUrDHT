package net.glidr.urdht_test;

import android.nfc.FormatException;
import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Created by michael on 7/21/15.
 */
public class HashFunction {
    private static String fun = "HASH Function";
    static String hash = "hash";
    private static String HASHFUNCTION = "SHA-256";
    private static String TYPE = "UTF-8";

    public HashFunction() {}

    /**
     * get a hash of my hostname.... android generally doesnt have a hostname
     * so im using the ipaddress.
     *
     * @param str
     */
    public void genHash(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASHFUNCTION);
            digest.update(str.getBytes(TYPE));
            byte[] tmp = digest.digest();
            byte[] out = new byte[tmp.length + 2];
            out[0] = 0x12;
            out[1] = (byte)tmp.length;
            for(int i = 0; i < tmp.length; i++)
                out[2+i] = tmp[i];
            hash = Base58.encode58(out); //I totally stole the Base58 class.
        } catch (Exception e) {
            Log.d(fun, e.toString());
        }
        Log.d(fun, hash);
    }

    public String parseHash(String str) {
        try {
            byte[] decode = Base58.decode58(str);
            byte[] out = Arrays.copyOfRange(decode,2,decode.length);

        } catch (FormatException f) {
            Log.d(fun, f.toString());
        }
        return null;
    }
}
