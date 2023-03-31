//
// Name                 Johnathan Graham
// Student ID           S2219349
// Programme of Study   Computing
//

package org.me.gcu.johnathan_graham_s2219349;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.Objects;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText txtAreaName, txtAreaLat, txtAreaLong, txtEqDate, txtEqTime, txtEqMagnitude, txtEqDepth, txtDistance, txtBearing;
    private Earthquake earthquake;

    private static float GetColour(Double magnitude) {
        if (magnitude <= 1) {
            return BitmapDescriptorFactory.HUE_GREEN;
        } else if (magnitude <= 2) {
            return BitmapDescriptorFactory.HUE_YELLOW;
        } else if (magnitude <= 3) {
            return BitmapDescriptorFactory.HUE_ORANGE;
        } else if (magnitude <= 4) {
            return BitmapDescriptorFactory.HUE_RED;
        } else {
            return BitmapDescriptorFactory.HUE_VIOLET;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Objects.requireNonNull(getSupportActionBar()).hide();

        Gson gson = new Gson();
        earthquake = gson.fromJson(getIntent().getStringExtra("EarthquakeJSON"), Earthquake.class);
        Log.e("DetailsActivity", earthquake.toString());

        txtAreaName = (EditText) findViewById(R.id.txtAreaName);
        txtAreaLat = (EditText) findViewById(R.id.txtAreaLat);
        txtAreaLong = (EditText) findViewById(R.id.txtAreaLong);
        txtEqDate = (EditText) findViewById(R.id.txtEqDate);
        txtEqTime = (EditText) findViewById(R.id.txtEqTime);
        txtEqMagnitude = (EditText) findViewById(R.id.txtEqMagnitude);
        txtEqDepth = (EditText) findViewById(R.id.txtEqDepth);
        txtDistance = (EditText) findViewById(R.id.txtDistance);
        txtBearing = (EditText) findViewById(R.id.txtBearing);

        txtAreaName.setText(earthquake.getLocation().getName());
        txtAreaLat.setText(earthquake.getLocation().getLatitude().toString());
        txtAreaLong.setText(earthquake.getLocation().getLongitude().toString());
        txtEqDate.setText(earthquake.getPublishDate().getDate());
        txtEqTime.setText(earthquake.getPublishDate().getTime());
        txtEqMagnitude.setText(String.format("%.2f", earthquake.getMagnitude()));
        txtEqDepth.setText(String.format("%.2fKM", earthquake.getDepth()));
        txtDistance.setText(String.format("%.2fKM", earthquake.getLocation().getDistanceFromGlasgowInKM()));
        txtBearing.setText(String.format("%.2fº", earthquake.getLocation().getBearingFromGlasgow()));

        loadMap();
    }

    private void loadMap() {
        SupportMapFragment mf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mf).getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        GoogleMap map = googleMap;
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night);
        map.setMapStyle(style);
        map.clear();


        LatLng latlong = new LatLng(earthquake.getLocation().getLatitude(), earthquake.getLocation().getLongitude());
        map.addMarker(new MarkerOptions()
                .position(latlong)
                .title(earthquake.toString())
                .icon(BitmapDescriptorFactory.defaultMarker(GetColour(earthquake.getMagnitude()))));


        LatLng pinCentered = new LatLng(earthquake.getLocation().getLatitude(), earthquake.getLocation().getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pinCentered, 8));


    }
}