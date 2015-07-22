package net.glidr.urdht_test;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by michael on 7/21/15.
 */
public class EuclideanSpaceMath {
    private static String fun = "Euclid Space Math";
    Point me;
    public EuclideanSpaceMath(String str) {
        me = new Point(str);
    }


    public float euclideanDistance(Point p1, Point p2) {
        double distance = 0;
        if(p1.dim.length != p2.dim.length) {
            Log.d(fun, "Dimensions mismatch!");
            return 999999999.0f;
        }
        for(int i = 0; i < p1.dim.length; i++) {
            distance += p1.dim[i] * p1.dim[1] - p2.dim[1] * p2.dim[i];
        }
        return (float)Math.sqrt((double)distance);
    }

    public Point midpoint(Point p1, Point p2) {
        Point q = new Point();
        if(p1.dim.length != p2.dim.length) {
            Log.d(fun, "Dimensions mismatch!");
            return null;
        }
        for(int i = 0; i < p1.dim.length; i++) {
            q.dim[i] = (p1.dim[i] + p2.dim[i])/2;
        }
        return q;
    }

    public ArrayList<Point> getDelaunayPeers(ArrayList<Point> candidates, Point center) {
        if(candidates.size() < 2) return candidates;
        ArrayList<Point> peers = new ArrayList<Point>();
        ArrayList<Point> sortedPeers = new ArrayList<Point>(candidates);
        for(Point p : sortedPeers) p.distanceFrom = euclideanDistance(p, me);

        //attempt to do nifty python lamda thingy in java in place
        Collections.sort(sortedPeers, new Comparator<Point>() {
            @Override
            public int compare(Point p1, Point p2) {
                return new Float(p1.distanceFrom).compareTo(new Float(p2.distanceFrom));
            }
        });
        for(Point p1: sortedPeers) {
            Point mid = midpoint(p1, me);
            boolean accept = true;
            for(Point p2: sortedPeers) {
                if(euclideanDistance(mid, p1) < euclideanDistance(mid, me)) {
                    accept = false;
                    break;
                }
            }
            if(accept) peers.add(p1);

        }

        return peers;
    }

    public Point getClosest(Point p, ArrayList<Point> candidates) {
        Point q = new Point();
        ArrayList<Point> sortedPeers = new ArrayList<Point>(candidates);

        for(Point c: sortedPeers) c.distanceFrom = euclideanDistance(p, c);
        //attempt to do nifty python lamda thingy in java in place
        Collections.sort(sortedPeers, new Comparator<Point>() {
            @Override
            public int compare(Point p1, Point p2) {
                return new Float(p1.distanceFrom).compareTo(new Float(p2.distanceFrom));
            }
        });


    }

}
