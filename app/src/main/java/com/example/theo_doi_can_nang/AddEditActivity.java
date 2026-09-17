package com.example.theo_doi_can_nang;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theo_doi_can_nang.data.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditActivity extends AppCompatActivity {

    private EditText edtDate;
    private EditText edtWeight;
    private EditText edtNote;

    private TextView tvWeightLabel;

    private DatabaseHelper databaseHelper;

    private int recordId = -1;

    // Đơn vị hiện tại
    private String weightUnit = "kg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_edit);

        // =========================================
        // Ánh xạ View
        // =========================================

        edtDate = findViewById(R.id.edtDate);
        edtWeight = findViewById(R.id.edtWeight);
        edtNote = findViewById(R.id.edtNote);

        tvWeightLabel =
                findViewById(R.id.tvWeightLabel);

        Button btnSave =
                findViewById(R.id.btnSave);

        Button btnCancel =
                findViewById(R.id.btnCancel);

        databaseHelper =
                new DatabaseHelper(this);

        // =========================================
        // Đọc đơn vị kg / lbs
        // =========================================

        android.content.SharedPreferences prefs =
                getSharedPreferences(
                        "app_settings",
                        MODE_PRIVATE
                );

        weightUnit =
                prefs.getString(
                        "weight_unit",
                        "kg"
                );

        updateWeightUnitUI();

        // =========================================
        // Kiểm tra thêm hay sửa
        // =========================================

        recordId =
                getIntent().getIntExtra(
                        "id",
                        -1
                );

        if (recordId != -1) {

            // =====================================
            // Chế độ SỬA
            // =====================================

            setTitle("Sửa cân nặng");

            String date =
                    getIntent().getStringExtra(
                            "date"
                    );

            float weightKg =
                    getIntent().getFloatExtra(
                            "weight",
                            0
                    );

            String note =
                    getIntent().getStringExtra(
                            "note"
                    );

            // Hiển thị ngày
            edtDate.setText(
                    convertDateForDisplay(date)
            );

            // Database luôn lưu KG
            // Nếu giao diện đang dùng lbs
            // thì đổi kg → lbs

            float displayWeight =
                    weightKg;

            if (weightUnit.equals("lbs")) {

                displayWeight =
                        kgToLbs(weightKg);
            }

            edtWeight.setText(
                    String.format(
                            Locale.getDefault(),
                            "%.1f",
                            displayWeight
                    )
            );

            if (note != null) {
                edtNote.setText(note);
            }

        } else {

            // =====================================
            // Chế độ THÊM
            // =====================================

            setTitle("Thêm cân nặng");

            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                    );

            edtDate.setText(
                    sdf.format(new Date())
            );
        }

        // =========================================
        // Nút Lưu
        // =========================================

        btnSave.setOnClickListener(
                v -> saveWeight()
        );

        // =========================================
        // Nút Hủy
        // =========================================

        btnCancel.setOnClickListener(
                v -> finish()
        );
    }

    // =================================================
    // Cập nhật giao diện kg / lbs
    // =================================================

    private void updateWeightUnitUI() {

        if (weightUnit.equals("lbs")) {

            tvWeightLabel.setText(
                    "Cân nặng (lbs)"
            );

            edtWeight.setHint(
                    "Ví dụ: 154.3"
            );

        } else {

            tvWeightLabel.setText(
                    "Cân nặng (kg)"
            );

            edtWeight.setHint(
                    "Ví dụ: 70"
            );
        }
    }

    // =================================================
    // Lưu cân nặng
    // =================================================

    private void saveWeight() {

        String date =
                edtDate.getText()
                        .toString()
                        .trim();

        String weightText =
                edtWeight.getText()
                        .toString()
                        .trim();

        String note =
                edtNote.getText()
                        .toString()
                        .trim();

        // =========================================
        // Kiểm tra ngày
        // =========================================

        if (date.isEmpty()) {

            edtDate.setError(
                    "Vui lòng nhập ngày"
            );

            return;
        }

        // =========================================
        // Kiểm tra cân nặng
        // =========================================

        if (weightText.isEmpty()) {

            edtWeight.setError(
                    "Vui lòng nhập cân nặng"
            );

            return;
        }

        try {

            weightText =
                    weightText.replace(
                            ",",
                            "."
                    );

            float inputWeight =
                    Float.parseFloat(
                            weightText
                    );

            if (inputWeight <= 0) {

                edtWeight.setError(
                        "Cân nặng phải lớn hơn 0"
                );

                return;
            }

            // =====================================
            // Chuyển cân nặng về KG
            // =====================================

            float weightKg =
                    inputWeight;

            if (weightUnit.equals("lbs")) {

                weightKg =
                        lbsToKg(inputWeight);
            }

            // =====================================
            // Chuyển ngày về yyyy-MM-dd
            // =====================================

            String databaseDate =
                    convertDateToDatabase(date);

            if (databaseDate.equals(date)) {

                edtDate.setError(
                        "Ngày không hợp lệ. Ví dụ: 17/09/2026"
                );

                return;
            }

            // =====================================
            // SỬA
            // =====================================

            if (recordId != -1) {

                int result =
                        databaseHelper.updateWeight(
                                recordId,
                                databaseDate,
                                weightKg,
                                note
                        );

                if (result > 0) {

                    Toast.makeText(
                            this,
                            "Đã cập nhật cân nặng",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                } else {

                    Toast.makeText(
                            this,
                            "Cập nhật thất bại",
                            Toast.LENGTH_SHORT
                    ).show();
                }

            }

            // =====================================
            // THÊM
            // =====================================

            else {

                long result =
                        databaseHelper.insertWeight(
                                databaseDate,
                                weightKg,
                                note
                        );

                if (result != -1) {

                    Toast.makeText(
                            this,
                            "Đã lưu cân nặng",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                } else {

                    Toast.makeText(
                            this,
                            "Lưu thất bại",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

        } catch (NumberFormatException e) {

            edtWeight.setError(
                    "Cân nặng không hợp lệ"
            );
        }
    }

    // =================================================
    // lbs → kg
    // =================================================

    private float lbsToKg(float lbs) {

        return lbs * 0.45359237f;
    }

    // =================================================
    // kg → lbs
    // =================================================

    private float kgToLbs(float kg) {

        return kg * 2.20462262f;
    }

    // =================================================
    // dd/MM/yyyy → yyyy-MM-dd
    // =================================================

    private String convertDateToDatabase(
            String date) {

        try {

            SimpleDateFormat inputFormat =
                    new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                    );

            inputFormat.setLenient(false);

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
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

    // =================================================
    // yyyy-MM-dd → dd/MM/yyyy
    // =================================================

    private String convertDateForDisplay(
            String date) {

        if (date == null ||
                date.isEmpty()) {

            return "";
        }

        try {

            SimpleDateFormat inputFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    );

            inputFormat.setLenient(false);

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