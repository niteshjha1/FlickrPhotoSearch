package com.niteshjha.search_image;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context mContext;
    private List<PhotoModel> mList;

    // Constructor to initialize the adapter with a list of PhotoModel objects
    public PhotoAdapter(Context mContext, List<PhotoModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    // ViewHolder class to hold the view items
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.gallery_item);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item and return a ViewHolder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the ViewHolder's views based on the item's position
        PhotoModel item = mList.get(position);

        // Load image using Glide library
        Glide.with(mContext)
                .load(item.getUrl())
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the list
        return mList.size();
    }

    // Method to add a new list of items to the existing list
    public void addAll(List<PhotoModel> newList) {
        mList.addAll(newList);
        notifyDataSetChanged();
    }

    // Method to get the list of items
    public List<PhotoModel> getList() {
        return mList;
    }
}
