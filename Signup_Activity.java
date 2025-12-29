package com.example.assignmentno03;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    EditText emailEt, passwordEt;
    Button signupBtn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        signupBtn = findViewById(R.id.signupBtn);

        auth = FirebaseAuth.getInstance();

        signupBtn.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = emailEt.getText().toString().trim();
        String pass = passwordEt.getText().toString().trim();

        if(!email.contains("@") || pass.length() < 6){
            Toast.makeText(this,"Invalid email or password", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this,"Registration Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
