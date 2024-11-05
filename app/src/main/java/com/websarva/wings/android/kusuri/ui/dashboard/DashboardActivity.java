package com.websarva.wings.android.kusuri.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.websarva.wings.android.kusuri.AppDatabase;
import com.websarva.wings.android.kusuri.HealthCare;
import com.websarva.wings.android.kusuri.HealthCareDao;
import com.websarva.wings.android.kusuri.Medication;
import com.websarva.wings.android.kusuri.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private TextView dateTextView;

    private EditText tempEditText;           //体温
    private EditText bpUpEditText;           //血圧（上）
    private EditText bpDownEditText;        //血圧（下）
    private EditText weightEditText;        //体重
    private EditText sugarEditText;          //血糖値

    private Button registerButton, deleteButton;    //登録・キャンセルボタン
    private ScrollView scrollView;
    private String currentDate;

    private AppDatabase db;
    private HealthCareDao healthCareDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        HCinitializeViews(); // 各ビューの初期化

        scrollView = findViewById(R.id.scrollView);
        // 日付の表示
        dateTextView = findViewById(R.id.dateTextView);
        currentDate = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).format(new Date());
        dateTextView.setText("本日 : " + currentDate);

        // 登録ボタンのクリックリスナー
        registerButton.setOnClickListener(v -> {
            String temp = tempEditText.getText().toString().trim();
            String bpUp = bpUpEditText.getText().toString().trim();
            String bpDown = bpDownEditText.getText().toString().trim();
            String weight = weightEditText.getText().toString().trim();
            String sugar = sugarEditText.getText().toString().trim();

            // 入力チェック：すべて空ならエラーメッセージを表示
            if (temp.isEmpty() && bpUp.isEmpty() && bpDown.isEmpty() && weight.isEmpty() && sugar.isEmpty()) {
                Toast.makeText(DashboardActivity.this, "登録する情報を入力してください", Toast.LENGTH_SHORT).show();
                return;
            }

//            // Medication オブジェクトを作成して保存
//            HealthCare healthCare = new HealthCare();
//            healthCare.temperature= Integer.parseInt(temp);
//            healthCare.pressureUp =


            Toast.makeText(DashboardActivity.this, "登録しました", Toast.LENGTH_SHORT).show();
            finish(); // アクティビティを終了
        });


        // キャンセルボタンのクリックリスナー
        deleteButton.setOnClickListener(v -> {
            finish();
            Toast.makeText(this, "キャンセルしました", Toast.LENGTH_SHORT).show();
        });

        // キーボード表示時のスクロール設定
        View rootView = findViewById(R.id.scrollView);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
            if (heightDiff > 100) { // キーボードが表示されている場合
                View focusedView = getCurrentFocus();
                if (focusedView instanceof EditText) {
                    scrollView.post(() -> scrollView.smoothScrollTo(0, focusedView.getBottom()));
                }
            }
        });


    }

    //服薬登録画面から画面部品の取得
    private void HCinitializeViews() {
        tempEditText = findViewById(R.id.tempEditText);                       //体温
        bpUpEditText = findViewById(R.id.bpUpEditText);                       //血圧(上)
        bpDownEditText = findViewById(R.id.bpDownEditText);                   //血圧（下）
        weightEditText = findViewById(R.id.weightEditText);                   //体重
        sugarEditText = findViewById(R.id.sugarEditText);                     //血糖値
        registerButton = findViewById(R.id.registerButton);                   //登録ボタン
        deleteButton = findViewById(R.id.deleteButton);                       //キャンセルボタン
    }
}
