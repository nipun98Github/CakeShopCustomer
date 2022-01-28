package com.customer.esweetcustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.customer.esweetcustomer.Holder.WishListHolder;
import com.customer.esweetcustomer.Model.Customer;
import com.customer.esweetcustomer.Model.MyCartModel;
import com.customer.esweetcustomer.Model.Product;
import com.customer.esweetcustomer.Model.WishList;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class WishListActivity extends NavigationOption {
    FirebaseFirestore db;

    private RecyclerView wishlistRecycleView;
    private FirestoreRecyclerAdapter<WishList, WishListHolder> adapter;
    private String productId;
    FirebaseStorage storage;
    private FirebaseAuth auth;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String productName;
    private double productPrice;

    public EditText promtqty;
    public Button promtMinusBtn;
    public Button promtPlusBtn;
    private int val;
    public String productImageUrl;
    private DrawerLayout drawer;
    private Toolbar toolbar;


    private ImageView ImageV;
    private TextView Email;
    private TextView UserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
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
                String imageUrlPath = customer.getImageUrlPath();

                StorageReference storageRef = storage.getReference();
                Log.d("sssssssssssssCustomerImageForHeaders", "" + storageRef);
                storageRef.child("Customerpictures/" + imageUrlPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String s = uri.toString();
                        Log.d("ssssssssssssss", "" + s);
                        Glide.with(WishListActivity.this).load(s).into(ImageV);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

            }
        });


        wishlistRecycleView = findViewById(R.id.wishlistRecycleView);
        wishlistRecycleView.setHasFixedSize(true);


        Query query = db.collection("AddToWishList").document(auth.getCurrentUser().getUid()).collection("User");

        FirestoreRecyclerOptions recycleoptions = new FirestoreRecyclerOptions.Builder<WishList>().setQuery(query, WishList.class).build();

        adapter = new FirestoreRecyclerAdapter<WishList, WishListHolder>(recycleoptions) {

            @NonNull
            @Override
            public WishListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item, parent, false);

                return new WishListHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull WishListHolder holder, int position, @NonNull WishList model) {


                productId = model.getProductID();
                Log.d("productIdproductId.....................", "" + productId);


                db.collection("Products").document(productId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Product product = documentSnapshot.toObject(Product.class);


                        productName = product.getProductName();
                        String productNote = product.getProductNote();
                        productImageUrl = product.getProductImageUrl();
                        productPrice = product.getProductPrice();

                        holder.wishListItemName.setText(productName);
                        holder.wishListItemNote.setText(productNote);
                        holder.wishListItemPrice.setText(String.valueOf(productPrice));
                        StorageReference storageRef = storage.getReference();
                        storageRef.child("ProductImages/" + productImageUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Got the download URL for 'users/me/profile.png'
                                String s = uri.toString();

//                        Log.d("HomeAactivity..............", "" + uri);
                                Glide.with(WishListActivity.this).load(uri).into(holder.wishlistImage);


                            }
                        });
                        holder.wishlistAddToCartBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addToCart();

                            }

                            private void addToCart() {


                                LayoutInflater li = LayoutInflater.from(WishListActivity.this);
                                View promptsView = li.inflate(R.layout.promt, null);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        WishListActivity.this);

                                // set prompts.xml to alertdialog builder
                                alertDialogBuilder.setView(promptsView);

                                promtqty = promptsView.findViewById(R.id.promtqty);
                                promtPlusBtn = promptsView.findViewById(R.id.promtPlusBtn);
                                promtMinusBtn = promptsView.findViewById(R.id.promtMinusBtn);

                                promtPlusBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        val = 1;
                                        try {

                                            val = Integer.parseInt(promtqty.getText().toString());
                                        } catch (Exception e) {
                                            val = 1;
                                        }
                                        if (val < 10) {
                                            val++;
                                            promtqty.setText(String.valueOf(val));

//                                            String itemSval = holder.productPriceField.getText().toString();
//                                            itemVal =Double.parseDouble(itemSval);
//                                            totalPrice=itemVal*val;
                                        }


                                    }
                                });

                                promtMinusBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        val = 1;
                                        try {

                                            val = Integer.parseInt(promtqty.getText().toString());
                                        } catch (Exception e) {
                                            val = 1;
                                        }
                                        if (val > 1) {
                                            val--;

                                        }

                                        promtqty.setText(String.valueOf(val));

