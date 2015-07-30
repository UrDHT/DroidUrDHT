package net.glidr.urdht_test;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by michael on 7/22/15.
 */
public class DHTLogic {
    private static String str = "DHTLogic";
    private static DataBase db;
    public DHTLogic(DataBase db) {
        this.db = db;
    }

    public static void runLogic() {
        Log.d(str, "Running DHT Logic");
        if(db.systemBootstrapped == false) {
            bootstrapSystem();
        } else {
            upkeepSystem(); //this is a hack for demo
            notifyOther();
        }

        for(String k:db.shortPeers.keySet()) {
            Log.d(str, k + " " + db.shortPeers.get(k)[0] + " " + db.shortPeers.get(k)[1]);
        }

    }

    /**
     * initial setup.
     * ping the bootstrap machine
     * get the bootstrap peers from the bootstrap machine
     * add the bootstrap peers to the shortpeers list
     *
     */
    public static void bootstrapSystem() {
        String tmp = db.bootstrap.addr.replace("/", "");
        String[] parts = tmp.split(":");
        if(!NetLogic.ping(parts[1], Integer.parseInt(parts[2]))) return;

        String bootStrap = NetLogic.getPeers(parts[1], Integer.parseInt(parts[2]));
        if(bootStrap == null) return;

        try {
            JSONArray jsonArray = new JSONArray(bootStrap);
            for(int i = 0; i < jsonArray.length(); i++) {
                try{
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String hashID = jsonObject.getString("id");
                    String addr = jsonObject.getString("addr");
                    String wsAddr = jsonObject.getString("wsAddr");
                    Log.d(str, hashID + " " + addr + " " + wsAddr);
                    //bootstrap to shortPeers
                    if(!db.shortPeersWriteLock) { //need to fix this so we spinlock eventually
                        db.shortPeersWriteLock = true;
                        db.insertPeerToPeerList(db.shortPeers, hashID, addr, wsAddr);
                        db.systemBootstrapped = true;
                        db.shortPeersWriteLock = false;
                    }
                } catch (JSONException j) {
                    Log.d(str, j.toString());
                }
            }
        } catch (JSONException j) {
            Log.d(str, j.toString());
        }
    }

    /**
     * SORCERY!
     *
     * do maths on shortpeers/longpeers
     * juggle peers from list to list as needed
     *
     * drop peers if necessary
     *
     * add new peers
     *
     */
    public static void upkeepSystem() {
        String[] parts = getRandomPeer();
        if(!NetLogic.ping(parts[1], Integer.parseInt(parts[2]))) return;

        String bootStrap = NetLogic.getPeers(parts[1], Integer.parseInt(parts[2]));
        if(bootStrap == null) return;

        try {
            JSONArray jsonArray = new JSONArray(bootStrap);
            for(int i = 0; i < jsonArray.length(); i++) {
                try{
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String hashID = jsonObject.getString("id");
                    String addr = jsonObject.getString("addr");
                    String wsAddr = jsonObject.getString("wsAddr");
                    //bootstrap to shortPeers
                    if(!db.shortPeersWriteLock) { //need to fix this so we spinlock eventually
                        db.shortPeersWriteLock = true;
                        db.insertPeerToPeerList(db.shortPeers, hashID, addr, wsAddr);
                        db.systemBootstrapped = true;
                        db.shortPeersWriteLock = false;
                    }
                } catch (JSONException j) {
                    Log.d(str, j.toString());
                }
            }
        } catch (JSONException j) {
            Log.d(str, j.toString());
        }

    }

    public static void notifyOther() {
        String[] parts = getRandomPeer();
        //Log.d(str, "Notify Other! " + parts[1]);
        Log.d(str, "Notify Other! 131.96.253.76");
        String url = "api/v0/peer/notify";

        String bootStrap = NetLogic.getPeers(parts[1], Integer.parseInt(parts[2]));
        if(bootStrap == null) return;

        try {
            Socket socket = new Socket("131.96.253.76", 8001);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            writer.write("POST "+ url +" HTTP/1.0rn");
            writer.write("Content-Length: "+db.self.json.length()+"rn");
            writer.write("Content-Type: application/x-www-form-urlencodedrn");
            writer.write("rn");
            writer.write(db.self.json);
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                Log.d(str, line);
            }
            writer.close();
            reader.close();
            socket.close();
        } catch (IOException e) {
            Log.d(str, e.toString());
        }
    }

    public static String[] getRandomPeer() {
        Random r = new Random();
        List<String> keys = new ArrayList<String>(db.shortPeers.keySet());
        String randKey = keys.get(r.nextInt(keys.size()));
        String[] arr = db.shortPeers.get(randKey);
        String tmp = arr[0].replace("/", "");
        String[] parts = tmp.split(":");
        return parts;
    }


}
