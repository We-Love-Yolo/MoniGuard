package com.weloveyolo.moniguard.adapter;

import android.app.DirectAction;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import com.weloveyolo.moniguard.api.Scene;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.BlackListViewHolder> {
    private List<String> blackList;
    private LayoutInflater layoutInflater;
    private Context context;
    public List<CheckBox> checkBoxes;
    public List<Guest> guestList;
    public List<String> sceneNameList;

    public BlackListAdapter(Context context, List<String> blackList) {
        this.context = context;
        this.blackList = blackList;
        this.layoutInflater = LayoutInflater.from(context);

        checkBoxes = new ArrayList<>();
        guestList = new ArrayList<>();
        sceneNameList = new ArrayList<>();
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
//        Glide.with(context)
//                .load(imageUrl)
//                .apply(RequestOptions.circleCropTransform())
//                .into(holder.faceImageView);
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


        checkBoxes.add(position, holder.checkBox);
        holder.photoUrl = blackList.get(position);
        holder.guest = guestList.get(position);
        holder.sceneName = sceneNameList.get(position);

    }

    @Override
    public int getItemCount() {
        return blackList.size();
    }

    public void addFaceImage(String imageUrl) {
        blackList.add(imageUrl);
        notifyItemInserted(blackList.size() - 1);
    }

    public String removeFaceImage(int index) {
        String temp = blackList.get(index);
        blackList.remove(index);
        notifyItemRemoved(index);
        return temp;
    }


    static class BlackListViewHolder extends RecyclerView.ViewHolder {
        ImageView faceImageView;
        CheckBox checkBox;
        String photoUrl;
        Guest guest;
        String sceneName;

        public BlackListViewHolder(@NonNull View itemView) {
            super(itemView);

            faceImageView = itemView.findViewById(R.id.screenshotitem1);
            checkBox = itemView.findViewById(R.id.check_box);

            // 跳转
            Context context = itemView.getContext();
            faceImageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, AlbumDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("guest", guest);
                intent.putExtras(bundle);
                intent.putExtra("photoUrl", photoUrl);
                intent.putExtra("sceneName", sceneName);
                intent.putExtra("isWhitelisted", false);
                context.startActivity(intent);
            });
        }
    }
}
