package com.example.assignmentno03;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailEt, passwordEt;
    Button loginBtn;
    TextView signupTv, forgotTv;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        loginBtn = findViewById(R.id.loginBtn);
        signupTv = findViewById(R.id.signupTv);
        forgotTv = findViewById(R.id.forgotTv);

        auth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(v -> loginUser());

        signupTv.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));

        forgotTv.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));

        // Auto-login if user is already logged in
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void loginUser() {
        String email = emailEt.getText().toString().trim();
        String pass = passwordEt.getText().toString().trim();

        if(email.isEmpty() || pass.isEmpty()){
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
