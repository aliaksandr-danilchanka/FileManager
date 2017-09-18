package myproject.spendeefilemanager.fragment.base;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import myproject.spendeefilemanager.R;
import myproject.spendeefilemanager.manager.FileManager;

/**
 * Created by Aliaksandr on 9/12/2017.
 */

public abstract class BaseFileManagerFragment extends Fragment {

    private ArrayList<File> mFilesAndFolders;

    public ArrayList<File> open(File file) {

        ArrayList<File> files = new ArrayList<>();

        if (!file.canRead()) {
            Toast.makeText(getContext(), getString(R.string.not_read_access), Toast.LENGTH_SHORT).show();
            return null;
        }

        if (file.isFile()) {

            MimeTypeMap mime = MimeTypeMap.getSingleton();
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            String mimeType = mime.getMimeTypeFromExtension(FileManager.getInstance()
                    .getExtension(file.getAbsolutePath()).substring(1));
            i.setDataAndType(Uri.fromFile(file), mimeType);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                getContext().startActivity(i);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), getString(R.string.no_handler_for_type), Toast.LENGTH_LONG).show();
            }
        } else if (file.isDirectory()) {
            files = openDirectory(file);
        }
        return files;
    }

    public ArrayList<File> openDirectory(File file) {
        if (mFilesAndFolders != null) {
            mFilesAndFolders.clear();
        } else {
            mFilesAndFolders = new ArrayList<>();
        }
        ArrayList<File> list = new ArrayList<>(Arrays.asList(file.listFiles()));
        ArrayList<File> listOfDirectory = new ArrayList<>();
        ArrayList<File> listOfFiles = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isDirectory()) {
                listOfDirectory.add(list.get(i));
            } else {
                listOfFiles.add(list.get(i));
            }
        }
        mFilesAndFolders.addAll(FileManager.getInstance().setSorted(listOfDirectory));
        mFilesAndFolders.addAll(FileManager.getInstance().setSorted(listOfFiles));
        return mFilesAndFolders;
    }


}
