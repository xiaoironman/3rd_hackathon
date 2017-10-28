/*
 * Copyright (c) 2011-2017 HERE Global B.V. and its affiliate(s).
 * All rights reserved.
 * The use of this software is conditional upon having a separate agreement
 * with a HERE company for the use or utilization of this software. In the
 * absence of such agreement, the use of the software is not allowed.
 */
package com.here.android.tutorial;

//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;

public class BasicMapActivity extends Activity {

    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    // map embedded in the map fragment
    private Map map = null;

    // map fragment embedded in this activity
    private MapFragment mapFragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
    }



    private void initialize() {
        setContentView(R.layout.activity_main);

        // Search for the map fragment to finish setup by calling init().
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();
                    // Set the map center to the Vancouver region (no animation)
                    map.setCenter(new GeoCoordinate(49.196261, -123.004773, 0.0),
                            Map.Animation.NONE);
                    // Set the zoom level to the average between min and max
                    map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }
            }
        });
    }

    public void click1(View view){
        final TextView textView1 = (TextView)findViewById(R.id.title);
        final EditText edittext = (EditText)findViewById(R.id.editText);
        final Button button = (Button)findViewById(R.id.button_search);

        String input = edittext.getText().toString();
        PositioningManager position_manager = PositioningManager.getInstance();
        position_manager.start(PositioningManager.LocationMethod.GPS);
        GeoPosition myposition = position_manager.getPosition();

        if(input.contains("N")){
//        map.setCenter(new GeoCoordinate(49.196261, -123.004773, 0.0), Map.Animation.NONE);
//        map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
        map.setCenter(myposition.getCoordinate(), Map.Animation.NONE);
        map.setZoomLevel(map.getMaxZoomLevel());}

        else {
            String input_numbers[] = input.split(",");
            double lat = Double.parseDouble(input_numbers[0]);
            double longt = Double.parseDouble(input_numbers[1]);
            double alt = Double.parseDouble(input_numbers[2]);
            map.setCenter(new GeoCoordinate(lat, longt, alt), Map.Animation.NONE);
            map.setZoomLevel(map.getMaxZoomLevel());}
        }

    public void click2(View view){
//        click1(view);
        final EditText edittext = (EditText)findViewById(R.id.editText);
        PositioningManager position_manager = PositioningManager.getInstance();
        position_manager.start(PositioningManager.LocationMethod.GPS);
        GeoPosition myposition = position_manager.getPosition();

        map.setCenter(myposition.getCoordinate(), Map.Animation.NONE);
        double lat = myposition.getCoordinate().getLatitude();
        double longt = myposition.getCoordinate().getLongitude();
        double alt = myposition.getCoordinate().getAltitude();

        GeoCoordinate pos2 = new GeoCoordinate(-lat, 180.0+longt, alt);
        map.setCenter(pos2, Map.Animation.NONE);
        map.setZoomLevel(map.getMaxZoomLevel());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }


    /**
     * Checks the dynamically controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }
}
