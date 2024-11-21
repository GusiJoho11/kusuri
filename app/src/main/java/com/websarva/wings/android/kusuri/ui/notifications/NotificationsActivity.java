package com.websarva.wings.android.kusuri.ui.notifications;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.websarva.wings.android.kusuri.AppDatabase;
import com.websarva.wings.android.kusuri.MainActivity;
import com.websarva.wings.android.kusuri.Medication;
import com.websarva.wings.android.kusuri.MedicationDao;
import com.websarva.wings.android.kusuri.R;
import com.websarva.wings.android.kusuri.ui.dashboard.DashboardActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotificationsActivity extends AppCompatActivity {

    private EditText medicineNameEdit;              //おくすり名
    private EditText  dosageEdit;                   //服用量の入力
    private Spinner medicationDosageSpinner;        //錠・包
    private EditText doscountEdit;                  //服薬回数
    private Spinner timingSpinner;                  //服薬タイミング
    private EditText medicationStartDateInput;      //服薬開始
    private EditText medicationEndDateInput;        //服薬終了
    private EditText memoEdit;                      //メモ
    private Spinner notificationSpinner;            //通知
    private Button registerButton, cancelButton;    //登録・キャンセルボタン

    private AppDatabase db;
    private MedicationDao medicationDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        initializeViews(); // 各ビューの初期化
        setupSpinners();   // Spinnerにアダプターを設定

        // 服用開始日入力欄をクリックしたときにDatePickerを表示
        medicationStartDateInput.setOnClickListener(v -> showDatePickerDialog(medicationStartDateInput));
        // 服用終了日入力欄をクリックしたときにDatePickerを表示
        medicationEndDateInput.setOnClickListener(v -> showDatePickerDialog(medicationEndDateInput));

        // 登録ボタンのクリックイベント
        registerButton.setOnClickListener(v -> {
            String medicineName = medicineNameEdit.getText().toString();                 //おくすり名
            String dosage = dosageEdit.getText().toString();                             //服用量
            String dosage_jo_ho = medicationDosageSpinner.getSelectedItem().toString();  //錠・包
            String dosageCount = doscountEdit.getText().toString();                      //服用回数
            String md_timing = timingSpinner.getSelectedItem().toString();              //服薬タイミング
            String startDateLong = medicationStartDateInput.getText().toString();       //服薬開始日
            String endDateLong = medicationEndDateInput.getText().toString();           //服薬終了日
            String memo = memoEdit.getText().toString();                                //メモ
            String notification = notificationSpinner.getSelectedItem().toString();     //通知

            if (medicineName.isEmpty()) {
                Toast.makeText(this, "おくすり名を入力してください", Toast.LENGTH_SHORT).show();
                return;
            }

            try {

            // 日付をタイムスタンプ(long)に変換する
            long StartDateLong = convertDateToTimestamp(startDateLong);
            long EndDateLong = convertDateToTimestamp(endDateLong);

            // Medication オブジェクトを作成して保存
            Medication medication = new Medication();
            medication.name = medicineName;
            medication.dosage = Integer.parseInt(dosage);
            medication.dosageSpinner = dosage_jo_ho;
            medication.frequency = Integer.parseInt(dosageCount);
            medication.timing = md_timing;
            medication.startdate = StartDateLong;
            medication.enddate = EndDateLong;
            medication.memo = memo;
            medication.reminder = notification;  // リマインダー設定


        // データベースに薬情報を挿入（バックグラウンドスレッドで処理）
        new Thread(() -> {
            medicationDao.insertMedication(medication);
//            runOnUiThread(this::displayMedications);  // メインスレッドでリストを更新

        }).start();
        } catch (NumberFormatException e) {
            if (this != null) {
                Toast.makeText(this, "全ての項目に値を入力してください。", Toast.LENGTH_LONG).show();
            }
        } catch (ParseException e){
            if (this != null) {
                Toast.makeText(this, "日付の形式が正しくありません。", Toast.LENGTH_LONG).show();
            }
        }
            Toast.makeText(NotificationsActivity.this, "おくすりを登録しました", Toast.LENGTH_LONG).show();
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

    // DatePickerDialogを表示し、選択した日付をEditTextにセットする
    private void showDatePickerDialog(EditText dateInput) {
        // 現在の日付を取得
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // DatePickerDialogを表示
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            // 選択された日付を "yyyy/MM/dd" の形式でEditTextにセット
            String selectedDate = year1 + "/" + (month1 + 1) + "/" + dayOfMonth;
            dateInput.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    //タイムスタンプに直してデータベースに登録
    private long convertDateToTimestamp(String dateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        Date date = dateFormat.parse(dateStr);
        return date != null ? date.getTime() : 0;  // nullの場合は0を返す
    }

    //おくすり登録画面から画面部品の取得
    private void initializeViews() {
        medicineNameEdit = findViewById(R.id.medicine_name_edit);               // 薬の名前
        dosageEdit = findViewById(R.id.dosage_input);                           // 服用量
        medicationDosageSpinner = findViewById(R.id.dosage_spinner);            // 錠・包
        doscountEdit = findViewById(R.id.doscount_input);                       // 服用回数
        timingSpinner = findViewById(R.id.MDtiming_spinner);                 //服薬タイミング
        medicationStartDateInput = findViewById(R.id.medication_startdate);     //服薬開始日
        medicationEndDateInput = findViewById(R.id.medication_enddate);         //服薬終了日
        memoEdit = findViewById(R.id.memo_edit);                                // メモ
        notificationSpinner = findViewById(R.id.notification_spinner);          // 通知
        registerButton = findViewById(R.id.register_button);                    //登録ボタン
        cancelButton = findViewById(R.id.cancel_button);                        //キャンセルボタン

    }

//錠・包と、リマインダーのドロップダウンリストの画面部品を取得
    private void setupSpinners() {
        setupSpinner(medicationDosageSpinner, R.array.dosage_options);
        setupSpinner(notificationSpinner, R.array.notification_options);
    }

    private void setupSpinner(Spinner spinner, int arrayResId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }



}
