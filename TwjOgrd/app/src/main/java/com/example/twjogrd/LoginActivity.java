package com.example.twjogrd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button signInButton;
    private TextView registerTextView;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        progressDialog = new ProgressDialog (this);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //goes to the profile activity, because user is already logged in
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }

        editTextEmail = (EditText) findViewById(R.id.signInEmail);
        editTextPassword = (EditText) findViewById(R.id.signInPassword);
        signInButton = (Button) findViewById(R.id.signInButton);
        registerTextView = (TextView) findViewById(R.id.registerTextView);

        signInButton.setOnClickListener(this);
        registerTextView.setOnClickListener(this);

    }

    private void signInUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //checking if the password or email is empty
        if(TextUtils.isEmpty(email)){
            //no email passed by user
            Toast.makeText(this, "Podaj swój adres email", Toast.LENGTH_SHORT).show();
            //Stops the function execution
            return;
        }
        if(TextUtils.isEmpty(password)){
            //no password passed by user
            Toast.makeText(this, "Musisz podać hasło", Toast.LENGTH_SHORT).show();
            return;
        }

        //Everything OK, showing progress dialog at first
        progressDialog.setMessage("Logowanie...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            //Starts profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        }
                    }
                });
    }


    @Override
    public void onClick(View v) {
        if(v == signInButton){
            //starting sign in method
            signInUser();
        }
        if(v == registerTextView){
            //going to the register activity
            finish();
            startActivity(new Intent(this, RegisterActivity.class));
        }

    }
}
