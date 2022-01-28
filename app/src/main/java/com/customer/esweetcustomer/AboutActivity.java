package com.customer.esweetcustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.customer.esweetcustomer.Model.Customer;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AboutActivity extends NavigationOption {
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ImageView ImageV;
    private TextView Email;
    private TextView UserName;
    FirebaseFirestore db;
    private String imageUrlPath;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View view = navigationView.inflateHeaderView(R.layout.nav_header);
        ImageV = view.findViewById(R.id.header_image_view);
        Email = view.findViewById(R.id.header_email);
        UserName = view.findViewById(R.id.header_username);

        UserName.setText(user.getDisplayName());
        Email.setText(user.getEmail());

        db.collection("Customers").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                Customer customer = documentSnapshot.toObject(Customer.class);
                imageUrlPath = customer.getImageUrlPath();

                StorageReference storageRef = storage.getReference();
                Log.d("sssssssssssssCustomerImageForHeaders", "" + storageRef);
                storageRef.child("Customerpictures/" + imageUrlPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String s = uri.toString();
                        Log.d("ssssssssssssss", "" + s);
                        Glide.with(AboutActivity.this).load(s).into(ImageV);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

            }
        });

        VideoView videoView =(VideoView)findViewById(R.id.videoView1);

        //Creating MediaController
        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);

        //specify the location of media file
        Uri uri=Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.a_file);

        //Setting MediaController and URI, then starting the videoView
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void drawerSet(DrawerLayout drawerLayout) {
        super.drawerSet(drawerLayout);
    }


}