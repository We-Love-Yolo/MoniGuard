package com.weloveyolo.moniguard.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.util.ArrayList;


public class MonitorActivity extends AppCompatActivity {

    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private VLCVideoLayout vlcVideoLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor);

        // 配置硬件解码器
        ArrayList<String> options = new ArrayList<>();
        options.add("--avcodec-hw=any");

        libVLC = new LibVLC(this, options);

        mediaPlayer = new MediaPlayer(libVLC);
        vlcVideoLayout = findViewById(R.id.view);

        // 设置媒体资源
        Media media = new Media(libVLC, Uri.parse("rtsp://admin:WUsan53408@192.168.239.109"));

        // 降低延迟
        media.addOption(":network-caching=200");    //200毫秒网络缓存
        media.addOption(":rtsp-tcp");   //低延迟的rtsp，而非默认的udp

        mediaPlayer.setMedia(media);

        mediaPlayer.setEventListener(event -> {
            if (event.type == MediaPlayer.Event.Opening) {
                MainActivity.ct.showLoadingToast("等待缓冲");
            } else if (event.type == MediaPlayer.Event.Playing) {
                MainActivity.ct.hideLoadingToast(()->{
                    MainActivity.ct.showSuccessToast("开始播放", 300);
                }, 100);
            }
        });

        mediaPlayer.attachViews(vlcVideoLayout, null, false, true);
        mediaPlayer.play();
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
            Intent intent = new Intent(this, AlbumActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace(); // 可以将异常信息输出到日志中
            Toast.makeText(this, "Error starting AlbumActivity", Toast.LENGTH_SHORT).show();
        }
    }

    // 返回上一页
    public void gotoHomePage(View view){
        finish();
    }

}
