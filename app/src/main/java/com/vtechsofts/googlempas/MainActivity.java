package com.vtechsofts.googlempas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        latitude = 12.9715987;
        longitude = 77.5945627;

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
        Log.e("raja", "onCameraMoveStarted: " + i);
    }

    @Override
    public void onCameraMove() {
        Log.e("raja", "onCameraMove");
    }

    @Override
    public void onCameraIdle() {
        Log.e("raja", "onCameraIdle");
    }

}
