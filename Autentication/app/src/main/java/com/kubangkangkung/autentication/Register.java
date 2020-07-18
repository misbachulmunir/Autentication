package com.kubangkangkung.autentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText jNama,jEmail,jPass,jPhone;
    Button jRegister;
    ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseFirestore db ;
    String userID;

    private static final String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        jNama =findViewById(R.id.id_name);
        jEmail =findViewById(R.id.id_email);
        jPass =findViewById(R.id.id_pass);
        jPhone =findViewById(R.id.id_phone);

        jRegister =findViewById(R.id.id_register);
        auth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar);
        db= FirebaseFirestore.getInstance();

        //ketika tombol register di tekan
        jRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nama=jNama.getText().toString();
                final String email=jEmail.getText().toString().trim();
                final String pass=jPass.getText().toString().trim();
                final String phone=jPhone.getText().toString();

                if(TextUtils.isEmpty(email)){
                    jEmail.setError("is Empety !");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                } if(TextUtils.isEmpty(pass)){
                    jPass.setError("is Empety !");
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                } if(pass.length()<6){
                    jPass.setError("Terlalu pendek minimal 6 karakter");
                    progressBar.setVisibility(View.INVISIBLE);
                }

                //jika user TIDAK KOSONG login
//                if(auth.getCurrentUser()!=null){
//                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
//                }

                progressBar.setVisibility(View.VISIBLE);

                //daftar user ke firebase
                auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            //send verivifation link
                            FirebaseUser usernya = auth.getCurrentUser();
                            //ambil id user untuk membuat database pribadi
                             userID = auth.getCurrentUser().getUid();
                            //buat dtabase firestore namanya document
                            DocumentReference documentReference=db.collection("users").document(userID);
                            Map<String,Object>user=new HashMap<>();
                            user.put("email",email);
                            user.put("nama",nama);
                            user.put("phone",phone);
                            user.put("pass",pass);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: "+userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: "+e.getMessage());
                                }
                            });
                            //kirim email verifikasi
                            usernya.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    Toast.makeText(Register.this, "Verifikas email telah dikirim", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Register.this, "Gagal mengirim email veryfikasi"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            Toast.makeText(Register.this, "Success", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }else {
                            Toast.makeText(Register.this, "Error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });


            }
        });
    }
    public void RegisterActivity(View view){
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }



}