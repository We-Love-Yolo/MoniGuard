package com.weloveyolo.moniguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.MoniGuardApi;
import com.weloveyolo.moniguard.api.Resident;

public class UserInfoUpdateActivity extends AppCompatActivity {

    EditText nickname;
    EditText phone;
    Resident resident;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info_update);
        //获取用户id
        int residentId = getIntent().getIntExtra("residentId", 0);
        nickname = findViewById(R.id.editText_nickname);
        phone = findViewById(R.id.editText_phone);

        getResident(residentId);

    }

    public void toBack(View view){
        setResult(Activity.RESULT_CANCELED, new Intent());
        finish();
    }

    public void getResident(int residentId) {
        new Thread(() -> {
            IMoniGuardApi moniGuardApi = new MoniGuardApi();
            moniGuardApi.getResidentsApi().getResident2( (resident, success) -> {
                if(success){
                    this.resident = resident;
                    runOnUiThread(() ->{
                        this.nickname.setText(resident.getNickname());
                        this.phone.setText((CharSequence) resident.getPhone());
                    });
                }
            });
        }).start();
    }
    public void saveGoBack(View view){
        if(nickname.getText().toString().trim().equals("")){
            MainActivity.ct.showErrorToast("昵称未修改", 1000);
            return;
        }
        if(phone.getText().toString().trim().equals("")){
            MainActivity.ct.showErrorToast("电话未修改", 1000);
            return;
        }
        resident.setNickname(String.valueOf(nickname.getText()));
        resident.setPhone(String.valueOf(phone.getText()));
        putResident();
        finish();
    }

    public void putResident(){

        new Thread(() -> {
            IMoniGuardApi moniGuardApi = new MoniGuardApi();
            moniGuardApi.getResidentsApi().putResident(resident, (result , success) -> {
                if(success){
                    runOnUiThread(() -> {
                        MainActivity.ct.showSuccessToast("个人信息已修改", 1000);

                    });
                }
            });

        }).start();
    }
}