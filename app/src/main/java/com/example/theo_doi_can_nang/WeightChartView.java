package com.example.theo_doi_can_nang;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.theo_doi_can_nang.data.WeightRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeightChartView extends View {

    // =========================================================
    // PAINT
    // =========================================================

    private Paint axisPaint;
    private Paint linePaint;
    private Paint pointPaint;
    private Paint textPaint;
    private Paint dateBackgroundPaint;

    // =========================================================
    // DỮ LIỆU
    // =========================================================

    private List<WeightRecord> records =
            new ArrayList<>();


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

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

        // =====================================================
        // TRỤC BIỂU ĐỒ
        // =====================================================

        axisPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        axisPaint.setStrokeWidth(2f);

        axisPaint.setStyle(
                Paint.Style.STROKE
        );


        // =====================================================
        // ĐƯỜNG BIỂU ĐỒ
        // =====================================================

        linePaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        linePaint.setStrokeWidth(5f);

        linePaint.setStyle(
                Paint.Style.STROKE
        );

        linePaint.setStrokeCap(
                Paint.Cap.ROUND
        );

        linePaint.setStrokeJoin(
                Paint.Join.ROUND
        );


        // =====================================================
        // ĐIỂM DỮ LIỆU
        // =====================================================

        pointPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        pointPaint.setStyle(
                Paint.Style.FILL
        );


        // =====================================================
        // CHỮ
        // =====================================================

        textPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint.setTextSize(28f);

        textPaint.setTypeface(
                android.graphics.Typeface.create(
                        android.graphics.Typeface.DEFAULT,
                        android.graphics.Typeface.NORMAL
                )
        );


        // =====================================================
        // NỀN PHÍA SAU NGÀY
        // =====================================================

        dateBackgroundPaint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        dateBackgroundPaint.setStyle(
                Paint.Style.FILL
        );


        // =====================================================
        // CẬP NHẬT MÀU
        // =====================================================

        updateColors();
    }


    // =========================================================
    // CẬP NHẬT MÀU
    // =========================================================

    private void updateColors() {

        boolean isDarkMode =
                (getResources().getConfiguration().uiMode
                        &
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                        ==
                        android.content.res.Configuration.UI_MODE_NIGHT_YES;


        if (isDarkMode) {

            // =================================================
            // DARK MODE
            // =================================================

            // Chữ thông thường
            textPaint.setColor(
                    Color.WHITE
            );


            // Trục biểu đồ
            axisPaint.setColor(
                    Color.rgb(
                            190,
                            190,
                            200
                    )
            );


            // Đường biểu đồ
            linePaint.setColor(
                    Color.rgb(
                            138,
                            106,
                            200
                    )
            );


            // Điểm dữ liệu
            pointPaint.setColor(
                    Color.rgb(
                            138,
                            106,
                            200
                    )
            );


            // Nền phía sau ngày
            dateBackgroundPaint.setColor(
                    Color.rgb(
                            45,
                            45,
                            54
                    )
            );

        } else {

            // =================================================
            // LIGHT MODE
            // =================================================

            // Chữ thông thường
            textPaint.setColor(
                    Color.rgb(
                            32,
                            33,
                            36
                    )
            );


            // Trục biểu đồ
            axisPaint.setColor(
                    Color.rgb(
                            100,
                            100,
                            110
                    )
            );


            // Đường biểu đồ
            linePaint.setColor(
                    Color.rgb(
                            111,
                            77,
                            181
                    )
            );


            // Điểm dữ liệu
            pointPaint.setColor(
                    Color.rgb(
                            111,
                            77,
                            181
                    )
            );


            // Nền phía sau ngày
            dateBackgroundPaint.setColor(
                    Color.rgb(
                            111,
                            77,
                            181
                    )
            );
        }
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
    // VẼ
    // =========================================================

    @Override
    protected void onDraw(
            Canvas canvas) {

        super.onDraw(canvas);

        // Cập nhật màu theo theme hiện tại
        updateColors();


        // Không có dữ liệu
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

        textPaint.setTextSize(28f);

        textPaint.setColor(
                Color.rgb(
                        32,
                        33,
                        36
                )
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


        // =====================================================
        // KHOẢNG BIỂU ĐỒ
        // =====================================================

        float left = 70f;

        float right =
                width - 30f;

        float top = 30f;

        // Chừa khoảng phía dưới cho ngày tháng
        float bottom =
                height - 70f;


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

                minWeight =
                        weight;
            }


            if (weight > maxWeight) {

                maxWeight =
                        weight;
            }
        }


        // =====================================================
        // TẠO KHOẢNG ĐỆM
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
        // PATH ĐƯỜNG BIỂU ĐỒ
        // =====================================================

        Path path =
                new Path();


        int count =
                records.size();


        // =====================================================
        // VẼ TỪNG ĐIỂM
        // =====================================================

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
            // PATH
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
            // ĐIỂM
            // =================================================

            canvas.drawCircle(
                    x,
                    y,
                    7f,
                    pointPaint
            );


            // =================================================
            // GIÁ TRỊ CÂN NẶNG
            // =================================================

            textPaint.setTextAlign(
                    Paint.Align.CENTER
            );

            textPaint.setTextSize(24f);

            textPaint.setColor(
                    isDarkMode()
                            ? Color.WHITE
                            : Color.rgb(
                            32,
                            33,
                            36
                    )
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
            // NGÀY
            // =================================================

            String date =
                    convertDateForDisplay(
                            record.getDate()
                    );


            // =================================================
            // KÍCH THƯỚC NỀN CHO NGÀY
            // =================================================

            textPaint.setTextSize(24f);

            float textWidth =
                    textPaint.measureText(
                            date
                    );


            float paddingHorizontal =
                    8f;


            float dateLeft =
                    x
                            -
                            textWidth / 2f
                            -
                            paddingHorizontal;


            float dateRight =
                    x
                            +
                            textWidth / 2f
                            +
                            paddingHorizontal;


            float dateTop =
                    bottom + 5f;


            float dateBottom =
                    bottom + 35f;


            // =================================================
            // NỀN PHÍA SAU NGÀY
            // =================================================

            canvas.drawRoundRect(
                    dateLeft,
                    dateTop,
                    dateRight,
                    dateBottom,
                    8f,
                    8f,
                    dateBackgroundPaint
            );


            // =================================================
            // CHỮ NGÀY
            // =================================================
            // LUÔN LUÔN MÀU TRẮNG
            // CẢ LIGHT MODE VÀ DARK MODE
            // =================================================

            textPaint.setColor(
                    Color.WHITE
            );

            textPaint.setTextSize(24f);

            textPaint.setTextAlign(
                    Paint.Align.CENTER
            );


            canvas.drawText(
                    date,
                    x,
                    bottom + 26f,
                    textPaint
            );
        }


        // =====================================================
        // VẼ ĐƯỜNG BIỂU ĐỒ
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

        textPaint.setTextSize(24f);


        textPaint.setColor(
                isDarkMode()
                        ? Color.WHITE
                        : Color.rgb(
                        32,
                        33,
                        36
                )
        );


        // MAX
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


        // MIN
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
    // KIỂM TRA DARK MODE
    // =========================================================

    private boolean isDarkMode() {

        return (
                getResources()
                        .getConfiguration()
                        .uiMode
                        &
                        android.content.res.Configuration
                                .UI_MODE_NIGHT_MASK
        )
                ==
                android.content.res.Configuration
                        .UI_MODE_NIGHT_YES;
    }


    // =========================================================
    // CHUYỂN NGÀY
    // yyyy-MM-dd -> dd/MM
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
                            "dd/MM",
                            Locale.getDefault()
                    );


            Date parsedDate =
                    inputFormat.parse(
                            date
                    );


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