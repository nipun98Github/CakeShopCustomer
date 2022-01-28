package com.customer.esweetcustomer;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.customer.esweetcustomer.Model.Customer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends NavigationOption {


    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ImageView ImageV;
    private TextView Email;
    private TextView UserName;
    FirebaseFirestore db;
    private String imageUrlPath;
    FirebaseStorage storage;
    private Button peditbtn;
    private EditText pname;
    private EditText pmobile;
    private EditText pemail;
    private EditText paddress;
    private String customerId;
    private ImageView pimage;
    private String cusdocid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        peditbtn = findViewById(R.id.pEditbtn);
        pname = findViewById(R.id.pname);
        pmobile = findViewById(R.id.pmobile);
        pemail = findViewById(R.id.pemail);
        paddress = findViewById(R.id.paddress);
        pimage = findViewById(R.id.pimage);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
        cusdocid = sp.getString("customerDocId", "empty");
        db.collection("Customers").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                Customer customer = documentSnapshot.toObject(Customer.class);
                imageUrlPath = customer.getImageUrlPath();
              customerId= customer.getGoogleUId();
                String cname = customer.getName();
                pname.setText(cname);
                String cemail = customer.getEmail();
                pemail.setText(cemail);
                String cmobile = customer.getMobile();
                pmobile.setText(cmobile);
                String cadddress = customer.getAddress();
                paddress.setText(cadddress);
                StorageReference storageRef = storage.getReference();
                Log.d("sssssssssssssCustomerImageForHeaders", "" + storageRef);
                storageRef.child("Customerpictures/" + imageUrlPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String s = uri.toString();
                        Log.d("ssssssssssssss", "" + s);
                        Glide.with(ProfileActivity.this).load(s).into(ImageV);
                        Glide.with(ProfileActivity.this).load(s).into(pimage);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

            }
        });

        peditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = pname.getText().toString();
                String address = pmobile.getText().toString();
                String email = pemail.getText().toString();
                String mobile = paddress.getText().toString();
Log.d("cusdocid>>>>>>>>>>>",""+cusdocid);
                db.collection("Customers").document(cusdocid).
                        update("name", name, "address", address, "email", email, "mobile", mobile).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Update successfully!!!!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Error!!!!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
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