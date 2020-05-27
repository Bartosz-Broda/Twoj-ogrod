package com.example.twjogrd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private TextView textViewEmail;
    private Button logoutButton;
    private ImageButton plusButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        //if user is not logged in, goes to the login activity
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewEmail = (TextView) findViewById(R.id.userEmail);
        textViewEmail.setText("Witaj, " + user.getEmail());
        logoutButton = (Button) findViewById(R.id.logoutButton);
        plusButton = (ImageButton) findViewById(R.id.plusButton);

        logoutButton.setOnClickListener(this);
        plusButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == plusButton){
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user.getEmail().equals("mlodszybro@wp.pl")){
                startActivity(new Intent(this, AddPlantExtendedActivity.class));
            }
            else {
                startActivity(new Intent(this, AddPlantActivity.class));
            }
        }

        if(v == logoutButton){
            finish();
            firebaseAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Toast.makeText(this, "OPCJE", Toast.LENGTH_SHORT).show();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
