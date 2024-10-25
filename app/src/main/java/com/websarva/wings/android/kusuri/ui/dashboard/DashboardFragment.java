package com.websarva.wings.android.kusuri.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.websarva.wings.android.kusuri.R;
import com.websarva.wings.android.kusuri.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private TextView medListTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // TextViewの初期設定
        medListTextView = root.findViewById(R.id.da_medListView);

        // 登録ボタンの設定
        Button button = binding.getRoot().findViewById(R.id.bt_da_reg);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DashboardActivity.class);
            startActivityForResult(intent, 1); // 結果を取得するリクエストコード1
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // 登録画面から戻ってきたときの処理
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            // Intentからデータを取得
            String date = data.getStringExtra("date");
            String temp = data.getStringExtra("temperature");
            String bp = data.getStringExtra("bloodPressure");
            String weight = data.getStringExtra("weight");
            String sugar = data.getStringExtra("bloodSugar");

            // 表示用の文字列を組み立てる
            StringBuilder medInfo = new StringBuilder();
            medInfo.append("登録日 : ").append(date).append("\n");

            if (!temp.isEmpty()) medInfo.append("体温 : ").append(temp).append("℃\n");
            if (!bp.isEmpty()) medInfo.append("血圧 : ").append(bp).append(" mmHg\n");
            if (!weight.isEmpty()) medInfo.append("体重 : ").append(weight).append(" kg\n");
            if (!sugar.isEmpty()) medInfo.append("血糖 : ").append(sugar).append(" mg/dL\n");

            // 結果をTextViewに表示
            medListTextView.setText(medInfo.toString());
        }
    }
}
