package com.example.theo_doi_can_nang.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AchievementManager {

    private static final String PREF_NAME = "app_achievements";

    public static final String ID_FIRST_STEP = "first_step";
    public static final String ID_STREAK_3 = "streak_3";
    public static final String ID_BURN_3KG = "burn_3kg";
    public static final String ID_REACH_GOAL = "reach_goal";

    public static List<Achievement> getAllAchievements(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        List<Achievement> list = new ArrayList<>();

        list.add(new Achievement(
                ID_FIRST_STEP,
                "Bước Khởi Đầu",
                "Ghi nhận cân nặng lần đầu tiên vào ứng dụng",
                "beginning_trophy.glb",
                prefs.getBoolean(ID_FIRST_STEP, false),
                prefs.getString(ID_FIRST_STEP + "_date", "")
        ));

        list.add(new Achievement(
                ID_STREAK_3,
                "Kiên Trì 3 Ngày",
                "Ghi nhận cân nặng ít nhất 3 bản ghi",
                "3days_trophy.glb",
                prefs.getBoolean(ID_STREAK_3, false),
                prefs.getString(ID_STREAK_3 + "_date", "")
        ));

        list.add(new Achievement(
                ID_BURN_3KG,
                "Bứt Phá 3KG",
                "Giảm thành công từ 3kg trở lên so với ban đầu",
                "3kg_trophy.glb",
                prefs.getBoolean(ID_BURN_3KG, false),
                prefs.getString(ID_BURN_3KG + "_date", "")
        ));

        list.add(new Achievement(
                ID_REACH_GOAL,
                "Chinh Phục Mục Tiêu",
                "Cân nặng hiện tại đạt mốc mục tiêu đề ra",
                "goal_trophy.glb",
                prefs.getBoolean(ID_REACH_GOAL, false),
                prefs.getString(ID_REACH_GOAL + "_date", "")
        ));

        return list;
    }

    public static List<Achievement> checkAndUnlockAchievements(Context context) {
        List<Achievement> newlyUnlocked = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences settingsPrefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        List<WeightRecord> records = dbHelper.getAllWeights();

        if (records == null || records.isEmpty()) {
            return newlyUnlocked;
        }

        WeightRecord latestRecord = records.get(0);
        float currentWeight = latestRecord.getWeight();

        float startWeight = settingsPrefs.getFloat("start_weight", 0f);
        float goalWeight = settingsPrefs.getFloat("goal_weight", 0f);

        String currentDateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        // 1. Bước Khởi Đầu (First Step)
        if (records.size() >= 1 && !prefs.getBoolean(ID_FIRST_STEP, false)) {
            unlockAchievement(prefs, ID_FIRST_STEP, currentDateStr);
            newlyUnlocked.add(getAchievementById(context, ID_FIRST_STEP));
        }

        // 2. Kiên Trì 3 Ngày (Streak 3)
        if (records.size() >= 3 && !prefs.getBoolean(ID_STREAK_3, false)) {
            unlockAchievement(prefs, ID_STREAK_3, currentDateStr);
            newlyUnlocked.add(getAchievementById(context, ID_STREAK_3));
        }

        // 3. Bứt Phá 3KG (Burn 3kg)
        if (startWeight > 0 && (startWeight - currentWeight) >= 3.0f && !prefs.getBoolean(ID_BURN_3KG, false)) {
            unlockAchievement(prefs, ID_BURN_3KG, currentDateStr);
            newlyUnlocked.add(getAchievementById(context, ID_BURN_3KG));
        }

        // 4. Chinh Phục Mục Tiêu (Reach Goal)
        if (goalWeight > 0 && currentWeight <= goalWeight && !prefs.getBoolean(ID_REACH_GOAL, false)) {
            unlockAchievement(prefs, ID_REACH_GOAL, currentDateStr);
            newlyUnlocked.add(getAchievementById(context, ID_REACH_GOAL));
        }

        return newlyUnlocked;
    }

    private static void unlockAchievement(SharedPreferences prefs, String id, String dateStr) {
        prefs.edit()
                .putBoolean(id, true)
                .putString(id + "_date", dateStr)
                .apply();
    }

    public static Achievement getAchievementById(Context context, String id) {
        List<Achievement> all = getAllAchievements(context);
        for (Achievement a : all) {
            if (a.getId().equals(id)) {
                return a;
            }
        }
        return null;
    }
}
