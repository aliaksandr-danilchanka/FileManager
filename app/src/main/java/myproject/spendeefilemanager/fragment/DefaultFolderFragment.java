package myproject.spendeefilemanager.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import myproject.spendeefilemanager.R;
import myproject.spendeefilemanager.adapter.DefaultFolderAdapter;
import myproject.spendeefilemanager.manager.FileManager;


/**
 * Created by Aliaksandr on 9/8/2017.
 */

public class DefaultFolderFragment extends Fragment {

    public static final String PATH_KEY = "PATH_KEY";
    public static final String DEFAULT_FOLDER_KEY = "DEFAULT_FOLDER_KEY";
    public static final String APP_PREFERENCES = "APP_PREFERENCES";

    private static File mPath;
    private LinearLayout mViewFileIsEmpty;
    private RecyclerView mRecyclerView;
    private ArrayList<File> mFilesAndFolders;
    private Toolbar mToolbar;
    private DefaultFolderAdapter mAdapter;
    private SharedPreferences mSettings;

    public static DefaultFolderFragment newInstance(String file) {

        Bundle args = new Bundle();

        args.putString(PATH_KEY, file);
        DefaultFolderFragment fragment = new DefaultFolderFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_start_folder, container, false);
        setHasOptionsMenu(true);
        mPath = new File(getArguments().getString(PATH_KEY));
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mViewFileIsEmpty = (LinearLayout) view.findViewById(R.id.view_file_is_empty);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);

        mSettings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        open(mPath);

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setHasFixedSize(true);
        }else{
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_default_folder, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.item_default_folder:
                setDefaultFolderDialog(getString(R.string.set_default_folder_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString(DEFAULT_FOLDER_KEY, mPath.getAbsolutePath());
                        editor.apply();
                        Toast.makeText(getContext(), getString(R.string.default_folder_selected), Toast.LENGTH_SHORT).show();
                    }
                });
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void open(File file) {

        if (!file.canRead()) {
            Toast.makeText(getContext(), getString(R.string.not_read_access), Toast.LENGTH_SHORT).show();
            return;
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
            openDirectory(file);
        }
    }

    public void openDirectory(File file) {
        if (mFilesAndFolders != null) {
            mFilesAndFolders.clear();
        } else {
            mFilesAndFolders = new ArrayList<>();
        }
        ArrayList<File> list = new ArrayList<>(Arrays.asList(file.listFiles()));
        if (list.size() != 0) {
            mFilesAndFolders = FileManager.getInstance().setSorted(list);
            initializeAdapter();
            showRecyclerView();
        } else {
            initializeAdapter();
            showFileIsEmptyView();
        }
    }

    private void setDefaultFolderDialog(String message, DialogInterface.OnClickListener onClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message)
                .setPositiveButton(getString(R.string.ok), onClickListener)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();

    }

    private void initializeAdapter() {
        mAdapter = new DefaultFolderAdapter(mFilesAndFolders, getContext(), mPath, new DefaultFolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                File singleItem = mFilesAndFolders.get(position);
                if (singleItem.isDirectory()) {
                    Fragment myFragment = DefaultFolderFragment.newInstance(singleItem.getAbsolutePath());
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations( R.anim.left_to_right_enter, R.anim.left_to_right_exit, R.anim.right_to_left_enter, R.anim.right_to_left_exit)
                            .replace(R.id.container_default_folder, myFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    open(singleItem);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showRecyclerView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mViewFileIsEmpty.setVisibility(View.GONE);
    }

    private void showFileIsEmptyView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mViewFileIsEmpty.setVisibility(View.VISIBLE);
    }
}
