package com.weloveyolo.moniguard.ui;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.activity.AddDeviceActivity;
import com.weloveyolo.moniguard.activity.AddSceneActivity;
import com.weloveyolo.moniguard.activity.MessageInfoActivity;
import com.weloveyolo.moniguard.activity.UserInfoUpdateActivity;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.MoniGuardApi;

import java.util.Objects;

import lombok.Getter;

public class MyFragment extends Fragment {

    private int EDIT_RESIDENT_CODE = 103;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tryShow();
        LinearLayout message = requireActivity().findViewById(R.id.message);
        int residentId = ((MainActivity)requireActivity()).resident.getResidentId();
        //跳转消息通知
        message.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MessageInfoActivity.class);
            intent.putExtra("residentId", residentId);  // 传入用户id
            startActivity(intent);
        });

        //跳转个人信息修改
        LinearLayout info= view.findViewById(R.id.info);
        info.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserInfoUpdateActivity.class);
            intent.putExtra("residentId", residentId);  // 传入用户id
            startActivityForResult(intent, EDIT_RESIDENT_CODE);
        });
    }

    public void tryShow() {
        MainActivity mainActivity = ((MainActivity) getActivity());
        if(mainActivity == null) return;
        if (mainActivity.resident != null) {
            requireActivity().runOnUiThread(() -> {
                TextView nameView = requireActivity().findViewById(R.id.cname);
                TextView phoneView = requireActivity().findViewById(R.id.phone_number);
                ImageView avatarView = requireActivity().findViewById(R.id.touxiang);
                    // 昵称
                    nameView.setText(mainActivity.resident.getNickname());

                    String phoneNumber = mainActivity.resident.getPhone();
                    // 手机号
                    phoneView.setText(phoneNumber == null ? "个人信息" : phoneNumber);
                    // 头像
                    if(mainActivity.resident.getRealAvatar() != null){
                        byte[] avatarBuf = mainActivity.resident.getRealAvatar();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(avatarBuf, 0, avatarBuf.length);
                        avatarView.setImageBitmap(bitmap);
                    }
            });
        } else {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK) return;
        if(requestCode == EDIT_RESIDENT_CODE){
            ((MainActivity) requireActivity()).getMyData();
        }
    }

}
