package com.websarva.wings.android.kusuri.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.websarva.wings.android.kusuri.AppDatabase;
import com.websarva.wings.android.kusuri.HealthCareDao;
import com.websarva.wings.android.kusuri.R;
import com.websarva.wings.android.kusuri.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {
    private AppDatabase db;
    private HealthCareDao healthCareDao;
    private FragmentDashboardBinding binding;
    private TextView medListTextView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getDatabase(requireContext());
        healthCareDao = db.healthCareDao();
    }

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

}
