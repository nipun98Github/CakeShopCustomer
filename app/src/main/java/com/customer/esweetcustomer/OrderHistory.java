package com.customer.esweetcustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.customer.esweetcustomer.Holder.OrderHolder;
import com.customer.esweetcustomer.Model.Customer;
import com.customer.esweetcustomer.Model.Invoice;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class OrderHistory extends NavigationOption {
    private Spinner spinner;
    private EditText startDate,endDate;
    private ImageView refresh;
    private RecyclerView recyclerView;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference orderCollection;
    private FirestoreRecyclerAdapter<Invoice, OrderHolder> fsorderAdapter;
    private String docid;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ImageView ImageV;
    private TextView Email;
    private TextView UserName;

    private String imageUrlPath;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        spinner = findViewById(R.id.spinner_order_status);
        startDate = findViewById(R.id.date_start);
        refresh = findViewById(R.id.refresh_orderlist);
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
                        Glide.with(OrderHistory.this).load(s).into(ImageV);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

            }
        });
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(OrderHistory.this);
//        String mail = sp.getString("email", "empty");
        docid = sp.getString("customerDocId", "empty");

        orderCollection = db.collection("Invoice");

        recyclerView = findViewById(R.id.orderlist_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrderHistory.this));

        setSpinner();

        setAdepter("All");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startDate.setText("");
                String selectedItem = spinner.getSelectedItem().toString();
                if (selectedItem.equals("Pending")){
                    setAdepter("Pending");
                }else if (selectedItem.equals("Delivered")){
                    setAdepter("Delivered");
                }else if (selectedItem.equals("Received")){
                    setAdepter("Received");
                }else{
                    setAdepter("All");
                }
            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(OrderHistory.this,
                        new DatePickerDialog.OnDateSetListener() {



                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                startDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                setAdepter("time");

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });



    }

    private void setAdepter(String all) {


        Query loadInvoice = orderCollection.whereEqualTo("userDocId",docid);
        if (all.equals("All")){
            loadInvoice = orderCollection.whereEqualTo("customerDocId",docid);
        }else if (all.equals("Pending")){
            loadInvoice = orderCollection.whereEqualTo("customerDocId",docid).whereEqualTo("status","ordered");
        }else if (all.equals("Delivered")){
            loadInvoice = orderCollection.whereEqualTo("customerDocId",docid).whereEqualTo("status","delivered");
        }else if (all.equals("Received")){
            loadInvoice = orderCollection.whereEqualTo("customerDocId",docid).whereEqualTo("status","received");
        }else{
            loadInvoice = orderCollection.whereEqualTo("customerDocId",docid).whereGreaterThanOrEqualTo("date",startDate.getText().toString());
        }

        FirestoreRecyclerOptions recyclerOptions = new FirestoreRecyclerOptions.Builder<Invoice>().setQuery(loadInvoice,Invoice.class).build();
        fsorderAdapter = new FirestoreRecyclerAdapter<Invoice,OrderHolder>(recyclerOptions){

            @NonNull
            @Override
            public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderhistory_list_item,parent,false);
                return new OrderHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull OrderHolder holder, int position, @NonNull Invoice model) {
                String id = getSnapshots().getSnapshot(position).getId();
                holder.orderId.setText("Order Id:"+ id);
                holder.orderdate.setText("Order On:" + model.getDate()+"/"+model.getTime());
                holder.orderamount.setText("Total Amount: Rs." +model.getSubTotal());

                if (model.getStatus().equals("ordered")){
                    holder.orderS.setText("Pending..");
                    holder.orderS.setTextColor(Color.parseColor("#DC0000"));
                    //  holder.orderS.setTextColor(Color.red(R.color.red));
                    holder.status.setImageResource(R.drawable.ic_pending);
                }else if (model.getStatus().equals("delivered")){

                    holder.orderS.setText("Delivered..");
                    holder.orderS.setTextColor(Color.parseColor("#14AA09"));
                    holder.status.setImageResource(R.drawable.ic_check);
                    holder.recieve.setImageResource(R.drawable.ic_order_now);
                    holder.recieve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            recievedOrder(id);
                        }
                    });

                }else if (model.getStatus().equals("received")){
                    holder.orderS.setText("Received..");
                    holder.status.setImageResource(R.drawable.ic_check);
                    holder.orderS.setTextColor(Color.parseColor("#000000"));
                    holder.recieve.setOnClickListener(null);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent invoice = new Intent(OrderHistory.this, InvoiceActivity.class);
                        invoice.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        invoice.putExtra("invoiceId",id);
                        startActivity(invoice);
                    }
                });

            }
        };

        //set Adapter
        recyclerView.setAdapter(fsorderAdapter);
        fsorderAdapter.startListening();
    }

    private void recievedOrder(String id) {
        orderCollection.document(id).update("status","received").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(OrderHistory.this, "Order received successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinner() {
        String[] status = {"All","Pending","Delivered","Received"};
        ArrayAdapter vListAdapter = new ArrayAdapter(OrderHistory.this,android.R.layout.simple_selectable_list_item,status);
        spinner.setAdapter(vListAdapter);
    }
    }
