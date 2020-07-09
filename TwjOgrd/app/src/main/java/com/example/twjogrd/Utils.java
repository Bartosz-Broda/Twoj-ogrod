package com.example.twjogrd;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Utils {
    public static void removePlant(String plantID){
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("User_plants")
                .child(plantID)
                .setValue(null);
    }

}
