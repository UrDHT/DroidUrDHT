package net.glidr.urdht_test;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            upkeepSystem();
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

    }

}
