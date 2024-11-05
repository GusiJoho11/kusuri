package com.websarva.wings.android.kusuri.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.websarva.wings.android.kusuri.Medication;
import com.websarva.wings.android.kusuri.R;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {
    private List<Medication> medicationList;

    public MedicationAdapter(List<Medication> medicationList) {
        this.medicationList = medicationList;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medication_list, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication medication = medicationList.get(position);
        holder.medicationName.setText(medication.name);
        holder.medicationDate.setText(medication.getFormattedCreationDate());
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    static class MedicationViewHolder extends RecyclerView.ViewHolder {
        TextView medicationName, medicationDate;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            medicationName = itemView.findViewById(R.id.medication_name);
            medicationDate =  itemView.findViewById(R.id.medication_date);
        }
    }

}