package com.weloveyolo.moniguard.ui;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.weloveyolo.moniguard.R;
import com.weloveyolo.moniguard.api.IMoniGuardApi;
import com.weloveyolo.moniguard.api.MoniGuardApi;

import java.util.Objects;


public class MyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initAccount();
    }

    public void initAccount(){
        IMoniGuardApi moniGuardApi = new MoniGuardApi();
        moniGuardApi.setAccessToken(getActivity().getSharedPreferences("user", MODE_PRIVATE).getString("token", ""));
        new Thread(() -> moniGuardApi.getResidentsApi().getResident((resident, success) -> {
            if (success) {
                getActivity().runOnUiThread(() -> {
//                    Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                    Log.i("LoginActivity", "Resident: " + resident);
                    TextView nameView = getActivity().findViewById(R.id.cname);
                    nameView.setText(resident.getName());
                });
            } else {
                TextView nameView = getActivity().findViewById(R.id.cname);
                nameView.setText("null");
            }
        })).start();
    }
}
