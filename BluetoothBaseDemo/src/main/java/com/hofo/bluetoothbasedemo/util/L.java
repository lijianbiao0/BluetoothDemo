package com.hofo.bluetoothbasedemo.util;


import android.text.TextUtils;
import android.util.Log;

public class L {
    private static boolean sDebug = true;
    private static String sTag = "AppDebug";

    public static void init(boolean debug, String tag){
        L.sDebug = debug;
        L.sTag = getFinalTag(tag);
    }

    public static void e( String msg) {
        if (!sDebug) return;
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        String s = targetStackTraceElement.toString();
        Log.e(sTag,  s.substring(s.indexOf("("))+"[" +msg+"]");
    }



    private static String getFinalTag(String tag){
        if (!TextUtils.isEmpty(tag)){
            return tag;
        }
        return sTag;
    }
    private static StackTraceElement getTargetStackTraceElement() {
        StackTraceElement targetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(L.class.getName());
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTrace;
    }
}
