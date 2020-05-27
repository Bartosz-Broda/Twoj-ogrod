package com.example.twjogrd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button registerButton;
    private TextView textViewSignin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //goes to the profile activity, because user is already logged in
            finish();
            startActivity(new Intent(this, ProfileActivity.class));
        }

        progressDialog = new ProgressDialog (this);
        registerButton = (Button) findViewById(R.id.buttonRegister);
        editTextEmail = (EditText) findViewById(R.id.adresEmail);
        editTextPassword = (EditText) findViewById(R.id.haslo);
        textViewSignin = (TextView) findViewById(R.id.textView);
        registerButton.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
        ref = FirebaseDatabase.getInstance().getReference().child("users");

    }

    private void registerUser(){
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
        progressDialog.setMessage("Rejestrowanie...");
        progressDialog.show();

        //Registering new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.hide();
                        if(task.isSuccessful()){
                            //user successfully registered and logged in
                            //we will start profile activity here and display a toast
                            Toast.makeText(RegisterActivity.this, "Zarejestrowano!", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Rejestracja nie powiodła się", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if(view == registerButton){
            registerUser();
        }
        else if (view == textViewSignin){
            //opens login activity here
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

    }
}
