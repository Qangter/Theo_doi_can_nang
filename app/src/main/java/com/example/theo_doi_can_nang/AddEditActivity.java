package com.example.theo_doi_can_nang;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.theo_doi_can_nang.data.DatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditActivity extends AppCompatActivity {

    private EditText edtDate;
    private EditText edtWeight;
    private EditText edtNote;

    private TextView tvWeightLabel;
    private ImageView imgProgressPhoto;

    private Button btnTakePhoto;
    private Button btnPickGallery;
    private Button btnRemovePhoto;

    private DatabaseHelper databaseHelper;

    private int recordId = -1;
    private String weightUnit = "kg";
    private String currentImagePath = null;

    private File tempCameraFile = null;
    private Uri tempCameraUri = null;

    // Launchers cho ActivityResult
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> getContentLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

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

        tvWeightLabel = findViewById(R.id.tvWeightLabel);
        imgProgressPhoto = findViewById(R.id.imgProgressPhoto);

        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnPickGallery = findViewById(R.id.btnPickGallery);
        btnRemovePhoto = findViewById(R.id.btnRemovePhoto);

        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        databaseHelper = new DatabaseHelper(this);

        // Đăng ký ActivityResultLaunchers
        initActivityResultLaunchers();

        // Đọc đơn vị kg / lbs
        android.content.SharedPreferences prefs =
                getSharedPreferences("app_settings", MODE_PRIVATE);

        weightUnit = prefs.getString("weight_unit", "kg");
        updateWeightUnitUI();

        // Kiểm tra thêm hay sửa
        recordId = getIntent().getIntExtra("id", -1);

        if (recordId != -1) {

            // Chế độ SỬA
            setTitle("Sửa cân nặng");

            String date = getIntent().getStringExtra("date");
            float weightKg = getIntent().getFloatExtra("weight", 0);
            String note = getIntent().getStringExtra("note");
            currentImagePath = getIntent().getStringExtra("imagePath");

            edtDate.setText(convertDateForDisplay(date));

            float displayWeight = weightKg;
            if (weightUnit.equals("lbs")) {
                displayWeight = kgToLbs(weightKg);
            }

            edtWeight.setText(
                    String.format(Locale.getDefault(), "%.1f", displayWeight)
            );

            if (note != null) {
                edtNote.setText(note);
            }

            updatePhotoUI();

        } else {

            // Chế độ THÊM
            setTitle("Thêm cân nặng");

            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            edtDate.setText(sdf.format(new Date()));
        }

        // Sự kiện chụp ảnh
        btnTakePhoto.setOnClickListener(v -> checkCameraPermissionAndLaunch());

        // Sự kiện chọn từ thư viện
        btnPickGallery.setOnClickListener(v -> getContentLauncher.launch("image/*"));

        // Sự kiện xóa ảnh
        btnRemovePhoto.setOnClickListener(v -> {
            currentImagePath = null;
            updatePhotoUI();
        });

        // Nút Lưu
        btnSave.setOnClickListener(v -> saveWeight());

        // Nút Hủy
        btnCancel.setOnClickListener(v -> finish());
    }

    private void initActivityResultLaunchers() {

        // Launcher chụp ảnh từ camera
        takePictureLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.TakePicture(),
                        success -> {
                            if (success && tempCameraFile != null && tempCameraFile.exists()) {
                                saveUriToInternalStorage(Uri.fromFile(tempCameraFile));
                            }
                        }
                );

        // Launcher chọn ảnh từ thư viện
        getContentLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        uri -> {
                            if (uri != null) {
                                saveUriToInternalStorage(uri);
                            }
                        }
                );

        // Launcher xin quyền camera
        cameraPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) {
                                launchCamera();
                            } else {
                                Toast.makeText(
                                        this,
                                        "Cần cấp quyền camera để chụp ảnh",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }

    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        try {
            File photosDir = new File(getFilesDir(), "photos");
            if (!photosDir.exists()) {
                photosDir.mkdirs();
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            tempCameraFile = new File(photosDir, "IMG_" + timeStamp + ".jpg");

            tempCameraUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    tempCameraFile
            );

            takePictureLauncher.launch(tempCameraUri);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể mở camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUriToInternalStorage(Uri sourceUri) {
        try {
            File photosDir = new File(getFilesDir(), "photos");
            if (!photosDir.exists()) {
                photosDir.mkdirs();
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File destFile = new File(photosDir, "PROGRESS_" + timeStamp + ".jpg");

            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            FileOutputStream outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            if (inputStream != null) {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
            }
            outputStream.close();

            currentImagePath = destFile.getAbsolutePath();
            updatePhotoUI();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể lưu ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePhotoUI() {
        if (currentImagePath != null && !currentImagePath.isEmpty() && new File(currentImagePath).exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath, options);

            if (bitmap != null) {
                imgProgressPhoto.setImageBitmap(bitmap);
                imgProgressPhoto.setVisibility(View.VISIBLE);
                btnRemovePhoto.setVisibility(View.VISIBLE);
                return;
            }
        }

        imgProgressPhoto.setVisibility(View.GONE);
        btnRemovePhoto.setVisibility(View.GONE);
    }

    private void updateWeightUnitUI() {
        if (weightUnit.equals("lbs")) {
            tvWeightLabel.setText("Cân nặng (lbs)");
            edtWeight.setHint("Ví dụ: 154.3");
        } else {
            tvWeightLabel.setText("Cân nặng (kg)");
            edtWeight.setHint("Ví dụ: 70");
        }
    }

    private void saveWeight() {

        String date = edtDate.getText().toString().trim();
        String weightText = edtWeight.getText().toString().trim();
        String note = edtNote.getText().toString().trim();

        if (date.isEmpty()) {
            edtDate.setError("Vui lòng nhập ngày");
            return;
        }

        if (weightText.isEmpty()) {
            edtWeight.setError("Vui lòng nhập cân nặng");
            return;
        }

        try {
            weightText = weightText.replace(",", ".");
            float inputWeight = Float.parseFloat(weightText);

            if (inputWeight <= 0) {
                edtWeight.setError("Cân nặng phải lớn hơn 0");
                return;
            }

            float weightKg = inputWeight;
            if (weightUnit.equals("lbs")) {
                weightKg = lbsToKg(inputWeight);
            }

            String databaseDate = convertDateToDatabase(date);
            if (databaseDate.equals(date)) {
                edtDate.setError("Ngày không hợp lệ. Ví dụ: 17/09/2026");
                return;
            }

            if (recordId != -1) {

                int result = databaseHelper.updateWeight(
                        recordId,
                        databaseDate,
                        weightKg,
                        note,
                        currentImagePath
                );

                if (result > 0) {
                    Toast.makeText(this, "Đã cập nhật cân nặng", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }

            } else {

                long result = databaseHelper.insertWeight(
                        databaseDate,
                        weightKg,
                        note,
                        currentImagePath
                );

                if (result != -1) {
                    Toast.makeText(this, "Đã lưu cân nặng", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (NumberFormatException e) {
            edtWeight.setError("Cân nặng không hợp lệ");
        }
    }

    private float lbsToKg(float lbs) {
        return lbs * 0.45359237f;
    }

    private float kgToLbs(float kg) {
        return kg * 2.20462262f;
    }

    private String convertDateToDatabase(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            inputFormat.setLenient(false);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);

            if (parsedDate != null) {
                return outputFormat.format(parsedDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    private String convertDateForDisplay(String date) {
        if (date == null || date.isEmpty()) {
            return "";
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            inputFormat.setLenient(false);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);

            if (parsedDate != null) {
                return outputFormat.format(parsedDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
}
