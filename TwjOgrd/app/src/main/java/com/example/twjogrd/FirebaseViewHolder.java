package com.example.twjogrd;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FirebaseViewHolder extends RecyclerView.ViewHolder {

    public TextView namepol,namelat;

    public FirebaseViewHolder(@NonNull View itemView) {
        super(itemView);

        namepol = itemView.findViewById(R.id.namePol);
        namelat = itemView.findViewById(R.id.nameLat);
    }
}
