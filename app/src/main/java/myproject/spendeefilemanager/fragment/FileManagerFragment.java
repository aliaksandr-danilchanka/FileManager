package myproject.spendeefilemanager.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import myproject.spendeefilemanager.R;
import myproject.spendeefilemanager.activity.MainActivity;
import myproject.spendeefilemanager.adapter.FileManagerAdapter;
import myproject.spendeefilemanager.manager.FileManager;
import myproject.spendeefilemanager.sparse.SparseBooleanArrayParcelable;


/**
 * Created by Aliaksandr on 9/6/2017.
 */

public class FileManagerFragment extends BaseFileManagerFragment {

    public static final String PATH_KEY = "PATH_KEY";
    public static final String SELECT_ITEM_KEY = "SELECT_ITEM_KEY";

    private static File mPath;
    private LinearLayout mViewFileIsEmpty;
    private RecyclerView mRecyclerView;
    private ArrayList<File> mFilesAndFolders;
    private Toolbar mToolbar;
    private FileManagerAdapter mAdapter;
    private ActionMode mActionModes;
    private boolean mClickAllowed;
    private SparseBooleanArray mSelectedItems;


    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater menuInflater = mode.getMenuInflater();
            menuInflater.inflate(R.menu.menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {

            switch (item.getItemId()) {

                case R.id.delete_button:
                    deleteDialog(getString(R.string.delete_dialog), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            delete(mAdapter.getSelectedItems());
                            mode.finish();
                        }
                    });
                    return true;

                default:
                    return false;
            }
        }


        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mode.finish();
            mActionModes = null;
            mAdapter.clearSelection();
            mClickAllowed = true;
        }

    };

    public static FileManagerFragment newInstance(String file) {

        Bundle args = new Bundle();

        args.putString(PATH_KEY, file);
        FileManagerFragment fragment = new FileManagerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_start_folder, container, false);

        mPath = new File(getArguments().getString(PATH_KEY));

        mClickAllowed = true;
        if (savedInstanceState != null) {
            mSelectedItems = (SparseBooleanArray) savedInstanceState.getParcelable(SELECT_ITEM_KEY);
            if (mSelectedItems.size() > 0) {
                mClickAllowed = false;
            }
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mViewFileIsEmpty = (LinearLayout) view.findViewById(R.id.view_file_is_empty);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
        mToolbar.setTitle(mPath.getName());

        mFilesAndFolders = open(mPath);
        setView(mFilesAndFolders);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setHasFixedSize(true);
        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }

        ((MainActivity) getActivity()).setFragmentRefreshListener(new MainActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh() {
                mFilesAndFolders = openDirectory(mPath);
                setView(mFilesAndFolders);
            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFilesAndFolders.size() > 0) {
            outState.putParcelable(SELECT_ITEM_KEY, new SparseBooleanArrayParcelable(mAdapter.getSelectedItemsArray()));
        }
    }

    private void setView(ArrayList<File> files) {
        if(files!=null) {
            if (files.size() > 0) {
                showRecyclerView();
            } else {
                showFileIsEmptyView();
            }
            initializeAdapter();
        }else{

        }
    }

    public void delete(ArrayList<File> files) {

        for (File file : files) {

            try {
                deleteFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(getContext(), getString(R.string.files_successfully_deleted), Toast.LENGTH_SHORT).show();
        openDirectory(mPath);
    }

    public boolean deleteFile(File file) {

        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory() && file.canWrite()) {
            ArrayList<File> subFiles = new ArrayList<>(Arrays.asList(file.listFiles()));

            for (File subFile : subFiles) {

                deleteFile(subFile);
            }
            file.delete();
        }

        return true;
    }

    private void deleteDialog(String message, DialogInterface.OnClickListener onClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(message)
                .setPositiveButton(getString(R.string.ok), onClickListener)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();

    }

    private void initializeAdapter() {
        mAdapter = new FileManagerAdapter(mFilesAndFolders, getContext(), mPath, new FileManagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                File singleItem = mFilesAndFolders.get(position);
                if (mClickAllowed) {
                    if (singleItem.isDirectory()) {
                        Fragment fragment = FileManagerFragment.newInstance(singleItem.getAbsolutePath());
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.left_to_right_enter, R.anim.left_to_right_exit, R.anim.right_to_left_enter, R.anim.right_to_left_exit)
                                .replace(R.id.container, fragment)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        open(singleItem);
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                mClickAllowed = false;

                if (!mPath.getAbsolutePath().equals(FileManager.getInstance().getStartUrl(getContext())) &&
                        position == 0) {
                } else {
                    if (mActionModes != null) {
                        mAdapter.toggleSelection(position);
                        mActionModes.setTitle(mAdapter.getSelectedItemsCount() + "  " + getString(R.string.info_items_selected));
                        if (mAdapter.getSelectedItemsCount() <= 0)
                            mActionModes.finish();
                        return;
                    }
                    mActionModes = getActivity().startActionMode(actionModeCallback);
                    mAdapter.toggleSelection(position);
                    mActionModes.setTitle(mAdapter.getSelectedItemsCount() + "  " + getString(R.string.info_items_selected));
                }
            }
        });
        if (mSelectedItems != null && mSelectedItems.size() > 0) {
            mClickAllowed = false;
            mAdapter.setSelectedItemsArray(mSelectedItems);
            mActionModes = getActivity().startActionMode(actionModeCallback);
            mActionModes.setTitle(mAdapter.getSelectedItemsCount() + "  " + getString(R.string.info_items_selected));
        }
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