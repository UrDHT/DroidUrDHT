/**
 * Created by michael on 7/19/15.
 */

package net.glidr.urdht_test;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class UrDHTWSService extends Service {
    private static String str = "Android UrDHTService";
    public static final String SERVICE_TYPE = "_http._tcp.";

    private static String SERVICE_NAME = "WS_TCP_SERVICE";
    private static int wsBindPort = 8552;
    private static String publicIP;

    NsdManager manager;
    NsdManager.RegistrationListener regListener;
    NsdManager.DiscoveryListener    discoListener;
    NsdManager.ResolveListener      resoListener;

    public IBinder onBind(Intent arg0) {
        Log.d(str, "Service Started");
        return null;
    }


    /***
     * called once, to create the service
     * not called directly, only called by the OS
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(str, "onCreate() called! Service Started");
        manager = (NsdManager) this.getSystemService(Context.NSD_SERVICE);
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
//        wsBindPort = intent.getStringExtra("wsBindPort");
//        publicIP = intent.getStringExtra("publicIP");

        Log.d(str, "onStartCommand() called!");
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


    public void initializeDiscoveryListener() {
        discoListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.d("NSD", "start discovery failed " + serviceType + " err: " + errorCode);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.d("NSD", "stop discovery failed " + serviceType + " err: " + errorCode);

            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d("NSD", "discovery started " + serviceType);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d("NSD", "discovery started " + serviceType);
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.d("NSD", "discovery Found " + serviceInfo.toString());
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.d("NSD", "discovery Lost " + serviceInfo.toString());
            }
        };
    }

    public void initResolveListener() {
        resoListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d(str, "Resolve failed " + serviceInfo + " err " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.d(str, "Resolved " + serviceInfo);
            }
        };
    }

    public void initRegistrationListener() {
        regListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Log.d("NSD", "registration failed " + nsdServiceInfo.toString());
            }
            @Override
            public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Log.d("NSD", "unregistration failed " + nsdServiceInfo.toString());
            }
            @Override
            public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
                Log.d("NSD", "service registered " + nsdServiceInfo.toString());
            }
            @Override
            public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
                Log.d("NSD", "service unregistered " + nsdServiceInfo.toString());
            }
        };
    }


    private class wsServiceThread extends Thread {
        ServerSocket socket = null;
        @Override
        public void run() {
            super.run();
            try {
                socket = new ServerSocket(0);
                socket.setReuseAddress(true);
                //socket.bind(new InetSocketAddress(wsBindPort));

            } catch (IOException e) {
                Log.d(str, e.toString());
                throw new RuntimeException(e);
            }
            int port = socket.getLocalPort();
            registerService(port);
            while(!Thread.currentThread().isInterrupted()) {
                Socket msock = null;
                try {
                    msock = socket.accept();
                    new clientThread(msock).start();
                } catch (IOException e) {
                    Log.d(str, e.toString());
                    throw new RuntimeException(e);
                }
            }
            tearDown();
        }
    }

    private class clientThread extends Thread {
        final Socket socket;
        final BufferedReader reader;
        final OutputStream output;

        public clientThread(Socket s) throws IOException {
            Log.d("client thread", "new client thread spawned at socket:" + s.toString());
            this.socket = s;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = socket.getOutputStream();
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
                } catch (IOException e) {
                    Log.d("clientThread", e.toString());
                }
            }
        }
    }

    public void registerService(int port) {
        tearDown();
        initRegistrationListener();
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(SERVICE_NAME);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port);

        manager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, regListener);
    }

    public void tearDown() {
        if(regListener != null) {
            try {
                manager.unregisterService(regListener);
            } finally {}
            regListener = null;
        }
    }
}