package com.customer.esweetcustomer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.customer.esweetcustomer.DirectinsLib.FetchURL;
import com.customer.esweetcustomer.GPS.GPSHelper;
import com.customer.esweetcustomer.Model.Admin;
import com.customer.esweetcustomer.Model.Customer;
import com.customer.esweetcustomer.pojo.mapDistanceObj;
import com.customer.esweetcustomer.pojo.mapTimeObj;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class MapsFragment extends Fragment {

    public Marker currentMarker;
    public GoogleMap currentGoogleMap;
    LatLng customerlocation;

    FirebaseFirestore db;
    private int LOCATION_PERMISSION = 100;
    FusedLocationProviderClient fusedLocationProviderClient;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsFragment.super.getContext());
            currentGoogleMap = googleMap;
            updateCustomerLocation();
            db = FirebaseFirestore.getInstance();
        }
    };

    public double shopLatitude;
    public double shopLongitude;
    private Polyline currentPoliline;
    private LatLng destinationlocation;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION) {
            if (permissions.length > 0) {
            }
            updateCustomerLocation();
        }
    }

    private void updateCustomerLocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

//                    Toast.makeText(MapsFragment.super.getContext(), "Location" + location.getLongitude() + "" + location.getLatitude(), Toast.LENGTH_SHORT).show();
                    customerlocation = new LatLng(location.getLatitude(), location.getLongitude());
                    destinationlocation = new LatLng(7.017126120174313, 79.94620835461768);



                    BitmapDescriptor ico_current = getBitmapDesc(getActivity(), R.drawable.ic_pin);
                    BitmapDescriptor ico_destination = getBitmapDesc(getActivity(), R.drawable.ic_location);

                    MarkerOptions current_location_marker = new MarkerOptions().icon(ico_current).draggable(true).position(customerlocation).title("I'm here");
                    MarkerOptions destination_location_marker = new MarkerOptions().icon(ico_destination).draggable(false).position(destinationlocation).title("I'want to go..");

                    currentMarker = currentGoogleMap.addMarker(current_location_marker);
                    currentGoogleMap.addMarker(destination_location_marker);

                    currentGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(customerlocation));
                    currentGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(15));

                    currentGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {
                            Log.d(TAG, "DragStarted");
                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {

                            Log.d(TAG, "Drag between");
                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {

                            Log.d(TAG, "Dragend");
                            customerlocation = marker.getPosition();
                            LatLng latLng = marker.getPosition();
                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            try {
                                android.location.Address address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
                                ((MapAddressActivity) getActivity()).setAddress(address);
                            } catch (Exception e) {

                            }


                            ((MapAddressActivity) getActivity()).setJobLatLang(customerlocation, destinationlocation
                            );
//                          asynctask eken url akata call krnva call karama apee data enva
                            new FetchURL() {

                                @Override
                                public void onTaskDone(Object... values) {

                                    if (currentPoliline != null) {
                                        currentPoliline.remove();

                                    }
                                    currentPoliline = currentGoogleMap.addPolyline((PolylineOptions) values[0]);
                                }

                                @Override
                                public void onDistanceTaskDone(mapDistanceObj distance) {
//                                    Toast.makeText(getActivity(), distance.getDistanceText(), Toast.LENGTH_SHORT).show();
                                    double startPrice = 50;
                                    double aditionalPricePerKm = 40;
//                                    ena distance eken 1km arala ithuru tika gannva
                                    double adtionalm = distance.getDistanceValM() - 1000;
//                                    A ena Meter gaana Km krnva
                                    double adtionalPrice = ((int) (adtionalm / 1000)) * aditionalPricePerKm;
                                    double estimatedPrice = startPrice + adtionalPrice;
//                                    Toast.makeText(getActivity(), "Your Estimated Price is: " + estimatedPrice, Toast.LENGTH_SHORT).show();
                                    ((MapAddressActivity) getActivity()).setEstimateValue(estimatedPrice);
                                }

                                @Override
                                public void onTimeTaskDone(mapTimeObj time) {
                                    ((MapAddressActivity) getActivity()).setDuration(time.getTimeInText());

                                }


                            }.execute(getUrl(customerlocation, destinationlocation, "driving"), "driving");


                        }
                    });

                } else {
                    Toast.makeText(MapsFragment.super.getContext(), "Location Not Found", Toast.LENGTH_SHORT).show();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MapsFragment.super.getContext(), "Location Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private BitmapDescriptor getBitmapDesc(FragmentActivity activity, int ic_tracking) {

        Drawable LAYER_1 = ContextCompat.getDrawable(activity, ic_tracking);
        LAYER_1.setBounds(0, 0, LAYER_1.getIntrinsicWidth(), LAYER_1.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(LAYER_1.getIntrinsicWidth(), LAYER_1.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        LAYER_1.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        GPSHelper gpsHelper = new GPSHelper(this);
        gpsHelper.getCurrentLocationListner(getContext());
    }

    public String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        Log.d(TAG, "URL:" + url);
        return url;
    }


}