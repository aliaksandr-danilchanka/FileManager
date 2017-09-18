package myproject.spendeefilemanager.adapter.base;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import myproject.spendeefilemanager.R;
import myproject.spendeefilemanager.manager.FileManager;

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.ListItemViewHolder> {

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_files, parent, false);

        return new ListItemViewHolder(view);
    }

    protected void setIcon(File file, ListItemViewHolder holder) {

        String extension;

        try {

            extension = FileManager.getInstance().getExtension(file.getAbsolutePath());

            if (file.isFile()) {

                switch (extension) {

                    case ".c":
                    case ".cpp":
                    case ".doc":
                    case ".docx":
                    case ".exe":
                    case ".h":
                    case ".html":
                    case ".java":
                    case ".log":
                    case ".txt":
                    case ".pdf":
                    case ".ppt":
                    case ".xls":holder.mIcon.setImageResource(R.drawable.ic_file);
                        break;

                    case ".3ga":
                    case ".aac":
                    case ".mp3":
                    case ".m4a":
                    case ".ogg":
                    case ".wav":
                    case ".wma":
                        holder.mIcon.setImageResource(R.drawable.ic_audio);
                        break;

                    case ".3gp":
                    case ".avi":
                    case ".mpg":
                    case ".mpeg":
                    case ".mp4":
                    case ".mkv":
                    case ".webm":
                    case ".wmv":
                    case ".vob":
                        holder.mIcon.setImageResource(R.drawable.ic_video);
                        break;

                    case ".ai":
                    case ".bmp":
                    case ".exif":
                    case ".gif":
                    case ".jpg":
                    case ".jpeg":
                    case ".png":
                    case ".svg":
                        holder.mIcon.setImageResource(R.drawable.ic_image);
                        break;

                    case ".rar":
                    case ".zip":
                    case ".ZIP":
                        holder.mIcon.setImageResource(R.drawable.ic_compressed);
                        break;

                    default:
                        holder.mIcon.setImageResource(R.drawable.ic_error);
                        break;
                }

            } else if (file.isDirectory()) {
                holder.mIcon.setImageResource(R.drawable.ic_folder);
            } else holder.mIcon.setImageResource(R.drawable.ic_error);

        } catch (Exception e) {
            holder.mIcon.setImageResource(R.drawable.ic_error);
        }
    }

    protected static class ListItemViewHolder extends RecyclerView.ViewHolder {

        public CardView mCardView;
        public TextView mTitle;
        public TextView mLastModified;
        public ImageView mIcon;
        LinearLayout mLinearLayout;

        ListItemViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.cardView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            mLastModified = (TextView) itemView.findViewById(R.id.lastModified);
        }
    }



}
