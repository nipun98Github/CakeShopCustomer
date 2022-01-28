package com.customer.esweetcustomer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.customer.esweetcustomer.Holder.CartHolder;
import com.customer.esweetcustomer.Interface.ItemClickListener;
import com.customer.esweetcustomer.Model.MyCartModel;

import com.customer.esweetcustomer.Model.Product;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;

public class CartPaymentActivity extends NavigationOption {

    double tt = 0.00;
    private Button buttonCheckOut;
    RecyclerView recyclerView;
    FirebaseStorage storage;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirestoreRecyclerAdapter<MyCartModel, CartHolder> recyclerAdapter;
    private HashMap<CartHolder, MyCartModel> list;

    private int value;
    private int val;
    private FirebaseFirestore db;
    private TextView totalQtyTV;
    private TextView subTotal;
    private int totalamount;

    private String itemSval;
    private int Qtyforcart;
    private int QtyforcartTot = 0;
    private TextView cartname;
    private String productDocID;

//    private TextView deliveryFee;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_payment);

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        db = FirebaseFirestore.getInstance();

        totalQtyTV = findViewById(R.id.totalQty);
        cartname = findViewById(R.id.cartname);

//        deliveryFee = findViewById(R.id.deliveryFee);
        subTotal = findViewById(R.id.subTotal);
        buttonCheckOut = findViewById(R.id.check_out);
        cartname.setText(auth.getCurrentUser().getDisplayName());
        recyclerView = findViewById(R.id.viewCartRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        productDocID = intent.getStringExtra("productDocID");
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CartPaymentActivity.this);
//        productDocID = sp.getString("productDocID", "empty");
//        Log.d("prodocid..........",productDocID);

        Query query = firestore.collection("AddToCart").document(auth.getCurrentUser().getUid()).collection("User");
        FirestoreRecyclerOptions recycleoptions = new FirestoreRecyclerOptions.Builder<MyCartModel>().setQuery(query, MyCartModel.class).build();

