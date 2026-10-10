package com.example.theo_doi_can_nang;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.theo_doi_can_nang.data.WeightRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeightChartView extends View {

    private Paint gridPaint;
    private Paint linePaint;
    private Paint fillPaint;
    private Paint pointOuterPaint;
    private Paint pointInnerPaint;
    private Paint textPaint;

    private int primaryColor;
    private int secondaryTextColor;
    private int textColor;
    private int cardStrokeColor;
    private int cardBgColor;

    private List<WeightRecord> records = new ArrayList<>();

    public WeightChartView(Context context) {
        super(context);
        init();
    }

    public WeightChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeightChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setStrokeWidth(2f);
        gridPaint.setStyle(Paint.Style.STROKE);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(6f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

        pointOuterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointOuterPaint.setStyle(Paint.Style.FILL);

        pointInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointInnerPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(26f);

        updateColors();
    }

    private void updateColors() {

        primaryColor = ContextCompat.getColor(getContext(), R.color.primary);
        secondaryTextColor = ContextCompat.getColor(getContext(), R.color.app_secondary_text);
        textColor = ContextCompat.getColor(getContext(), R.color.app_text);
        cardStrokeColor = ContextCompat.getColor(getContext(), R.color.card_stroke);
        cardBgColor = ContextCompat.getColor(getContext(), R.color.app_card);

        gridPaint.setColor(cardStrokeColor);
        linePaint.setColor(primaryColor);
        pointOuterPaint.setColor(primaryColor);
        pointInnerPaint.setColor(cardBgColor);
        textPaint.setColor(textColor);
    }

    public void setData(List<WeightRecord> records) {
        if (records == null) {
            this.records = new ArrayList<>();
        } else {
            this.records = new ArrayList<>(records);
        }

        updateColors();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        updateColors();

        if (records == null || records.isEmpty()) {
            drawEmptyChart(canvas);
            return;
        }

        drawChart(canvas);
    }

    private void drawEmptyChart(Canvas canvas) {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(secondaryTextColor);
        canvas.drawText("Chưa có dữ liệu cân nặng", centerX, centerY, textPaint);
    }

    private void drawChart(Canvas canvas) {

        float width = getWidth();
        float height = getHeight();

        float left = 75f;
        float right = width - 40f;
        float top = 40f;
        float bottom = height - 60f;

        // Tìm Min / Max Cân Nặng
        float minWeight = records.get(0).getWeight();
        float maxWeight = records.get(0).getWeight();

        for (WeightRecord record : records) {
            float w = record.getWeight();
            if (w < minWeight) minWeight = w;
            if (w > maxWeight) maxWeight = w;
        }

        float range = maxWeight - minWeight;
        if (range == 0) {
            range = 1f;
        }

        minWeight -= range * 0.15f;
        maxWeight += range * 0.15f;

        // 1. Vẽ đường lưới ngang nhẹ nhàng
        int gridLines = 4;
        for (int g = 0; g <= gridLines; g++) {
            float gridY = top + g * (bottom - top) / gridLines;
            canvas.drawLine(left, gridY, right, gridY, gridPaint);
        }

        int count = records.size();

        // Tọa độ các điểm (Points)
        float[] xCoords = new float[count];
        float[] yCoords = new float[count];

        for (int i = 0; i < count; i++) {
            WeightRecord record = records.get(i);

            if (count == 1) {
                xCoords[i] = (left + right) / 2f;
            } else {
                xCoords[i] = left + i * (right - left) / (count - 1);
            }

            float w = record.getWeight();
            float ratio = (w - minWeight) / (maxWeight - minWeight);
            yCoords[i] = bottom - ratio * (bottom - top);
        }

        // 2. Tạo Path cho đường cong mượt (Smooth Curve)
        Path curvePath = new Path();
        curvePath.moveTo(xCoords[0], yCoords[0]);

        if (count == 1) {
            curvePath.lineTo(xCoords[0], yCoords[0]);
        } else {
            for (int i = 0; i < count - 1; i++) {
                float x1 = xCoords[i];
                float y1 = yCoords[i];
                float x2 = xCoords[i + 1];
                float y2 = yCoords[i + 1];

                float cx1 = x1 + (x2 - x1) / 2f;
                float cy1 = y1;
                float cx2 = x1 + (x2 - x1) / 2f;
                float cy2 = y2;

                curvePath.cubicTo(cx1, cy1, cx2, cy2, x2, y2);
            }
        }

        // 3. Tạo Gradient Fill bên dưới đường cong
        Path fillPath = new Path(curvePath);
        fillPath.lineTo(xCoords[count - 1], bottom);
        fillPath.lineTo(xCoords[0], bottom);
        fillPath.close();

        int startAlphaColor = Color.argb(80, Color.red(primaryColor), Color.green(primaryColor), Color.blue(primaryColor));
        int endAlphaColor = Color.argb(0, Color.red(primaryColor), Color.green(primaryColor), Color.blue(primaryColor));

        LinearGradient gradient = new LinearGradient(
                0, top, 0, bottom,
                startAlphaColor, endAlphaColor,
                Shader.TileMode.CLAMP
        );
        fillPaint.setShader(gradient);

        // Vẽ Gradient Fill
        canvas.drawPath(fillPath, fillPaint);

        // Vẽ Đường cong mượt
        canvas.drawPath(curvePath, linePaint);

        // 4. Vẽ các Điểm (Data Points) & Nhãn
        for (int i = 0; i < count; i++) {
            float x = xCoords[i];
            float y = yCoords[i];
            WeightRecord record = records.get(i);

            // Vòng tròn ngoài (Màu primary)
            canvas.drawCircle(x, y, 10f, pointOuterPaint);
            // Vòng tròn trong (Màu card background)
            canvas.drawCircle(x, y, 5f, pointInnerPaint);

            // Nhãn Giá trị Cân nặng
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setColor(textColor);
            textPaint.setTextSize(24f);

            canvas.drawText(
                    String.format(Locale.getDefault(), "%.1f", record.getWeight()),
                    x,
                    y - 18,
                    textPaint
            );

            // Nhãn Ngày
            textPaint.setColor(secondaryTextColor);
            textPaint.setTextSize(22f);
            String date = convertDateForDisplay(record.getDate());
            canvas.drawText(date, x, bottom + 35, textPaint);
        }

        // 5. Hiển thị Giá trị Min / Max ở lề trái
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setColor(secondaryTextColor);
        textPaint.setTextSize(20f);

        canvas.drawText(
                String.format(Locale.getDefault(), "%.1f", maxWeight),
                left - 10,
                top + 10,
                textPaint
        );

        canvas.drawText(
                String.format(Locale.getDefault(), "%.1f", minWeight),
                left - 10,
                bottom,
                textPaint
        );
    }

    private String convertDateForDisplay(String date) {
        try {
            java.text.SimpleDateFormat inputFormat =
                    new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            java.text.SimpleDateFormat outputFormat =
                    new java.text.SimpleDateFormat("dd/MM", Locale.getDefault());

            java.util.Date parsedDate = inputFormat.parse(date);
            if (parsedDate != null) {
                return outputFormat.format(parsedDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
}
