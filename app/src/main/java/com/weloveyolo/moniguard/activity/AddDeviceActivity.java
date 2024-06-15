package com.weloveyolo.moniguard.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.Camera;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.MoniGuardApi;

import java.util.Date;

public class AddDeviceActivity extends AppCompatActivity {

    private int sceneId;    // 场景id
    private EditText name;
    private EditText key;
    private EditText description;
    private String links;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_device);

        sceneId = getIntent().getIntExtra("sceneId", 0);

        name = findViewById(R.id.edit_scene_name);
        key = findViewById(R.id.edit_key);
        description = findViewById(R.id.edit_description);


        Button finish_button = findViewById(R.id.button_finish);
        finish_button.setOnClickListener(v -> {

            if(name.getText().toString().trim().equals("")) {
                MainActivity.ct.showErrorToast("设备名为空", 1000);
                return;
            }

            if(key.getText().toString().trim().equals("")) {
                MainActivity.ct.showErrorToast("摄像头key为空", 1000);
                return;
            }
            String name_content = name.getText().toString().trim();
            int key_content =Integer.parseInt(key.getText().toString().trim());
            String description_content = description.getText().toString().trim();
            addDevice(name_content,key_content,description_content);
        });
    }

    // 返回上一页
    public void toBack(View view){
        setResult(Activity.RESULT_CANCELED, new Intent());
        finish();
    }

    public void addDevice(String deviceName,int devicePinCode,String deviceDescription){
        new Thread(() -> {
            IMoniGuardApi moniGuardApi = new MoniGuardApi();
            MainActivity.ct.showLoadingToast("加载中");
            moniGuardApi.getScenesApi().confirmCameraCreation(sceneId, devicePinCode, deviceName, deviceDescription, ((result, success) -> {
                if(success) {
                    links = result;
                    runOnUiThread(()->{
                        MainActivity.ct.hideLoadingToast(()->{
                            MainActivity.ct.showSuccessToast("设备已添加", 1000);
                        }, 100);
                    });
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                }
                else{
                    runOnUiThread(()->{
                        MainActivity.ct.hideLoadingToast(()->{
                            MainActivity.ct.showErrorToast("设备添加失败", 1000);
                        }, 100);
                    });
                }
            }));
        }).start();
    }

    public void quickName(View view){
        String quick_name = (String) ((Button)view).getText();
        name.setText(quick_name);

    }
}