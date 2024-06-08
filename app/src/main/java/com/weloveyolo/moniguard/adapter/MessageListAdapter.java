package com.weloveyolo.moniguard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.Message;

import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MyHolder>{
    private List<Message> messageList;
    private LayoutInflater inflater;
    public MessageListAdapter(Context context, List<Message> messages) {
        inflater = LayoutInflater.from(context);
        messageList = new ArrayList<>();
    }

    @Override
    @NotNull
    public MessageListAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_monitor, parent, false);
        return new MessageListAdapter.MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListAdapter.MyHolder holder, int position) {
        Message message = messageList.get(position);
        String time = message.getCreatedAt();
        // 解析时间字符串
        ZonedDateTime zonedDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            zonedDateTime = ZonedDateTime.parse(time);
        }
        int month = 12;
        int day = 31;
        int hour = 0;
        int minute = 0;

        // 提取月、日和时间
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            month = zonedDateTime.getMonthValue();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            day = zonedDateTime.getDayOfMonth();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            hour = zonedDateTime.getHour();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            minute = zonedDateTime.getMinute();
        }
        holder.message_time.setText(String.valueOf(month)+"月"+String.valueOf(day)+"日"+" "+String.valueOf(hour)+":"+String.valueOf(minute));
        holder.picture_time.setText(String.valueOf(hour)+":"+String.valueOf(minute));

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


        public MyHolder(@NonNull View itemView) {

            super(itemView);
            message_time = itemView.findViewById(R.id.message_time);
            photo = itemView.findViewById(R.id.photo);
            scene = itemView.findViewById(R.id.scene);
            camera = itemView.findViewById(R.id.camera);
            picture_time = itemView.findViewById(R.id.picture_time);
//            cameraImageButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // 在这里执行点击跳转操作，例如跳转到另一个 Activity
//                    Context context = itemView.getContext();
//                    Intent intent = new Intent(context, MonitorActivity.class); // 替换 YourActivity 为目标 Activity 的类名
//                    context.startActivity(intent);
//                }
//            });
        }
}

}


