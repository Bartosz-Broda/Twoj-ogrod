package com.example.twjogrd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddToBaseActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton backButton;
    EditText nameEditText;
    Button saveButton;
    DatabaseReference reff;
    Plant plant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_base);

        backButton = (ImageButton) findViewById(R.id.back2Button);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        saveButton = (Button) findViewById(R.id.saveButton);
        reff = FirebaseDatabase.getInstance().getReference().child("Plants");
        plant = new Plant();

        backButton.setOnClickListener(this);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plant.setName(nameEditText.getText().toString().trim());

                reff.push().setValue(plant);
                Toast.makeText(AddToBaseActivity.this, "Zapisano w bazie", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == backButton){
           /* int x = plant.getID();
            plant.setID(x); */
            finish();
            startActivity(new Intent(this, AddPlantExtendedActivity.class));
        }

    }
}
