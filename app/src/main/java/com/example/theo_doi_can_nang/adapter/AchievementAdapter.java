package com.example.theo_doi_can_nang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theo_doi_can_nang.R;
import com.example.theo_doi_can_nang.TrophyDialog;
import com.example.theo_doi_can_nang.data.Achievement;

import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder> {

    private List<Achievement> list;

    public AchievementAdapter(List<Achievement> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_achievement, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Achievement achievement = list.get(position);
        Context context = holder.itemView.getContext();

        holder.tvTitle.setText(achievement.getTitle());
        holder.tvDescription.setText(achievement.getDescription());

        String emoji = "🏆";
        String model = achievement.getModelFileName();
        if (model != null) {
            if (model.contains("bronze")) {
                emoji = "🥉";
            } else if (model.contains("silver")) {
                emoji = "🥈";
            } else if (model.contains("gold")) {
                emoji = "🥇";
            } else if (model.contains("crown")) {
                emoji = "👑";
            }
        }
        holder.tvTrophyEmoji.setText(emoji);

        if (achievement.isUnlocked()) {
            holder.tvStatusBadge.setText("ĐÃ ĐẠT");
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.bmi_normal_text));
            if (holder.tvStatusBadge.getBackground() != null) {
                holder.tvStatusBadge.getBackground().setTint(ContextCompat.getColor(context, R.color.bmi_normal_bg));
            }

            if (achievement.getUnlockedDate() != null && !achievement.getUnlockedDate().isEmpty()) {
                holder.tvUnlockedDate.setText("Ngày: " + achievement.getUnlockedDate());
                holder.tvUnlockedDate.setVisibility(View.VISIBLE);
            } else {
                holder.tvUnlockedDate.setVisibility(View.GONE);
            }

            holder.itemView.setAlpha(1.0f);
        } else {
            holder.tvStatusBadge.setText("CHƯA ĐẠT");
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.app_secondary_text));
            if (holder.tvStatusBadge.getBackground() != null) {
                holder.tvStatusBadge.getBackground().setTint(ContextCompat.getColor(context, R.color.card_stroke));
            }

            holder.tvUnlockedDate.setVisibility(View.GONE);
            holder.itemView.setAlpha(0.65f);
        }

        // Bấm vào cúp để xem 3D xoay 360 độ
        holder.itemView.setOnClickListener(v -> TrophyDialog.show(context, achievement));
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public static class AchievementViewHolder extends RecyclerView.ViewHolder {

        TextView tvStatusBadge;
        TextView tvTrophyEmoji;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvUnlockedDate;

        public AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvTrophyEmoji = itemView.findViewById(R.id.tvTrophyEmoji);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvUnlockedDate = itemView.findViewById(R.id.tvUnlockedDate);
        }
    }
}
