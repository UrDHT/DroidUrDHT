package net.glidr.urdht_test;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by michael on 7/19/15.
 */

public class FileIO {
    private String str = "Android UrDHT: FILE IO";

    private String dir;
    private Context ctx;

    public FileIO(Context c) {
        ctx = c;
        dir = ctx.getFilesDir().toString();
    }

    /***
     * check if storage is writable
     * @return
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /***
     * check if storage is readable
     * @return
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    FileOutputStream getFileOutputStream(String fname) {
        try {
            return ctx.openFileOutput(fname, ctx.MODE_PRIVATE);
        } catch (FileNotFoundException f) {
            Log.d(str, f.toString());
        }
        return null;
    }

}
