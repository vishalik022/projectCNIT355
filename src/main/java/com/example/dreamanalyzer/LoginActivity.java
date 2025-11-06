package com.example.dreamanalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.*;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText emailEt, passEt;
    private Button loginBtn, signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        emailEt = findViewById(R.id.email);
        passEt = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);

        loginBtn.setOnClickListener(v -> {
            String e = emailEt.getText().toString();
            String p = passEt.getText().toString();
            if (e.isEmpty() || p.isEmpty()) { Toast.makeText(this, "Fill in credentials", Toast.LENGTH_SHORT).show(); return; }
            auth.signInWithEmailAndPassword(e,p).addOnCompleteListener(task -> {
                if (task.isSuccessful()) startMain();
                else Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            });
        });

        signupBtn.setOnClickListener(v -> {
            String e = emailEt.getText().toString();
            String p = passEt.getText().toString();
            if (e.isEmpty() || p.isEmpty()) { Toast.makeText(this, "Fill in credentials", Toast.LENGTH_SHORT).show(); return; }
            auth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(task -> {
                if (task.isSuccessful()) startMain();
                else Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            });
        });
    }

    private void startMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) startMain();
    }
}
