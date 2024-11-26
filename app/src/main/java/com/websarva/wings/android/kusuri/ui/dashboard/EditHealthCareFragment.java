package com.websarva.wings.android.kusuri.ui.dashboard;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.websarva.wings.android.kusuri.AppDatabase;
import com.websarva.wings.android.kusuri.HealthCare;
import com.websarva.wings.android.kusuri.HealthCareDao;
import com.websarva.wings.android.kusuri.Medication;
import com.websarva.wings.android.kusuri.R;

public class EditHealthCareFragment extends Fragment {
    private TextView dateTextView;

    private EditText tempEditText;           //体温
    private EditText weightEditText;        //体重
    private EditText bpUpEditText;           //血圧（上）
    private EditText bpDownEditText;        //血圧（下）
    private Spinner hcTimingSpinner;        //食前・食後
    private EditText sugarEditText;          //血糖値

    private HealthCareDao healthCareDao;
    private HealthCare currentHealthCare;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_healthcare, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        healthCareDao = AppDatabase.getDatabase(requireContext()).healthCareDao();
        tempEditText = view.findViewById(R.id.tempEditText_ud);
        weightEditText = view.findViewById(R.id.weightEditText_ud);
        bpUpEditText = view.findViewById(R.id.bpUpEditText_ud);
        bpDownEditText = view.findViewById(R.id.bpDownEditText_ud);
        hcTimingSpinner = view.findViewById(R.id.HCtiming_spinner_ud);
        sugarEditText = view.findViewById(R.id.sugarEditText_ud);
        Button saveButton = view.findViewById(R.id.registerButton_ud);
        Button cancelButton = view.findViewById(R.id.deleteButton_ud);

        int healthCareId = getArguments().getInt("healthCareId", -1);
        // IDからHealthCareを取得し、各フィールドに設定


        new Thread(() -> {
            currentHealthCare = healthCareDao.getHealthCareById(healthCareId);
            if (currentHealthCare != null) {
                getActivity().runOnUiThread(() -> {
                    tempEditText.setText(String.valueOf(currentHealthCare.temperature));
                    weightEditText.setText(String.valueOf(currentHealthCare.weight));
                    bpUpEditText.setText(String.valueOf(currentHealthCare.pressureUp));
                    bpDownEditText.setText(String.valueOf(currentHealthCare.pressureDown));
                    //　Spinnerの項目から一致するインデックスを探して設定（食前・食後）
                    String dosageSpinner = currentHealthCare.hc_timing;
                    for (int i = 0; i < hcTimingSpinner.getCount(); i++) {
                        if (hcTimingSpinner.getItemAtPosition(i).toString().equals(dosageSpinner)) {
                            hcTimingSpinner.setSelection(i);
                            break;
                        }
                    }
                    sugarEditText.setText(String.valueOf(currentHealthCare.sugar));
                });
            }
        }).start();

        // 保存ボタンでデータを更新
        saveButton.setOnClickListener(v -> {
                Log.d("EditHealthCareFragment", "EditHealthCareFragment saveButton.setOnClickListener");
                double temperature = Double.parseDouble(tempEditText.getText().toString());
                double weight = Double.parseDouble(weightEditText.getText().toString());
                int presserUp = Integer.parseInt(bpUpEditText.getText().toString());
                int presserDown = Integer.parseInt(bpDownEditText.getText().toString());
                String hcTiming = hcTimingSpinner.getSelectedItem().toString();
                int suger = Integer.parseInt(sugarEditText.getText().toString());

                new Thread(() -> {
                    Log.d("EditHealthCareFragment", "Thread");
                    currentHealthCare.temperature = temperature;
                    currentHealthCare.weight = weight;
                    currentHealthCare.pressureUp = presserUp;
                    currentHealthCare.pressureDown = presserDown;
                    currentHealthCare.hc_timing = hcTiming;
                    currentHealthCare.sugar = suger;
                    healthCareDao.updateHealthCare(currentHealthCare);

                    getActivity().runOnUiThread(() -> {
                        getActivity().getSupportFragmentManager().popBackStack();
                        Toast.makeText(getActivity(), "健康状態を更新しました", Toast.LENGTH_LONG).show();
                    });
                }).start();
        });

        // キャンセルボタンのクリックイベント
        cancelButton.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();// 画面を閉じる
            Toast.makeText(getActivity(), "キャンセルしました", Toast.LENGTH_SHORT).show();
        });
    }
}