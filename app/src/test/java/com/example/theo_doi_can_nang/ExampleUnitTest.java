package com.example.theo_doi_can_nang;

import com.example.theo_doi_can_nang.data.Achievement;
import com.example.theo_doi_can_nang.data.WeightRecord;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ExampleUnitTest {

    @Test
    public void testWeightRecordModel() {
        WeightRecord recordWithoutPhoto = new WeightRecord(1, "2026-10-09", 70.0f, "Ghi chú test");
        assertEquals(1, recordWithoutPhoto.getId());
        assertEquals("2026-10-09", recordWithoutPhoto.getDate());
        assertEquals(70.0f, recordWithoutPhoto.getWeight(), 0.001f);
        assertEquals("Ghi chú test", recordWithoutPhoto.getNote());
        assertNull(recordWithoutPhoto.getImagePath());

        WeightRecord recordWithPhoto = new WeightRecord(2, "2026-10-10", 68.5f, "Có ảnh", "/path/to/photo.jpg");
        assertEquals(2, recordWithPhoto.getId());
        assertEquals("/path/to/photo.jpg", recordWithPhoto.getImagePath());

        recordWithPhoto.setImagePath("/new/path/to/photo.jpg");
        assertEquals("/new/path/to/photo.jpg", recordWithPhoto.getImagePath());
    }

    @Test
    public void testAchievementModel() {
        Achievement achievement = new Achievement(
                "burn_3kg",
                "Bứt Phá 3KG",
                "Giảm thành công từ 3kg trở lên so với ban đầu",
                "trophy_gold.glb",
                false,
                ""
        );

        assertEquals("burn_3kg", achievement.getId());
        assertEquals("Bứt Phá 3KG", achievement.getTitle());
        assertEquals("Giảm thành công từ 3kg trở lên so với ban đầu", achievement.getDescription());
        assertEquals("trophy_gold.glb", achievement.getModelFileName());
        assertFalse(achievement.isUnlocked());
        assertEquals("", achievement.getUnlockedDate());

        achievement.setUnlocked(true);
        achievement.setUnlockedDate("10/10/2026");

        assertTrue(achievement.isUnlocked());
        assertEquals("10/10/2026", achievement.getUnlockedDate());
    }

    @Test
    public void testKgAndLbsConversion() {
        float kg = 70.0f;
        float lbs = kg * 2.20462262f;
        float backToKg = lbs * 0.45359237f;

        assertEquals(154.323f, lbs, 0.01f);
        assertEquals(70.0f, backToKg, 0.01f);
    }

    @Test
    public void testBmiCalculationAndClassification() {
        float weightKg = 70.0f;
        float heightCm = 175.0f;
        float heightM = heightCm / 100f;

        float bmi = weightKg / (heightM * heightM); // 70 / (1.75^2) = 22.857
        assertEquals(22.857f, bmi, 0.01f);

        assertEquals("Thiếu cân", getBMIStatus(17.5f));
        assertEquals("Bình thường", getBMIStatus(22.8f));
        assertEquals("Thừa cân", getBMIStatus(27.5f));
        assertEquals("Béo phì", getBMIStatus(32.0f));
    }

    private String getBMIStatus(float bmi) {
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

    @Test
    public void testProgressCalculation() {
        float startWeight = 80.0f;
        float currentWeight = 75.0f;
        float goalWeight = 70.0f;

        float progress = (startWeight - currentWeight) / (startWeight - goalWeight) * 100f;
        assertEquals(50.0f, progress, 0.01f);
    }

    @Test
    public void testDateConversion() {
        String inputDate = "17/09/2026";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            inputFormat.setLenient(false);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = inputFormat.parse(inputDate);

            assertNotNull(parsedDate);
            String databaseDate = outputFormat.format(parsedDate);
            assertEquals("2026-09-17", databaseDate);
        } catch (Exception e) {
            org.junit.Assert.fail("Date conversion threw an exception: " + e.getMessage());
        }
    }
}
