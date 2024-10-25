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

import com.websarva.wings.android.kusuri.R;
import com.websarva.wings.android.kusuri.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private static final int REQUEST_CODE = 1;  // リクエストコード

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
}
