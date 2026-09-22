package com.example.theo_doi_can_nang;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theo_doi_can_nang.adapter.WeightAdapter;
import com.example.theo_doi_can_nang.data.DatabaseHelper;
import com.example.theo_doi_can_nang.data.WeightRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // =========================================================
    // VIEW
    // =========================================================

    private RecyclerView recyclerView;

    private TextView tvCurrentWeight;
    private TextView tvLastDate;
    private TextView tvBMI;
    private TextView tvBMIStatus;

    private TextView tvGoalWeight;
    private TextView tvProgress;
    private TextView tvProgressDescription;
    private TextView tvRecordCount;

    private ProgressBar progressGoal;

    private Button btnAdd;
    private ImageButton btnSettings;

    private Button btnWeek;
    private Button btnMonth;
    private Button btnYear;

    private WeightChartView weightChart;

    // =========================================================
    // DATA
    // =========================================================

    private DatabaseHelper databaseHelper;
    private WeightAdapter adapter;

    private List<WeightRecord> weightList;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // =====================================================
        // Đọc chế độ sáng / tối
        // =====================================================

        SharedPreferences prefs =
                getSharedPreferences(
                        "app_settings",
                        MODE_PRIVATE
                );

        boolean darkMode =
                prefs.getBoolean(
                        "dark_mode",
                        false
                );

        if (darkMode) {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );

        } else {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        }

        // =====================================================
        // Layout
        // =====================================================

        setContentView(R.layout.activity_main);

        // =====================================================
        // Notification Channel
        // =====================================================

        NotificationHelper.createNotificationChannel(this);

        // =====================================================
        // Xin quyền thông báo Android 13+
        // =====================================================

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.TIRAMISU) {

            if (checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        100
                );
            }
        }

        // =====================================================
        // ÁNH XẠ VIEW
        // =====================================================

        recyclerView =
                findViewById(R.id.recyclerView);

        tvCurrentWeight =
                findViewById(R.id.tvCurrentWeight);

        tvLastDate =
                findViewById(R.id.tvLastDate);

        tvBMI =
                findViewById(R.id.tvBMI);

        tvBMIStatus =
                findViewById(R.id.tvBMIStatus);

        tvGoalWeight =
                findViewById(R.id.tvGoalWeight);

        tvProgress =
                findViewById(R.id.tvProgress);

        tvProgressDescription =
                findViewById(R.id.tvProgressDescription);

        tvRecordCount =
                findViewById(R.id.tvRecordCount);

        progressGoal =
                findViewById(R.id.progressGoal);

        btnAdd =
                findViewById(R.id.btnAdd);

        btnSettings =
                findViewById(R.id.btnSettings);

        btnWeek =
                findViewById(R.id.btnWeek);

        btnMonth =
                findViewById(R.id.btnMonth);

        btnYear =
                findViewById(R.id.btnYear);

        weightChart =
                findViewById(R.id.weightChart);

        // =====================================================
        // DATABASE
        // =====================================================

        databaseHelper =
                new DatabaseHelper(this);

        // =====================================================
        // RECYCLERVIEW
        // =====================================================

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        // =====================================================
        // NÚT THÊM CÂN NẶNG
        // =====================================================

        btnAdd.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            AddEditActivity.class
                    );

            startActivity(intent);
        });

        // =====================================================
        // NÚT CÀI ĐẶT
        // =====================================================

        btnSettings.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            SettingsActivity.class
                    );

            startActivity(intent);
        });

        // =====================================================
        // BỘ LỌC BIỂU ĐỒ
        // =====================================================

        btnWeek.setOnClickListener(v ->
                updateChartByPeriod("week")
        );

        btnMonth.setOnClickListener(v ->
                updateChartByPeriod("month")
        );

        btnYear.setOnClickListener(v ->
                updateChartByPeriod("year")
        );
    }


    // =========================================================
    // ON RESUME
    // =========================================================

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadData();
        }
    }


    // =========================================================
    // LOAD DATA
    // =========================================================

    private void loadData() {

        // =====================================================
        // Lấy toàn bộ bản ghi từ SQLite
        // =====================================================

        weightList =
                databaseHelper.getAllWeights();

        // =====================================================
        // Nếu database trả về null
        // =====================================================

        if (weightList == null) {
            weightList =
                    new ArrayList<>();
        }

        // =====================================================
        // Tạo Adapter mới với toàn bộ dữ liệu
        // =====================================================

        adapter =
                new WeightAdapter(weightList);

        recyclerView.setAdapter(adapter);

        // =====================================================
        // Cập nhật thông tin tổng quan
        // =====================================================

        updateSummary();

        // =====================================================
        // Cập nhật biểu đồ
        // =====================================================

        updateChart();
    }


    // =========================================================
    // UPDATE SUMMARY
    // =========================================================

    private void updateSummary() {

        // =====================================================
        // Không có dữ liệu
        // =====================================================

        if (weightList == null ||
                weightList.isEmpty()) {

            tvCurrentWeight.setText(
                    "-- kg"
            );

            tvLastDate.setText(
                    "Chưa có dữ liệu"
            );

            tvBMI.setText(
                    "BMI: --"
            );

            tvBMIStatus.setText(
                    "Chưa có dữ liệu"
            );

            tvGoalWeight.setText(
                    "-- kg"
            );

            tvProgress.setText(
                    "0%"
            );

            tvProgressDescription.setText(
                    "Chưa có dữ liệu cân nặng"
            );

            progressGoal.setProgress(0);

            tvRecordCount.setText(
                    "0 bản ghi"
            );

            return;
        }

        // =====================================================
        // BẢN GHI MỚI NHẤT
        // =====================================================

        WeightRecord latestRecord =
                weightList.get(0);

        float currentWeight =
                latestRecord.getWeight();

        String latestDate =
                latestRecord.getDate();

        // =====================================================
        // CÂN NẶNG HIỆN TẠI
        // =====================================================

        tvCurrentWeight.setText(
                String.format(
                        Locale.getDefault(),
                        "%.1f kg",
                        currentWeight
                )
        );

        // =====================================================
        // NGÀY GẦN NHẤT
        // =====================================================

        tvLastDate.setText(
                "Ngày " +
                        convertDateForDisplay(
                                latestDate
                        )
        );

        // =====================================================
        // SỐ BẢN GHI
        // =====================================================

        tvRecordCount.setText(
                weightList.size() +
                        " bản ghi"
        );

        // =====================================================
        // SHARED PREFERENCES
        // =====================================================

        SharedPreferences prefs =
                getSharedPreferences(
                        "app_settings",
                        MODE_PRIVATE
                );

        float height =
                prefs.getFloat(
                        "height",
                        0
                );

        float startWeight =
                prefs.getFloat(
                        "start_weight",
                        0
                );

        float goalWeight =
                prefs.getFloat(
                        "goal_weight",
                        0
                );

        // =====================================================
        // BMI
        // =====================================================

        if (height > 0) {

            float bmi =
                    calculateBMI(
                            currentWeight,
                            height
                    );

            tvBMI.setText(
                    String.format(
                            Locale.getDefault(),
                            "BMI: %.2f",
                            bmi
                    )
            );

            tvBMIStatus.setText(
                    getBMIStatus(bmi)
            );

        } else {

            tvBMI.setText(
                    "BMI: --"
            );

            tvBMIStatus.setText(
                    "Chưa nhập chiều cao"
            );
        }

        // =====================================================
        // MỤC TIÊU CÂN NẶNG
        // =====================================================

        if (goalWeight > 0) {

            tvGoalWeight.setText(
                    String.format(
                            Locale.getDefault(),
                            "%.1f kg",
                            goalWeight
                    )
            );

        } else {

            tvGoalWeight.setText(
                    "Chưa đặt mục tiêu"
            );
        }

        // =====================================================
        // TIẾN ĐỘ MỤC TIÊU
        // =====================================================

        if (startWeight > 0 &&
                goalWeight > 0) {

            int progress =
                    calculateProgress(
                            startWeight,
                            currentWeight,
                            goalWeight
                    );

            progressGoal.setProgress(
                    progress
            );

            tvProgress.setText(
                    progress + "%"
            );

            tvProgressDescription.setText(
                    "Bạn đã hoàn thành " +
                            progress +
                            "% mục tiêu"
            );

        } else {

            progressGoal.setProgress(0);

            tvProgress.setText(
                    "0%"
            );

            tvProgressDescription.setText(
                    "Hãy thiết lập cân nặng ban đầu và mục tiêu"
            );
        }
    }


    // =========================================================
    // TÍNH BMI
    // =========================================================

    private float calculateBMI(
            float weight,
            float heightCm) {

        float heightM =
                heightCm / 100f;

        if (heightM <= 0) {
            return 0;
        }

        return weight /
                (heightM * heightM);
    }


    // =========================================================
    // PHÂN LOẠI BMI
    // =========================================================

    private String getBMIStatus(
            float bmi) {

        if (bmi < 18.5f) {

            return "Thiếu cân";
        }

        if (bmi < 25f) {

            return "Bình thường";
        }

        if (bmi < 30f) {

            return "Thừa cân";
        }

        return "Béo phì";
    }


    // =========================================================
    // TÍNH TIẾN ĐỘ MỤC TIÊU
    // =========================================================

    private int calculateProgress(
            float startWeight,
            float currentWeight,
            float goalWeight) {

        // =====================================================
        // Trường hợp cân nặng ban đầu
        // bằng cân nặng mục tiêu
        // =====================================================

        if (startWeight == goalWeight) {

            if (currentWeight == goalWeight) {

                return 100;
            }

            return 0;
        }

        // =====================================================
        // Công thức tính tiến độ
        // =====================================================

        float progress =
                (startWeight - currentWeight)
                        /
                        (startWeight - goalWeight)
                        * 100f;

        // =====================================================
        // Giới hạn từ 0 -> 100
        // =====================================================

        progress =
                Math.max(
                        0,
                        Math.min(
                                100,
                                progress
                        )
                );

        return Math.round(progress);
    }


    // =========================================================
    // BIỂU ĐỒ TOÀN BỘ DỮ LIỆU
    // =========================================================

    private void updateChart() {

        // =====================================================
        // Không có dữ liệu
        // =====================================================

        if (weightList == null ||
                weightList.isEmpty()) {

            weightChart.setData(
                    new ArrayList<WeightRecord>()
            );

            return;
        }

        // =====================================================
        // Copy danh sách
        // =====================================================

        ArrayList<WeightRecord> chartList =
                new ArrayList<>(
                        weightList
                );

        // =====================================================
        // Database:
        // mới nhất -> cũ nhất
        //
        // Biểu đồ:
        // cũ nhất -> mới nhất
        // =====================================================

        Collections.reverse(
                chartList
        );

        weightChart.setData(
                chartList
        );
    }


    // =========================================================
    // BIỂU ĐỒ THEO TUẦN / THÁNG / NĂM
    // =========================================================

    private void updateChartByPeriod(
            String period) {

        // =====================================================
        // Không có dữ liệu
        // =====================================================

        if (weightList == null ||
                weightList.isEmpty()) {

            weightChart.setData(
                    new ArrayList<WeightRecord>()
            );

            return;
        }

        // =====================================================
        // Calendar hiện tại
        // =====================================================

        Calendar calendar =
                Calendar.getInstance();

        Date now =
                calendar.getTime();

        calendar.setTime(now);

        // =====================================================
        // Xác định khoảng thời gian
        // =====================================================

        if (period.equals("week")) {

            // 7 ngày gần nhất
            calendar.add(
                    Calendar.DAY_OF_YEAR,
                    -7
            );

        } else if (period.equals("month")) {

            // 30 ngày gần nhất
            calendar.add(
                    Calendar.DAY_OF_YEAR,
                    -30
            );

        } else {

            // 365 ngày gần nhất
            calendar.add(
                    Calendar.DAY_OF_YEAR,
                    -365
            );
        }

        Date limitDate =
                calendar.getTime();

        // =====================================================
        // Danh sách dữ liệu lọc
        // =====================================================

        ArrayList<WeightRecord> filteredList =
                new ArrayList<>();

        // =====================================================
        // Copy danh sách
        // =====================================================

        ArrayList<WeightRecord> chartList =
                new ArrayList<>(
                        weightList
                );

        // =====================================================
        // Đảo từ mới -> cũ
        // thành cũ -> mới
        // =====================================================

        Collections.reverse(
                chartList
        );

        // =====================================================
        // Lọc dữ liệu
        // =====================================================

        for (WeightRecord record :
                chartList) {

            try {

                SimpleDateFormat sdf =
                        new SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                        );

                Date recordDate =
                        sdf.parse(
                                record.getDate()
                        );

                if (recordDate != null &&
                        !recordDate.before(
                                limitDate
                        )) {

                    filteredList.add(
                            record
                    );
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        // =====================================================
        // Hiển thị dữ liệu đã lọc
        // =====================================================

        weightChart.setData(
                filteredList
        );
    }


    // =========================================================
    // CHUYỂN yyyy-MM-dd -> dd/MM/yyyy
    // =========================================================

    private String convertDateForDisplay(
            String date) {

        try {

            SimpleDateFormat inputFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    );

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                    );

            Date parsedDate =
                    inputFormat.parse(date);

            if (parsedDate != null) {

                return outputFormat.format(
                        parsedDate
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return date;
    }
}