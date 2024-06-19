package com.weloveyolo.moniguard.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.activity.PhotoDetailActivity;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.Message;
import com.weloveyolo.moniguard.api.MoniGuardApi;

import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MyHolder> {
    private List<Message> messageList;
    private LayoutInflater inflater;

    public MessageListAdapter(Context context, List<Message> messages) {
        inflater = LayoutInflater.from(context);
        messageList = messages;  // 使用传入的 messages 列表
    }

    @Override
    @NotNull
    public MessageListAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.message, parent, false);  // Ensure the correct layout is inflated
        return new MessageListAdapter.MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListAdapter.MyHolder holder, int position) {
        Message message = messageList.get(position);
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
        holder.photo.setImageResource(Integer.parseInt(message.getContent()));
        holder.cameraId = message.getCameraId();
        String cameraName;
        new Thread(() -> {
            IMoniGuardApi moniGuardApi = new MoniGuardApi();
//            cameraName = moniGuardApi.getScenesApi(
//                    holder.camera.setText();
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

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            message_time = itemView.findViewById(R.id.message_time);
            photo = itemView.findViewById(R.id.photo);
            scene = itemView.findViewById(R.id.scene);
            camera = itemView.findViewById(R.id.camera);
            picture_time = itemView.findViewById(R.id.picture_time);

            photo.setOnClickListener(v -> {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, PhotoDetailActivity.class);
                context.startActivity(intent);
            });
        }
    }
}
