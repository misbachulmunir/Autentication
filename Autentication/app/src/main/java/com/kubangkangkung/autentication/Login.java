package com.kubangkangkung.autentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText lName,lPass;
    Button lbtnLogin;
    ProgressBar progressBar;
    FirebaseAuth auth;
    TextView lupapassword,register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        lName=findViewById(R.id.id_lemail);
        lPass=findViewById(R.id.id_lpass);
        lbtnLogin=findViewById(R.id.id_login);
        progressBar=findViewById(R.id.progressBarl);
        lupapassword=findViewById(R.id.id_forgot_password);
        register=findViewById(R.id.id_register_here);
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



                    LoginFrbs(email, pass);


            }
        });
        //ketika register here di tekan
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

        //ketika btn lupa passwor di tekan
        lupapassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail=new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog =new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.");
                passwordResetDialog.setView(resetMail);
                //Ketika tombol yes ditekan
                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    String mail=resetMail.getText().toString();

                    auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Login.this, "Success send link, Please check your mail", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this, "Link tidak terkirim"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    }
                });
                //ketika tombol no di tekan
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                    passwordResetDialog.create().show();
            }
        });
    }

    private void LoginFrbs(String email, String pass) {
        //login with firebase
        auth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Success", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(Login.this, "Error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }
//    public void Loginkemenu(View view){
//        startActivity(new Intent(getApplicationContext(),Register.class));
//
//    }

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