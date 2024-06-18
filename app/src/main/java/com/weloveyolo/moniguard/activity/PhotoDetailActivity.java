package com.weloveyolo.moniguard.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.Message;
import com.weloveyolo.moniguard.api.MoniGuardApi;
import com.weloveyolo.moniguard.util.HttpClient;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Response;

public class PhotoDetailActivity extends AppCompatActivity {

    private ImageView mainPhoto;
    private TextView dateAndTime;
    private TextView photoPath;
    private ImageView smallPhoto;
    private TextView strangeName;
    private TextView deviceName;

    private Message message;

    private String mainUrl = "https://i0.hdslb.com/bfs/archive/01b66bc9494cf8bda4419b111d5da291492912b2.jpg";
    private String smallUrl = "https://img2.baidu.com/it/u=1306524414,3423224355&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=800";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        message = (Message) getIntent().getSerializableExtra("message");

        mainPhoto = findViewById(R.id.main_photo);
        dateAndTime = findViewById(R.id.date_time);
        photoPath = findViewById(R.id.img_path);
        smallPhoto = findViewById(R.id.stranger_photo);
        strangeName = findViewById(R.id.stranger_name);
        deviceName = findViewById(R.id.device_name);

        fillMainPhoto();
        fillSmallPhoto();
        try {
            fillOthers();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void fillSmallPhoto() {
        new Thread(() -> {
            Response response = HttpClient.get(smallUrl, null);
            try {
                byte[] imgByte = response.body().bytes();
                Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                runOnUiThread(()->{
                    smallPhoto.setImageBitmap(bitmap);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void fillOthers() throws ParseException {
        IMoniGuardApi moniGuardApi = new MoniGuardApi();

        // 日期时间
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        originalFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // 设置时区为 UTC
        // 解析日期字符串
        Date date = originalFormat.parse(message.getCreatedAt());
        // 创建一个新的 SimpleDateFormat 对象用于格式化日期
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy/MM/dd EEEE HH:mm:ss", Locale.CHINESE);
        newFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区为上海时区
        // 格式化日期
        String formattedDateString = newFormat.format(date);
        dateAndTime.setText(formattedDateString);

        // 设备名
        new Thread(()->{
            moniGuardApi.getScenesApi().getScenes((sceneList, success)->{
                if (!success) return;
                sceneList.forEach(scene -> {

                });
            });
        }).start();
    }

    public void toBack(View view){
        finish();
    }
}
