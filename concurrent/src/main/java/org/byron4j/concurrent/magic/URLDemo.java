package org.byron4j.concurrent.magic;

import java.net.URL;
import java.util.HashSet;

/**
 * Java魔法之java.net.URL
 */
public class URLDemo {
    public static void main(String[] args) throws Exception {
        HashSet set = new HashSet();
        set.add(new URL("http://google.com"));
        System.out.println(set.contains(new URL("http://google.com")));
        Thread.sleep(60000);
        System.out.println(set.contains(new URL("http://google.com")));
    }
}
