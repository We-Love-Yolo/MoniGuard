package com.weloveyolo.moniguard.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.activity.MonitorActivity;
import com.weloveyolo.moniguard.api.Camera;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CameraListAdapter extends RecyclerView.Adapter<CameraListAdapter.MyHolder> {
    private List<Camera> deviceList;
    private LayoutInflater inflater;

    public CameraListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        deviceList = new ArrayList<>();
    }

    @NonNull
    @Override
    public CameraListAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_monitor, parent, false);
        return new CameraListAdapter.MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CameraListAdapter.MyHolder holder, int position) {
        Camera device = deviceList.get(position);
        holder.deviceTextView.setText(device.getName());
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public void addDevice(String cameraName) {
        deviceList.add(new Camera(cameraName, new Date()));
    }

    public void clear() {
        deviceList.clear();
    }


    static class MyHolder extends RecyclerView.ViewHolder {


        TextView deviceTextView;
        ImageButton cameraImageButton;

        public MyHolder(@NonNull View itemView) {

            super(itemView);
            deviceTextView = itemView.findViewById(R.id.camera_name);
            cameraImageButton = itemView.findViewById(R.id.camera_button);
            cameraImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 在这里执行点击跳转操作，例如跳转到另一个 Activity
                    Context context = itemView.getContext();
                    Intent intent = new Intent(context, MonitorActivity.class); // 替换 YourActivity 为目标 Activity 的类名
                    context.startActivity(intent);
                }
            });
        }
    }
}