package com.bjiang.map_ex;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends Activity {

    private static LatLng goodLatLng = new LatLng(37, -120);
    private GoogleMap googleMap;
    private EditText et_address, et_finalAddress;
    LatLng addressPos, finalAddressPos;
    Marker addressMarkerp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_address = (EditText) findViewById(R.id.addressEditText);
        et_finalAddress = (EditText) findViewById(R.id.finalAddressEditText);


        // Initial Map
        try {

            if(googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // Put a dot on my current location
        googleMap.setMyLocationEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setTrafficEnabled(true);
        // 3D building
        googleMap.setBuildingsEnabled(true);
        // Get zoom button
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(goodLatLng)
                .title("Hello"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showAddressMarker(View view) {

        String newAddress = et_address.getText().toString();

        if(newAddress != null) {
            new PlaceAMarker().exeute();
        }
    }

    public void getDirections(View view) {
    }

    class PlaceAMarker extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            String startAddress = params[0];
            return null;
        }
    }
}
