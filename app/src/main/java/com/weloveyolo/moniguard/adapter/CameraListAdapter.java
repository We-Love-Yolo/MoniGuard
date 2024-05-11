package com.weloveyolo.moniguard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.Camera;

import java.util.List;

public class CameraListAdapter extends RecyclerView.Adapter<CameraListAdapter.MyHolder> {

    private List<Camera> cameras;

    public CameraListAdapter(List<Camera> cameras) {
        this.cameras = cameras;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_monitor, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Camera currentCamera = cameras.get(position);
        holder.cameraName.setText(currentCamera.getName());

        // 根据connectState值设置连接状态的ImageView和TextView
        if (currentCamera.isConnectState()) {
            // 连接状态为true，设置ImageView为绿色圆圈，TextView为"已连接"
            holder.connectColor.setImageResource(R.drawable.green_circle);
            holder.connectState.setText(R.string.connected);
        } else {
            // 连接状态为false，设置ImageView为红色圆圈，TextView为"未连接"
            holder.connectColor.setImageResource(R.drawable.red_circle);
            holder.connectState.setText(R.string.unconnected);
        }
    }

    @Override
    public int getItemCount() {
        return cameras.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public ImageView connectColor;
        TextView cameraName;
        TextView connectState;

        public MyHolder(View itemView) {
            super(itemView);
            cameraName = itemView.findViewById(R.id.camera_name);
            connectState = itemView.findViewById(R.id.connect_state);
            connectColor = itemView.findViewById(R.id.connect_color);
        }
    }

    private onItemClickListener mOnItemClickListener;
    public onItemClickListener getmOnItemClickListener() {
        return mOnItemClickListener;
    }
    public void setOnItemClickListener(onItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
    public interface onItemClickListener{
        void onItemClick(int position);
    }
}
