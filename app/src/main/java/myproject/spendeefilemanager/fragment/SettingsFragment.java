package myproject.spendeefilemanager.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import myproject.spendeefilemanager.R;
import myproject.spendeefilemanager.manager.FileManager;

import static myproject.spendeefilemanager.fragment.DefaultFolderFragment.APP_PREFERENCES;
import static myproject.spendeefilemanager.fragment.DefaultFolderFragment.DEFAULT_FOLDER_KEY;

/**
 * Created by Aliaksandr on 9/8/2017.
 */

public class SettingsFragment extends Fragment {

    private LinearLayout mDeaultFolderView;
    private TextView mTextPath;
    private ImageView mIconForDufaultFolder;
    private SharedPreferences mSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, null);

        mSettings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        mDeaultFolderView = (LinearLayout) view.findViewById(R.id.view_default_folder);
        mTextPath = (TextView) view.findViewById(R.id.default_folder_path);
        mIconForDufaultFolder = (ImageView) view.findViewById(R.id.icon_for_settings);

        if (mSettings.contains(DEFAULT_FOLDER_KEY)) {
            mTextPath.setText(mSettings.getString(DEFAULT_FOLDER_KEY, ""));
        } else {
            mTextPath.setText(FileManager.getInstance().getStartUrl(getActivity()));
        }
        mIconForDufaultFolder.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_for_default_folder_settings));

        mDeaultFolderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment myFragment = DefaultFolderFragment.newInstance(FileManager.getInstance().getStartUrl(getContext()));
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations( R.anim.left_to_right_enter, R.anim.left_to_right_exit, R.anim.right_to_left_enter, R.anim.right_to_left_exit)
                        .replace(R.id.container_settings, myFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
