package com.kubangkangkung.autentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText lName,lPass;
    Button lbtnLogin;
    ProgressBar progressBar;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        lName=findViewById(R.id.id_lemail);
        lPass=findViewById(R.id.id_lpass);
        lbtnLogin=findViewById(R.id.id_login);
        progressBar=findViewById(R.id.progressBarl);
        auth=FirebaseAuth.getInstance();

        lbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email=lName.getText().toString().trim();
                String pass=lPass.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    lName.setError("is Empety !");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                } if(TextUtils.isEmpty(pass)){
                    lPass.setError("is Empety !");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                } if(pass.length()<6){
                    lPass.setError("Terlalu pendek minimal 6 karakter");
                    progressBar.setVisibility(View.INVISIBLE);
                }

                //login with firebase
               auth.signInWithEmailAndPassword(email,pass)
                       .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if (task.isSuccessful()) {
                                   Toast.makeText(Login.this, "Success", Toast.LENGTH_SHORT).show();
                                   progressBar.setVisibility(View.INVISIBLE);
                                   startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                   finish();

                               } else {
                                   Toast.makeText(Login.this, "Error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                   progressBar.setVisibility(View.INVISIBLE);
                               }
                           }
                       });
            }
        });
    }
    public void LoginActivity(View view){
        startActivity(new Intent(getApplicationContext(),Register.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (auth.getCurrentUser()!=null){
            updateUI(currentUser);
        }

    }

    private void updateUI(FirebaseUser currentUser) {

        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
}