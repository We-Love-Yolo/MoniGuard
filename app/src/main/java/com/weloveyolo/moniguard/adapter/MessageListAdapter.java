package com.weloveyolo.moniguard.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.activity.PhotoDetailActivity;
import com.weloveyolo.moniguard.api.Camera;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.Message;
import com.weloveyolo.moniguard.api.MoniGuardApi;
import com.weloveyolo.moniguard.api.Scene;

import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MyHolder> {
    private List<Message> messageList;
    private LayoutInflater inflater;
    private MainActivity mainActivity;

    public MessageListAdapter(Context context, List<Message> messages, MainActivity mainActivity) {
        inflater = LayoutInflater.from(context);
        messageList = messages;  // 使用传入的 messages 列表
        this.mainActivity = mainActivity;
    }

    @Override
    @NotNull
    public MessageListAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.message, parent, false);  // Ensure the correct layout is inflated
        return new MessageListAdapter.MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListAdapter.MyHolder holder, int position) {
        if (messageList == null || position >= messageList.size()) {
            // 处理错误情况，例如显示错误信息
            return;
        }

        Message message = messageList.get(position);
        holder.message = message;
        String time = message.getCreatedAt();
        ZonedDateTime zonedDateTime = parseZonedDateTime(time);

        //解析时间需要手机的安卓版本26以上
        if (zonedDateTime != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int month = zonedDateTime.getMonthValue();
            int day = zonedDateTime.getDayOfMonth();
            int hour = zonedDateTime.getHour();
            int minute = zonedDateTime.getMinute();
            holder.message_time.setText(month + "月" + day + "日 " + hour + ":" + minute);
            holder.picture_time.setText(hour + ":" + minute);
        }

        // 设置图像资源，假设 message.getType() 返回图像资源 ID
//        holder.photo.setImageResource(message.getType());

//        holder.scene.setText(message.getContent());
//        holder.camera.setText(message.getContent()); // 修改为根据 message 数据设置合适的值

        // 使用View.post()确保Glide在主线程执行
        holder.photo.post(() -> {
            // 确保message.getContent()不为空
            String content = message.getContent();
            if (content != null && !content.isEmpty()) {
                Glide.with(holder.photo.getContext())
                        .load(content)
                        .into(holder.photo);
            }
        });
//        Camera camera = new Camera();
        // 确保message.getCameraId()不为空
        int cameraId = message.getCameraId();
        if (cameraId == 0) {
            // 处理错误情况，例如显示错误信息
            return;
        }
        holder.cameraId = cameraId;
        new Thread(()-> {
            IMoniGuardApi moniGuardApi = new MoniGuardApi();
            moniGuardApi.getScenesApi().getCamera(cameraId, (result, success) -> {
                if (success) {
                    mainActivity.runOnUiThread(() -> {
                        holder.camera.post(() -> {
                            if (result != null) {
                                holder.camera.setText(result.getName());
                                holder.cameraName = result.getName();
                                // 确保mainActivity是final或者有效的作用域内变量
                                for (Scene scene : mainActivity.scenes) {
                                    if (scene.getSceneId() == result.getSceneId()) {
                                        holder.sceneId = scene.getSceneId();
                                        holder.scene.post(() -> holder.scene.setText(scene.getName()));
                                        break;
                                    }
                                }
                            }
                        });
                    });
                }
            });
        }).start();

    }

    private ZonedDateTime parseZonedDateTime(String time) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return ZonedDateTime.parse(time);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Getter
    @Setter
    static class MyHolder extends RecyclerView.ViewHolder {
        TextView message_time;
        ImageView photo;
        TextView scene;
        TextView camera;
        TextView picture_time;
        int cameraId;

        Message message;
        String cameraName;
        int sceneId;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            message_time = itemView.findViewById(R.id.message_time);
            photo = itemView.findViewById(R.id.photo);
            scene = itemView.findViewById(R.id.scene);
            camera = itemView.findViewById(R.id.camera);
            picture_time = itemView.findViewById(R.id.picture_time);

            itemView.setOnClickListener(v -> {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, PhotoDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("message", message);
                intent.putExtras(bundle);
                intent.putExtra("cameraName", cameraName);
                intent.putExtra("sceneId", sceneId);
                intent.putExtra("guestId", 2);  // TODO 待修改
                context.startActivity(intent);
            });
        }
    }
}
