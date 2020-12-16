package com.example.twjogrd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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

import com.example.twjogrd.interfaces.AgroWeatherApi;
import com.example.twjogrd.interfaces.CurrentWeatherApi;
import com.example.twjogrd.model.AgroWeather;
import com.example.twjogrd.model.AgroWeatherDetails;
import com.example.twjogrd.model.CurrentWeather;
import com.example.twjogrd.model.CurrentWeatherDetails;
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

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
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
    private TextView soilM;
    private TextView wind;
    private TextView temperature;
    private ImageButton plusButton;
    private ToggleButton saveDelBtn;
    private ProgressBar progressBar;
    private ImageButton infoBtn;


    private RecyclerView recyclerView;
    private ArrayList<DataSetFire> arrayList;
    private FirebaseRecyclerOptions<DataSetFire> options;
    private FirebaseRecyclerAdapter<DataSetFire, FirebaseViewHolder> adapter;
    private DatabaseReference databaseReference;

    private LocationManager locationManager;
    private LocationListener locationListener;

    public static double actualSoil;
    public static double actualTemp;

    public ProfileActivity() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
        soilM = findViewById(R.id.textSoilMoisture);
        temperature = findViewById(R.id.textTemperature);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        plusButton = findViewById(R.id.plusButton);
        plusButton.setOnClickListener(this);

        infoBtn = findViewById(R.id.infoButton);
        infoBtn.setOnClickListener(this);

        saveDelBtn = findViewById(R.id.toggleButton);
        saveDelBtn.setOnClickListener(this);
        SharedPreferences sharedPrefs = getSharedPreferences("com.example.twjogrd", MODE_PRIVATE);
        saveDelBtn.setChecked(sharedPrefs.getBoolean("SaveCityButton", false));

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                callForCurrentWeather(location.getLatitude(), location.getLongitude());
                callForAgroWeather(location.getLatitude(), location.getLongitude());

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
                showgpsAlert();
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
                startActivity(new Intent(this, AddPlantActivity.class));
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
                final AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileActivity.this);
                dialog.setTitle("Alert")
                        .setMessage("Czy chcesz usunąć tę lokalizację? Aplikacja automatycznie przypisze nową lokalizację, używając Twojej aktualnej pozycji.")
                        .setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                SharedPreferences.Editor editor = getSharedPreferences("com.example.twjogrd", MODE_PRIVATE).edit();
                                editor.putBoolean("SaveCityButton", false);
                                editor.apply();
                                getCurrentLocation();
                            }
                        })
                        .setNegativeButton("Anuluj", (paramDialogInterface, paramInt) -> {
                            saveDelBtn.setChecked(true);
                        });
                dialog.show();
            }
        }
        if(v == infoBtn){
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Info")
                    .setMessage("Ten parametr pokazuje przybliżoną wilgotność gleby na głębokości 10 - 40cm w Twojej okolicy.")
                    .setNegativeButton("OK", (paramDialogInterface, paramInt) -> {
                    });
            dialog.show();
        }

    }

    //displaying plants saved by user in recyclerview just like in AddPlantActivity
    private void displayUserPlants(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild("User_plants")) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recyclerView = findViewById(R.id.userPlantList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<>();
        Query keyQuery = FirebaseDatabase.getInstance().getReference("Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).child("User_plants");
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

                Log.d(TAG,"onResponse: ACTUAL SOIL: " + actualSoil);
                Log.d(TAG,"onResponse: ACTUAL TEMP: " + actualTemp);

                if(actualSoil > Double.parseDouble(model.getWilg_min()+"d")+2){
                    holder.min_soil.setText("OK");
                    holder.min_soil.setTextColor(Color.GREEN);
                }else if(actualSoil > Double.parseDouble(model.getWilg_min()+"d")) {
                    holder.min_soil.setText("Kontrola");
                    holder.min_soil.setTextColor(Color.YELLOW);
                }else if(actualSoil == 0){
                        holder.min_soil.setText("Brak danych");
                        holder.min_soil.setTextColor(Color.BLACK);
                }else{
                    holder.min_soil.setText("Podlej!");
                    holder.min_soil.setTextColor(Color.RED);
                }

                if(actualTemp > Double.parseDouble(model.getTemp_min()+"d")+5){
                    holder.temp_min.setText("OK");
                    holder.temp_min.setTextColor(Color.GREEN);
                }else if(actualTemp > Double.parseDouble(model.getTemp_min()+"d")){
                    holder.temp_min.setText("Niska");
                    holder.temp_min.setTextColor(Color.YELLOW);
                }else{
                    holder.temp_min.setText("Krytyczna!");
                    holder.temp_min.setTextColor(Color.RED);
                }

                holder.googleSearchBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                                String term = holder.namepol.getText().toString();
                                intent.putExtra(SearchManager.QUERY, term);
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(ProfileActivity.this, "Wyszukiwanie niemożliwe "+e , Toast.LENGTH_SHORT).show();
                            }
                    }
                });
                holder.deletePlantBtn.setOnClickListener(v -> {
                    String plantID = model.getIdrosliny();
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileActivity.this);
                    dialog.setTitle("Alert")
                            .setMessage("Czy chcesz usunąć tę roślinę ze swojego ogrodu?")
                            .setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    Utils.removePlant(plantID);
                                    Toast.makeText(ProfileActivity.this, "Usunięto z ogrodu", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Anuluj", (paramDialogInterface, paramInt) -> {
                            });
                    dialog.show();
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

    private void showgpsAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Włącz lokalizację")
                .setMessage("Twoja lokalizacja jest wyłączona.\nWłącz GPS aby używać aplikacji.")
                .setPositiveButton("Przejdź do ustawień lokalizacji", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(){
        if (saveDelBtn.isChecked()) {
            SharedPreferences sharedPrefs = getSharedPreferences("com.example.twjogrd", MODE_PRIVATE);
            double latitude = getDouble(sharedPrefs, "latitude", 0);
            double longitude = getDouble(sharedPrefs, "longitude", 0);
            callForCurrentWeather(latitude, longitude);
            callForAgroWeather(latitude, longitude);
        }else{
            locationManager.requestLocationUpdates("gps", 2000, 30, locationListener);
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

                //converting m/s to km/h and rounding the results
                double windspd = Double.parseDouble(response.body().getData().get(0).getWind_spd()+"d");
                windspd = windspd*3.6;
                windspd = Math.round(windspd * 100);
                windspd = windspd/100;
                double precipitation = Double.parseDouble(response.body().getData().get(0).getPrecip()+"d");
                precipitation = Math.round(precipitation * 100);
                precipitation = precipitation/100;

                city.setText(detailsList.get(0).getCity_name());
                wind.setText("Wiatr: "+windspd+"km/h");
                temperature.setText("Temperatura: "+detailsList.get(0).getTemp()+"°C");
                actualTemp = Double.parseDouble(detailsList.get(0).getTemp());

                if(windspd > 40){
                    wind.setText("Wiatr: "+windspd+"km/h" + " (Silny wiatr - zabezpiecz rośliny)");
                    wind.setTextColor(Color.RED);
                }else{
                    wind.setText("Wiatr: "+windspd+"km/h");
                    wind.setTextColor(city.getTextColors());
                }

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

    private void callForAgroWeather(double latitude, double longitude){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AgroWeatherApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AgroWeatherApi api = retrofit.create(AgroWeatherApi.class);

        //Some code to get an actual and yesterday date required by API.
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = formatter.format(todayDate);
        String tommorowString = formatter.format(tommorow());
        Log.d("DATA: ", todayString);
        Log.d("DATA: ", tommorowString);

        Call<AgroWeather> call = api.getAgroWeather(latitude , longitude, todayString ,tommorowString);

        call.enqueue(new Callback<AgroWeather>() {
            @Override
            public void onResponse(Call<AgroWeather> call, Response<AgroWeather> response) {
                if(response.body()!=null) {
                    Log.d(TAG, "onResponse: Server Response (agro): " + response.toString());
                    Log.d(TAG, "onResponse: received Information (agro): " + response.body().toString());

                ArrayList<AgroWeatherDetails> detailsList  = response.body().getData();

                    double soilMoisture = Double.parseDouble(response.body().getData().get(0).getSoilm_10_40cm() + "d");
                    soilMoisture = soilMoisture / 3;
                    soilMoisture = Math.round(soilMoisture * 100);
                    soilMoisture = soilMoisture / 100;

                    soilM.setText("Wilgotność gleby: " + soilMoisture + "%");
                    actualSoil = soilMoisture;

                    Log.d("soil moisture 10-40cm: ", detailsList.get(0).getSoilm_10_40cm());
                    Log.d("soil moisture 10-40cm: ", soilMoisture + "%");
                }else{
                    Toast.makeText(ProfileActivity.this, "Brak danych dot. wilgotnosci", Toast.LENGTH_SHORT).show();
                    soilM.setText("Wilgotność gleby: Brak danych");
                }

            }

            @Override
            public void onFailure(Call<AgroWeather> call, Throwable t) {
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

    private Date tommorow() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
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
        }

        return super.onOptionsItemSelected(item);
    }
}
