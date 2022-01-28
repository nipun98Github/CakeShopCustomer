package com.customer.esweetcustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.customer.esweetcustomer.Holder.CategoryHolder;
import com.customer.esweetcustomer.Interface.ItemClickListener;
import com.customer.esweetcustomer.Model.Category;
import com.customer.esweetcustomer.Model.Customer;
import com.customer.esweetcustomer.Model.SlideAdaptor;
import com.customer.esweetcustomer.Model.SliderItem;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends NavigationOption {

    private DrawerLayout drawer;
    private Toolbar toolbar;


    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    FirebaseStorage storage;
    private CircleImageView ImageV;
    private TextView UserName;
    private TextView Email;
    private String name;
    private String email;
    private FirebaseUser mUser;
    private ImageView imageView_home;
    private Drawable profile;
    private RecyclerView recycleCategory;
    private LinearLayoutManager layoutmanager;
    FirestoreRecyclerAdapter<Category, CategoryHolder> firestoreRecyclerAdapter;
    private String imageUrlPath;
    private String CustomerImageForHeader;
    private FloatingActionButton floatingBtn;
    private String CategoryNameBundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = FirebaseFirestore.getInstance();
        recycleCategory = findViewById(R.id.recycleCategory);
        recycleCategory.setHasFixedSize(true);
        storage = FirebaseStorage.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        floatingBtn = findViewById(R.id.floatingBtn);
        drawer = findViewById(R.id.drawer_layout);
        floatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ProductActivity.class));
            }
        });
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
                        Glide.with(HomeActivity.this).load(s).into(ImageV);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

            }
        });


        final SliderView sliderView = findViewById(R.id.imageSlider);


        SlideAdaptor adapter = new SlideAdaptor(HomeActivity.this);
        adapter.addItem(new SliderItem("Birthday Cake", "https://birthdaycake24.com/uploads/worigin/2017/02/18/name-on-birthday-cake58a78dd85a1cf_c7aa32baa53417e93cc6efbd2adfaf44.jpg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"));
        adapter.addItem(new SliderItem("Chocolate Cake", "https://i.ytimg.com/vi/sdFCd4DgjDM/maxresdefault.jpg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"));
        adapter.addItem(new SliderItem("Wedding Cake", "https://5restaurante.com/wp-content/uploads/2020/02/Wedding-Cake0.jpg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"));
        adapter.addItem(new SliderItem("StrawBerry Cake", "https://food-images.files.bbci.co.uk/food/recipes/grandmas_chocolate_cake_60838_16x9.jpg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"));
        adapter.addItem(new SliderItem("Butter Cake", "https://www.momswhothink.com/wp-content/uploads/pound-cake.jpg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"));
        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();

        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                sliderView.setCurrentPagePosition(position);

            }
        });
        Query query = db.collection("Category");

        FirestoreRecyclerOptions recycleoptions = new FirestoreRecyclerOptions.Builder<Category>().setQuery(query, Category.class).build();

        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Category, CategoryHolder>(recycleoptions) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryHolder holder, int position, @NonNull Category model) {
                holder.categoryName.setText(model.getCategoryName());

                StorageReference storageRef = storage.getReference();
                storageRef.child("CategoryImages/" + model.getCategoryImageUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String s = uri.toString();
                        Glide.with(HomeActivity.this).load(s).into(holder.categoryImage);

                    }
                });


                Log.d("cegelryname.....>>>>>>>>>>>>>>>imegeurl........>>>>>>>>>>>>>", model.getCategoryImageUrl());
                Category category = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(HomeActivity.this, "" + category.getCategoryName(), Toast.LENGTH_SHORT).show();
                        final Intent pintent = new Intent(HomeActivity.this, ProductActivity.class);

                        pintent.putExtra("categoryName", category.getCategoryName());

                        startActivity(pintent);

                    }

                });
            }

            @NonNull
            @Override
            public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);

                return new CategoryHolder(view);
            }
        };
        recycleCategory.setAdapter(firestoreRecyclerAdapter);
        recycleCategory.setLayoutManager(new LinearLayoutManager(this));
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



    private void showPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
        alert.setMessage("Are you sure?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        logout(); // Last step. Logout function

                    }


                }).setNegativeButton("Cancel", null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    private void logout() {
        startActivity(new Intent(this, LoginActivity.class));
        signOut();
        finish();
    }

    private void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        // [END auth_fui_signout]
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.example_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.signout) {
            // This is first step.
            showPopup();
            return true;
        }
        switch (item.getItemId()) {
            case R.id.signout:
                signOut();
                return true;


            case R.id.nav_cart_payment_icon:
                startActivity(new Intent(HomeActivity.this, CartPaymentActivity.class));
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        firestoreRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firestoreRecyclerAdapter.stopListening();
    }


}