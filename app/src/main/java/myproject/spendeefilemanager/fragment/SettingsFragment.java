package myproject.spendeefilemanager.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import myproject.spendeefilemanager.R;
import myproject.spendeefilemanager.manager.FileManager;

/**
 * Created by Aliaksandr on 9/8/2017.
 */

public class SettingsFragment extends Fragment {

    private Button mDeaultFolderButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, null);

        mDeaultFolderButton = (Button) view.findViewById(R.id.button_default_folder);

        mDeaultFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment myFragment = DefaultFolderFragment.newInstance(FileManager.getInstance().getStartUrl(getContext()));
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_settings, myFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