        recyclerAdapter = new FirestoreRecyclerAdapter<MyCartModel, CartHolder>(recycleoptions) {


            @NonNull
            @Override
            public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_row, parent, false);
                return new CartHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CartHolder holder, int position, @NonNull MyCartModel model) {


                db.collection("Products").document(model.getProductID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Product product = documentSnapshot.toObject(Product.class);

                        holder.cartItemName.setText(model.getProductName());
                        holder.cartItemPrice.setText(String.valueOf(model.getProductPrice()));
                        holder.cartItemTotalPrice.setText(String.valueOf(Integer.parseInt(String.valueOf(model.getTotalQty())) * Double.parseDouble(String.valueOf(product.getProductPrice()))));
                        holder.cartItemQty.setText(String.valueOf(model.getTotalQty()));

                        holder.myCartModel = model;
                        StorageReference storageRef = storage.getReference();

                        storageRef.child("ProductImages/" + product.getProductImageUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Got the download URL for 'users/me/profile.png'
                                String s = uri.toString();

                                Log.d("CartactivityCartactivityCartactivity.............", "" + uri);
                                Glide.with(CartPaymentActivity.this).load(uri).into(holder.cartImage);
                            }

                        });
                    }
                });

                holder.cartItemPlusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        val = 0;
                        try {
                            val = Integer.parseInt(holder.cartItemQty.getText().toString());
                        } catch (Exception e) {
                            val = 0;
                        }
                        if (val < 10) {
                            val++;
                            holder.cartItemQty.setText(String.valueOf(val));


                            itemSval = holder.cartItemPrice.getText().toString();
                            Log.d("cartItemPrice", "" + holder.cartItemPrice.getText().toString());
                            double itemVal = Double.parseDouble(itemSval);
                            double itemTot = itemVal * val;
                            holder.cartItemTotalPrice.setText(String.valueOf(itemTot));
                            String cartDocId = getSnapshots().getSnapshot(position).getId();
                            Log.d("cartDocId", "" + cartDocId);

                            db.collection("AddToCart").document(auth.getCurrentUser().getUid()).collection("User").document(cartDocId).update("totalPrice", itemTot, "totalQty", val).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(CartPaymentActivity.this, "update sucess", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    }
                });
                holder.cartItemMinusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int val = 1;
                        try {

                            val = Integer.parseInt(holder.cartItemQty.getText().toString());
                        } catch (Exception e) {
                            val = 1;
                        }
                        if (val > 1) {
                            val--;

                        }

                        holder.cartItemQty.setText(String.valueOf(val));

                        itemSval = holder.cartItemPrice.getText().toString();
                        Log.d("cartItemPrice", "" + holder.cartItemPrice.getText().toString());
                        double itemVal = Double.parseDouble(itemSval);

                        double itemTot = itemVal * val;
                        holder.cartItemTotalPrice.setText(String.valueOf(itemTot));

                        String cartDocId = getSnapshots().getSnapshot(position).getId();
                        Log.d("cartDocId", "" + cartDocId);

                        db.collection("AddToCart").document(auth.getCurrentUser().getUid()).collection("User").document(cartDocId).update("totalPrice", itemTot, "totalQty", val).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CartPaymentActivity.this, "update sucess", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(CartPaymentActivity.this, "Sorry Nothing To Show ", Toast.LENGTH_SHORT).show();
                    }
                });


                holder.cartItemRemoveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartPaymentActivity.this);
                        builder.setTitle("Delete Task");
                        builder.setMessage("Are You Sure Want to Remove this From Cart ??");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                db.collection("AddToCart").document(auth.getCurrentUser().getUid())
                                        .collection("User").document(getSnapshots().getSnapshot(position).getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(CartPaymentActivity.this, "Your Item was Deleted from Cart", Toast.LENGTH_LONG).show();
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

        db.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User").whereEqualTo("customerGoogleUid", auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<MyCartModel> myCartModels = queryDocumentSnapshots.toObjects(MyCartModel.class);

                for (MyCartModel cart : myCartModels) {
                    String totalQty = String.valueOf(cart.getTotalQty());
                    Qtyforcart = Integer.parseInt(totalQty);
                    QtyforcartTot = QtyforcartTot + Integer.parseInt(totalQty);

                    db.collection("Products").document(cart.getProductID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Product product = documentSnapshot.toObject(Product.class);
                            double productPrice = product.getProductPrice();
                            String pr = String.valueOf(Qtyforcart * productPrice);
                            tt = tt + Double.parseDouble(pr);

                            totalQtyTV.setText(String.valueOf(QtyforcartTot));
                            db.collection("AddToCart").document(auth.getCurrentUser().getUid())
                                    .collection("User").whereEqualTo("customerGoogleUid", auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                private double totalPrice = 0.00;

                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    List<MyCartModel> myCartModels = queryDocumentSnapshots.toObjects(MyCartModel.class);
                                    for (MyCartModel cartModel : myCartModels) {

                                        totalPrice = totalPrice + cartModel.getTotalPrice();
                                        subTotal.setText(String.valueOf(totalPrice));
                                    }
                                }
                            });




                        }
                    });
                }


            }
        });


        recyclerView.setAdapter(recyclerAdapter);


        recyclerView.setLayoutManager(new LinearLayoutManager(CartPaymentActivity.this));
        buttonCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerAdapter.getItemCount() != 0) {
                    Intent paymentintent = new Intent(CartPaymentActivity.this, PaymentOptionActivity.class);
                    paymentintent.putExtra("subtotal", subTotal.getText().toString());

                    startActivity(paymentintent);

                }
            }
        });


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

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent redirectIntent = new Intent(CartPaymentActivity.this, LoginActivity.class);
//                        redirectIntent.setFlags(Intent.FLAG);
                        startActivity(redirectIntent);
                        finishActivity(100);
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
    public void onBackPressed() {
        finish();
    }


}