package com.customer.esweetcustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;


import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.customer.esweetcustomer.Holder.ProductHolder;
import com.customer.esweetcustomer.Interface.ItemClickListener;
import com.customer.esweetcustomer.Model.Category;
import com.customer.esweetcustomer.Model.MyCartModel;

import com.customer.esweetcustomer.Model.Product;
import com.customer.esweetcustomer.Model.WishList;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProductActivity extends NavigationOption {


    private RecyclerView ProductRecycle;
    FirebaseAuth auth;
    RatingBar RatingBar;
    float myrating = 0;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    CollectionReference collectionReference;
    FirebaseStorage storage;

    double totalPrice;

    private FirestoreRecyclerAdapter<Product, ProductHolder> recyclerAdapter;

    private Button AddItems, RemoveItems;
    private Button viewCartBtn;
    private Spinner spinner;

    String saveCurrentTime, saveCurrentDate;
    private String SpinnerCategory;
    private Toolbar toolbar;
    private String productNameIntent;
    private String productPriceIntent;
    private String productQtyIntent;
    private String categoryName;
    Query query;
    private String productID;
    private String proIDforWishList;
    private Product product;
    private double itemVal;
    private int val;
    public String customerdocId;
    public ProgressDialog loading;



//    private String productImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        loading = new ProgressDialog(this);
        storage = FirebaseStorage.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = FirebaseFirestore.getInstance();
        ProductRecycle = findViewById(R.id.product_recycle);
        customerdocId = auth.getCurrentUser().getUid();
//        Intent intent = getIntent();
//        intent.getStringExtra("categoryName");

        Intent intent = getIntent();
        if (intent.hasExtra("categoryName")) {
            categoryName = intent.getStringExtra("categoryName");
            // If extra was passed then this block is executed because hasExtra() method would return true on getting extra.
//            Toast.makeText(this, "haaaa---" + categoryName, Toast.LENGTH_SHORT).show();
            query = db.collection("Products").whereEqualTo("productCategoryName", categoryName);
        } else {
//            Toast.makeText(this, "naaaaaa", Toast.LENGTH_SHORT).show();
            query = db.collection("Products");
            // If extra was not passed then this block is executed because hasExtra() method would return false on not getting extra.
        }

        viewCartBtn = findViewById(R.id.viewCartBtn);
        spinner = findViewById(R.id.spinnerProduct);
        List<String> categories = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProductActivity.this, R.layout.support_simple_spinner_dropdown_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    categoryName = spinner.getSelectedItem().toString();
                    query = db.collection("Products").whereEqualTo("productCategoryName", categoryName);
                } else {
                    query = db.collection("Products");
                }

                if (recyclerAdapter != null) {
                    recyclerAdapter.stopListening();
                }
                loadData();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        db.collection("Category").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    categories.add("Select the Category");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String CategoryId = document.getId();
                        Category category1 = document.toObject(Category.class);
                        adapter.notifyDataSetChanged();
                        String categoryName = category1.getCategoryName();

                        Log.d("category...............>>>>>>>>>", categoryName);
                        categories.add(categoryName);
                    }
                    adapter.notifyDataSetChanged();
                    spinner.setSelection(categories.indexOf(categoryName));


                }
            }

        });




        loadData();

        ProductRecycle.setLayoutManager(new LinearLayoutManager(ProductActivity.this));


    }

    private void loadData() {
        FirestoreRecyclerOptions recycleoptions = new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();
        recyclerAdapter = new FirestoreRecyclerAdapter<Product, ProductHolder>(recycleoptions) {
            @NonNull
            @Override
            public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
                return new ProductHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductHolder holder, int position, @NonNull Product model) {


                holder.productNameField.setText(model.getProductName());
                holder.productNoteField.setText(model.getProductNote());
                holder.productPriceField.setText(String.valueOf(model.getProductPrice()));
                holder.product = model;

                StorageReference storageRef = storage.getReference();

                storageRef.child("ProductImages/" + model.getProductImageUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        String s = uri.toString();

//                        Log.d("HomeAactivity..............", "" + uri);
                        Glide.with(ProductActivity.this).load(uri).into(holder.productImage);
                    }
                });
                holder.increaseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        val = 1;
                        try {

                            val = Integer.parseInt(holder.qtyField.getText().toString());
                        } catch (Exception e) {
                            val = 1;
                        }
                        if (val < 10) {
                            val++;
                            holder.qtyField.setText(String.valueOf(val));

                            String itemSval = holder.productPriceField.getText().toString();
                            itemVal = Double.parseDouble(itemSval);
                            totalPrice = itemVal * val;
                        }


                    }
                });
                holder.decreaseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        val = 1;
                        try {

                            val = Integer.parseInt(holder.qtyField.getText().toString());
                        } catch (Exception e) {
                            val = 1;
                        }
                        if (val > 1) {
                            val--;

                        }

                        holder.qtyField.setText(String.valueOf(val));

                        String itemSval = holder.productPriceField.getText().toString();
                        itemVal = Double.parseDouble(itemSval);
                        totalPrice = itemVal * val;


                    }
                });


                holder.cartButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToCart();

                    }


                    private void addToCart() {

                        productID = getSnapshots().getSnapshot(position).getId();


                        db.collection("Products").document(productID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                product = documentSnapshot.toObject(Product.class);
                                product.getProductName();
                               product.getProductImageUrl();

                                Calendar calendar = Calendar.getInstance();

                                SimpleDateFormat currentDate = new SimpleDateFormat("MM dd,yyyy");
                                saveCurrentDate = currentDate.format(calendar.getTime());

                                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss: a");
                                saveCurrentTime = currentTime.format(calendar.getTime());


                                String qty = holder.qtyField.getText().toString();
                                int intQty = Integer.parseInt(qty);
                                double productPrice = Double.parseDouble(String.valueOf(model.getProductPrice()));
                                totalPrice = intQty * productPrice;

                                db.collection("AddToCart").document(auth.getCurrentUser().getUid())
                                        .collection("User").whereEqualTo("customerGoogleUid", auth.getCurrentUser().getUid()).whereEqualTo("productID", productID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                                                    Toast.makeText(ProductActivity.this, String.valueOf(eq) + " Item add your cart", Toast.LENGTH_SHORT).show();


                                                }
                                            });
                                        } else {
//
                                            MyCartModel myCartModel = new MyCartModel();
                                            myCartModel.setProductID(productID);
                                            myCartModel.setProductName(product.getProductName());
                                            myCartModel.setProductPrice(product.getProductPrice());
                                            myCartModel.setProductImageUrl(product.getProductImageUrl());
                                            myCartModel.setTotalQty(intQty);
                                            myCartModel.setCustomerGoogleUid(auth.getCurrentUser().getUid());
                                            myCartModel.setTotalPrice(totalPrice);
                                            myCartModel.setCurrentDate(saveCurrentDate);
                                            myCartModel.setCurrentTime(saveCurrentTime);

                                            db.collection("AddToCart").document(auth.getCurrentUser().getUid())
                                                    .collection("User").add(myCartModel)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {

                                                            Toast.makeText(ProductActivity.this, "Add to cart Success", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    }
                                });


                            }
                        });
                    }

                });
