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
    DataBase db;
    final Socket socket;
    final BufferedReader reader;
    final OutputStream output;
    final String service;

    public ClientThread(Socket s, String service, DataBase db) throws IOException {
        Log.d("client thread", "new client thread spawned at socket:" + s.toString());
        this.socket = s;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = socket.getOutputStream();
        this.service= service;
        this.db = db;
    }

    @Override
    public void run() {
        super.run();
        String data;
        while(!Thread.currentThread().isInterrupted()) {
            try {
                data = reader.readLine();
                if(data == null) {
                    Log.d("thread", "connection closed");
                    this.interrupt();
                    return;
                }
                Log.d("client thread", "data : " + data);

                if(data.toLowerCase().startsWith("get"))  {
                    String[] tmp = data.split(" ");
                    if(!tmp[1].startsWith("/api")) continue;
                    output.write(ParseRequest.parseRequest(service, tmp[0].toUpperCase().trim(), tmp[1].trim(), "", db).getBytes());
                    Thread.currentThread().interrupt();
                } else if(data.toLowerCase().startsWith("post")) {
                    String[] tmp = data.split(" ");
                    if(!tmp[1].startsWith("/api")) continue;
                    int contentLength = 0;
                    while(!(data = reader.readLine()).equals("")) {
                        if(data.toLowerCase().startsWith("content-length"))
                        {
                            String[] tmp2 = data.split(" ");
                            contentLength = Integer.parseInt(tmp2[1]);
                        }
                    }
                    StringBuilder sb = new StringBuilder();
                    for(int i = 0; i < contentLength; i++) {
                        sb.append((char)reader.read());
                    }
                    output.write(ParseRequest.parseRequest(service, tmp[0].toUpperCase().trim(), tmp[1].toLowerCase().trim(), sb.toString(), db).getBytes());
                    Thread.currentThread().interrupt();
                }
            } catch (IOException e) {
                Log.d("clientThread", e.toString());
                Thread.currentThread().interrupt();
            }
        }
        try {
            output.close();
            reader.close();
            socket.close();
        } catch (IOException e) {
            Log.d("clientThread", e.toString());
        }
    }


}
