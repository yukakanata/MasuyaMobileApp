package com.yusuffahrudin.masuyamobileapp.customer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.adapter.AdapterLVCust;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomerActivity extends AppCompatActivity implements View.OnClickListener {

    private AdapterLVCust adapterRV;
    private ListView lv_customer;
    private List<Customer> listCust = new ArrayList<>();
    private SessionManager sessionManager;
    private Map<String, Integer> mapIndex;
    private static String name, level, kdkota;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String TAG = CustomerActivity.class.getSimpleName();
    private static String url_select;

    public static final String TAG_KDCUST = "KdCust";
    public static final String TAG_NMCUST = "NmCust";
    public static final String TAG_TYPECUST = "TypeCust";
    public static final String TAG_ALM1 = "Alm1";
    public static final String TAG_KOTA = "Kota";
    public static final String TAG_TELP1 = "Telp1";
    public static final String TAG_TELP2 = "Telp2";
    public static final String TAG_SALDO = "Saldo";
    public static final String TAG_KOORDINAT = "Koordinat";
    public static final String TAG_SALES = "NmSales";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("My Customer");
        setContentView(R.layout.activity_customer);
        Toolbar toolbar = findViewById(R.id.toolbar_customer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        name = user.get(SessionManager.kunci_email);
        level = user.get(SessionManager.level);
        kdkota = user.get(SessionManager.kdkota);
        Toast.makeText(CustomerActivity.this, name, Toast.LENGTH_LONG).show();

        //menghubungkan variabel dengan layout view dan java
        lv_customer = findViewById(R.id.lv_customer);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

        //untuk mengisi data dari JSON ke Adapter
        adapterRV = new AdapterLVCust(CustomerActivity.this, listCust);
        lv_customer.setAdapter(adapterRV);

        selectCustomer();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                selectCustomer();
            }
        });
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
        url_select = a.URL() + "customer/select_customer.php";

        final ProgressDialog progressDialog = ProgressDialog.show(CustomerActivity.this, "", "Please Wait...");
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
                // dismiss the progress dialog
                progressDialog.dismiss();

                //notifikasi adanya perubahan data pada adapter
                adapterRV.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error Volley : "+ error.getMessage());
                new DialogAlert(error.getMessage(), "error", CustomerActivity.this);
                // dismiss the progress dialog
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
                    item.setTypecust(obj.getString(TAG_TYPECUST));
                    item.setKdkel(obj.getString("KdKel"));
                    item.setAlm1(obj.getString(TAG_ALM1));
                    item.setAlm2(obj.getString("Alm2"));
                    item.setAlm3(obj.getString("Alm3"));
                    item.setKota(obj.getString(TAG_KOTA));
                    item.setTelp1(obj.getString(TAG_TELP1));
                    item.setTelp2(obj.getString(TAG_TELP2));
                    item.setSaldo(obj.getDouble(TAG_SALDO));
                    item.setKoordinat(obj.getString(TAG_KOORDINAT));
                    item.setSales(obj.getString(TAG_SALES));

                    //menambah item ke array
                    listCust.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //notifikasi adanya perubahan data pada adapter
            adapterRV.notifyDataSetChanged();
            getIndexList(listCust);

            displayIndex();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getIndexList(List<Customer> customer) {
        mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < customer.size(); i++) {
            String cust = customer.get(i).getKdcust();
            String index = customer.get(i).getKdcust().substring(0, 1);

            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
    }

    private void displayIndex() {
        LinearLayout indexLayout = findViewById(R.id.side_index);

        TextView textView;
        List<String> indexList = new ArrayList<String>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(this);
            indexLayout.addView(textView);
        }
    }

    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;
        lv_customer.setSelection(mapIndex.get(selectedIndex.getText()));
    }
}
