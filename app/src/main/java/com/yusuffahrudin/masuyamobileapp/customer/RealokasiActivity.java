package com.yusuffahrudin.masuyamobileapp.customer;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealokasiActivity extends AppCompatActivity implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private LinearLayout btn_simpan_lokasi;
    private TextView tv_alamat;
    private LatLng latLng;
    private double lat, lng;
    private List<Address> addressList;
    private String kdcust, kdkota;
    private SessionManager sessionManager;
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Simpan Koordinat");
        setContentView(R.layout.activity_realokasi);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        kdkota = user.get(SessionManager.kdkota);

        Intent i = this.getIntent();
        lat = i.getExtras().getDouble("lat");
        lng = i.getExtras().getDouble("lng");
        kdcust = i.getExtras().getString("kdcust");

        btn_simpan_lokasi = findViewById(R.id.btn_simpan_lokasi);
        tv_alamat = findViewById(R.id.tv_alamat);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        configureCameraIdle();

        btn_simpan_lokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println(lat+" "+lng);
                simpanKoordinat();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng lokasi = new LatLng(lat, lng);
        float zoomLevel = 18.0f; //This goes up to 21
        mMap.addMarker(new MarkerOptions().position(lokasi));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, zoomLevel));
        mMap.setOnCameraIdleListener(onCameraIdleListener);
    }

    private void configureCameraIdle() {
        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                latLng = mMap.getCameraPosition().target;
                Geocoder geocoder=new Geocoder(getApplicationContext());
                try {
                    addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                    if (addressList != null && addressList.size() > 0) {
                        //String locality = addressList.get(0).getAddressLine(0);
                        //String country = addressList.get(0).getCountryName();
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(addressList.get(0).getAddressLine(0)));
                        tv_alamat.setText(addressList.get(0).getAddressLine(0));
                        lat = latLng.latitude;
                        lng = latLng.longitude;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    private void simpanKoordinat(){
        Server a = new Server(kdkota);
        String url_insert_koordinat = a.URL() + "customer/insert_koordinat.php";

        StringRequest strReq = new StringRequest(Request.Method.POST, url_insert_koordinat, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.d(getLocalClassName(), "Response : " + response);

                try{
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1){
                        Intent intent = new Intent(RealokasiActivity.this, CustomerDetailActivity.class);
                        intent.putExtra("lat", String.valueOf(lat));
                        intent.putExtra("lng", String.valueOf(lng));
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                        //new DialogAlert(jObj.getString("message"), "success", RealokasiActivity.this);
                    } else {
                        new DialogAlert(jObj.getString("message"), "error", RealokasiActivity.this);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", RealokasiActivity.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("kdcust", kdcust);
                params.put("latlng", lat+", "+lng);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
}
