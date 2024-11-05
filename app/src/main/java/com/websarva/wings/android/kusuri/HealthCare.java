package com.websarva.wings.android.kusuri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity
public class HealthCare {
    @PrimaryKey(autoGenerate = true)
    public int id;                  //自動生成されるID
    public double temperature;        //体温
    public int pressureUp;            //血圧（上）
    public int pressureDown;            //血圧（下）
    public double weight;              //体重
    public int sugar;

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    public long createdAt;     // 登録日時（Unixタイムスタンプ）

    // デフォルトのコンストラクタで現在のタイムスタンプを設定
    public HealthCare() {
//        this.name = "";
//        this.dosage = 0;
//        this.frequency = 0;
//        this.startdate = 0;
//        this.enddate = 0;
//        this.reminder = "false";
        this.createdAt = System.currentTimeMillis(); // 現在のタイムスタンプを設定
    }

    // 日付をフォーマットして返すメソッド
    public String getFormattedCreationDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(createdAt));
    }
}
