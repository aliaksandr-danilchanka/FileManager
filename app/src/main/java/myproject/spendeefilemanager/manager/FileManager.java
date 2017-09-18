package myproject.spendeefilemanager.manager;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FileManager {

    private static FileManager sInstance;

    public String getStartUrl() {
        String temp;
        temp = Environment.getExternalStorageDirectory().toString();
        return temp;
    }

    public ArrayList<File> setSorted(ArrayList<File> files) {
        ArrayList<File> mFiles = removeHiddenFiles(files);
        Collections.sort(mFiles, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        return mFiles;
    }

    private boolean isHiddenFile(File file) {
        return file.getName().startsWith(".");
    }

    private ArrayList<File> removeHiddenFiles(ArrayList<File> files) {

        ArrayList<File> list = new ArrayList<>();

        for (File file : files) {
            if (!isHiddenFile(file))
                list.add(file);
        }

        return list;
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

    public static synchronized FileManager getInstance() {

        if (sInstance == null)
            sInstance = new FileManager();

        return sInstance;
    }
}
