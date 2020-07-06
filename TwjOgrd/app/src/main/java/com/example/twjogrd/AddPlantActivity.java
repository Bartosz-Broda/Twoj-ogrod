package com.example.twjogrd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class AddPlantActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton backButton;
    private RecyclerView recyclerView;
    private ArrayList<DataSetFire>arrayList;
    private FirebaseRecyclerOptions<DataSetFire> options;
    private FirebaseRecyclerOptions<DataSetFire> optionsForSearch;
    private FirebaseRecyclerAdapter<DataSetFire,FirebaseViewHolder> adapter;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    //private DatabaseReference mRef;

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.setMessage("Ładowanie...");
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
        setContentView(R.layout.activity_add_plant);

        backButton = findViewById(R.id.backButton);
        progressDialog = new ProgressDialog(this);
        recyclerView = findViewById(R.id.plantList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("plants");
        databaseReference.keepSynced(true);
        options = new FirebaseRecyclerOptions.Builder<DataSetFire>().setQuery(databaseReference, DataSetFire.class).build();

        adapter = new FirebaseRecyclerAdapter<DataSetFire, FirebaseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FirebaseViewHolder holder, int position, @NonNull final DataSetFire model) {

                holder.namepol.setText(model.getNazwa());
                holder.namelat.setText(model.getNazwa_lac());
                holder.id.setText(model.getIdrosliny());
                holder.temp_min.setText(model.getTemp_min());
                holder.min_soil.setText(model.getWilg_min());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference("Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+
                                "/User_plants").child(holder.id.getText().toString().trim())
                                .setValue(true);
                        Toast.makeText(AddPlantActivity.this, "Roślina została dodana do Twojego ogrodu", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(AddPlantActivity.this, ProfileActivity.class));

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
                return new FirebaseViewHolder(LayoutInflater.from(AddPlantActivity.this).inflate(R.layout.row,parent, false));
            }
        };

        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchbar_menu, menu);
        MenuItem item  = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //filter as you type
                firebaseSearch(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    //searching data
    private void firebaseSearch(String searchText){
        String query;
        if(searchText.length()>0) {
            query = searchText.substring(0, 1).toUpperCase() + searchText.substring(1).toLowerCase();
        }else{
            query = searchText;
        }

        Query firebaseSearchQuery = databaseReference.orderByChild("nazwa").startAt(query).endAt(query+"\uf8ff");
        optionsForSearch = new FirebaseRecyclerOptions.Builder<DataSetFire>().setQuery(firebaseSearchQuery, DataSetFire.class).build();

        FirebaseRecyclerAdapter<DataSetFire, FirebaseViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<DataSetFire, FirebaseViewHolder>(optionsForSearch) {
                    @NonNull
                    @Override
                    public FirebaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return new FirebaseViewHolder(LayoutInflater.from(AddPlantActivity.this).inflate(R.layout.row,parent, false));
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull FirebaseViewHolder holder, int position, @NonNull DataSetFire model) {
                        holder.namepol.setText(model.getNazwa());
                        holder.namelat.setText(model.getNazwa_lac());
                        holder.id.setText(model.getIdrosliny());
                        holder.temp_min.setText(model.getTemp_min());
                        holder.min_soil.setText(model.getWilg_min());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseDatabase.getInstance().getReference("Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+
                                        "/User_plants").child(holder.id.getText().toString().trim())
                                        .setValue(true);
                                Toast.makeText(AddPlantActivity.this, "Roślina została dodana do Twojego ogrodu", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(AddPlantActivity.this, ProfileActivity.class));

                        /*Intent intent = new Intent(AddPlantActivity.this,Main2Activity.class);
                        intent.putExtra("namepol", model.getNazwa());
                        intent.putExtra("namelat", model.getNazwa_lac());
                        startActivity(intent);*/
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onClick(View v) {
        if(v == backButton)
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }

}

