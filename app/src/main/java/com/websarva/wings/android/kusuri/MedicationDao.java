package com.websarva.wings.android.kusuri;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MedicationDao {
    // 薬をデータベースに挿入
    @Insert
    void insertMedication(Medication medication);

    // 薬のすべてのレコードを取得
    @Query("SELECT * FROM Medication")
    List<Medication> getAllMedications();

    // 作成日の降順で薬を取得
    @Query("SELECT * FROM Medication ORDER BY  createdAt DESC")
    LiveData<List<Medication>> getAllMedicationsByCreationDate();


}
