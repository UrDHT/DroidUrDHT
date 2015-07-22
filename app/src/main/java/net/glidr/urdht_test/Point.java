package net.glidr.urdht_test;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by michael on 7/21/15.
 */
public class Point {
    public float[] dim;
    public float distanceFrom;

    public Point() {}
    public Point(float[] arr) {
        dim = new float[arr.length];
        dim = Arrays.copyOfRange(arr, 0, dim.length);
    }
    public Point(String str, int i) {
        dim = new float[i];
        for(i = 0; i < dim.length; i++)
            dim[i] = generateFloat(str);
    }

    public float generateFloat(String str) {
        Random r = new Random();
        r.setSeed(Long.parseLong(str));
        return r.nextFloat();
    }

    public String toString() {
        String out = "";
        if(dim.length > 0) {
            for(int i = 0; i < dim.length; i++) {
                out += " Point[" + i + "] = " + dim[i] + "\n";
            }
        } else {
            out = "Point Class is Empty!";
        }
        return out;
    }
}
