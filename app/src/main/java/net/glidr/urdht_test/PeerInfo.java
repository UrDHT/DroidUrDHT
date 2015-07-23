package net.glidr.urdht_test;

/**
 * Created by michael on 7/22/15.
 */
public class PeerInfo {
    public String id = null;
    public String addr = null;
    public String wsAddr = null;

    public PeerInfo(String id, String addr, String wsAddr) {
        this.id = id;
        this.addr = addr;
        this.wsAddr = wsAddr;
    }

}
