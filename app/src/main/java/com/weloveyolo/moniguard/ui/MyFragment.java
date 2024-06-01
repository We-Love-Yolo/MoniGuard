package com.weloveyolo.moniguard.ui;

import static android.content.Context.MODE_PRIVATE;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.weloveyolo.moniguard.MainActivity;
import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.MoniGuardApi;

import java.util.Objects;

import lombok.Getter;

public class MyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tryShow();
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
                    if(mainActivity.resident.getAvatar() != null){
                        byte[] avatarBuf = mainActivity.resident.getAvatar();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(avatarBuf, 0, avatarBuf.length);
                        avatarView.setImageBitmap(bitmap);
                    }
            });
        } else {

        }
    }
}
