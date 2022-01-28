package com.customer.esweetcustomer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.customer.esweetcustomer.Model.Customer;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private int SIGN_IN = 100;

    View loginButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String customerId;
    public String fcmToken;
    public String customer_id;
    private String NameFirebase;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.login_btn);
        loginButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                createSignInIntent();
            }

        });
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {

                    fcmToken = task.getResult();
                    Toast.makeText(LoginActivity.this, "Token" + fcmToken, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
//        HashMap<String, String> map = new HashMap<>();
//        map.put("uname", "dinuk");
//        map.put("pword", "123");
//        sendData(LoginActivity.this, "http://192.168.1.102:8080/Androidweb/A", map);
    }


//    public void sendData(Context context, String url, final Map<String, String> params) {
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jo = new JSONObject(response);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("respoooooo-------" + response);
////response text
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //error message
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                return params;
//            }
//
//            @Override
//            public String getBodyContentType() {
//                return "application/x-www-form-urlencoded; charset=UTF-8";
//            }
//        };
//        stringRequest.setShouldCache(false);
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                0,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        requestQueue.getCache().clear();
//        requestQueue.add(stringRequest);
//
//    }

    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());
        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build()
                , SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN) {
            // IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "Sign In success", Toast.LENGTH_LONG).show();
                String email = user.getEmail();
                String auth = user.getUid();
                String name = user.getDisplayName();

                Log.d("MAIN", name);
                Log.d("MAIN", email);

                db.collection("Customers").whereEqualTo("email", email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Customer> customerList = queryDocumentSnapshots.toObjects(Customer.class);
                        if (customerList.size() > 0) {
                            Customer customer = customerList.get(0);

                            customer_id = queryDocumentSnapshots.getDocuments().get(0).getId();



                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("customerDocId", customer_id);
                            editor.apply();


                            updateFcmToken(customer_id);

                            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                            homeIntent.putExtra("auth_name", name + "");
                            homeIntent.putExtra("auth_email", email + "");
                            homeIntent.putExtra("customerDocId", customer_id + "");
                            homeIntent.putExtra("telephone", customer.getMobile());
                            homeIntent.putExtra("auth_id", auth + "");
//                            homeIntent.putExtra("CustomerImage", NameFirebase + "");


                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(homeIntent);
                            finish();
                        } else {
                            Intent registerIntent = new Intent(LoginActivity.this, Register.class);
                            registerIntent.putExtra("auth_name", name + "");
                            registerIntent.putExtra("auth_email", email + "");
                            registerIntent.putExtra("auth_id", auth + "");
                            registerIntent.putExtra("customerDocId", customer_id + "");

                            registerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(registerIntent);
                            finish();
                        }
                    }
                    //List<Customer> customerList = queryDocumentSnapshots.toObjects(Customer.class);


                });


                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    private void updateFcmToken(String customer_id) {
//        customer id aka customer_id vena document id aka denna
        db.collection("Customers").document(customer_id).update("fcmId", fcmToken).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(LoginActivity.this, "updateFcmToken", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void signOut() {
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

    public void delete() {
        // [START auth_fui_delete]
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        // [END auth_fui_delete]
    }

    public void themeAndLogo() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();

        // [START auth_fui_theme_logo]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.ic_user)
                        .build(),
                SIGN_IN);
        // [END auth_fui_theme_logo]
    }

    public void privacyAndTerms() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();
        // [START auth_fui_pp_tos]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html")
                        .build(),
                SIGN_IN);
        // [END auth_fui_pp_tos]
    }

}