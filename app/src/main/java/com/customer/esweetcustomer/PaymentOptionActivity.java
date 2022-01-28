package com.customer.esweetcustomer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.customer.esweetcustomer.Model.Customer;
import com.customer.esweetcustomer.Model.Invoice;
import com.customer.esweetcustomer.Model.InvoiceItem;
import com.customer.esweetcustomer.Model.MyCartModel;
import com.customer.esweetcustomer.Model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;

public class PaymentOptionActivity extends AppCompatActivity {

    private static final int PAYHERE_REQUEST = 10001;
    private Button currrent_location;
    private Button pay_now;
    public String subtotal;
    private RadioGroup myRadioGroup;
    private TextView textView_paymentMethod;
    private RadioButton cashOnRadio;
    private RadioButton visaRadio;
    private String textview_address;
    private String deliveryFee;
    private String estimateTime;
    private TextView cpTotal;
    private TextView cpAddress;
    private TextView cpdeliveryCharges;
    private TextView cpSubTotal;

    private double tot;
    private String maptotal;
    private TextView cpMobile;
    private TextView cpToWhom;
    private TextView cpPostalCode;
    private String docid;
    private String total;
    private String deliveryCharges;
    private String postalcode;
    private String address;
    private String toWhom;
    private String mobile;
    private FirebaseAuth auth;
    private CollectionReference invoiceItemCollection;
    private CollectionReference userCollection;
    private CollectionReference invoiceCollection;
    private FirebaseFirestore db;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String TAG = "PaymentOptionActivity";

    private String subtotalshare;
    private String subtotalbuynow;
    private String maptotalbuynow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_option);


        db = FirebaseFirestore.getInstance();
        invoiceCollection = db.collection("Invoice");
        userCollection = db.collection("Customers");
        invoiceItemCollection = db.collection("InvoiceItem");
        auth = FirebaseAuth.getInstance();

//        Bundle bundle = getIntent().getExtras();


        Log.d("paytotalpaytotal", "" + total);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(PaymentOptionActivity.this);
        docid = sp.getString("customerDocId", "empty");
