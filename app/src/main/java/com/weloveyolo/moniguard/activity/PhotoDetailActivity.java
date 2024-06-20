package com.weloveyolo.moniguard.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.Guest;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.Message;
import com.weloveyolo.moniguard.api.MoniGuardApi;
import com.weloveyolo.moniguard.util.HttpClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Response;

public class PhotoDetailActivity extends AppCompatActivity {

    private ImageView mainPhoto;
    private TextView dateAndTime;
    private TextView photoName;
    private ImageView smallPhoto;
    private TextView strangeName;
    private TextView deviceName;

    private Message message;
    private String cameraName;
    private int sceneId;
    private int guestId;

    private String mainUrl = "https://i0.hdslb.com/bfs/archive/01b66bc9494cf8bda4419b111d5da291492912b2.jpg";
    private String defaultSmallUrl = "https://img2.baidu.com/it/u=1306524414,3423224355&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=800";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        message = (Message) getIntent().getSerializableExtra("message");
        String content = message.getContent();
        // 解析content
        try{
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(content, Map.class);
            guestId = (int) Math.round((Double) map.get("guestId"));
            mainUrl = (String) map.get("photoUrl");
        } catch (Exception e) {
            guestId = 2;
        }

        cameraName = getIntent().getStringExtra("cameraName");
        sceneId = getIntent().getIntExtra("sceneId", 3);


        mainPhoto = findViewById(R.id.main_photo);
        dateAndTime = findViewById(R.id.date_time);
        photoName = findViewById(R.id.img_path);
        smallPhoto = findViewById(R.id.stranger_photo);
        strangeName = findViewById(R.id.stranger_name);
        deviceName = findViewById(R.id.device_name);

        fillMainPhoto();
        fillSmallPhoto();
        fillOthers();
    }

    private void fillMainPhoto() {
        new Thread(() -> {
            Response response = HttpClient.get(mainUrl, null);
            try {
                byte[] imgByte = response.body().bytes();
                Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                runOnUiThread(()->{
                    mainPhoto.setImageBitmap(bitmap);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void fillOthers() {
        // 日期时间
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

        Date date = null;
        try {
            date = originalFormat.parse(message.getCreatedAt());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy/MM/dd EEEE HH:mm:ss", Locale.CHINESE);
        newFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区为上海时区

        String formattedDateString = newFormat.format(date);
        dateAndTime.setText(formattedDateString);

        // 设备名
        deviceName.setText(cameraName);

        // 主图名
        try {
            date = originalFormat.parse(message.getCreatedAt());
        } catch (Exception e) {
            e.printStackTrace(); // 处理异常
        }
        SimpleDateFormat newFormat2 = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
        newFormat2.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区为上海时区
        photoName.setText("IMG_" + newFormat2.format(date));

        // 访客名字
        IMoniGuardApi moniGuardApi = new MoniGuardApi();
        new Thread(()->{
            moniGuardApi.getScenesApi().getGuest(sceneId, (guestList, success)->{
                if (!success) return;
                if (guestList.isEmpty()) {
                    guestList.add(new Guest(guestId, sceneId, "坏蛋", "2024-06-20T16:25:25.1361709+08:00", false, ""));
                }
                for (Guest guest : guestList) {
                    if (guest.getGuestId() == guestId) {
                        runOnUiThread(()->{
                            strangeName.setText(guest.getName());
                        });
                        break;
                    }
                }
            });
        }).start();
    }

    private void fillSmallPhoto() {
        new Thread(() -> {
            IMoniGuardApi moniGuardApi = new MoniGuardApi();
            moniGuardApi.getAnalysisApi().getFaceImageByGuest((result, success)->{
                if (success) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
                    runOnUiThread(()->{
                        smallPhoto.setImageBitmap(bitmap);
                    });
                } else {
                    try {
                        Response response = HttpClient.get(defaultSmallUrl, null);
                        byte[] imgByte = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                        runOnUiThread(()->{
                            smallPhoto.setImageBitmap(bitmap);
                        });
                    } catch (Exception ex) {}
                }
            }, guestId);
        }).start();
    }

    public void toBack(View view){
        finish();
    }
}
