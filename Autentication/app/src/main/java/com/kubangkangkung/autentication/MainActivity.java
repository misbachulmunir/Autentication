package com.kubangkangkung.autentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;

public class MainActivity extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ImageView poto;
    TextView jenegane,teks1,tnama,temail,tphone;
    Button tombol,ubahgambar;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseFirestore fbstore;
    String userID;
    ImageView profileImage;
    StorageReference storageReference;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        poto=findViewById(R.id.id_gambar_main);
       // jenegane=findViewById(R.id.id_nama_main);
        teks1=findViewById(R.id.textView3);
        tnama=findViewById(R.id.txtnama);
        temail=findViewById(R.id.txtemail);
        tphone=findViewById(R.id.txtphone);
        tombol=findViewById(R.id.button);

        //inisialisasi image
        profileImage=findViewById(R.id.id_gambar_main);
        ubahgambar=findViewById(R.id.id_ubahgambar);
        //inisialisasi untuk ambil data dari firestore
        fbstore=FirebaseFirestore.getInstance();
        userID=auth.getCurrentUser().getUid();
        



        //firbase storage
        storageReference= FirebaseStorage.getInstance().getReference();

        //ambil data
        DocumentReference documentReference=fbstore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
           tnama.setText(value.getString("nama"));
           tphone.setText(value.getString("phone"));
           temail.setText(value.getString("email"));
            }
        });

        //tamplkan gambar dari firebase
        StorageReference storageRef =storageReference.child("users/"+auth.getCurrentUser().getUid()+"profile.jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Gambar profile tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });

        //cek apakah sudah terverifikasi
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
//            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
//            String uid = user.getUid();

           // jenegane.setText(email);
            Log.d(TAG, "onCreate: "+email +photoUrl);
            FirebaseUser userlogin = auth.getCurrentUser();
            if(userlogin.isEmailVerified()){
               teks1.setText("Verified");
            }else {
                teks1.setText("Not Verified");
            }


            //ketika tombol ubah gambar ditekan
            ubahgambar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openGalery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(openGalery,1000);
                }
            });
        }

    }
//ketika gambar di pilih
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageurl=data.getData();
              //  profileImage.setImageURI(imageurl);
               //pamggil method upload image ke fire store
                UploadImage(imageurl);
            }else {
                Log.d(TAG, "onActivityResult: "+Activity.RESULT_OK);
            }
        }
    }

    //method sign out memakai onclick
    public void Logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();

    }
    //method upload image 
    private void UploadImage(Uri imageUrl){
        final ProgressDialog loding=new ProgressDialog(MainActivity.this);
        loding.setMessage("Tunggu Sebentar...");
        loding.show();
        final StorageReference fileref=storageReference.child("users/"+auth.getCurrentUser().getUid()+"profile.jpg");
        fileref.putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                        loding.dismiss();
                    }
                });
                Toast.makeText(MainActivity.this, "Berhasil Di Ubah", Toast.LENGTH_SHORT).show();     
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Gagal Mengubah", Toast.LENGTH_SHORT).show();
            }
        });
    }
}