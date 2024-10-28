package com.websarva.wings.android.kusuri.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.websarva.wings.android.kusuri.AppDatabase;
import com.websarva.wings.android.kusuri.Medication;
import com.websarva.wings.android.kusuri.MedicationDao;
import com.websarva.wings.android.kusuri.R;
import com.websarva.wings.android.kusuri.databinding.FragmentNotificationsBinding;

import java.util.List;

public class NotificationsFragment extends Fragment {
    private AppDatabase db;
    private MedicationDao medicationDao;
    private FragmentNotificationsBinding binding;
    private static final int REQUEST_CODE = 1;  // リクエストコード


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getDatabase(requireContext());
        medicationDao = db.medicationDao();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 登録ボタンのクリックでNotificationsActivityを開く
        binding.btNoReg.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationsActivity.class);
            startActivityForResult(intent, REQUEST_CODE);  // Activityを開始
        });
        // ここでデータを表示する処理を行う
        displayMedications();
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            // Intentからデータを取得
            String medicineName = data.getStringExtra("medicineName");
            String memo = data.getStringExtra("memo");
            String dosage = data.getStringExtra("dosage");
            String doseCount = data.getStringExtra("doseCount");
            String usePeriod = data.getStringExtra("usePeriod");
            String notification = data.getStringExtra("notification");

            // TextViewにデータを表示
            binding.noMedListView.setText(
                    String.format("お薬名: %s\nメモ: %s\n服用量: %s\n服用回数: %s\n期間: %s\n通知: %s",
                            medicineName, memo, dosage, doseCount, usePeriod, notification));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void displayMedications() {         //薬のデータを取得し画面に表示
        // データベースからすべての薬情報を取得（バックグラウンドスレッド）
        new Thread(() -> {
            List<Medication> medications = medicationDao.getAllMedications();
            StringBuilder displayText = new StringBuilder();
            for (Medication medication : medications) {
                displayText.append("名前: ").append(medication.name)
                        .append(", 服用量: ").append(medication.dosage).append("\n")
                        .append(", 服用回数: ").append(medication.frequency).append("\n")
                        .append(", 服用開始: ").append(medication.startdate).append("\n")
                        .append(", 服用終了: ").append(medication.enddate).append("\n")
                        .append(", メモ: ").append(medication.memo).append("\n")
                        .append(", リマインダー: ").append(medication.reminder).append("\n");


            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> binding.noMedListView.setText(displayText.toString()));
            }

        }).start();
    }
}
