package com.example.twjogrd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class AddPlantExtendedActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton backButton;
    private Button addToBaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant_extended);

        backButton = (ImageButton) findViewById(R.id.backButton);
        addToBaseButton = (Button) findViewById(R.id.addToBaseButton);

        backButton.setOnClickListener(this);
        addToBaseButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == backButton){
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }

        if(v == addToBaseButton){
            finish();
            startActivity(new Intent(this, AddToBaseActivity.class));
        }
    }
}
