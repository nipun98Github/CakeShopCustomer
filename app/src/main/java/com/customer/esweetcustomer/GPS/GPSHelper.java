package com.customer.esweetcustomer.GPS;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.customer.esweetcustomer.MapAddressActivity;
import com.customer.esweetcustomer.MapsFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;


import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class GPSHelper implements LocationListener {

    private static final String TAG = "GPSHelper";
    MapsFragment mapFragment;

    public GPSHelper(MapsFragment mapFragment) {
        this.mapFragment = mapFragment;
    }

    public Location getCurrentLocationListner(Context mContext) {
        int MIN_TIME_BW_UPDATES = 200;
        int MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;
        Location loc = null;
        Double latitude, longitude;

        LocationManager locationManager = (LocationManager) mContext
                .getSystemService(LOCATION_SERVICE);

        // getting GPS status
        Boolean checkGPS = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        Boolean checkNetwork = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!checkGPS && !checkNetwork) {
            Toast.makeText(mContext, "No Service Provider Available", Toast.LENGTH_SHORT).show();
        } else {
            //this.canGetLocation = true;
            // First get location from Network Provider
            if (checkNetwork) {
                Toast.makeText(mContext, "Network", Toast.LENGTH_SHORT).show();
                try {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        loc = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    }

                    if (loc != null) {
                        Log.d(TAG, "getCurrentLocation: " + loc.getLatitude() + ", " + loc.getLongitude());

                        return loc;

                    }
                } catch (SecurityException e) {

                }
            }
        }
        // if GPS Enabled get lat/long using GPS Services
        if (checkGPS) {
            Toast.makeText(mContext, "GPS", Toast.LENGTH_SHORT).show();
            if (loc == null) {
                try {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        loc = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null) {
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();
                        }
                    }
                } catch (SecurityException e) {

                }
            }
        }
        Location locErr = null;
        return locErr;
    }


    @Override
    public void onLocationChanged(Location location) {


        if (mapFragment != null) {
//            Toast.makeText(mapFragment.getActivity(), "Location Changed :" + location.getLatitude() + " " +
//                    location.getLongitude(), Toast.LENGTH_SHORT).show();
//            try {
//                Geocoder geocoder = new Geocoder(mapFragment.getActivity(), Locale.getDefault());
//                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                String address = addresses.get(0).getAddressLine(0);
//                ((MapAddressActivity) mapFragment.getActivity()).setAddress(address);
//
//                //PACK DATA
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            if (mapFragment.currentMarker != null) {
//        ape Map Fragment akat ona krana rider ge device eke sensor aka access krla
//        current location aka aran apee map Fragment akat set kranava
//        location aka change vena sarayak gaane mee method aka call venva

                LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                mapFragment.currentMarker.setPosition(currentPosition);
                mapFragment.currentGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
//                mapFragment.currentGooglMap.moveCamera(CameraUpdateFactory.zoomTo(13));
//                mapFragment.getSeletedJob().update("currentRider_lat", location.getLatitude(),"currentRider_lon",location.getLongitude());

//                try {
//
//                    mapFragment.getcurrentJobFromHomeActivity().update("currentCustomer_lat", location.getLatitude(), "currentCustomer_lon", location.getLongitude());
//                } catch (Exception e) {
//                }


            }
        }


    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}