//

                holder.cartItemWishListBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        productID = getSnapshots().getSnapshot(position).getId();
                        addToWishList(getSnapshots().getSnapshot(position).getId(), customerdocId);
                    }

                    public void addToWishList(String productID, String customerdocId) {
                        loading.setTitle("Updating WishList");
                        loading.setMessage("Please Wait, while we are checking the credentials.");
                        loading.setCanceledOnTouchOutside(false);
                        loading.show();
                        db.collection("AddToWishList").document(auth.getCurrentUser().getUid()).collection("User")
                                .whereEqualTo("productID", productID).whereEqualTo("customerGoogleUid", customerdocId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> documentsSnapList = queryDocumentSnapshots.getDocuments();
                                if (documentsSnapList.size() > 0) {
                                    DocumentSnapshot d = documentsSnapList.get(0);
                                    String wishlist_id = d.getId();
                                    db.collection("AddToWishList").document(auth.getCurrentUser().getUid()).collection("User")
                                            .document(wishlist_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            loading.dismiss();
                                            holder.cartItemWishListBtn.setBackgroundResource(R.drawable.ic_wish_line);
                                            Toast.makeText(ProductActivity.this, "Item Removed From Wishlist", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                                } else {
                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat currentDate = new SimpleDateFormat("MM:dd:yyyy");
                                    saveCurrentDate = currentDate.format(calendar.getTime());
                                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                                    saveCurrentTime = currentTime.format(calendar.getTime());

                                    WishList wishList = new WishList();
                                    wishList.setCustomerGoogleUid(customerdocId);
                                    wishList.setProductID(productID);
                                    wishList.setCurrentTime(saveCurrentTime);
                                    wishList.setCurrentDate(saveCurrentDate);

                                    db.collection("AddToWishList").document(auth.getCurrentUser().getUid()).collection("User").add(wishList).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            loading.dismiss();

                                            holder.cartItemWishListBtn.setBackgroundResource(R.drawable.ic_wish_full);

                                            Toast.makeText(ProductActivity.this, "Item Added to wishlist Success", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                                }
                            }
                        });


                    }


                });


                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
//                        Toast.makeText(ProductActivity.this, "maaruu unaa", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.buyItNowButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent paymentOptionIntent = new Intent(ProductActivity.this, PaymentOptionActivity.class);
                        double v1 = Double.parseDouble(holder.productPriceField.getText().toString());
                        double v2 = Double.parseDouble( holder.qtyField.getText().toString());
                       double tot=v1*v2;
                        paymentOptionIntent.putExtra("subtotalbuynow",""+tot);
                        startActivity(paymentOptionIntent);
                    }
                });

                db.collection("AddToWishList").document(auth.getCurrentUser().getUid()).collection("User")
                        .whereEqualTo("productID", getSnapshots().getSnapshot(position).getId()).whereEqualTo("customerGoogleUid", customerdocId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentsSnapList = queryDocumentSnapshots.getDocuments();
                        if (documentsSnapList.size() > 0) {
                            holder.cartItemWishListBtn.setBackgroundResource(R.drawable.ic_wish_full);
                        }else {
                            holder.cartItemWishListBtn.setBackgroundResource(R.drawable.ic_wish_line);

                        }
                    }
                });

                viewCartBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cartinIntent = new Intent(ProductActivity.this, CartPaymentActivity.class);
                cartinIntent.putExtra("productDocID", getSnapshots().getSnapshot(position).getId());
//                cartinIntent.putExtra("productPriceIntent", productPriceIntent);
//                cartinIntent.putExtra("productQtyIntent", productQtyIntent);
                        startActivity(cartinIntent);
                    }
                });
            }
        };
        ProductRecycle.setAdapter(null);
        ProductRecycle.setAdapter(recyclerAdapter);
        recyclerAdapter.startListening();
    }



    @Override
    protected void onStart() {

        super.onStart();

        recyclerAdapter.startListening();

    }


    @Override
    protected void onStop() {
        super.onStop();
        recyclerAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent redirectIntent = new Intent(ProductActivity.this, LoginActivity.class);
//                        redirectIntent.setFlags(Intent.FLAG);
                        startActivity(redirectIntent);
                        finishActivity(100);
                    }
                });
        // [END auth_fui_signout]
    }


}