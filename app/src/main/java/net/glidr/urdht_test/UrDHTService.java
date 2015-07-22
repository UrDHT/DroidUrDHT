/**
 * Created by michael on 7/19/15.
 */

package net.glidr.urdht_test;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class UrDHTService extends Service {
    private static String str = "Android Service";
    public static final String SERVICE_TYPE = "_http._tcp.";

    private static String SERVICE_NAME1 = "UrDHT_SERVICE";
    private static String SERVICE_NAME2 = "UrDHT_WS_SERVICE";
    private static int bindPort = 8551;
    private static int wsBindPort = 8552;

    private static String publicIP = "oops";
    private static String localIP = "oops";

    NsdManager manager;
    NsdManager.RegistrationListener regListener;
    NsdManager.DiscoveryListener    discoListener;
    NsdManager.ResolveListener      resoListener;

    public IBinder onBind(Intent arg0) {
        Log.d(str, "Service Started");
        return null;
    }


    /***
     * called once, to create the service1
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

        publicIP = intent.getStringExtra("publicIP");
        localIP = intent.getStringExtra("localIP");

        Log.d(str, "onStartCommand() called!");
        new serviceThread().start();
        new wsServiceThread().start();

        return super.onStartCommand(intent, flags, startId);
    }

    /***
     * destoy service
     */
    @Override
    public void onDestroy() {
        //TODO
        tearDown();
        super.onDestroy();
        Log.d(str, "onDestroy() called!");
    }

    /**
     * filling out protos
     */
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

    /**
     * filling out protos
     */
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

    /**
     * filling out protos
     */
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

    /***
     * service thread, binds to addr port and listens
     * spans client thread when needed
     */
    private class serviceThread extends Thread {
        ServerSocket socket = null;
        @Override
        public void run() {
            super.run();
            try {
                socket = new ServerSocket();
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(bindPort));

            } catch (IOException e) {
                Log.d(str, e.toString());
                throw new RuntimeException(e);
            }
            int port = socket.getLocalPort();
            registerService(port, SERVICE_NAME1);
            while(!Thread.currentThread().isInterrupted()) {
                Socket msock;
                try {
                    msock = socket.accept();
                    new ClientThread(msock, SERVICE_NAME1).start();
                } catch (IOException e) {
                    Log.d(str, e.toString());
                    throw new RuntimeException(e);
                }
            }
            tearDown();
        }
    }

    /***
     * ws service thread, binds to addr port and listens
     * spans ws client thread when needed
     */
    private class wsServiceThread extends Thread {
        ServerSocket socket = null;
        @Override
        public void run() {
            super.run();
            try {
                socket = new ServerSocket();
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(wsBindPort));

            } catch (IOException e) {
                Log.d(str, e.toString());
                throw new RuntimeException(e);
            }
            int port = socket.getLocalPort();
            registerService(port, SERVICE_NAME2);
            while(!Thread.currentThread().isInterrupted()) {
                Socket msock = null;
                try {
                    msock = socket.accept();
                    new WSClientThread(msock, SERVICE_NAME2).start();
                } catch (IOException e) {
                    Log.d(str, e.toString());
                    throw new RuntimeException(e);
                }
            }
            tearDown();
        }
    }


    /**
     * register services with the network service discovery manager
     * @param port
     * @param name
     */
    public void registerService(int port, String name) {
        tearDown();
        initRegistrationListener();
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(name);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port);

        manager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, regListener);
    }

    /**
     * cleanup
     */
    public void tearDown() {
        Log.d(str, localIP + " ==> " + publicIP);
        if(regListener != null) {
            try {
                manager.unregisterService(regListener);
            } finally {}
            regListener = null;
        }
    }
}