//        subtotalshare = sp.getString("maptotal", "empty");
        Log.d("docid", "" + docid);
        cpTotal = findViewById(R.id.cpTotal);
        cpAddress = findViewById(R.id.cpAddress);
        cpdeliveryCharges = findViewById(R.id.cpdeliveryCharges);
        cpSubTotal = findViewById(R.id.cpSubTotal);
        cpMobile = findViewById(R.id.cpMobile);
        cpToWhom = findViewById(R.id.cpToWhom);
        cpPostalCode = findViewById(R.id.cpPostalCode);


        Intent intent = getIntent();
        subtotal = intent.getStringExtra("subtotal");
        maptotal = intent.getStringExtra("maptotal");
        subtotalbuynow = intent.getStringExtra("subtotalbuynow");
        maptotalbuynow = intent.getStringExtra("maptotalbuynow");

        Log.d("subtotalsubtotal...", "" + subtotalbuynow);
        textview_address = intent.getStringExtra("textview_address");
        deliveryFee = intent.getStringExtra("deliveryFee");
        Log.d("deliveryFeedeliveryFee...", "" + deliveryFee);

        if (maptotal == null) {
            cpTotal.setText(subtotal);

        } else {
            cpTotal.setText(String.valueOf(maptotal));

        }   if (maptotalbuynow == null) {
            cpTotal.setText(subtotalbuynow);

        } else {
            cpTotal.setText(String.valueOf(maptotalbuynow));

        }
        cpAddress.setText(textview_address);

        if (deliveryFee != null) {

            cpdeliveryCharges.setText(String.valueOf(deliveryFee));
        }
        if (maptotal != null && deliveryFee != null) {
            double subt = Double.parseDouble(maptotal) + Double.parseDouble(deliveryFee);
            cpSubTotal.setText(String.valueOf(subt));

        }

        myRadioGroup = findViewById(R.id.myRadioGroup);
        cashOnRadio = findViewById(R.id.cashOnRadio);
        visaRadio = findViewById(R.id.visaRadio);
        textView_paymentMethod = findViewById(R.id.textView_paymentMethod);


        currrent_location = findViewById(R.id.currrent_location);
        pay_now = findViewById(R.id.pay_now);


        currrent_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapintent = new Intent(PaymentOptionActivity.this, MapAddressActivity.class);
                mapintent.putExtra("total", subtotal);
                mapintent.putExtra("totalbuynow", subtotalbuynow);
                startActivity(mapintent);


            }
        });
        pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getPaymentGetway();

            }

            public void getPaymentGetway() {
                InitRequest req = new InitRequest();
                req.setMerchantId("1218204");       // Your Merchant PayHere ID
                req.setMerchantSecret("8MPoJ189Z008RiXeBtKZSW4vXOMI98uf38X2JrinWWky"); // Your Merchant secret (Add your app at Settings > Domains & Credentials, to get this))
                req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
                req.setAmount(Double.parseDouble(cpSubTotal.getText().toString()));             // Final Amount to be charged
                req.setOrderId("invoiceId");        // Unique Reference ID
                req.setItemsDescription("Cake Town Customer Payment");  // Item description title
                req.setCustom1("This is the custom message 1");
                req.setCustom2("This is the custom message 2");
                req.getCustomer().setFirstName(auth.getCurrentUser().getDisplayName());
                req.getCustomer().setLastName("perera");
                req.getCustomer().setEmail(auth.getCurrentUser().getEmail());
                req.getCustomer().setPhone(auth.getCurrentUser().getPhoneNumber());
                req.getCustomer().getAddress().setAddress(cpAddress.getText().toString());
                req.getCustomer().getAddress().setCity("Gampaha District");
                req.getCustomer().getAddress().setCountry("Sri Lanka");


                Intent intent = new Intent(PaymentOptionActivity.this, PHMainActivity.class);
                intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
                PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
                startActivityForResult(intent, PAYHERE_REQUEST); //unique request ID like private final static int PAYHERE_REQUEST = 11010;


            }
        });


        RadioGroup rg = (RadioGroup) findViewById(R.id.myRadioGroup);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.cashOnRadio:

                        textView_paymentMethod.setText("Your Payment Method is " + cashOnRadio.getText().toString());
                        break;
                    case R.id.visaRadio:


                        textView_paymentMethod.setText("Your Payment Method is " + visaRadio.getText().toString());
                        break;

                }

            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
            if (resultCode == Activity.RESULT_OK) {
                String msg;
                if (response != null) {
                    if (response.isSuccess()) {

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat currentDate = new SimpleDateFormat("d-M-yyyy");
                        saveCurrentDate = currentDate.format(calendar.getTime());
                        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                        saveCurrentTime = currentTime.format(calendar.getTime());

                        Invoice invoice = new Invoice();
                        invoice.setCustomerDocId(docid);
                        invoice.setAddress(cpAddress.getText().toString());
                        invoice.setDate(saveCurrentDate);
                        invoice.setTime(saveCurrentTime);
                        invoice.setContactNumber(Integer.parseInt(cpMobile.getText().toString()));
                        invoice.setStatus("ordered");
                        invoice.setPaymentType(textView_paymentMethod.getText().toString());
                        invoice.setPostalCode(Integer.parseInt(cpPostalCode.getText().toString()));
                        invoice.setRecipientName(cpToWhom.getText().toString());
                        invoice.setSubTotal(Double.parseDouble(cpSubTotal.getText().toString()));

                        invoiceCollection.add(invoice).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                String invoiceId = documentReference.getId();

                                db.collection("AddToCart").document(auth.getCurrentUser().getUid())
                                        .collection("User").whereEqualTo("customerGoogleUid", auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("complete", "tasksuccess");
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                MyCartModel cart = document.toObject(MyCartModel.class);

                                                db.collection("Products").document(cart.getProductID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                        Product product = documentSnapshot.toObject(Product.class);
                                                        String productId = documentSnapshot.getId();


                                                        InvoiceItem item = new InvoiceItem();
                                                        item.setInvoiceDocId(invoiceId);
                                                        item.setProductDocId(productId);
                                                        item.setItemQty(String.valueOf(cart.getTotalQty()));
                                                        item.setItemTotal(String.valueOf(cart.getTotalPrice()));

                                                        invoiceItemCollection.add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Log.d("itemitemitemitem..........", "itemitemitemitem");
                                                                db.collection("AddToCart").document(auth.getCurrentUser().getUid())
                                                                        .collection("User").document(document.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

//
                                                                    }
                                                                });
                                                            }


                                                        });

                                                    }


                                                });
                                                Log.d("aaaa", cart.getProductID());

                                            }

                                            Intent oo = new Intent(PaymentOptionActivity.this, InvoiceActivity.class);

                                            oo.putExtra("invoiceId", invoiceId);
                                            startActivity(oo);
                                            cpMobile.setText("");
                                            cpToWhom.setText("");
                                            cpAddress.setText("");
                                            cpPostalCode.setText("");
                                            cpTotal.setText("");
                                            cpdeliveryCharges.setText("");
                                            cpSubTotal.setText("");
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }

//                                        Log.d("onSuccessonSuccessonSuccess.........", "onsuccessonsuccess");

                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PaymentOptionActivity.this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        Toast.makeText(this, "Success Payment", Toast.LENGTH_SHORT).show();


                    } else {
                        msg = "Result:" + response.toString();
                    }
                } else {
                    msg = "Result: no response";
                }
                //Log.d(TAG, msg);
                //  textView.setText(msg);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response != null) {
                    Toast.makeText(this, response.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    // textView.setText("User canceled the request");
                    Toast.makeText(this, "User canceled the request", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}