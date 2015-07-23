package net.glidr.urdht_test;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by michael on 7/22/15.
 */
public abstract class NetLogic {
    private static String str = "NetLogic";


    public static boolean ping(String addr, int port) {
        Log.d(str, addr + " " + port);
        try {
            Socket socket = new Socket(addr, port);
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            out.println("GET /api/v0/peer/ping");
            out.println();
            out.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String input;
            int count = 0;
            while((input = reader.readLine()) != null) {
                if(input.contains("PONG")) count++;
            }
            reader.close();
            out.close();
            socket.close();
            if(count > 0) return true;
        } catch (IOException e) {
            Log.d(str, e.toString());
        }
        return false;
    }

    public static String getPeers(String addr, int port) {
        try {
            Socket socket = new Socket(addr, port);
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            out.println("GET /api/v0/peer/getPeers");
            out.println();
            out.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            out.close();
            socket.close();
            return sb.toString();
        } catch (IOException e) {
            Log.d(str, e.toString());
        }
        return null;
    }
}
