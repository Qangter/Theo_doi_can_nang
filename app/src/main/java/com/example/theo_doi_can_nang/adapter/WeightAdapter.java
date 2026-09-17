package com.example.theo_doi_can_nang.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theo_doi_can_nang.AddEditActivity;
import com.example.theo_doi_can_nang.R;
import com.example.theo_doi_can_nang.data.DatabaseHelper;
import com.example.theo_doi_can_nang.data.WeightRecord;

import java.util.List;

public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {

    private List<WeightRecord> list;

    public WeightAdapter(List<WeightRecord> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public WeightViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weight, parent, false);

        return new WeightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull WeightViewHolder holder,
            int position) {

        WeightRecord record = list.get(position);

        holder.tvDate.setText(
                convertDateForDisplay(record.getDate())
        );

        holder.tvWeight.setText(
                String.format("%.1f kg", record.getWeight())
        );

        holder.tvNote.setText(record.getNote());

        // =========================
        // NÚT XÓA
        // =========================
        holder.btnDelete.setOnClickListener(v -> {

            Context context = v.getContext();

            new AlertDialog.Builder(context)
                    .setTitle("Xóa bản ghi")
                    .setMessage("Bạn có chắc muốn xóa bản ghi này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {

                        DatabaseHelper databaseHelper =
                                new DatabaseHelper(context);

                        int result =
                                databaseHelper.deleteWeight(record.getId());

                        if (result > 0) {

                            list.remove(holder.getAdapterPosition());

                            notifyItemRemoved(holder.getAdapterPosition());

                            Toast.makeText(
                                    context,
                                    "Đã xóa bản ghi",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {

                            Toast.makeText(
                                    context,
                                    "Xóa thất bại",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        // =========================
        // NÚT SỬA
        // =========================
        holder.btnEdit.setOnClickListener(v -> {

            Context context = v.getContext();

            Intent intent =
                    new Intent(context, AddEditActivity.class);

            intent.putExtra("id", record.getId());
            intent.putExtra("date", record.getDate());
            intent.putExtra("weight", record.getWeight());
            intent.putExtra("note", record.getNote());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class WeightViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvDate;
        TextView tvWeight;
        TextView tvNote;

        Button btnEdit;
        Button btnDelete;

        public WeightViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tvDate);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvNote = itemView.findViewById(R.id.tvNote);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
    private String convertDateForDisplay(String date) {

        try {

            java.text.SimpleDateFormat inputFormat =
                    new java.text.SimpleDateFormat(
                            "yyyy-MM-dd",
                            java.util.Locale.getDefault()
                    );

            java.text.SimpleDateFormat outputFormat =
                    new java.text.SimpleDateFormat(
                            "dd/MM/yyyy",
                            java.util.Locale.getDefault()
                    );

            java.util.Date parsedDate =
                    inputFormat.parse(date);

            return outputFormat.format(parsedDate);

        } catch (Exception e) {

            return date;
        }
    }
}