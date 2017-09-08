package myproject.spendeefilemanager.manager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

/**
 * Created by Aliaksandr on 9/6/2017.
 */

public class FileManager {

    private static FileManager sInstance;

    public static synchronized FileManager getInstance() {

        if(sInstance == null)
            sInstance = new FileManager();

        return sInstance;
    }

    public String getStartUrl(Context context){
        String temp;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            temp = "/";
        else temp = Environment.getExternalStorageDirectory().toString();
        return temp;
    }

    public String getExtension(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf("."));
            if (ext.contains("%")) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.contains("/")) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }
}
