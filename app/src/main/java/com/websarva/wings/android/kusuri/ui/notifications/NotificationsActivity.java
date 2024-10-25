package com.websarva.wings.android.kusuri.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.websarva.wings.android.kusuri.R;

public class NotificationsActivity extends AppCompatActivity {

    private Spinner dosageSpinner, notificationSpinner;
    private EditText medicineNameEdit, memoEdit;
    private Button registerButton, cancelButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initializeViews(); // 各ビューの初期化
        setupSpinners();   // Spinnerにアダプターを設定

        // 登録ボタンのクリックイベント
        registerButton.setOnClickListener(v -> {
            String medicineName = medicineNameEdit.getText().toString();
            String memo = memoEdit.getText().toString();
            String dosage = dosageSpinner.getSelectedItem().toString();
            //String doseCount = doseCountSpinner.getSelectedItem().toString();
            //String usePeriod = usePeriodSpinner.getSelectedItem().toString();
            String notification = notificationSpinner.getSelectedItem().toString();

            if (medicineName.isEmpty()) {
                Toast.makeText(this, "おくすり名を入力してください", Toast.LENGTH_SHORT).show();
                return;
            }

            // 入力データをIntentで返す
            Intent resultIntent = new Intent();
            resultIntent.putExtra("medicineName", medicineName);
            resultIntent.putExtra("memo", memo);
            resultIntent.putExtra("dosage", dosage);
            //resultIntent.putExtra("doseCount", doseCount);
            //resultIntent.putExtra("usePeriod", usePeriod);
            resultIntent.putExtra("notification", notification);
            setResult(RESULT_OK, resultIntent);  // 結果を設定
            finish();  // Activityを閉じる
        });

        // キャンセルボタンのクリックイベント
        cancelButton.setOnClickListener(v -> {
            finish();  // 画面を閉じる
            Toast.makeText(this, "キャンセルしました", Toast.LENGTH_SHORT).show();
        });
    }

    private void initializeViews() {
        dosageSpinner = findViewById(R.id.dosage_spinner);
        //doseCountSpinner = findViewById(R.id.dose_count_spinner);
        //usePeriodSpinner = findViewById(R.id.use_period_spinner);
        notificationSpinner = findViewById(R.id.notification_spinner);

        medicineNameEdit = findViewById(R.id.medicine_name_edit);
        memoEdit = findViewById(R.id.memo_edit);

        registerButton = findViewById(R.id.register_button);
        cancelButton = findViewById(R.id.cancel_button);
    }

    private void setupSpinners() {
        setupSpinner(dosageSpinner, R.array.dosage_options);
        //setupSpinner(doseCountSpinner, R.array.dose_count_options);
        //setupSpinner(usePeriodSpinner, R.array.use_period_options);
        setupSpinner(notificationSpinner, R.array.notification_options);
    }

    private void setupSpinner(Spinner spinner, int arrayResId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
