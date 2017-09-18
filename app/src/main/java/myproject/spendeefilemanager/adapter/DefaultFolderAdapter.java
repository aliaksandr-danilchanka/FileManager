package myproject.spendeefilemanager.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import myproject.spendeefilemanager.R;
import myproject.spendeefilemanager.adapter.base.BaseAdapter;
import myproject.spendeefilemanager.manager.FileManager;

/**
 * Created by Aliaksandr on 9/8/2017.
 */

public class DefaultFolderAdapter extends BaseAdapter {

    private ArrayList<File> mFilesAndFolders;
    private OnItemClickListener mOnItemClickListener;
    private Context mContext;
    private File mBeforeFile;


    public DefaultFolderAdapter(ArrayList<File> filesAndFolders, Context context, File file, OnItemClickListener onItemClickListener) {
        this.mFilesAndFolders = filesAndFolders;
        this.mOnItemClickListener = onItemClickListener;
        if (!file.getAbsolutePath().equals(FileManager.getInstance().getStartUrl(context))) {
            File beforeFile = new File(file.getAbsolutePath()
                    .substring(0, file.getAbsolutePath().length() - file.getName().length() - 1));
            this.mBeforeFile = beforeFile;
            this.mFilesAndFolders.add(0, mBeforeFile);
        }
        this.mContext = context;
    }

    @Override
    public void onBindViewHolder(BaseAdapter.ListItemViewHolder holder, final int position) {
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
    }

    @Override
    public int getItemCount() {
        return mFilesAndFolders.size();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
}
