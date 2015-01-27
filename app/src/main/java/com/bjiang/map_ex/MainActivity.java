package com.bjiang.map_ex;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends Activity {

    private static LatLng goodLatLng = new LatLng(37, -120);
    private GoogleMap googleMap;
    private EditText et_address, et_finalAddress;
    LatLng addressPos, finalAddressPos;
    Marker addressMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_address = (EditText) findViewById(R.id.addressEditText);
        et_finalAddress = (EditText) findViewById(R.id.finalAddressEditText);


        // Initial Map
        try {

            if (googleMap == null) {
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

        if (newAddress != null) {
            new PlaceAMarker().execute(newAddress);
        }
    }

    public void getDirections(View view) {

        String startingAddress = et_address.getText().toString();
        String finalAddress = et_finalAddress.getText().toString();

        if ((startingAddress.equals("")) || finalAddress.equals("")) {
            Toast.makeText(this, "Enter a starting and Ending address", Toast.LENGTH_SHORT).show();
        } else {
            new GetDirections().execute(startingAddress, finalAddress);
        }
    }

    class PlaceAMarker extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String startAddress = params[0];
            startAddress = startAddress.replaceAll(" ", "%20");

            getLatLng(startAddress, false);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            addressMarker = googleMap.addMarker(new MarkerOptions()
            .position(addressPos).title("Address"));
        }
    }

    class GetDirections extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params) {
            String startAddress =  params[0];
            startAddress = startAddress.replaceAll(" ", "%20");
            getLatLng(startAddress, false);

            String endingAddress = params[1];
            endingAddress = endingAddress.replaceAll(" ", "%20");
            getLatLng(endingAddress, true);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String geoUriString = "http://maps.google.com/maps?addr=" +
                    addressPos.latitude + "," +
                    addressPos.longitude + "&daddr=" +
                    finalAddressPos.latitude + "," +
                    finalAddressPos.longitude;

            Intent mapCall = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUriString));
            startActivity(mapCall);
        }
    }

    protected void getLatLng(String address, boolean setDestination) {
        String uri = "http://maps.google.com/maps/api/geocode/json?address="
                + address + "&sensor=false";

        HttpGet httpGet = new HttpGet(uri);

        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();

            InputStream stream = entity.getContent();

            int byteData;
            while ((byteData = stream.read()) != -1) {
                stringBuilder.append((char) byteData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        double lat = 0.0, lng = 0.0;

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
            lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");
            lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (setDestination) {
            finalAddressPos = new LatLng(lat, lng);
        } else {
            addressPos = new LatLng(lat, lng);
        }

    }
}
