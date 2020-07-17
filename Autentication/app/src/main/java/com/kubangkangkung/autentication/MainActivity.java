package com.kubangkangkung.autentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ImageView poto;
    TextView jenegane,teks1;
    Button tombol;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        poto=findViewById(R.id.id_gambar_main);
        jenegane=findViewById(R.id.id_nama_main);
        teks1=findViewById(R.id.textView3);
        tombol=findViewById(R.id.button);
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();

            jenegane.setText(email);
            Log.d(TAG, "onCreate: "+email +photoUrl);
            FirebaseUser userlogin = auth.getCurrentUser();
            if(userlogin.isEmailVerified()){
               teks1.setText("Verified");
            }else {
                teks1.setText("Not Verified");
            }
        }

    }

    //method sign out memakai onclick
    public void Logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();

    }
}