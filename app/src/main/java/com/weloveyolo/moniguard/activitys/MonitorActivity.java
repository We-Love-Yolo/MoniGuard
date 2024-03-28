package com.weloveyolo.moniguard.activitys;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.weloveyolo.moniguard.R;
public class MonitorActivity extends AppCompatActivity {

    // Your other code here...

    // This method is invoked when the ImageView is clicked

//    public void goto_homepage(View view) {
//        // Create an intent to start HomeActivity
//        Intent intent = new Intent(this, HomeActivity.class);
//        // Start the activity
//        startActivity(intent);
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor);

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

