package myproject.spendeefilemanager.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import myproject.spendeefilemanager.R;
import myproject.spendeefilemanager.activity.DefaultFolderActivity;
import myproject.spendeefilemanager.manager.FileManager;

import static myproject.spendeefilemanager.fragment.DefaultFolderFragment.APP_PREFERENCES;
import static myproject.spendeefilemanager.fragment.DefaultFolderFragment.DEFAULT_FOLDER_KEY;

/**
 * Created by Aliaksandr on 9/8/2017.
 */

public class SettingsFragment extends Fragment {

    private RelativeLayout mDeaultFolderView;
    private TextView mTextPath;
    private SharedPreferences mSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, null);

        mSettings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        mDeaultFolderView = (RelativeLayout) view.findViewById(R.id.view_default_folder);
        mTextPath = (TextView) view.findViewById(R.id.default_folder_path);

        if (mSettings.contains(DEFAULT_FOLDER_KEY)) {
            mTextPath.setText(mSettings.getString(DEFAULT_FOLDER_KEY, ""));
        } else {
            mTextPath.setText(FileManager.getInstance().getStartUrl(getActivity()));
        }

        mDeaultFolderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DefaultFolderActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
