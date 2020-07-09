package com.example.twjogrd;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

public class FirebaseViewHolder extends RecyclerView.ViewHolder {

    public TextView namepol,namelat,id,temp_min,min_soil;
    public ImageView deletePlantBtn;

    public FirebaseViewHolder(@NonNull View itemView) {
        super(itemView);

        namepol = itemView.findViewById(R.id.namePol);
        namelat = itemView.findViewById(R.id.nameLat);
        id = itemView.findViewById(R.id.plantID);
        temp_min = itemView.findViewById(R.id.temp_min);
        min_soil = itemView.findViewById(R.id.wilg_min);
        deletePlantBtn = itemView.findViewById(R.id.deletePlant);
    }
}
