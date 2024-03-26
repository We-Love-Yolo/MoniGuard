package com.weloveyolo.moniguard.activitys;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.weloveyolo.moniguard.R;
public class MonitorActivity extends AppCompatActivity {

    // Your other code here...

    // This method is invoked when the ImageView is clicked
    public void goto_homepage(View view) {
        // Create an intent to start HomeActivity
        Intent intent = new Intent(this, HomeActivity.class);
        // Start the activity
        startActivity(intent);
    }

    public void goto_album(View view) {
        // Create an intent to start HomeActivity
        Intent intent = new Intent(this, AlbumActivity.class);
        // Start the activity
        startActivity(intent);
    }
}

