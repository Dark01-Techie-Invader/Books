package com.example.assignmentno03;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailEt;
    Button resetBtn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEt = findViewById(R.id.emailEt);
        resetBtn = findViewById(R.id.resetBtn);
        auth = FirebaseAuth.getInstance();

        resetBtn.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String email = emailEt.getText().toString().trim();

        if(email.isEmpty()){
            Toast.makeText(this,"Enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(a ->
                        Toast.makeText(this,"Reset email sent", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
