package net.glidr.urdht_test;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Enumeration;


/**
 * Created by michael on 7/19/15.
 *
 * nat traveral is relatively simple, but also requires firewalls to be set up correctly
 * so I am not doing this right now.
 *
 */
public class GetMyIpAddress {
    private static String addr = "Android UrDHT GET_IP";
    static String inetAddr = "0.0.0.0";
    static String publicIP = "0.0.0.0";
    static String bindPort = "8001";
    static String wsBindPort = "8002";
    static String hostname = "NONE";
    static boolean pub;

    /***
     *
     * @return a string with an ipv4 address. private only.
     */
    private String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while(ifaces.hasMoreElements()) {
                NetworkInterface en = ifaces.nextElement();
                Enumeration<InetAddress> ipAddr = en.getInetAddresses();
                while(ipAddr.hasMoreElements()) {
                    InetAddress i = ipAddr.nextElement();
                    if(!i.isLoopbackAddress()) {
                        if(InetAddressUtils.isIPv4Address(i.getHostAddress().toString())) {
                            Log.d(addr, "hostname " + i.getCanonicalHostName());
                            Log.d(addr, i.getHostAddress().toString());
                            hostname = i.getCanonicalHostName();
                            return i.getHostAddress().toString();
                        }
                    }
                }
            }

        } catch (SocketException e) {
            Log.d(addr, e.toString());
        }
        return null;
    }

    /***
     * update the objects string
     */
    public void updateIpAddress() {
        inetAddr = getLocalIpAddress();
        pub = isPublic(inetAddr);
    }

    /***
     * constructor, populate string
     */
    public GetMyIpAddress() {
    }

    /***
     * check if this is a public or private ip address
     * @return true or false
     */
    private boolean isPublic(String s) {
        String[] arr = s.split("\\.");

        int A = Integer.parseInt(arr[0]);
        int B = Integer.parseInt(arr[1]);
        //never public
        if(A == 10) { return false; }

        //might not be public
        else if(A == 172) {
            if(B >= 16 && B <= 32) { return false; }
        }
        else if(A == 192) {
            if(B == 68) { return false; }
        }
        return true;
    }

    public String getGateway() {
        Log.d(addr, "Getting public ip");
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://4.ifcfg.me/ip");
            HttpResponse res = client.execute(get);
            HttpEntity entity = res.getEntity();
            if(entity != null) {
                publicIP = EntityUtils.toString(entity);
            } else {
                publicIP = "Null String";
            }
        } catch (Exception e) {
            Log.d(addr, e.toString());
        }
        Log.d(addr, publicIP);
        return publicIP;
    }

    public boolean isOnline() {
        try{
            Socket s = new Socket();
            //this is one of google's round robin ips
            SocketAddress sa = new InetSocketAddress("173.194.219.106", 80);
            s.connect(sa, 5000);
            s.close();
            return true;
        } catch (Exception e) {
            Log.d("Not Online!", e.toString());
            return false;
        }
    }

}
