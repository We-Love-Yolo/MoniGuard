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
public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.BlackListViewHolder> {
    private List<String> blackList;
    private LayoutInflater layoutInflater;
    private Context context;

    public BlackListAdapter(Context context, List<String> blackList) {
        this.context = context;
        this.blackList = blackList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public BlackListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_blacklist, parent, false);
        return new BlackListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BlackListViewHolder holder, int position) {
        String imageUrl = blackList.get(position);
        // 使用 Glide 加载图片
        Glide.with(context)
                .load(imageUrl)
                .into(holder.faceImageView);
    }

    @Override
    public int getItemCount() {
        return blackList.size();
    }

    public void addFaceImage(String imageUrl) {
        blackList.add(imageUrl);
        notifyItemInserted(blackList.size() - 1);
    }

    static class BlackListViewHolder extends RecyclerView.ViewHolder {
        ImageView faceImageView;

        public BlackListViewHolder(@NonNull View itemView) {
            super(itemView);
            faceImageView = itemView.findViewById(R.id.screenshotitem1);
        }
    }
}
