package net.glidr.urdht_test;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by michael on 7/22/15.
 */
public class ClientThread extends Thread {
    final String ENCODING = "ISO-8859-1";

    final Socket socket;
    final BufferedReader reader;
    final OutputStream output;
    final String service;

    public ClientThread(Socket s, String service) throws IOException {
        Log.d("client thread", "new client thread spawned at socket:" + s.toString());
        this.socket = s;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = socket.getOutputStream();
        this.service= service;
    }

    @Override
    public void run() {
        super.run();
        while(!Thread.currentThread().isInterrupted()) {
            try {
                String data = reader.readLine();
                if(data == null) {
                    Log.d("thread", "connection closed");
                    Thread.currentThread().interrupt();
                    return;
                }
                Log.d("client thread", "data : " + data);

                if(data.toLowerCase().startsWith("get") || data.toLowerCase().startsWith("post")) {
                    String[] tmp = data.split(" ");
                    //mangle the strings
                    ParseRequest.parseRequest(service, tmp[0].toUpperCase().trim(), tmp[1].toLowerCase().trim());

                }

            } catch (IOException e) {
                Log.d("clientThread", e.toString());
            }
//            reader.close();
//            socket.close();
//            Thread.currentThread().interrupt();
//            return;
       }
    }


}
