package net.glidr.urdht_test;

import android.app.Application;
import android.util.Log;


import java.util.HashMap;

/**
 * Created by michael on 7/22/15.
 */
public class DataBase extends Application {
    private static String str = "DATABASE";
    public static GetMyIpAddress gmi = new GetMyIpAddress();
    public static HashFunction hash = new HashFunction();

    public static HashMap<String, HashMap<String, String>> messages = new HashMap<String, HashMap<String, String>>();

    public static HashMap<String, String[]> shortPeers = new HashMap<String, String[]>();
    public static boolean shortPeersWriteLock = false;

    public static HashMap<String, String[]> longPeers = new HashMap<String, String[]>();
    public static boolean longPeersWriteLock = false;

    public static HashMap<String, Byte[]> store = new HashMap<String, Byte[]>();
    public static HashMap<String, Point[]> locations = new HashMap<String, Point[]>();

    public static BootStrap bootstrap = new BootStrap();

    public static boolean systemBootstrapped = false;

    public static void insertPeerToPeerList(HashMap peerList, String hashID, String addr, String wsAddr) {
        String[] tmp = {addr, wsAddr};
        peerList.put(hashID, tmp);
    }

    public static void deletePeerFromPeerList(HashMap peerList, String hashID, String addr, String wsAddr) {
        peerList.remove(hashID);
    }

    public static void insertMessageToMessages(String hashID, float timestamp, String message) {
        String tmp = "" + timestamp;
        HashMap<String, String> tmpMap = new HashMap<String, String>();
        tmpMap.put(tmp, message);
        messages.put(hashID, tmpMap);
    }
}
