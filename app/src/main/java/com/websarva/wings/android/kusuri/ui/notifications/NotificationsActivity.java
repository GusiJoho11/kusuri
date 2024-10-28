package com.websarva.wings.android.kusuri.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.websarva.wings.android.kusuri.AppDatabase;
import com.websarva.wings.android.kusuri.Medication;
import com.websarva.wings.android.kusuri.MedicationDao;
import com.websarva.wings.android.kusuri.R;

import java.text.ParseException;

public class NotificationsActivity extends AppCompatActivity {

    private EditText medicineNameEdit;          //おくすり名
    private EditText  dosageEdit;                //服用量の入力
    private Spinner medicationDosageSpinner;              //錠・包
    private EditText doscountEdit;            //服薬回数
//    private EditText medicationStartDateInput;//服薬開始
//    private EditText medicationEndDateInput;  //服薬終了
    private EditText memoEdit;                  //メモ
    private Spinner notificationSpinner;        //通知
    private Button registerButton, cancelButton;//登録・キャンセルボタン

    private AppDatabase db;
    private MedicationDao medicationDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initializeViews(); // 各ビューの初期化
        setupSpinners();   // Spinnerにアダプターを設定

        // 登録ボタンのクリックイベント
        registerButton.setOnClickListener(v -> {
            String medicineName = medicineNameEdit.getText().toString();    //おくすり名
            String dosage = dosageEdit.getText().toString(); //服用量
            String dosage_jo_ho = medicationDosageSpinner.getSelectedItem().toString(); //錠・包
            String dosageCount = doscountEdit.getText().toString();  //服用回数

            //String usePeriod = usePeriodSpinnerge.getSelectedItem().toString();

            String memo = memoEdit.getText().toString();
            String notification = notificationSpinner.getSelectedItem().toString();

            if (medicineName.isEmpty()) {
                Toast.makeText(this, "おくすり名を入力してください", Toast.LENGTH_SHORT).show();
                return;
            }

            // 入力データをIntentで返す
//            Intent resultIntent = new Intent();
//            resultIntent.putExtra("medicineName", medicineName);
//            resultIntent.putExtra("memo", memo);
//            resultIntent.putExtra("dosage", dosage);
//            resultIntent.putExtra("doseCount", doseCount);
//            //resultIntent.putExtra("usePeriod", usePeriod);
//            resultIntent.putExtra("notification", notification);
//            setResult(RESULT_OK, resultIntent);  // 結果を設定

            // Medication オブジェクトを作成して保存
            Medication medication = new Medication();
            medication.name = medicineName;
            medication.dosage = Integer.parseInt(dosage);
            medication.frequency = Integer.parseInt(dosageCount);
//            medication.startdate = startDateLong;
//            medication.enddate = endDateLong;
            medication.memo = memo;
            medication.reminder = notification;  // リマインダー設定

            try {
            // データベースに薬情報を挿入（バックグラウンドスレッドで処理）
            new Thread(() -> {
                medicationDao.insertMedication(medication);
//            runOnUiThread(this::displayMedications);  // メインスレッドでリストを更新

            }).start();
        } catch (NumberFormatException e) {
            if (this != null) {
                Toast.makeText(this, "全ての項目に値を入力してください。", Toast.LENGTH_LONG).show();
            }
        }
            finish();  // Activityを閉じる
        });

        // キャンセルボタンのクリックイベント
        cancelButton.setOnClickListener(v -> {
            finish();  // 画面を閉じる
            Toast.makeText(this, "キャンセルしました", Toast.LENGTH_SHORT).show();
        });

        db = AppDatabase.getDatabase(this);
        medicationDao = db.medicationDao();
    }

    private void initializeViews() {
        medicineNameEdit = findViewById(R.id.medicine_name_edit); // 薬の名前
        dosageEdit = findViewById(R.id.dosage_input); // 服用量
        medicationDosageSpinner = findViewById(R.id.dosage_spinner); // 錠・包

        doscountEdit = findViewById(R.id.doscount_input); // 服用回数

        //doseCountSpinner = findViewById(R.id.dose_count_spinner);
        //usePeriodSpinner = findViewById(R.id.use_period_spinner);

        memoEdit = findViewById(R.id.memo_edit);    // メモ

        notificationSpinner = findViewById(R.id.notification_spinner);  // 通知

        registerButton = findViewById(R.id.register_button);
        cancelButton = findViewById(R.id.cancel_button);
    }

    private void setupSpinners() {
        setupSpinner(medicationDosageSpinner, R.array.dosage_options);
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
