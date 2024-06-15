package com.weloveyolo.moniguard.adapter;

import android.app.DirectAction;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.weloveyolo.moniguard.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class WhiteListAdapter extends RecyclerView.Adapter<WhiteListAdapter.WhiteListViewHolder> {
    private List<String> whiteList;
    private LayoutInflater layoutInflater;
    private Context context;

    public WhiteListAdapter(Context context, List<String> whiteList) {
        this.context = context;
        this.whiteList = whiteList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public WhiteListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_blacklist, parent, false);
        return new WhiteListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WhiteListViewHolder holder, int position) {
        String imageUrl = whiteList.get(position);
        // 使用 Glide 加载图片
        Glide.with(context)
                .load(imageUrl)
                .into(holder.faceImageView);
    }

    @Override
    public int getItemCount() {
        return whiteList.size();
    }

    public void addFaceImage(String imageUrl) {
        whiteList.add(imageUrl);
        notifyItemInserted(whiteList.size() - 1);
    }

    static class WhiteListViewHolder extends RecyclerView.ViewHolder {
        ImageView faceImageView;

        public WhiteListViewHolder(@NonNull View itemView) {
            super(itemView);
            faceImageView = itemView.findViewById(R.id.screenshotitem1);
        }
    }
}
