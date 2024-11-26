package com.websarva.wings.android.kusuri.ui.notifications;

import static android.text.Selection.setSelection;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.websarva.wings.android.kusuri.AppDatabase;
import com.websarva.wings.android.kusuri.Medication;
import com.websarva.wings.android.kusuri.MedicationDao;
import com.websarva.wings.android.kusuri.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditMedicationFragment extends Fragment {
    private EditText medicationNameInput, medicationDosageInput, medicationFrequencyInput,
            medicationStartDateInput, medicationEndDateInput, medicationMemoInput;
    private Spinner medicationTimingSpinner,medicationDosageSpinner, medicationReminderInput;
    private MedicationDao medicationDao;
    private Medication currentMedication;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_medication, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        medicationDao = AppDatabase.getDatabase(requireContext()).medicationDao();
        medicationNameInput = view.findViewById(R.id.medicine_name_ud);     //おくすり名
        medicationFrequencyInput = view.findViewById(R.id.doscount_input_ud);  //服用回数
        medicationDosageInput = view.findViewById(R.id.dosage_input_ud);    //服用量
        medicationDosageSpinner = view.findViewById(R.id.dosage_spinner_ud);//錠・包
        medicationTimingSpinner = view.findViewById(R.id.MDtiming_spinner_ud); //服薬タイミング
        medicationStartDateInput = view.findViewById(R.id.medication_startdate_ud);//服用期間（開始）
        medicationEndDateInput = view.findViewById(R.id.medication_enddate_ud);//服用期間（終了）
        medicationMemoInput = view.findViewById(R.id.memo_edit_ud);//メモ
        medicationReminderInput = view.findViewById(R.id.notification_spinner_ud);//通知（リマインダー）
        Button saveButton = view.findViewById(R.id.register_button_ud);
        Button cancelButton = view.findViewById(R.id.cancel_button_ud);


        int medicationId = getArguments().getInt("medicationId", -1);

        // IDからMedicationを取得し、各フィールドに設定
        new Thread(() -> {
            currentMedication = medicationDao.getMedicationById(medicationId);
            if (currentMedication != null) {
                getActivity().runOnUiThread(() -> {
                    medicationNameInput.setText(currentMedication.name);
                    medicationFrequencyInput.setText(String.valueOf(currentMedication.frequency));
                    medicationDosageInput.setText(String.valueOf(currentMedication.dosage));
                    medicationStartDateInput.setText(String.valueOf(currentMedication.getStartDate()));
                    medicationEndDateInput.setText(String.valueOf(currentMedication.getEndDate()));
                    medicationMemoInput.setText(String.valueOf(currentMedication.memo));

                    //　Spinnerの項目から一致するインデックスを探して設定（錠・包）
                    String dosageSpinner = currentMedication.dosageSpinner;
                    for (int i = 0; i < medicationDosageSpinner.getCount(); i++) {
                        if (medicationDosageSpinner.getItemAtPosition(i).toString().equals(dosageSpinner)) {
                            medicationDosageSpinner.setSelection(i);
                            break;
                        }
                    }

                    //　Spinnerの項目から一致するインデックスを探して設定（服薬タイミング）
                    String timingSpinner = currentMedication.timing;
                    for (int i = 0; i < medicationTimingSpinner.getCount(); i++) {
                        if (medicationTimingSpinner.getItemAtPosition(i).toString().equals(timingSpinner)) {
                            medicationTimingSpinner.setSelection(i);
                            break;
                        }
                    }

                    //　Spinnerの項目から一致するインデックスを探して設定（通知）
                    String reminder = currentMedication.reminder;
                    for (int i = 0; i < medicationReminderInput.getCount(); i++) {
                        if (medicationReminderInput.getItemAtPosition(i).toString().equals(reminder)) {
                            medicationReminderInput.setSelection(i);
                            break;
                        }
                    }
                });
            }
        }).start();

        // 服用開始日入力欄をクリックしたときにDatePickerを表示
        medicationStartDateInput.setOnClickListener(
                v -> showDatePickerDialog(medicationStartDateInput,currentMedication.startdate));
        // 服用終了日入力欄をクリックしたときにDatePickerを表示
        medicationEndDateInput.setOnClickListener(
                v -> showDatePickerDialog(medicationEndDateInput,currentMedication.enddate));


        // 保存ボタンでデータを更新
        saveButton.setOnClickListener(v -> {
            Log.d("EditMedicationFragment", "EditMedicationFragment saveButton.setOnClickListener");
            String name = medicationNameInput.getText().toString();
            int frequency = Integer.parseInt(medicationFrequencyInput.getText().toString());
            int dosage = Integer.parseInt(medicationDosageInput.getText().toString());
            String dosageSpinner = medicationDosageSpinner.getSelectedItem().toString();
            String timingSpinner = medicationTimingSpinner.getSelectedItem().toString();
            String startdate = medicationStartDateInput.getText().toString();
            String enddate = medicationEndDateInput.getText().toString();
            String memo = medicationMemoInput.getText().toString();
            String reminder = medicationReminderInput.getSelectedItem().toString();

            new Thread(() -> {
                currentMedication.name = name;
                currentMedication.frequency = frequency;
                currentMedication.dosage = dosage;
                currentMedication.dosageSpinner = dosageSpinner;
                currentMedication.timing = timingSpinner;
                try {
                    currentMedication.startdate = convertDateToTimestamp(startdate);
                    currentMedication.enddate = convertDateToTimestamp(enddate);
                } catch (ParseException ex) {
                    currentMedication.startdate = System.currentTimeMillis();
                    currentMedication.enddate = System.currentTimeMillis();
                }

                currentMedication.memo = memo;
                currentMedication.reminder = reminder;
                medicationDao.updateMedication(currentMedication);
            }).start();

            getActivity().getSupportFragmentManager().popBackStack();
        });

        // キャンセルボタンのクリックイベント
        cancelButton.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();// 画面を閉じる
            Toast.makeText(getActivity(), "キャンセルしました", Toast.LENGTH_SHORT).show();
        });

    }



    //タイムスタンプに直してデータベースに登録
    private long convertDateToTimestamp(String dateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        Date date = dateFormat.parse(dateStr);
        return date != null ? date.getTime() : 0;  // nullの場合は0を返す
    }


    // DatePickerDialogを表示し、選択した日付をEditTextにセットする
    private void showDatePickerDialog(EditText dateInput, long timestamp) {
        // 現在の日付を取得
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // DatePickerDialogを表示
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, year1, month1, dayOfMonth) -> {
            // 選択された日付を "yyyy/MM/dd" の形式でEditTextにセット
            String selectedDate = year1 + "/" + (month1 + 1) + "/" + dayOfMonth;
            dateInput.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }
}
