package net.glidr.urdht_test;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 7/22/15.
 */
public abstract class ParseRequest {
    private static final HashMap<String, String> peerMap = new HashMap<>();
    private static final HashMap<String, String> clientMap  = new HashMap<>();
    private static final HashMap<String, String> wsIgnoreMap = new HashMap<>();
    static{
        final Map<String, String> pm = peerMap;
        pm.put("getpeer", "GET");
        pm.put("notify", "POST");
        pm.put("getmyip", "GET");
        pm.put("ping", "GET");
        final Map<String, String> cm = clientMap;
        cm.put("seek", "GET");
        cm.put("store", "POST");
        cm.put("get", "GET");
        cm.put("post", "POST");
        cm.put("poll", "GET");
        final Map<String, String> ws = wsIgnoreMap;
        ws.put("getpeer", "ignore");
        ws.put("notify", "ignore");
        ws.put("ping", "ignore");
    }

    /**
     * uri should be all lowercase, ensure this upstream
     * method should be uppercase, ensure this upstream
     * @param service
     * @param method
     * @param uri
     */
    public static void parseRequest(String service, String method, String uri) {
        Log.d("Client:parseRequest:", service + " " + method + " " + uri);
        String[] parts = uri.split("/");
        //Log.d("Client:parseRequest:", " 0 " + parts[0] + " 1 " + parts[1] + " 2 " + parts[2]+ " 3 " + parts[3]+ " 4 " + parts[4]);
        //parts[0] is empty string... yay split.
        if(parts.length >= 5) {
            if(parts[4].equals("seek") || parts[4].equals("get") || parts[4].equals("poll")) {
                //there should be a part 4
                //part 4 should be a recordID, seek is the same for client/peer
                //get pulls something out of the db?
                if(parts.length == 6)
                    Log.d("Client:parseRequest:", "(seek/get/poll)" + parts[4] + " for " + parts[5]);

                if(parts.length == 7)
                    Log.d("Client:parseRequest:", "(poll)" + parts[4] + " for " + parts[5] + " " + parts[6]);
            }
            if(parts[4].equals("getpeers")) {
                Log.d("Client:parseRequest:", "(getPeers)" + parts[4]);

            }
            if(parts[4].equals("getmyip")) {
                Log.d("Client:parseRequest:", "(getmyip)" + parts[4]);

            }
            if(parts[4].equals("ping")) {
                Log.d("Client:parseRequest:", "(ping)" + parts[4]);

            }
            if(parts[4].equals("notify")) {
                Log.d("Client:parseRequest:", "(notify)" + parts[4]);

            }

        }
    }




}
