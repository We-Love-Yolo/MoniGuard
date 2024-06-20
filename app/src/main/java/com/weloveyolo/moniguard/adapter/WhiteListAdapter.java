package com.weloveyolo.moniguard.adapter;

import android.app.DirectAction;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.activity.AlbumDetailActivity;
import com.weloveyolo.moniguard.api.Guest;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.MoniGuardApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class WhiteListAdapter extends RecyclerView.Adapter<WhiteListAdapter.WhiteListViewHolder> {
    private List<String> whiteList;
    private LayoutInflater layoutInflater;
    private Context context;
    public List<Guest> guestList;
    public List<String> sceneNameList;

    public WhiteListAdapter(Context context, List<String> whiteList) {
        this.context = context;
        this.whiteList = whiteList;
        this.layoutInflater = LayoutInflater.from(context);

        guestList = new ArrayList<>();
        sceneNameList = new ArrayList<>();
    }

    @NonNull
    @Override
    public WhiteListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_whitelist, parent, false);
        return new WhiteListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WhiteListViewHolder holder, int position) {
        String imageUrl = whiteList.get(position);
        if (holder.faceImageView != null && imageUrl != null) {
//            Glide.with(context)
//                    .load(imageUrl)
//                    .apply(RequestOptions.circleCropTransform()) // 应用
//                    .into(holder.faceImageView);

            IMoniGuardApi moniGuardApi = new MoniGuardApi();
            Handler mainHandler = new Handler(Looper.getMainLooper());
            new Thread(() -> {
                moniGuardApi.getAnalysisApi().getByteArrayWithToken((result, success) -> {
                    if (!success) return;
                    mainHandler.post(() -> {
                        Glide.with(context)
                                .load(result) // 使用从API获取的字节数组
                                .apply(RequestOptions.circleCropTransform())
                                .into(holder.faceImageView);
                    });
                }, imageUrl);
            }).start();


            holder.photoUrl = whiteList.get(position);
            holder.guest = guestList.get(position);
            holder.sceneName = sceneNameList.get(position);
        }
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

        String photoUrl;
        Guest guest;
        String sceneName;

        public WhiteListViewHolder(@NonNull View itemView) {
            super(itemView);

            faceImageView = itemView.findViewById(R.id.screenshotitem1);

            // 跳转
            Context context = itemView.getContext();
            faceImageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, AlbumDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("guest", guest);
                intent.putExtras(bundle);
                intent.putExtra("photoUrl", photoUrl);
                intent.putExtra("sceneName", sceneName);
                intent.putExtra("isWhitelisted", true);
                context.startActivity(intent);
            });
        }
    }
}
