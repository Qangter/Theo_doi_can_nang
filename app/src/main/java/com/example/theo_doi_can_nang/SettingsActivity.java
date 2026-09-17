package com.example.theo_doi_can_nang;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private EditText edtHeight;
    private EditText edtStartWeight;
    private EditText edtGoalWeight;

    private TextView tvStartWeightLabel;
    private TextView tvGoalWeightLabel;

    private RadioGroup radioUnit;
    private RadioButton radioKg;
    private RadioButton radioLbs;

    private Button btnReminderTime;
    private Button btnSaveSettings;

    private SwitchCompat switchReminder;
    private SwitchCompat switchDarkMode;

    private int reminderHour = 7;
    private int reminderMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        // ===============================
        // Ánh xạ các View
        // ===============================

        edtHeight =
                findViewById(R.id.edtHeight);

        edtStartWeight =
                findViewById(R.id.edtStartWeight);

        edtGoalWeight =
                findViewById(R.id.edtGoalWeight);

        tvStartWeightLabel =
                findViewById(R.id.tvStartWeightLabel);

        tvGoalWeightLabel =
                findViewById(R.id.tvGoalWeightLabel);

        radioUnit =
                findViewById(R.id.radioUnit);

        radioKg =
                findViewById(R.id.radioKg);

        radioLbs =
                findViewById(R.id.radioLbs);

        btnReminderTime =
                findViewById(R.id.btnReminderTime);

        btnSaveSettings =
                findViewById(R.id.btnSaveSettings);

        switchReminder =
                findViewById(R.id.switchReminder);

        switchDarkMode =
                findViewById(R.id.switchDarkMode);

        // ===============================
        // Đọc cài đặt đã lưu
        // ===============================

        getSavedSettings();

        // ===============================
        // Thay đổi đơn vị kg / lbs
        // ===============================

        radioUnit.setOnCheckedChangeListener(
                (group, checkedId) -> {

                    if (checkedId == R.id.radioLbs) {

                        tvStartWeightLabel.setText(
                                "Cân nặng ban đầu (lbs)"
                        );

                        tvGoalWeightLabel.setText(
                                "Cân nặng mục tiêu (lbs)"
                        );

                        edtStartWeight.setHint(
                                "Ví dụ: 154.3"
                        );

                        edtGoalWeight.setHint(
                                "Ví dụ: 143.3"
                        );

                    } else {

                        tvStartWeightLabel.setText(
                                "Cân nặng ban đầu (kg)"
                        );

                        tvGoalWeightLabel.setText(
                                "Cân nặng mục tiêu (kg)"
                        );

                        edtStartWeight.setHint(
                                "Ví dụ: 70"
                        );

                        edtGoalWeight.setHint(
                                "Ví dụ: 65"
                        );
                    }
                }
        );

        // ===============================
        // Chọn giờ nhắc
        // ===============================

        btnReminderTime.setOnClickListener(
                v -> showTimePicker()
        );

        // ===============================
        // Lưu cài đặt
        // ===============================

        btnSaveSettings.setOnClickListener(
                v -> saveSettings()
        );
    }

    // =========================================================
    // ĐỌC CÀI ĐẶT ĐÃ LƯU
    // =========================================================

    private void getSavedSettings() {

        SharedPreferences prefs =
                getSharedPreferences(
                        "app_settings",
                        MODE_PRIVATE
                );

        // ===============================
        // Chiều cao
        // ===============================

        float height =
                prefs.getFloat(
                        "height",
                        0
                );

        if (height > 0) {

            edtHeight.setText(
                    String.valueOf(height)
            );
        }

        // ===============================
        // Cân nặng ban đầu
        // ===============================

        float startWeightKg =
                prefs.getFloat(
                        "start_weight",
                        0
                );

        // ===============================
        // Cân nặng mục tiêu
        // ===============================

        float goalWeightKg =
                prefs.getFloat(
                        "goal_weight",
                        0
                );

        // ===============================
        // Đơn vị
        // ===============================

        String unit =
                prefs.getString(
                        "weight_unit",
                        "kg"
                );

        if (unit.equals("lbs")) {

            radioLbs.setChecked(true);

            if (startWeightKg > 0) {

                float startWeightLbs =
                        kgToLbs(startWeightKg);

                edtStartWeight.setText(
                        String.format(
                                Locale.getDefault(),
                                "%.1f",
                                startWeightLbs
                        )
                );
            }

            if (goalWeightKg > 0) {

                float goalWeightLbs =
                        kgToLbs(goalWeightKg);

                edtGoalWeight.setText(
                        String.format(
                                Locale.getDefault(),
                                "%.1f",
                                goalWeightLbs
                        )
                );
            }

        } else {

            radioKg.setChecked(true);

            if (startWeightKg > 0) {

                edtStartWeight.setText(
                        String.valueOf(
                                startWeightKg
                        )
                );
            }

            if (goalWeightKg > 0) {

                edtGoalWeight.setText(
                        String.valueOf(
                                goalWeightKg
                        )
                );
            }
        }

        // ===============================
        // Giờ nhắc
        // ===============================

        reminderHour =
                prefs.getInt(
                        "reminder_hour",
                        7
                );

        reminderMinute =
                prefs.getInt(
                        "reminder_minute",
                        0
                );

        updateReminderTimeText();

        // ===============================
        // Trạng thái nhắc nhở
        // ===============================

        boolean reminderEnabled =
                prefs.getBoolean(
                        "reminder_enabled",
                        false
                );

        switchReminder.setChecked(
                reminderEnabled
        );

        // ===============================
        // Trạng thái Dark Mode
        // ===============================

        boolean darkMode =
                prefs.getBoolean(
                        "dark_mode",
                        false
                );

        switchDarkMode.setChecked(
                darkMode
        );
    }

    // =========================================================
    // CHỌN GIỜ
    // =========================================================

    private void showTimePicker() {

        TimePickerDialog dialog =
                new TimePickerDialog(
                        this,
                        (view, hourOfDay, minute) -> {

                            reminderHour =
                                    hourOfDay;

                            reminderMinute =
                                    minute;

                            updateReminderTimeText();
                        },
                        reminderHour,
                        reminderMinute,
                        true
                );

        dialog.show();
    }

    // =========================================================
    // HIỂN THỊ GIỜ
    // =========================================================

    private void updateReminderTimeText() {

        String time =
                String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        reminderHour,
                        reminderMinute
                );

        btnReminderTime.setText(time);
    }

    // =========================================================
    // LƯU CÀI ĐẶT
    // =========================================================

    private void saveSettings() {

        String heightText =
                edtHeight.getText()
                        .toString()
                        .trim();

        String startWeightText =
                edtStartWeight.getText()
                        .toString()
                        .trim();

        String goalWeightText =
                edtGoalWeight.getText()
                        .toString()
                        .trim();

        // ===============================
        // Kiểm tra dữ liệu rỗng
        // ===============================

        if (heightText.isEmpty()) {

            edtHeight.setError(
                    "Vui lòng nhập chiều cao"
            );

            return;
        }

        if (startWeightText.isEmpty()) {

            edtStartWeight.setError(
                    "Vui lòng nhập cân nặng ban đầu"
            );

            return;
        }

        if (goalWeightText.isEmpty()) {

            edtGoalWeight.setError(
                    "Vui lòng nhập cân nặng mục tiêu"
            );

            return;
        }

        try {

            heightText =
                    heightText.replace(",", ".");

            startWeightText =
                    startWeightText.replace(",", ".");

            goalWeightText =
                    goalWeightText.replace(",", ".");

            float height =
                    Float.parseFloat(
                            heightText
                    );

            float startWeight =
                    Float.parseFloat(
                            startWeightText
                    );

            float goalWeight =
                    Float.parseFloat(
                            goalWeightText
                    );

            // ===============================
            // Kiểm tra giá trị
            // ===============================

            if (height <= 0) {

                edtHeight.setError(
                        "Chiều cao phải lớn hơn 0"
                );

                return;
            }

            if (startWeight <= 0) {

                edtStartWeight.setError(
                        "Cân nặng phải lớn hơn 0"
                );

                return;
            }

            if (goalWeight <= 0) {

                edtGoalWeight.setError(
                        "Mục tiêu phải lớn hơn 0"
                );

                return;
            }

            // ===============================
            // Xác định đơn vị
            // ===============================

            String unit;

            if (radioLbs.isChecked()) {

                unit = "lbs";

            } else {

                unit = "kg";
            }

            // ===============================
            // Chuyển về kg để lưu
            // ===============================

            float startWeightKg =
                    startWeight;

            float goalWeightKg =
                    goalWeight;

            if (unit.equals("lbs")) {

                startWeightKg =
                        lbsToKg(startWeight);

                goalWeightKg =
                        lbsToKg(goalWeight);
            }

            // ===============================
            // Dark Mode
            // ===============================

            boolean darkMode =
                    switchDarkMode.isChecked();

            // ===============================
            // Nhắc nhở
            // ===============================

            boolean reminderEnabled =
                    switchReminder.isChecked();

            // ===============================
            // Lưu SharedPreferences
            // ===============================

            SharedPreferences prefs =
                    getSharedPreferences(
                            "app_settings",
                            MODE_PRIVATE
                    );

            prefs.edit()
                    .putFloat(
                            "height",
                            height
                    )
                    .putFloat(
                            "start_weight",
                            startWeightKg
                    )
                    .putFloat(
                            "goal_weight",
                            goalWeightKg
                    )
                    .putString(
                            "weight_unit",
                            unit
                    )
                    .putBoolean(
                            "dark_mode",
                            darkMode
                    )
                    .putInt(
                            "reminder_hour",
                            reminderHour
                    )
                    .putInt(
                            "reminder_minute",
                            reminderMinute
                    )
                    .putBoolean(
                            "reminder_enabled",
                            reminderEnabled
                    )
                    .apply();

            // ===============================
            // Áp dụng Dark Mode
            // ===============================

            if (darkMode) {

                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                );

            } else {

                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                );
            }

            // ===============================
            // Cài đặt nhắc nhở
            // ===============================

            if (reminderEnabled) {

                ReminderManager.setReminder(
                        this,
                        reminderHour,
                        reminderMinute
                );

                Toast.makeText(
                        this,
                        "Đã bật nhắc nhở lúc " +
                                String.format(
                                        Locale.getDefault(),
                                        "%02d:%02d",
                                        reminderHour,
                                        reminderMinute
                                ),
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                ReminderManager.cancelReminder(
                        this
                );

                Toast.makeText(
                        this,
                        "Đã tắt nhắc nhở",
                        Toast.LENGTH_SHORT
                ).show();
            }

            finish();

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Dữ liệu không hợp lệ",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =========================================================
    // CHUYỂN lbs -> kg
    // =========================================================

    private float lbsToKg(float lbs) {

        return lbs * 0.45359237f;
    }

    // =========================================================
    // CHUYỂN kg -> lbs
    // =========================================================

    private float kgToLbs(float kg) {

        return kg * 2.20462262f;
    }
}