//                                        String itemSval = holder.productPriceField.getText().toString();
//                                        itemVal =Double.parseDouble(itemSval);
//                                        totalPrice=itemVal*val;

                                    }
                                });

                                // set dialog message
                                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        Log.d("prommmmmmt............", promtqty.getText().toString());
                                        holder.editTextResult.setText(promtqty.getText().toString());
                                        Calendar calendar = Calendar.getInstance();

                                        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy");
                                        saveCurrentDate = currentDate.format(calendar.getTime());

                                        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss: a");
                                        saveCurrentTime = currentTime.format(calendar.getTime());


                                        int intQty = 1;

//ADDING CART FIREBASE START

                                        db.collection("AddToCart").document(auth.getCurrentUser().getUid())
                                                .collection("User").whereEqualTo("customerGoogleUid", auth.getCurrentUser().getUid()).whereEqualTo("productID", model.getProductID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                                                if (documents.size() > 0) {
                                                    DocumentSnapshot documentSnapshot = documents.get(0);
                                                    String cartDocID = documentSnapshot.getId();
                                                    MyCartModel myCartModel = documentSnapshot.toObject(MyCartModel.class);
                                                    String totalQty = String.valueOf(myCartModel.getTotalQty());
                                                    int i = Integer.parseInt(totalQty);
                                                    int eq = intQty + i;
                                                    db.collection("AddToCart").document(auth.getCurrentUser().getUid()).collection("User").document(cartDocID).update("totalQty", eq, "totalPrice", eq * productPrice).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(WishListActivity.this, String.valueOf(eq) + " Item add your cart", Toast.LENGTH_SHORT).show();


                                                        }
                                                    });
                                                } else {

                                                    MyCartModel myCartModel = new MyCartModel();
                                                    myCartModel.setProductID(model.getProductID());
                                                    myCartModel.setCurrentDate(saveCurrentDate);
                                                    myCartModel.setProductImageUrl(product.getProductImageUrl());
                                                    myCartModel.setCurrentTime(saveCurrentTime);
                                                    myCartModel.setProductName(holder.wishListItemName.getText().toString());
                                                    myCartModel.setProductPrice(Double.parseDouble(holder.wishListItemPrice.getText().toString()));
                                                    double itemprice = Double.parseDouble(holder.wishListItemPrice.getText().toString());
                                                    myCartModel.setTotalPrice(itemprice * Integer.parseInt(promtqty.getText().toString()));
                                                    myCartModel.setCustomerGoogleUid(auth.getCurrentUser().getUid());
                                                    myCartModel.setTotalQty(Integer.parseInt(promtqty.getText().toString()));


                                                    db.collection("AddToCart").document(auth.getCurrentUser().getUid())
                                                            .collection("User").add(myCartModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {

                                                            Toast.makeText(WishListActivity.this, "Add to cart Success", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }
                                        });
//


                                    }
                                })
                                        .setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });

                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();

                                // show it
                                alertDialog.show();


                            }


                        });


                    }


                });


                holder.wishlistRemoveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteboxcallForDelete();
                    }

                    public void deleteboxcallForDelete() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(WishListActivity.this);
                        builder.setTitle("Delete Task");
                        builder.setMessage("Are You Sure Want to Delete this From WishList ??");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("AddToWishList").document(auth.getCurrentUser().getUid())
                                        .collection("User").document(getSnapshots().getSnapshot(position).getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(WishListActivity.this, "Your Item was Deleted from WishList", Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();

                    }
                });


            }
        };

        wishlistRecycleView.setAdapter(adapter);
        wishlistRecycleView.setLayoutManager(new

                LinearLayoutManager(this));


    }


    @Override
    public void drawerSet(DrawerLayout drawerLayout) {
        super.drawerSet(drawerLayout);
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
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}