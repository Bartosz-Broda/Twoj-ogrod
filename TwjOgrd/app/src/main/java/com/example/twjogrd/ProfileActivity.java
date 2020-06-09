package com.example.twjogrd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private TextView textViewEmail;
    private Button logoutButton;
    private ImageButton plusButton;
    private DatabaseReference userPlants;

    private RecyclerView recyclerView;
    private ArrayList<DataSetFire>arrayList;
    private FirebaseRecyclerOptions<DataSetFire> options;
    private FirebaseRecyclerAdapter<DataSetFire,FirebaseViewHolder> adapter;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.setMessage("Ładowanie Twoich roślin...");
        progressDialog.show();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


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
        textViewEmail = findViewById(R.id.userEmail);
        textViewEmail.setText("Witaj, " + user.getEmail());
        logoutButton = findViewById(R.id.logoutButton);
        plusButton = findViewById(R.id.plusButton);
        logoutButton.setOnClickListener(this);
        plusButton.setOnClickListener(this);

        displayUserPlants();

    }

    @Override
    public void onClick(View v) {
        if(v == plusButton){
            FirebaseUser user = firebaseAuth.getCurrentUser();
            assert user != null;
            if (user.getEmail().equals("mlodszybro@wp.pl")){
                finish();
                startActivity(new Intent(this, AddPlantExtendedActivity.class));
            }
            else {
                finish();
                startActivity(new Intent(this, AddPlantActivity.class));
            }
        }

        if(v == logoutButton){
            finish();
            firebaseAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
    
    private void displayUserPlants(){
        //displaying plants saved by user in recyclerview just like in AddPlantActivity
        recyclerView = findViewById(R.id.userPlantList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<DataSetFire>();
        progressDialog = new ProgressDialog(this);
        Query keyQuery = FirebaseDatabase.getInstance().getReference("Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/User_plants");
        databaseReference = FirebaseDatabase.getInstance().getReference("plants");
        databaseReference.keepSynced(true);
        //below line matches keys from User_plants to keys in plants node
        options = new FirebaseRecyclerOptions.Builder<DataSetFire>().setIndexedQuery(keyQuery, databaseReference, DataSetFire.class).build();

        adapter = new FirebaseRecyclerAdapter<DataSetFire, FirebaseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FirebaseViewHolder holder, int position, @NonNull final DataSetFire model) {

                holder.namepol.setText(model.getNazwa());
                holder.namelat.setText(model.getNazwa_lac());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ProfileActivity.this, "Ta roślina została już dodana do Twojego ogrodu", Toast.LENGTH_SHORT).show();

                        /*Intent intent = new Intent(AddPlantActivity.this,Main2Activity.class);
                        intent.putExtra("namepol", model.getNazwa());
                        intent.putExtra("namelat", model.getNazwa_lac());
                        startActivity(intent);*/
                    }
                });
            }

            @NonNull
            @Override
            public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                progressDialog.dismiss();
                return new FirebaseViewHolder(LayoutInflater.from(ProfileActivity.this).inflate(R.layout.row,parent, false));
            }
        };

        recyclerView.setAdapter(adapter);
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
