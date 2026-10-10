package com.example.theo_doi_can_nang;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theo_doi_can_nang.adapter.AchievementAdapter;
import com.example.theo_doi_can_nang.data.Achievement;
import com.example.theo_doi_can_nang.data.AchievementManager;

import java.util.List;

public class AchievementsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAchievements;
    private AchievementAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        ImageButton btnBack = findViewById(R.id.btnBack);
        recyclerViewAchievements = findViewById(R.id.recyclerViewAchievements);

        btnBack.setOnClickListener(v -> finish());

        recyclerViewAchievements.setLayoutManager(new GridLayoutManager(this, 2));

        loadAchievements();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAchievements();
    }

    private void loadAchievements() {
        List<Achievement> list = AchievementManager.getAllAchievements(this);
        adapter = new AchievementAdapter(list);
        recyclerViewAchievements.setAdapter(adapter);
    }
}
