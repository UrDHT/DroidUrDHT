package net.glidr.urdht_test;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by michael on 7/22/15.
 */
public class WSClientThread extends Thread {
    final Socket socket;
    final BufferedReader reader;
    final OutputStream output;
    final String service;
    DataBase db;

    public WSClientThread(Socket s, String service, DataBase db) throws IOException {
        Log.d("client thread", "new client thread spawned at socket:" + s.toString());
        this.socket = s;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = socket.getOutputStream();
        this.service = service;
        this.db = db;
    }

    @Override
    public void run() {
        super.run();
        while(!Thread.currentThread().isInterrupted()) {
            try {
                String data = reader.readLine();
                if(data == null) {
                    Log.d("wsClient", "connection closed");
                    Thread.currentThread().interrupt();
                    return;
                }
                Log.d("wsClient thread", "data : " + data);
            } catch (IOException e) {
                Log.d("wsClientThread", e.toString());
            }
        }
    }
}
