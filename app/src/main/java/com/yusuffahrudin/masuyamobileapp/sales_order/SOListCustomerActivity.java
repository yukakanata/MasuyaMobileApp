package com.yusuffahrudin.masuyamobileapp.sales_order;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
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
import com.yusuffahrudin.masuyamobileapp.adapter.AdapterLVSOCust;
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

public class SOListCustomerActivity extends AppCompatActivity implements View.OnClickListener {

    private AdapterLVSOCust adapterRV;
    ListView lv_customer;
    List<Customer> listCustomer = new ArrayList<>();
    SessionManager sessionManager;
    Map<String, Integer> mapIndex;
    private static String name, level, kdkota;

    private static final String TAG = SOListCustomerActivity.class.getSimpleName();
    private static String url_select;

    public static final String TAG_KDCUST = "KdCust";
    public static final String TAG_NMCUST = "NmCust";
    public static final String TAG_KDKEL = "KdKel";
    public static final String TAG_ALM1 = "Alm1";
    public static final String TAG_ALM2 = "Alm2";
    public static final String TAG_ALM3 = "Alm3";
    public static final String TAG_KDSALES = "KdSales";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("List Customer");
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

        //menghubungkan variabel dengan layout view dan java
        lv_customer = findViewById(R.id.lv_customer);

        //untuk mengisi data dari JSON ke Adapter
        adapterRV = new AdapterLVSOCust(SOListCustomerActivity.this, listCustomer);
        lv_customer.setAdapter(adapterRV);

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
        listCustomer.clear();
        adapterRV.notifyDataSetChanged();
        Server a = new Server(kdkota);
        url_select = a.URL() + "customer/select_customer.php";

        final ProgressDialog progressDialog = ProgressDialog.show(SOListCustomerActivity.this, "", "Please Wait...");
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
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", SOListCustomerActivity.this);
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
                    System.out.println();
                    item.setKdcust(obj.getString(TAG_KDCUST));
                    item.setNmcust(obj.getString(TAG_NMCUST));
                    item.setKdkel(obj.getString(TAG_KDKEL));
                    item.setAlm1(obj.getString(TAG_ALM1));
                    item.setAlm2(obj.getString(TAG_ALM2));
                    item.setAlm3(obj.getString(TAG_ALM3));
                    item.setKdsales(obj.getString(TAG_KDSALES));

                    //menambah item ke array
                    listCustomer.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //notifikasi adanya perubahan data pada adapter
            adapterRV.notifyDataSetChanged();
            getIndexList(listCustomer);

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
