package com.example.theo_doi_can_nang;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.theo_doi_can_nang.data.DatabaseHelper;
import com.example.theo_doi_can_nang.data.WeightRecord;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private DatabaseHelper dbHelper;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dbHelper = new DatabaseHelper(context);
    }

    @After
    public void tearDown() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.theo_doi_can_nang", appContext.getPackageName());
    }

    @Test
    public void testDatabaseCrudOperationsWithImagePath() {
        // 1. Insert record with image path
        long insertId = dbHelper.insertWeight("2026-10-09", 70.5f, "Test insert with photo", "/data/user/0/com.example.theo_doi_can_nang/files/photos/test.jpg");
        assertTrue(insertId != -1);

        // 2. Query record
        List<WeightRecord> records = dbHelper.getAllWeights();
        assertNotNull(records);
        assertTrue(!records.isEmpty());

        WeightRecord insertedRecord = null;
        for (WeightRecord r : records) {
            if (r.getId() == (int) insertId) {
                insertedRecord = r;
                break;
            }
        }

        assertNotNull(insertedRecord);
        assertEquals("2026-10-09", insertedRecord.getDate());
        assertEquals(70.5f, insertedRecord.getWeight(), 0.001f);
        assertEquals("Test insert with photo", insertedRecord.getNote());
        assertEquals("/data/user/0/com.example.theo_doi_can_nang/files/photos/test.jpg", insertedRecord.getImagePath());

        // 3. Update record with updated image path
        int updateResult = dbHelper.updateWeight(
                (int) insertId,
                "2026-10-09",
                69.8f,
                "Updated note",
                "/data/user/0/com.example.theo_doi_can_nang/files/photos/updated_test.jpg"
        );
        assertEquals(1, updateResult);

        // Verify update
        records = dbHelper.getAllWeights();
        WeightRecord updatedRecord = null;
        for (WeightRecord r : records) {
            if (r.getId() == (int) insertId) {
                updatedRecord = r;
                break;
            }
        }
        assertNotNull(updatedRecord);
        assertEquals(69.8f, updatedRecord.getWeight(), 0.001f);
        assertEquals("Updated note", updatedRecord.getNote());
        assertEquals("/data/user/0/com.example.theo_doi_can_nang/files/photos/updated_test.jpg", updatedRecord.getImagePath());

        // 4. Delete record
        int deleteResult = dbHelper.deleteWeight((int) insertId);
        assertEquals(1, deleteResult);
    }
}
