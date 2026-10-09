package com.example.theo_doi_can_nang.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weight_tracker.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_NAME = "weight_records";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_IMAGE_PATH = "image_path";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_DATE + " TEXT NOT NULL, " +
                        COLUMN_WEIGHT + " REAL NOT NULL, " +
                        COLUMN_NOTE + " TEXT, " +
                        COLUMN_IMAGE_PATH + " TEXT" +
                        ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        if (oldVersion < 3) {
            try {
                db.execSQL(
                        "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_IMAGE_PATH + " TEXT"
                );
            } catch (Exception e) {

                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                onCreate(db);
            }
        } else {

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    // =====================================================
    // THÊM CÂN NẶNG
    // =====================================================

    public long insertWeight(
            String date,
            float weight,
            String note) {
        return insertWeight(date, weight, note, null);
    }

    public long insertWeight(
            String date,
            float weight,
            String note,
            String imagePath) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(COLUMN_DATE, date);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_IMAGE_PATH, imagePath);

        long result =
                db.insert(
                        TABLE_NAME,
                        null,
                        values
                );

        db.close();

        return result;
    }

    // =====================================================
    // LẤY TẤT CẢ BẢN GHI
    // =====================================================

    public List<WeightRecord> getAllWeights() {

        List<WeightRecord> list =
                new ArrayList<>();

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor =
                db.query(
                        TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        COLUMN_DATE + " DESC"
                );

        if (cursor.moveToFirst()) {

            do {

                int id =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        COLUMN_ID
                                )
                        );

                String date =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        COLUMN_DATE
                                )
                        );

                float weight =
                        cursor.getFloat(
                                cursor.getColumnIndexOrThrow(
                                        COLUMN_WEIGHT
                                )
                        );

                String note =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        COLUMN_NOTE
                                )
                        );

                int imgIdx = cursor.getColumnIndex(COLUMN_IMAGE_PATH);
                String imagePath = imgIdx != -1 ? cursor.getString(imgIdx) : null;

                list.add(
                        new WeightRecord(
                                id,
                                date,
                                weight,
                                note,
                                imagePath
                        )
                );

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }

    // =====================================================
    // SỬA
    // =====================================================

    public int updateWeight(
            int id,
            String date,
            float weight,
            String note) {
        return updateWeight(id, date, weight, note, null);
    }

    public int updateWeight(
            int id,
            String date,
            float weight,
            String note,
            String imagePath) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(COLUMN_DATE, date);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_IMAGE_PATH, imagePath);

        int result =
                db.update(
                        TABLE_NAME,
                        values,
                        COLUMN_ID + "=?",
                        new String[]{
                                String.valueOf(id)
                        }
                );

        db.close();

        return result;
    }

    // =====================================================
    // XÓA
    // =====================================================

    public int deleteWeight(int id) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        int result =
                db.delete(
                        TABLE_NAME,
                        COLUMN_ID + "=?",
                        new String[]{
                                String.valueOf(id)
                        }
                );

        db.close();

        return result;
    }
}
