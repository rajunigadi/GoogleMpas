package com.vtechsofts.googlempas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by rajunigadi on 10/08/16.
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener, View.OnClickListener {

    private String TAG = "Google Maps";

    private GoogleMap mMap;
    private EditText etLocation;
    private TextView tvStandard;
    private TextView tvSatellite;

    private AddressResultReceiver mResultReceiver;
    private String address;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mResultReceiver = new AddressResultReceiver(new Handler());

        etLocation = (EditText) findViewById(R.id.et_location);
        tvStandard = (TextView) findViewById(R.id.tv_standard);
        tvSatellite = (TextView) findViewById(R.id.tv_satellite);

        tvStandard.setOnClickListener(this);
        tvSatellite.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        latitude = 12.9715987;
        longitude = 77.5945627;

        startIntentService();

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Bengaluru"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);

        if (googleMap == null) {
            Toast.makeText(this, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
        } else {
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setOnCameraMoveStartedListener(this);
            googleMap.setOnCameraMoveListener(this);
            googleMap.setOnCameraIdleListener(this);
        }
    }

    @Override
    public void onCameraMoveStarted(int i) {
        Log.e(TAG, "onCameraMoveStarted: " + i);
        if(i == REASON_GESTURE) {
            etLocation.setVisibility(View.GONE);
        } else {
            etLocation.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCameraMove() {
        Log.e("raja", "onCameraMove");
    }

    @Override
    public void onCameraIdle() {
        Log.e(TAG, "onCameraIdle");
        etLocation.setVisibility(View.VISIBLE);
        if(mMap != null) {
            latitude = mMap.getCameraPosition().target.latitude;
            longitude = mMap.getCameraPosition().target.longitude;
            LatLng latLng = new LatLng(latitude, longitude);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("User marker"));
            startIntentService();
        }
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, AddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LATITUDE_DATA_EXTRA, latitude);
        intent.putExtra(Constants.LONGITUDE_DATA_EXTRA, longitude);
        startService(intent);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.tv_standard) {
            if (mMap != null) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        } else if(view.getId() == R.id.tv_satellite) {
            if (mMap != null) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            address = resultData.getString(Constants.RESULT_DATA_KEY);

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //Toast.makeText(MainActivity.this, address, Toast.LENGTH_SHORT).show();
                if(etLocation != null) {
                    etLocation.setText(address);
                }
            }
        }
    }
}
