 /**
 * Created by michael on 7/19/15.
 */

package net.glidr.urdht_test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UrDHTService extends Service {
    private static String str = "Android UrDHTService";
    public FileIO fIO;

    @Override
    public IBinder onBind(Intent arg0) {
        //TODO
        return null;
    }


    /***
     * called once, to create the service
     * not called directly, only called by the OS
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(str, "onCreate() called!");
    }

    /***
     * method called to start the service with options
     *
     * @param intent
     * @param flags
     * @param startId
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO
        Log.d(str, "onStartCommand() called!");
        fIO = new FileIO(this);

        return super.onStartCommand(intent, flags, startId);
    }

    /***
     * destoy service?
     */
    @Override
    public void onDestroy() {
        //TODO
        super.onDestroy();
        Log.d(str, "onDestroy() called!");
    }
}
