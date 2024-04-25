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

        initAccount();
    }

    public void initAccount() {
        IMoniGuardApi moniGuardApi = new MoniGuardApi();
        new Thread(() -> {
            moniGuardApi.getResidentsApi().getResident((resident, success) -> {
                requireActivity().runOnUiThread(() -> {
                    TextView nameView = requireActivity().findViewById(R.id.cname);
                    if (success) {
                        nameView.setText(resident.getNickname());
                    } else {
                        nameView.setText("未知");
                    }
                });
            });
        }).start();
    }
}
