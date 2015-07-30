package net.glidr.urdht_test;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 7/22/15.
 */
public abstract class ParseRequest {
    private static final String log = "Client:parseRequest:";
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
    public static String parseRequest(String service, String method, String uri, String data, DataBase db) {
        Log.d("Client:parseRequest:", service + " " + method + " " + uri);
        String[] parts = uri.split("/");
        //Log.d("Client:parseRequest:", " 0 " + parts[0] + " 1 " + parts[1] + " 2 " + parts[2]+ " 3 " + parts[3]+ " 4 " + parts[4]);
        //parts[0] is empty string... yay split.
        if(parts.length >= 5) {
            //there should be a part 4
            //part 4 should be a recordID, seek is the same for client/peer
            //get pulls something out of the db?
            if(parts.length == 6) {
                Log.d(log, "(seek/get/post/store)" + parts[4] + " for " + parts[5]);
                if(parts[4].equals("seek")) return seek(parts[5], method, parts[3], service);
                if(parts[4].equals("get")) return get(parts[5], method, parts[3], service);
                if(parts[4].equals("post"))  return post(parts[5], data, method, parts[3], service);
                if(parts[4].equals("store"))  return store(parts[5], data, method, parts[3], service);
            } else if(parts.length == 7) {
                if(parts[4].equals("poll")) return poll(parts[5], parts[6], method, parts[3], service);
                Log.d(log, "(poll)" + parts[4] + " for " + parts[5] + " " + parts[6]);
            } else if(parts[4].equals("getPeers")) {
                Log.d(log, "(getPeers)" + parts[4]);
                return getPeers(method, parts[3], service, db);
            } else if (parts[4].equals("getmyIP")) {
                Log.d(log, "(getmyIP)" + parts[4]);
                return getmyIP(method, parts[3], service, db);
            } else if (parts[4].equals("ping")) {
                Log.d(log, "(ping)" + parts[4]);
                return ping(method, parts[3], service);
            } else if(parts[4].equals("notify")) {
                Log.d(log, "(notify)" + parts[4]);
                return notify(data, method, parts[3], service);
            }
        }
        return null;
    }

    //GET METHODS

    /**
     * Search transient store for id
     *
     * @param hashID id of thing being looked for
     * @param method GET
     * @param type peer or client
     * @param service type of service being used... websocket etc.
     * @return json object of id, default to my id
     */
    public static String seek(String hashID, String method, String type, String service) {
        Log.d(log, hashID + " " + method + " " + type + " " + service);
        return null;
    }

    /**
     * Search DB object for id
     *
     * @param hashID id of thing being looked for
     * @param method GET
     * @param type client
     * @param service type of service being used... websocket etc.
     * @return json object of id, default to my id
     */
    public static String get(String hashID, String method, String type, String service) {
        Log.d(log, hashID + " " + method + " " + type + " " + service);
        return null;
    }

    /**
     * Search DB for ID, timestamp
     *
     * @param hashID of thing being looked for
     * @param timestamp of message to start from
     * @param method GET
     * @param type client
     * @param service type of service being used... websocket etc.
     * @return json list of everything for ID since timestamp
     */
    public static String poll(String hashID, String timestamp, String method, String type, String service) {
        Log.d(log, hashID + " " + timestamp + " " + method + " " + type + " " + service);
        return null;
    }

    /**
     * Ping
     *
     * @param method GET
     * @param type peer
     * @param service type of service being used... websocket etc.
     * @return pong
     */
    public static String ping(String method, String type, String service) {
        Log.d(log, "(ping)" + method + " " + type + " "+ service);
        return "\"PONG\"";
    }

    /**
     *
     * @param method GET
     * @param type peer
     * @param service type of service being used... websocket etc.
     * @return return  Public Ip Address of request
     */
    public static String getmyIP(String method, String type, String service, DataBase db) {
        Log.d(log,  method + " " + type + " " + service);
        return db.gmi.publicIP;
    }

    /**
     * Handle requests for peers list
     *
     * @param method GET
     * @param type peers
     * @param service type of service being used... websocket etc.
     * @return json object of current peers
     */
    public static String getPeers(String method, String type, String service, DataBase db) {
        Log.d(log,  method + " " + type + " " + service);
        try {
            JSONArray jsonArray = new JSONArray();
            for (String k : db.shortPeers.keySet()) {
                String id = k.toString();
                String addr = db.shortPeers.get(k)[0].toString();
                String wsAddr = db.shortPeers.get(k)[1].toString();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", id);
                jsonObject.put("addr", addr);
                jsonObject.put("wsAddr", wsAddr);
                jsonArray.put(jsonObject);
            }
            for (String k : db.longPeers.keySet()) {
                String id = k.toString();
                String addr = db.longPeers.get(k)[0].toString();
                String wsAddr = db.longPeers.get(k)[1].toString();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", id);
                jsonObject.put("addr", addr);
                jsonObject.put("wsAddr", wsAddr);
                jsonArray.put(jsonObject);
            }
            return jsonArray.toString().replaceAll("\\\\","");
        } catch (JSONException j) {
            Log.d(log, j.toString());
        }
        return "";
    }

    //POST methods

    /**
     * handle post requests for messages to be stored in the persistent db
     * message will be timestamped on insertion
     *
     * @param hashID id to store
     * @param data information to be posted
     * @param method POST
     * @param type client
     * @param service type of service being used... websocket etc.
     * @return void
     */
    public static String post(String hashID, String data, String method, String type, String service) {
        Log.d(log, hashID + " " + data + " " + method + " " + type + " " + service);
        return null;
    }

    /**
     * handle store bytes in the db
     * message will be timestamped on insertion
     *
     * @param hashID id to store
     * @param data information to be posted
     * @param method POST
     * @param type client
     * @param service type of service being used... websocket etc.
     * @return void
     */
    public static String store(String hashID, String data, String method, String type, String service) {
        Log.d(log, hashID + " " + data + " " + method + " " + type + " " + service);
        return null;
    }

    /**
     * Remote node just notified me via post that it exists
     *
     * @param data some information as json
     * @param method POST
     * @param type peer
     * @param service type of service being used... websocket etc.
     * @return void
     */
    public static String notify(String data, String method, String type, String service) {
        Log.d(log, data + " " + method + " " + type + " " + service);
        //StringBulder sb = new StringBuilder();


        return null;
    }


}
