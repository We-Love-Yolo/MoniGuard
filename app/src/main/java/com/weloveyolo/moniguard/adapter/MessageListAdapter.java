package com.weloveyolo.moniguard.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
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
import java.util.Map;
import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MyHolder> {

    private Context context;

    private List<Message> messageList;
    private LayoutInflater inflater;
    private MainActivity mainActivity;

    public MessageListAdapter(Context context, List<Message> messages, MainActivity mainActivity) {
        this.context = context;
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


//            String content = "{\"text\": \"\u964c\u751f\u4eba\u6765\u8bbf\", \"guestId\": 2, \"photoUrl\": \"https://mgapi.bitterorange.cn/Analysis/GetPhoto/6\"}";

            // 解析content
            String text;
            String photoUrl;
            try{
                Gson gson = new Gson();
                Map<String, Object> map = gson.fromJson(content, Map.class);
                // 从Map中获取值
                text = (String) map.get("text");
                photoUrl = (String) map.get("photoUrl");
            } catch (Exception e) {
                text = "神秘的消息";
                photoUrl = "https://img2.baidu.com/it/u=1306524414,3423224355&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=800";
            }

            // 设置消息文字内容
            holder.messageText.setText(text);


            // 设置图片
            if (photoUrl != null && !photoUrl.isEmpty()) {
//                Glide.with(holder.photo.getContext())
//                        .load(photoUrl)
//                        .into(holder.photo);
                IMoniGuardApi moniGuardApi = new MoniGuardApi();
                Handler mainHandler = new Handler(Looper.getMainLooper());
                String finalPhotoUrl = photoUrl;
                new Thread(() -> {
                    moniGuardApi.getAnalysisApi().getByteArrayWithToken((result, success) -> {
                        if (!success) return;
                        mainHandler.post(() -> {
                            Glide.with(context)
                                    .load(result) // 使用从API获取的字节数组
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(holder.photo);
                        });
                    }, finalPhotoUrl);
                }).start();
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
        TextView messageText;
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
            messageText = itemView.findViewById(R.id.message_text);
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
                context.startActivity(intent);
            });
        }
    }
}
