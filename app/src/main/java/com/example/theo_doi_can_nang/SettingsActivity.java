package com.example.theo_doi_can_nang;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
        // ÁNH XẠ VIEW
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
        // ĐỌC CÀI ĐẶT
        // ===============================

        getSavedSettings();

        // ===============================
        // ĐỔI KG / LBS
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
        // CHỌN GIỜ NHẮC
        // ===============================

        btnReminderTime.setOnClickListener(
                v -> showTimePicker()
        );

        // ===============================
        // LƯU
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
        // CHIỀU CAO
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
        // CÂN NẶNG BAN ĐẦU
        // ===============================

        float startWeightKg =
                prefs.getFloat(
                        "start_weight",
                        0
                );

        // ===============================
        // CÂN NẶNG MỤC TIÊU
        // ===============================

        float goalWeightKg =
                prefs.getFloat(
                        "goal_weight",
                        0
                );

        // ===============================
        // ĐƠN VỊ
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
                        String.valueOf(startWeightKg)
                );
            }

            if (goalWeightKg > 0) {

                edtGoalWeight.setText(
                        String.valueOf(goalWeightKg)
                );
            }
        }

        // ===============================
        // GIỜ NHẮC
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
        // TRẠNG THÁI NHẮC NHỞ
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
        // DARK MODE
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
        // KIỂM TRA RỖNG
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
            // KIỂM TRA GIÁ TRỊ
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
            // XÁC ĐỊNH ĐƠN VỊ
            // ===============================

            String unit;

            if (radioLbs.isChecked()) {

                unit = "lbs";

            } else {

                unit = "kg";
            }

            // ===============================
            // CHUYỂN VỀ KG
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
            // DARK MODE
            // ===============================

            boolean darkMode =
                    switchDarkMode.isChecked();

            // ===============================
            // NHẮC NHỞ
            // ===============================

            boolean reminderEnabled =
                    switchReminder.isChecked();

            // ===============================
            // LƯU SHAREDPREFERENCES
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
            // DARK MODE
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
            // XỬ LÝ NHẮC NHỞ
            // ===============================

            if (reminderEnabled) {

                setupReminder();

            } else {

                ReminderManager.cancelReminder(
                        this
                );

                Toast.makeText(
                        this,
                        "Đã tắt nhắc nhở",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            }

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Dữ liệu không hợp lệ",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =========================================================
    // THIẾT LẬP REMINDER
    // =========================================================

    private void setupReminder() {

        // Android 12 trở lên
        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.S) {

            if (!ReminderManager.canScheduleExactAlarm(
                    this)) {

                Toast.makeText(
                        this,
                        "Cần cấp quyền Báo thức và lời nhắc",
                        Toast.LENGTH_LONG
                ).show();

                try {

                    Intent intent =
                            new Intent(
                                    Settings
                                            .ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                            );

                    startActivity(intent);

                } catch (Exception e) {

                    e.printStackTrace();

                    Toast.makeText(
                            this,
                            "Không thể mở cài đặt quyền báo thức",
                            Toast.LENGTH_LONG
                    ).show();
                }

                return;
            }
        }

        // Đã có quyền → đặt alarm
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

        finish();
    }

    // =========================================================
    // lbs -> kg
    // =========================================================

    private float lbsToKg(float lbs) {

        return lbs * 0.45359237f;
    }

    // =========================================================
    // kg -> lbs
    // =========================================================

    private float kgToLbs(float kg) {

        return kg * 2.20462262f;
    }
}