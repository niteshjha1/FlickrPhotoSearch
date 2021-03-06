package com.niteshjha.search_image;

import android.content.Context;
import android.content.Intent;
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
    private static List<PhotoModel> mList;

    private PhotoModel mItem;
    public ImageView mPhoto;

    public PhotoAdapter(Context mContext, List<PhotoModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.gallery_item);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e("Item number clicked", String.valueOf(getAdapterPosition()));


                    Intent intent = new Intent(itemView.getContext(), FullImage.class);
                    intent.putExtra("Image_Url", mList.get(getAdapterPosition()).getUrl());

                    itemView.getContext().startActivity(intent);


//                    intent.putExtra("name", String.valueOf(getAdapterPosition()));
//                    context.startActivity(intent);
                }
            });
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
