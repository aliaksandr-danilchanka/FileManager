package myproject.spendeefilemanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.SparseBooleanArray;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import myproject.spendeefilemanager.R;
import myproject.spendeefilemanager.adapter.base.BaseAdapter;
import myproject.spendeefilemanager.manager.FileManager;

/**
 * Created by Aliaksandr on 9/6/2017.
 */

public class FileManagerAdapter extends BaseAdapter {

    private ArrayList<File> mFilesAndFolders;
    private File mBeforeFile;
    private OnItemClickListener mOnItemClickListener;
    private Context mContext;
    private SparseBooleanArray mSelectedItems;

    public FileManagerAdapter(ArrayList<File> filesAndFolders, Context context, File file, OnItemClickListener onItemClickListener) {
        this.mFilesAndFolders = filesAndFolders;
        this.mOnItemClickListener = onItemClickListener;
        if (!file.getAbsolutePath().equals(FileManager.getInstance().getStartUrl(context))) {
            File beforeFile = new File(file.getAbsolutePath()
                    .substring(0, file.getAbsolutePath().length() - file.getName().length() - 1));
            this.mBeforeFile = beforeFile;
            this.mFilesAndFolders.add(0, mBeforeFile);
        }
        mSelectedItems = new SparseBooleanArray();
        this.mContext = context;
    }

    @Override
    public void onBindViewHolder(final ListItemViewHolder holder, final int position) {

        final File singleItem = mFilesAndFolders.get(position);

        if (singleItem == mBeforeFile) {
            holder.mTitle.setText(mContext.getString(R.string.before_package_name));
            holder.mLastModified.setText(singleItem.getAbsolutePath());
            holder.mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_back));
        } else {
            holder.mTitle.setText(singleItem.getName());
            holder.mLastModified.setText(new Date(singleItem.lastModified()).toString());
            setIcon(singleItem, holder);
        }


        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });

        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemClickListener.onItemLongClick(v, position);
                return true;
            }
        });

        if (mSelectedItems.get(position, false)) {
            holder.mCardView.setCardBackgroundColor(Color.parseColor("#6666FF"));
        } else holder.mCardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));

    }

    @Override
    public int getItemCount() {
        return mFilesAndFolders.size();
    }

    public void clearSelection() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        if (mSelectedItems.get(position, false)) {
            mSelectedItems.delete(position);
        } else {
            mSelectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    public ArrayList<File> getSelectedItems() {
        ArrayList<File> list = new ArrayList<>();

        for (int i = 0; i < mSelectedItems.size(); i++) {
            list.add(mFilesAndFolders.get(mSelectedItems.keyAt(i)));
        }
        return list;
    }

    public SparseBooleanArray getSelectedItemsArray() {
        return mSelectedItems;
    }

    public void setSelectedItemsArray(SparseBooleanArray selectedItems) {
        this.mSelectedItems = selectedItems;
    }

    public int getSelectedItemsCount() {
        return mSelectedItems.size();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
        public void onItemLongClick(View view, int position);
    }
}
