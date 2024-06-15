package com.weloveyolo.moniguard.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.activity.AboutActivity;
import com.weloveyolo.moniguard.activity.MessageInfoActivity;
import com.weloveyolo.moniguard.activity.PhotoDetailActivity;
import com.weloveyolo.moniguard.activity.UserInfoUpdateActivity;

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

        //跳转拨号
        LinearLayout toCS = view.findViewById(R.id.to_cusService);
        toCS.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                startActivity(intent);
            } catch (Exception e) {
                MainActivity.ct.showErrorToast("无法拨号", 500);
            }
        });

        //关于
        LinearLayout about = view.findViewById(R.id.about);
        about.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
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
