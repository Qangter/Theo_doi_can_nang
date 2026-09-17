package com.example.theo_doi_can_nang;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.theo_doi_can_nang.data.WeightRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeightChartView extends View {

    private Paint axisPaint;
    private Paint linePaint;
    private Paint pointPaint;
    private Paint textPaint;

    private List<WeightRecord> records =
            new ArrayList<>();

    public WeightChartView(Context context) {
        super(context);
        init();
    }

    public WeightChartView(
            Context context,
            @Nullable AttributeSet attrs) {

        super(context, attrs);
        init();
    }

    public WeightChartView(
            Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init();
    }


    // =========================================================
    // KHỞI TẠO PAINT
    // =========================================================

    private void init() {

        axisPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        axisPaint.setStrokeWidth(2f);
        axisPaint.setStyle(
                Paint.Style.STROKE
        );


        linePaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        linePaint.setStrokeWidth(5f);
        linePaint.setStyle(
                Paint.Style.STROKE
        );


        pointPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        pointPaint.setStyle(
                Paint.Style.FILL
        );


        textPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint.setTextSize(28f);

        updateColors();
    }


    // =========================================================
    // LẤY MÀU THEO THEME
    // =========================================================

    private void updateColors() {

        int textColor =
                ContextCompat.getColor(
                        getContext(),
                        R.color.app_text
                );

        axisPaint.setColor(
                textColor
        );

        linePaint.setColor(
                textColor
        );

        pointPaint.setColor(
                textColor
        );

        textPaint.setColor(
                textColor
        );
    }


    // =========================================================
    // NHẬN DỮ LIỆU
    // =========================================================

    public void setData(
            List<WeightRecord> records) {

        if (records == null) {

            this.records =
                    new ArrayList<>();

        } else {

            this.records =
                    new ArrayList<>(
                            records
                    );
        }

        updateColors();

        invalidate();
    }


    // =========================================================
    // VẼ BIỂU ĐỒ
    // =========================================================

    @Override
    protected void onDraw(
            Canvas canvas) {

        super.onDraw(canvas);

        // Đảm bảo màu luôn đúng
        // theo Light / Dark Mode
        updateColors();

        if (records == null ||
                records.isEmpty()) {

            drawEmptyChart(canvas);

            return;
        }

        drawChart(canvas);
    }


    // =========================================================
    // BIỂU ĐỒ RỖNG
    // =========================================================

    private void drawEmptyChart(
            Canvas canvas) {

        float centerX =
                getWidth() / 2f;

        float centerY =
                getHeight() / 2f;

        textPaint.setTextAlign(
                Paint.Align.CENTER
        );

        canvas.drawText(
                "Chưa có dữ liệu cân nặng",
                centerX,
                centerY,
                textPaint
        );
    }


    // =========================================================
    // VẼ BIỂU ĐỒ
    // =========================================================

    private void drawChart(
            Canvas canvas) {

        float width =
                getWidth();

        float height =
                getHeight();


        // Khoảng cách biểu đồ
        float left = 70f;
        float right = width - 30f;
        float top = 30f;
        float bottom = height - 55f;


        // =====================================================
        // TÌM MIN / MAX
        // =====================================================

        float minWeight =
                records.get(0).getWeight();

        float maxWeight =
                records.get(0).getWeight();

        for (WeightRecord record :
                records) {

            float weight =
                    record.getWeight();

            if (weight < minWeight) {
                minWeight = weight;
            }

            if (weight > maxWeight) {
                maxWeight = weight;
            }
        }


        // =====================================================
        // TẠO KHOẢNG BIỂU ĐỒ
        // =====================================================

        float range =
                maxWeight - minWeight;

        if (range == 0) {
            range = 1;
        }

        minWeight -=
                range * 0.1f;

        maxWeight +=
                range * 0.1f;


        // =====================================================
        // VẼ TRỤC
        // =====================================================

        canvas.drawLine(
                left,
                top,
                left,
                bottom,
                axisPaint
        );

        canvas.drawLine(
                left,
                bottom,
                right,
                bottom,
                axisPaint
        );


        // =====================================================
        // ĐƯỜNG BIỂU ĐỒ
        // =====================================================

        Path path =
                new Path();

        int count =
                records.size();


        for (int i = 0;
             i < count;
             i++) {

            WeightRecord record =
                    records.get(i);


            // =================================================
            // TỌA ĐỘ X
            // =================================================

            float x;

            if (count == 1) {

                x =
                        (left + right) / 2f;

            } else {

                x =
                        left
                                +
                                i *
                                        (right - left)
                                        /
                                        (count - 1);
            }


            // =================================================
            // TỌA ĐỘ Y
            // =================================================

            float weight =
                    record.getWeight();

            float ratio =
                    (weight - minWeight)
                            /
                            (maxWeight - minWeight);

            float y =
                    bottom
                            -
                            ratio *
                                    (bottom - top);


            // =================================================
            // NỐI ĐƯỜNG
            // =================================================

            if (i == 0) {

                path.moveTo(
                        x,
                        y
                );

            } else {

                path.lineTo(
                        x,
                        y
                );
            }


            // =================================================
            // VẼ ĐIỂM
            // =================================================

            canvas.drawCircle(
                    x,
                    y,
                    7f,
                    pointPaint
            );


            // =================================================
            // HIỂN THỊ CÂN NẶNG
            // =================================================

            textPaint.setTextAlign(
                    Paint.Align.CENTER
            );

            canvas.drawText(
                    String.format(
                            Locale.getDefault(),
                            "%.1f",
                            weight
                    ),
                    x,
                    y - 15,
                    textPaint
            );


            // =================================================
            // HIỂN THỊ NGÀY
            // =================================================

            String date =
                    convertDateForDisplay(
                            record.getDate()
                    );

            canvas.drawText(
                    date,
                    x,
                    bottom + 30,
                    textPaint
            );
        }


        // =====================================================
        // VẼ ĐƯỜNG
        // =====================================================

        canvas.drawPath(
                path,
                linePaint
        );


        // =====================================================
        // MIN / MAX
        // =====================================================

        textPaint.setTextAlign(
                Paint.Align.RIGHT
        );

        canvas.drawText(
                String.format(
                        Locale.getDefault(),
                        "%.1f",
                        maxWeight
                ),
                left - 10,
                top + 10,
                textPaint
        );

        canvas.drawText(
                String.format(
                        Locale.getDefault(),
                        "%.1f",
                        minWeight
                ),
                left - 10,
                bottom,
                textPaint
        );
    }


    // =========================================================
    // ĐỔI NGÀY
    // =========================================================

    private String convertDateForDisplay(
            String date) {

        try {

            java.text.SimpleDateFormat
                    inputFormat =
                    new java.text.SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    );

            java.text.SimpleDateFormat
                    outputFormat =
                    new java.text.SimpleDateFormat(
                            "dd/MM",
                            Locale.getDefault()
                    );

            java.util.Date parsedDate =
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