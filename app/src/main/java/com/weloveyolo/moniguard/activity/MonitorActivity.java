package com.weloveyolo.moniguard.activity;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.adapter.GridSpacingItemDecoration;
import com.weloveyolo.moniguard.adapter.ScreenshotListAdapter;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.io.File;


public class MonitorActivity extends AppCompatActivity {

    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private VLCVideoLayout vlcVideoLayout;
    private RecyclerView recyclerView;
    private ScreenshotListAdapter screenshotListAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor);
        //在截图相册显示1
        recyclerView = findViewById(R.id.screeshot_album);
        //设置网状布局行列数
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        //行间距
        int spacing = getResources().getDimensionPixelSize(R.dimen.spacing); // 从资源中获取间隙尺寸
        boolean includeEdge = false; // 如果你想在网格的边缘也有间隔的话
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, spacing, includeEdge));
        recyclerView.setTop(-300);

        // 配置硬件解码器
//        ArrayList<String> options = new ArrayList<>();
//        options.add("--avcodec-hw=any");
//        libVLC = new LibVLC(this, options);11

        vlcVideoLayout = findViewById(R.id.view);

        libVLC = new LibVLC(this);

        mediaPlayer = new MediaPlayer(libVLC);

        // 设置媒体资源
//        Media media = new Media(libVLC, Uri.parse("rtsp://admin:WUsan53408@192.168.239.109"));
        Media media = new Media(libVLC, Uri.parse("rtmp://liteavapp.qcloud.com/live/liteavdemoplayerstreamid"));

        // 降低延迟
        media.addOption(":network-caching=200");    //200毫秒网络缓存
        media.addOption(":rtsp-tcp");   //低延迟的rtsp，而非默认的udp

        media.setHWDecoderEnabled(false, false);
        mediaPlayer.setMedia(media);


        mediaPlayer.setEventListener(event -> {
            if (event.type == MediaPlayer.Event.Opening) {
                MainActivity.ct.showLoadingToast("等待缓冲");
            } else if (event.type == MediaPlayer.Event.Playing) {
                MainActivity.ct.hideLoadingToast(()->{
                    MainActivity.ct.showSuccessToast("开始播放", 200);
                }, 100);
            }
        });

        media.release();
        mediaPlayer.setScale(0);
//        mediaPlayer.attachViews(vlcVideoLayout, null, false, false);
        mediaPlayer.play();

        // 全屏
        ImageView imageView = findViewById(R.id.imageView3);
        imageView.setOnClickListener(v -> {
            fullScreen();
        });
        // 截图
        ImageView imageView2 = findViewById(R.id.imageView2);
        imageView2.setOnClickListener(v -> {
            screenshot();
        });
        // 音量
        ImageView voice = findViewById(R.id.voice);
        voice.setOnClickListener(v -> {
            int cur = mediaPlayer.getVolume();
            if (cur == 0) {
                cur = 100;
            } else {
                cur = 0;
            }
            mediaPlayer.setVolume(cur);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaPlayer.attachViews(vlcVideoLayout, null, false, false);
        mediaPlayer.play();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.detachViews();
        mediaPlayer.stop();
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

    // 全屏
    public void fullScreen(){
        // 获取父布局的引用
        FrameLayout parentLayout = findViewById(R.id.fragment);

        // 设置VLCVideoLayout的布局参数
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );

        LinearLayout linearLayout = findViewById(R.id.linear);
        linearLayout.removeView(vlcVideoLayout);


        // 将VLCVideoLayout添加到父布局中
        parentLayout.addView(vlcVideoLayout, layoutParams);
        parentLayout.setVisibility(View.VISIBLE);

        ImageView imageView = findViewById(R.id.exit_full);
        imageView.setVisibility(View.VISIBLE);
        imageView.setElevation(1);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mediaPlayer.detachViews();
        mediaPlayer.stop();
        mediaPlayer.attachViews(vlcVideoLayout, null, false, false);
        mediaPlayer.play();
    }

    // 返回上一页
    public void gotoHomePage(View view){
        finish();
    }

    // 退出全屏
    public void exitFullScreen(View view){
        FrameLayout parentLayout = findViewById(R.id.fragment);
        parentLayout.removeView(vlcVideoLayout);
        parentLayout.setVisibility(View.GONE);

        // 设置VLCVideoLayout的布局参数
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout linearLayout = findViewById(R.id.linear);
        // 将VLCVideoLayout添加到父布局中
        linearLayout.addView(vlcVideoLayout, layoutParams);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mediaPlayer.detachViews();
        mediaPlayer.stop();
        mediaPlayer.attachViews(vlcVideoLayout, null, false, false);
        mediaPlayer.play();
    }

    public void screenshot(){
        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Screenshots/Moniguard";
        // 创建目录路径
        File dir = new File(storagePath);
        // 检查目录是否存在,如果不存在则创建
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (mediaPlayer.getSnapshot(storagePath)) {
            MainActivity.ct.showSuccessToast("截图成功", 500);
        } else {
            MainActivity.ct.showSuccessToast("截图失败", 500);
        }

        // 通知系統相冊刷新
        Intent intent =new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(storagePath)));
        sendBroadcast(intent);

        //新增截图
        screenshotListAdapter.addScreenshot(storagePath);
        screenshotListAdapter.notifyDataSetChanged();

    }
    public void clearALL(View v){
        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Screenshots/Moniguard";
        screenshotListAdapter.clearAllScreenshots(storagePath);
    }


}
