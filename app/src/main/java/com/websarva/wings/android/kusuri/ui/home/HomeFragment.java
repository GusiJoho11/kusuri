package com.websarva.wings.android.kusuri.ui.home;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//import com.example.kusuri.databinding.FragmentHomeBinding;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.websarva.wings.android.kusuri.databinding.FragmentHomeBinding;
import com.websarva.wings.android.kusuri.ui.home.DotDecorator;

import java.util.HashSet;
import java.util.Set;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MaterialCalendarView calendarView;
    private CheckBox morningCheckBox, noonCheckBox, eveningCheckBox;
    private CalendarDay selectedDate;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // UIコンポーネントの取得
        calendarView = binding.calendarView;
        morningCheckBox = binding.morningCh;
        noonCheckBox = binding.noonCh;
        eveningCheckBox = binding.eveningCh;

        // SharedPreferences の初期化
        preferences = getActivity().getSharedPreferences("calendar_prefs", 0);

        // アプリ起動時に保存されたドットを復元
        loadSavedDots();

        // 今日の日付を取得して選択状態にする
        CalendarDay today = CalendarDay.today();
        calendarView.setDateSelected(today, true); // 今日の日付を選択
        selectedDate = today;  // 選択された日付を保持

        // 今日の日付のチェックボックスの状態を復元
        String dateKey = getDateKey(selectedDate);
        boolean morningChecked = preferences.getBoolean(dateKey + "_morning", false);
        boolean noonChecked = preferences.getBoolean(dateKey + "_noon", false);
        boolean eveningChecked = preferences.getBoolean(dateKey + "_evening", false);

        morningCheckBox.setChecked(morningChecked);
        noonCheckBox.setChecked(noonChecked);
        eveningCheckBox.setChecked(eveningChecked);

        // カレンダーの日付選択リスナーを設定
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = date; // 選択された日付を保持
            String newDateKey = getDateKey(selectedDate); // 日付をキーに変換

            // 新しく選択された日付に対するチェックボックスの状態を復元
            boolean newMorningChecked = preferences.getBoolean(newDateKey + "_morning", false);
            boolean newNoonChecked = preferences.getBoolean(newDateKey + "_noon", false);
            boolean newEveningChecked = preferences.getBoolean(newDateKey + "_evening", false);

            morningCheckBox.setChecked(newMorningChecked);
            noonCheckBox.setChecked(newNoonChecked);
            eveningCheckBox.setChecked(newEveningChecked);

            updateCalendarMarker(); // デコレーターを更新
        });

        // チェックボックスのリスナーを設定
        morningCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateCalendarMarker());
        noonCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateCalendarMarker());
        eveningCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateCalendarMarker());
    }

    private void updateCalendarMarker() {
        if (selectedDate == null) return;

        // 選択された日付をキーとしてチェックボックスの状態を保存
        String dateKey = getDateKey(selectedDate);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(dateKey + "_morning", morningCheckBox.isChecked());
        editor.putBoolean(dateKey + "_noon", noonCheckBox.isChecked());
        editor.putBoolean(dateKey + "_evening", eveningCheckBox.isChecked());

        // すべてのチェックボックスがチェックされている場合は日付を保存
        Set<String> savedDates = preferences.getStringSet("saved_dates", new HashSet<>());
        if (morningCheckBox.isChecked() && noonCheckBox.isChecked() && eveningCheckBox.isChecked()) {
            savedDates.add(dateKey); // チェック済みの日付を保存
        } else {
            savedDates.remove(dateKey); // チェックが外れた場合は削除
        }

        editor.putStringSet("saved_dates", savedDates);
        editor.apply();

        // カレンダーにデコレーターを追加または削除
        calendarView.removeDecorators();
        for (String savedDateKey : savedDates) {
            CalendarDay day = getCalendarDayFromKey(savedDateKey); // キーからCalendarDayを復元
            calendarView.addDecorator(new DotDecorator(day, Color.RED));
        }
        calendarView.invalidateDecorators();
    }

    private void loadSavedDots() {
        // 保存された日付のドットを復元
        Set<String> savedDates = preferences.getStringSet("saved_dates", new HashSet<>());
        for (String savedDateKey : savedDates) {
            CalendarDay day = getCalendarDayFromKey(savedDateKey); // キーからCalendarDayを復元
            calendarView.addDecorator(new DotDecorator(day, Color.RED));
        }
    }

    // CalendarDay から一意のキーを生成
    private String getDateKey(CalendarDay date) {
        return date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
    }

    // 一意のキーから CalendarDay を生成
    private CalendarDay getCalendarDayFromKey(String dateKey) {
        String[] parts = dateKey.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        return CalendarDay.from(year, month, day);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
