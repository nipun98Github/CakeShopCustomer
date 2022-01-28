package com.customer.esweetcustomer;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.customer.esweetcustomer.Model.Customer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Register extends AppCompatActivity {

    public static int FILE_CHOOSER_CODE = 100;
    Button registerButton;
    FirebaseFirestore db;


    ImageView profilePicture;
    EditText nameField, emailField, mobileField, addressField;
    DatePicker picker;
    TextView tvw;

    private Button cameraBtn;
    private Button galleryBtn;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    private String currentPhotoPath;
    private File file;
    private StorageReference storageReference;
    private String authId;
    private String name;
    private String email;

    private Customer customer;
public static String IMAGEurlforFirebase;

    private String imageFileName;
    private File f;
    private String nameFirebase;
//    private String FcmId;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = FirebaseFirestore.getInstance();


        cameraBtn = findViewById(R.id.cameraBtn);
        profilePicture = findViewById(R.id.profilePicture);
        nameField = findViewById(R.id.register_name_field);
        emailField = findViewById(R.id.register_email_field);
        mobileField = findViewById(R.id.register_mobile_field);
        addressField = findViewById(R.id.register_address_field);
        galleryBtn = findViewById(R.id.galleryBtn);
        registerButton = findViewById(R.id.register_btn);
        tvw = (TextView) findViewById(R.id.register_date_textField);
        picker = (DatePicker) findViewById(R.id.datePicker);
        picker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                tvw.setText("Your Birthday is : " + picker.getDayOfMonth() + "/" + (picker.getMonth() + 1) + "/" + picker.getYear());

            }
        });
        storageReference = FirebaseStorage.getInstance().getReference();

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("auth_name");
        email = bundle.getString("auth_email");
        authId = bundle.getString("auth_id");
//        FcmId = bundle.getString("FcmId");

        nameField.setText(name);
        emailField.setText(email);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }

        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String userName = nameField.getText().toString();
                String userEmail = emailField.getText().toString();
                String userMobile = mobileField.getText().toString();
                String userAddress = addressField.getText().toString();

                customer = new Customer();
                customer.setName(userName);
                customer.setEmail(userEmail);
                customer.setMobile(userMobile);
                customer.setAddress(userAddress);
                customer.setGoogleUId(authId);
                customer.setDate(+picker.getDayOfMonth() + "/" + (picker.getMonth() + 1) + "/" + picker.getYear());
                customer.setImageUrlPath(getNameFirebase());

                db.collection("Customers").add(customer).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Intent redirectIntent = new Intent(Register.this, LoginActivity.class);
//                        redirectIntent.putExtra("CustomerImage",nameFirebase);
                        startActivity(redirectIntent);
//                        finish();
                        Toast.makeText(Register.this, "Registraion Successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Save Error!", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
        //oncreate endddddd
    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                f = new File(currentPhotoPath);
                profilePicture.setImageURI(Uri.fromFile(f));

                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));
                Log.d("currentPhotoPath", "" + currentPhotoPath);

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);


                uploadImageToFirebase(f.getName(), contentUri);
                Log.d("filele", "" + f.getName());

            }

        }


        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " + imageFileName);
                profilePicture.setImageURI(contentUri);
                Log.d("imagefiele", "" + imageFileName);
                uploadImageToFirebase(imageFileName, contentUri);


            }


        }

    }

    public String getNameFirebase() {
        return nameFirebase;
    }

    private void uploadImageToFirebase(String nameFirebase, Uri contentUri) {
        this.nameFirebase = nameFirebase;


        final StorageReference image = storageReference.child("Customerpictures/" + nameFirebase);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {


                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image URl is " + nameFirebase);




                    }
                });

                Toast.makeText(Register.this, "Image Is Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, "Upload Failled.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.customer.esweetcustomer.provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


}


