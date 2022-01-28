package com.customer.esweetcustomer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

public class NavigationOption extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


                return true;



        }

//
//    private void showPopup() {
//        AlertDialog.Builder alert = new AlertDialog.Builder(NavigationOption.this);
//        alert.setMessage("Are you sure?")
//                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        logout(); // Last step. Logout function
//
//                    }
//
//
//                }).setNegativeButton("Cancel", null);
//
//        AlertDialog alert1 = alert.create();
//        alert1.show();
//    }
//
//    private void logout() {
//        startActivity(new Intent(this, LoginActivity.class));
//        signOut();
//        finish();
//    }
//
//    private void signOut() {
//        // [START auth_fui_signout]
//        AuthUI.getInstance()
//                .signOut(this)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // ...
//                    }
//                });
//        // [END auth_fui_signout]
//    }

    public void drawerSet(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

//    @Override
//    public void onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }


    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            switch (item.getItemId()) {
                case R.id.nav_home:
                    Intent homei = new Intent(NavigationOption.this, HomeActivity.class);
                    startActivity(homei);

                    break;

                case R.id.nav_profile:

                    Intent profilei = new Intent(NavigationOption.this, ProfileActivity.class);
                    startActivity(profilei);

                    break;

                case R.id.nav_customize_orders:
                    Intent cusOrder = new Intent(NavigationOption.this, CustomizeOrderActivity.class);
                    startActivity(cusOrder);

                    break;

                case R.id.nav_wishlist:

                    Intent wish = new Intent(NavigationOption.this, WishListActivity.class);
                    startActivity(wish);

                    break;

                case R.id.nav_cart_payment:
                    Intent cartpay = new Intent(NavigationOption.this, CartPaymentActivity.class);
                    startActivity(cartpay);

                    break;

                case R.id.nav_order_details_history:
                    Intent cusOrderhis = new Intent(NavigationOption.this, OrderHistory.class);
                    startActivity(cusOrderhis);

                    break;

                case R.id.nav_about:

                    Intent about = new Intent(NavigationOption.this, AboutActivity.class);
                    startActivity(about);



                    break;

        }

        return true;
    }

}

