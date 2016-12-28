package fi.harism.curl;

import android.util.Log;

/**
 * Created by zhenian on 2016/12/13.
 */

public class L {

    public static final String TAG = "[ZN]";
    public static void e(String msg){
        Log.e(getTag(),msg);
    }

    private static String getTag() {
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[4];

        String className = stackTrace.getClassName();
        StringBuilder sb = new StringBuilder(TAG);
        sb.append(className.substring(className.lastIndexOf('.') + 1)).append(".");
        sb.append(stackTrace.getMethodName()).append("#").append(stackTrace.getLineNumber());
        return sb.toString();
    }
}
