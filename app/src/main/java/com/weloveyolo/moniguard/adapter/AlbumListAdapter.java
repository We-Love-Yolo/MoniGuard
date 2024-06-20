package com.weloveyolo.moniguard.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.util.HttpClient;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.ViewHolder> {

    private List<String> mData; // 假设您的数据类型是String列表

    // 构造函数，传入数据
    public AlbumListAdapter(List<String> data) {
        this.mData = data;
    }

    // 创建新的视图（由布局管理器调用）
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 创建一个新的视图
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        view.setMinimumHeight(500);
        view.setMinimumWidth(300);
        return new ViewHolder(view);
    }

    // 替换视图的内容（由布局管理器调用）
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - 获取元素数据
        String url = mData.get(position);

        // - 替换视图内容
        Handler mainHandler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            Response response = HttpClient.get(url, null);
            try {
                byte[] bytes = response.body().bytes();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mainHandler.post(() -> holder.imageView.setImageBitmap(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 返回数据集的大小（由布局管理器调用）
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // 提供对视图的引用（自定义的ViewHolder）
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            // 定义点击事件、长按事件等
            imageView = view.findViewById(R.id.album_item);
        }
    }
}
