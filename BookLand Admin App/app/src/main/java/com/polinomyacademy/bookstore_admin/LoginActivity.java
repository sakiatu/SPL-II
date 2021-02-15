package com.polinomyacademy.bookstore_admin;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private TextInputLayout email_til, password_til;
    private ProgressBar progressBar;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (firebaseAuth.getCurrentUser() != null) {
            openHomePage();
            finish();
            return;
        }

        progressBar = findViewById(R.id.progressBar);
        email_til = findViewById(R.id.email_til);
        password_til = findViewById(R.id.password_til);

        btn_login = findViewById(R.id.login_btn);
        btn_login.setOnClickListener(v -> handleLogin());
    }

    private void openHomePage() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void handleLogin() {
        btn_login.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        String email = email_til.getEditText().getText().toString();
        String password = password_til.getEditText().getText().toString();
        email_til.setErrorEnabled(false);
        password_til.setErrorEnabled(false);

        if (email.isEmpty()) {
            email_til.setError("Enter Email");
            return;
        } else if (password.isEmpty()) {
            password_til.setError("Enter Password");
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                        openHomePage();
                        finish();
                    } else {
                        Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                        btn_login.setEnabled(true);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }
}