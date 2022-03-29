package com.godwin.jsonparser.common;

/**
 * Created by Godwin on 4/21/2018 12:32 PM for json.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class Logger {
    public static void i(String message) {
        System.out.println(message);
    }

    public static void d(String message) {
        System.out.println(message);
    }

    public static void e(String message) {
        System.err.println(message);
    }

    public static void e(Throwable throwable) {
        throwable.printStackTrace();
    }

}
