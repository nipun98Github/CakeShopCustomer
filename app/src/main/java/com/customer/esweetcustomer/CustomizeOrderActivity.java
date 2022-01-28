package com.customer.esweetcustomer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.customer.esweetcustomer.Model.Category;
import com.customer.esweetcustomer.Model.Customer;
import com.customer.esweetcustomer.Model.CustomizeOrder;
import com.customer.esweetcustomer.Model.Invoice;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.common.net.InternetDomainName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.customer.esweetcustomer.Register.GALLERY_REQUEST_CODE;

public class CustomizeOrderActivity extends NavigationOption {
    private static final int REQUEST_IMAGE_CAPTURE = 7070;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ImageView ImageV;
    private TextView Email;
    private TextView UserName;
    FirebaseFirestore db;
    private String imageUrlPath;
    FirebaseStorage storage;
    private Spinner spinner;
    private String categoryName;
    private EditText cosize;
    private EditText codescription;
    private EditText cocolor;
    private ImageView coimage;
    private Button cameraBtn2;
    private Button galleryBtn2;
    private Button couploadbtn;
    private Button ordernowBtn;
    private Button cancelbtn;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    private String currentPhotoPath;
    private String nameFirebase;

    private String imageFileName;

    private String cusdocid;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private FirebaseAuth auth;
    private File file;
    private String nameFirbase;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_order);
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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CustomizeOrderActivity.this);
        cusdocid = sp.getString("customerDocId", "empty");

        spinner = findViewById(R.id.coCategories);
        cosize = findViewById(R.id.cosize);
        codescription = findViewById(R.id.codescription);
        cocolor = findViewById(R.id.cocolor);
        coimage = findViewById(R.id.coimage);
        cameraBtn2 = findViewById(R.id.cameraBtn2);
        galleryBtn2 = findViewById(R.id.galleryBtn2);
        couploadbtn = findViewById(R.id.couploadbtn);
        ordernowBtn = findViewById(R.id.ordernowBtn);
        cancelbtn = findViewById(R.id.cancelbtn);
        storageRef = FirebaseStorage.getInstance().getReference();

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
                        Glide.with(CustomizeOrderActivity.this).load(s).into(ImageV);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

            }
        });

        List<String> categories = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CustomizeOrderActivity.this, R.layout.support_simple_spinner_dropdown_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    categoryName = spinner.getSelectedItem().toString();
//                    query = db.collection("Products").whereEqualTo("productCategoryName", categoryName);
                } else {
//                    query = db.collection("Products");
                }


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

        cameraBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture(v);

            }
        });
        galleryBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);

            }
        });

    }

    public void takePicture(View view) {
        Intent imagetakeintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (imagetakeintent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(imagetakeintent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            Uri imageUri = getImageUri(CustomizeOrderActivity.this, bitmap);
            uploadImageToFirebase(imageUri);
//            Date currentTime = Calendar.getInstance().getTime();
//            imageFileName =  currentTime+ getFileExt(uri);
            coimage.setImageBitmap(bitmap);


        }
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();

//                Date currentTime = Calendar.getInstance().getTime();
//                imageFileName =  currentTime+ getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " + imageFileName);
                coimage.setImageURI(contentUri);
                Log.d("imagefiele", "" + imageFileName);
                uploadImageToFirebase(contentUri);


            }


        }

        ordernowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String size = cosize.getText().toString();
                String description = codescription.getText().toString();
                String color = cocolor.getText().toString();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("d-M-yyyy");
                saveCurrentDate = currentDate.format(calendar.getTime());
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                saveCurrentTime = currentTime.format(calendar.getTime());


                CustomizeOrder order = new CustomizeOrder();
                order.setCategoryName(categoryName);
                order.setSize(size);
                order.setDescription(description);
                order.setColor(color);
                order.setOrderImageUrlPath(getNameFirbase());
                db.collection("CustomizeOrder").add(order).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CustomizeOrderActivity.this, "Order sent Successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(CustomizeOrderActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CustomizeOrderActivity.this, " order Error", Toast.LENGTH_SHORT).show();

                    }
                });


            }

        });
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public String getNameFirbase() {
        return nameFirbase;
    }

    public void uploadImageToFirebase(Uri contentUri) {
        this.nameFirbase = String.valueOf(contentUri);
        StorageReference image = storageRef.child("CategoryImages/" + nameFirbase);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(CustomizeOrderActivity.this, "Image Is Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CustomizeOrderActivity.this, "Upload Failled.", Toast.LENGTH_SHORT).show();
            }
        });

    }

}