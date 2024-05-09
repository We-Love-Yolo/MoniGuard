package com.weloveyolo.moniguard.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.util.CustomToast;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.util.ArrayList;


public class MonitorActivity extends AppCompatActivity {

    // Your other code here...

    // This method is invoked when the ImageView is clicked

    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private VLCVideoLayout vlcVideoLayout;
    private CustomToast ct;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor);

        ArrayList<String> options = new ArrayList<>();


        // 添加LibVLC选项
//        options.add("--no-drop-late-frames");
//        options.add("--no-skip-frames");

//        options.add(":file-caching=50");//文件缓存
//        options.add(":network-caching=50");//网络缓存
//
//        options.add(":live-caching=50");//直播缓存
//        options.add(":sout-mux-caching=50");//输出缓存
//        options.add(":codec=mediacodec,iomx,all");

        libVLC = new LibVLC(this, options);
        mediaPlayer = new MediaPlayer(libVLC);
        vlcVideoLayout = findViewById(R.id.view);

        // 设置媒体资源
        Media media = new Media(libVLC, Uri.parse("rtsp://192.168.137.146:554/ch01.264"));
        mediaPlayer.setMedia(media);

        ct = new CustomToast(this);

        mediaPlayer.setEventListener(event -> {
            if (event.type == MediaPlayer.Event.Opening) {
                ct.showSuccessToast("准备", 50);
            } else if (event.type == MediaPlayer.Event.Buffering) {
                ct.showSuccessToast("在缓冲", 50);
            } else if (event.type == MediaPlayer.Event.Playing) {
                ct.showSuccessToast("开始播放", 50);
            }
        });

        mediaPlayer.play();
        mediaPlayer.attachViews(vlcVideoLayout, null, false, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer.setVolume(100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.setVolume(0);
    }

    // 进入智能相册
    public void goto_album(View view) {
        try {
            // Create an intent to start AlbumActivity
            Intent intent = new Intent(this, AlbumActivity.class);
            // Start the activity
            startActivity(intent);
        } catch (Exception e) {
            // Handle any exceptions that occur during activity startup
            e.printStackTrace(); // 可以将异常信息输出到日志中
            // 或者显示一个错误提示给用户
            Toast.makeText(this, "Error starting AlbumActivity", Toast.LENGTH_SHORT).show();
        }
    }

    // 返回上一页
    public void gotoHomePage(View view){
        onBackPressed();
    }



}
