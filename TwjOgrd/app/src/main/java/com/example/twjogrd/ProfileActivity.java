package com.example.twjogrd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.twjogrd.model.CurrentWeather;
import com.example.twjogrd.model.CurrentWeatherDetails;
import com.example.twjogrd.ui.Cords;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProfileActivity";

    private FirebaseAuth firebaseAuth;

    private String userEmail;
    private TextView city;
    private TextView precip;
    private TextView wind;
    private TextView temperature;
    private ImageButton plusButton;
    private ToggleButton saveDelBtn;
    private ProgressBar progressBar;

    private RecyclerView recyclerView;
    private ArrayList<DataSetFire> arrayList;
    private FirebaseRecyclerOptions<DataSetFire> options;
    private FirebaseRecyclerAdapter<DataSetFire, FirebaseViewHolder> adapter;
    private DatabaseReference databaseReference;

    private LocationManager locationManager;
    private LocationListener locationListener;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();
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
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userEmail = user.getEmail();

        city = findViewById(R.id.textCity);
        wind = findViewById(R.id.textWind);
        precip = findViewById(R.id.textPrecip);
        temperature = findViewById(R.id.textTemperature);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        plusButton = findViewById(R.id.plusButton);
        plusButton.setOnClickListener(this);

        saveDelBtn = findViewById(R.id.toggleButton);
        saveDelBtn.setOnClickListener(this);
        SharedPreferences sharedPrefs = getSharedPreferences("com.example.twjogrd", MODE_PRIVATE);
        saveDelBtn.setChecked(sharedPrefs.getBoolean("SaveCityButton", false));



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                callForCurrentWeather(location.getLatitude(), location.getLongitude());

                SharedPreferences.Editor editor = getSharedPreferences("com.example.twjogrd", MODE_PRIVATE).edit();
                putDouble(editor, "latitude", location.getLatitude()).apply();
                putDouble(editor, "longitude", location.getLongitude()).apply();

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            },10);
            return;
        } else {
            getCurrentLocation();
        }


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
        if(v == saveDelBtn){
            if (saveDelBtn.isChecked())
            {
                SharedPreferences.Editor editor = getSharedPreferences("com.example.twjogrd", MODE_PRIVATE).edit();
                editor.putBoolean("SaveCityButton", true);
                editor.apply();
            }
            else
            {
                SharedPreferences.Editor editor = getSharedPreferences("com.example.twjogrd", MODE_PRIVATE).edit();
                editor.putBoolean("SaveCityButton", false);
                editor.apply();

                //this.getSharedPreferences("com.example.twjogrd", MODE_PRIVATE).edit().clear().apply();


            }
        }
    }

    //displaying plants saved by user in recyclerview just like in AddPlantActivity
    private void displayUserPlants(){
        recyclerView = findViewById(R.id.userPlantList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<DataSetFire>();
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
                holder.id.setText(model.getIdrosliny());
                holder.temp_min.setText(model.getTemp_min());
                holder.min_soil.setText(model.getWilg_min());
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
                progressBar.setVisibility(View.GONE);
                return new FirebaseViewHolder(LayoutInflater.from(ProfileActivity.this).inflate(R.layout.row_for_profile,parent, false));
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    getCurrentLocation();
                }
        }

    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(){
        if (saveDelBtn.isChecked()) {
            SharedPreferences sharedPrefs = getSharedPreferences("com.example.twjogrd", MODE_PRIVATE);
            double latitude = getDouble(sharedPrefs, "latitude", 0);
            double longitude = getDouble(sharedPrefs, "longitude", 0);
            callForCurrentWeather(latitude, longitude);
        }else{
            locationManager.requestLocationUpdates("gps", 20000, 30, locationListener);
        }
    }


    private void callForCurrentWeather(double latitude, double longitude){
        //Here Weatherbit.io api is called to get an actual data about weather.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CurrentWeatherApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CurrentWeatherApi api = retrofit.create(CurrentWeatherApi.class);

        Call<CurrentWeather> call = api.getCurrentWeather(latitude , longitude);

        call.enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                Log.d(TAG,"onResponse: Server Response: " + response.toString());
                Log.d(TAG,"onResponse: received Information: " + response.body().toString());
                ArrayList<CurrentWeatherDetails> detailsList  = response.body().getData();

                double windspd = Double.parseDouble(response.body().getData().get(0).getWind_spd()+"d");
                windspd = windspd*3.6;
                windspd = Math.round(windspd * 100);
                windspd = windspd/100;

                city.setText(detailsList.get(0).getCity_name());
                wind.setText("Wiatr: "+windspd+"km/h");
                precip.setText("Opady: "+detailsList.get(0).getPrecip()+"mm/h");
                temperature.setText("Temperatura: "+detailsList.get(0).getTemp()+"°C");

                for(int i=0; i<detailsList.size(); i++){
                    Log.d("temp", detailsList.get(i).getTemp());
                    Log.d("city_name", detailsList.get(i).getCity_name());
                    Log.d("precip", detailsList.get(i).getPrecip());
                    Log.d("wind_spd", detailsList.get(i).getWind_spd());
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    //SETTER for SharedPreferences (used for storing latitude and longitude double values)
    SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    //GETTER for SharedPreferences (used for getting latitude and longitude double values)
    double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);

        MenuItem item = menu.findItem(R.id.user);

        item.setTitle(userEmail);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                finish();
                firebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
            case R.id.settings:
                Toast.makeText(this, "OPCJE", Toast.LENGTH_SHORT).show();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}