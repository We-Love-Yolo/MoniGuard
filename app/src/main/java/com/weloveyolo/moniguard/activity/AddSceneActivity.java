package com.weloveyolo.moniguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.MoniGuardApi;

public class AddSceneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_scene);

        EditText editText = findViewById(R.id.edit_title);

        Button done_button = findViewById(R.id.button_done);
        done_button.setOnClickListener(v -> {
            String content = editText.getText().toString().trim();
            editText.setText("");
            editText.clearFocus();
            if(content.isEmpty()) return;
            addScene(content);
        });
    }

    public void addScene(String sceneName){
        new Thread(() -> {
            IMoniGuardApi moniGuardApi = new MoniGuardApi();
            moniGuardApi.getScenesApi().postScene(sceneName, ((result, success) -> {
                if(success) {
                    runOnUiThread(() -> {
                        MainActivity.ct.showSuccessToast("场景已创建", 1000);
                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                    });
                }
            }));
        }).start();
    }

    public void toBack(View view){
        setResult(Activity.RESULT_CANCELED, new Intent());
        finish();
    }
}
