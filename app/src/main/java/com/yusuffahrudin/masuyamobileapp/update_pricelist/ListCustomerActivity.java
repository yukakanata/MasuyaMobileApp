package com.yusuffahrudin.masuyamobileapp.update_pricelist;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.adapter.AdapterRVCustUpdatePricelist;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.data.Customer;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yusuf fahrudin on 16-05-2017.
 */

public class ListCustomerActivity extends AppCompatActivity {

    private AdapterRVCustUpdatePricelist adapterRV;
    RecyclerView rv_update_price_cust;
    List<Customer> listCust = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    SessionManager sessionManager;
    private static String name, level, kdkota;

    private static final String TAG = ListCustomerActivity.class.getSimpleName();
    private static String url_select;

    public static final String TAG_KDCUST = "KdCust";
    public static final String TAG_NMCUST = "NmCust";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("List Customer");
        setContentView(R.layout.activity_list_customer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        name = user.get(SessionManager.kunci_email);
        level = user.get(SessionManager.level);
        kdkota = user.get(SessionManager.kdkota);
        Toast.makeText(ListCustomerActivity.this, name, Toast.LENGTH_LONG).show();

        //menghubungkan variabel dengan layout view dan java
        rv_update_price_cust = findViewById(R.id.rv_update_price_cust);
        rv_update_price_cust.setHasFixedSize(true);

        /**
         * Kita menggunakan LinearLayoutManager untuk list standar
         * yang hanya berisi daftar item
         * disusun dari atas ke bawah
         */

        layoutManager = new LinearLayoutManager(this);
        rv_update_price_cust.setLayoutManager(layoutManager);

        //untuk mengisi data dari JSON ke Adapter
        adapterRV = new AdapterRVCustUpdatePricelist(ListCustomerActivity.this, listCust);
        rv_update_price_cust.setAdapter(adapterRV);

        selectCustomer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapterRV.getFilter().filter(newText);
                return true;
            }
        });
    }

    //fungsi untuk select data dari database
    public void selectCustomer() {
        listCust.clear();
        adapterRV.notifyDataSetChanged();
        Server a = new Server(kdkota);
        url_select = a.URL() + "updateprice/customer/select_customer_khusus.php";

        final ProgressDialog progressDialog = ProgressDialog.show(ListCustomerActivity.this, "", "Please Wait...");
        new Thread() {
            public void run() {
                try{
                    sleep(10000);
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
            }
        }.start();

        StringRequest strReq = new StringRequest(Request.Method.POST, url_select, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response);
                setRV(response);
                progressDialog.dismiss();

                //notifikasi adanya perubahan data pada adapter
                adapterRV.notifyDataSetChanged();
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", ListCustomerActivity.this);
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                if(level.equalsIgnoreCase("sales")){
                    params.put("user", name);
                } else {
                    params.put("user", "");
                }

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }

    //fungsi untuk memasukkan data dari database ke dalam arraylist
    private void setRV(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");
            for (int i = 0; i < result.length(); i++) {
                try {
                    JSONObject obj = result.getJSONObject(i);

                    Customer item = new Customer();

                    item.setKdcust(obj.getString(TAG_KDCUST));
                    item.setNmcust(obj.getString(TAG_NMCUST));

                    //menambah item ke array
                    listCust.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //notifikasi adanya perubahan data pada adapter
            adapterRV.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
