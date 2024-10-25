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

import com.websarva.wings.android.kusuri.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private TextView dateTextView;
    private EditText tempEditText, bpEditText, weightEditText, sugarEditText;
    private Button registerButton, deleteButton;
    private ScrollView scrollView;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        scrollView = findViewById(R.id.scrollView);

        // 日付の表示
        dateTextView = findViewById(R.id.dateTextView);
        currentDate = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).format(new Date());
        dateTextView.setText("本日 : " + currentDate);

        // レイアウトのバインディング
        tempEditText = findViewById(R.id.tempEditText);
        bpEditText = findViewById(R.id.bpEditText);
        weightEditText = findViewById(R.id.weightEditText);
        sugarEditText = findViewById(R.id.sugarEditText);

        registerButton = findViewById(R.id.registerButton);
        deleteButton = findViewById(R.id.deleteButton);

        // 登録ボタンのクリックリスナー
        registerButton.setOnClickListener(v -> {
            String temp = tempEditText.getText().toString().trim();
            String bp = bpEditText.getText().toString().trim();
            String weight = weightEditText.getText().toString().trim();
            String sugar = sugarEditText.getText().toString().trim();

            // 入力チェック：すべて空ならエラーメッセージを表示
            if (temp.isEmpty() && bp.isEmpty() && weight.isEmpty() && sugar.isEmpty()) {
                Toast.makeText(DashboardActivity.this, "登録する情報を入力してください", Toast.LENGTH_SHORT).show();
                return;
            }

            // Intentにデータを格納
            Intent resultIntent = new Intent();
            resultIntent.putExtra("date", currentDate);
            resultIntent.putExtra("temperature", temp);
            resultIntent.putExtra("bloodPressure", bp);
            resultIntent.putExtra("weight", weight);
            resultIntent.putExtra("bloodSugar", sugar);

            Toast.makeText(DashboardActivity.this, "登録しました", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, resultIntent);
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
}
