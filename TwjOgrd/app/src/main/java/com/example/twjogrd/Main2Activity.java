package com.example.twjogrd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        String namepol = getIntent().getStringExtra("namepol");
        String namelat = getIntent().getStringExtra("namelat");
        Log.i("OUR VALUE", namepol);
        Log.i("OUR VALUE 2", namelat);
        Toast.makeText(this, ""+namepol, Toast.LENGTH_SHORT).show();

    }
}
