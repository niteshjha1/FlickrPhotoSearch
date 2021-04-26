package com.niteshjha.search_image;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import static java.lang.String.valueOf;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context mContext;
    private List<PhotoModel> mList;

    private PhotoModel mItem;
    public ImageView mPhoto;

    public PhotoAdapter(Context mContext, List<PhotoModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.gallery_item);
        }
    }

    @NonNull
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item,
                parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.ViewHolder holder, int position) {

        final PhotoModel item = mList.get(position);

        Glide.with(mContext)
                .load(item.getUrl())
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount_NITESH", valueOf(mList.size()));
        return mList.size();
    }

    public void addAll(List<PhotoModel> newList) {
        mList.addAll(newList);
    }

    public void clearList() {
        mList.clear();
    }


}
