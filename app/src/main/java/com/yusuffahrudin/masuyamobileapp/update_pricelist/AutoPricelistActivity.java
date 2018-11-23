package com.yusuffahrudin.masuyamobileapp.update_pricelist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.data.ArrayTampung;
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
 * Created by yusuf fahrudin on 17-05-2017.
 */

public class AutoPricelistActivity extends AppCompatActivity {

    int success;
    String message, kdkota;
    ListView lv_cust;
    List<Customer> itemList = new ArrayList<>();
    Adapter adapter; String name;
    SessionManager sessionManager;

    private static final String TAG = AutoPricelistActivity.class.getSimpleName();

    private static String url_select;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Auto Update Pricelist");
        setContentView(R.layout.activity_auto_pricelist);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_cust = findViewById(R.id.lv_cust);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        name = user.get(SessionManager.kunci_email);
        kdkota = user.get(SessionManager.kdkota);
        Toast.makeText(AutoPricelistActivity.this, name, Toast.LENGTH_LONG).show();

        adapter = new Adapter(AutoPricelistActivity.this, itemList);
        lv_cust.setAdapter(adapter);

        getData();

        lv_cust.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(AutoPricelistActivity.this, DetailAutoPricelist.class);
                intent.putExtra("kdcust", itemList.get(position).getKdcust());
                startActivity(intent);
                ArrayTampung.getListData().clear();
                Toast.makeText(AutoPricelistActivity.this, itemList.get(position).getKdcust(), Toast.LENGTH_LONG).show();
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

    public void getData() {
        adapter.notifyDataSetChanged();

        Server a = new Server(kdkota);
        url_select = a.URL() + "updateprice/select_customer.php";

        final ProgressDialog progressDialog = ProgressDialog.show(AutoPricelistActivity.this, "", "Please Wait...");
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
                Log.v(TAG, "Response : " + response);
                setRV(response);
                progressDialog.dismiss();

                //notifikasi adanya perubahan data pada adapter
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", AutoPricelistActivity.this);
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("user", name);

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

                    item.setKdcust(obj.getString("KdCust"));
                    item.setNmcust(obj.getString("NmCust"));
                    item.setAlamat(obj.getString("Alm1"));

                    //menambah item ke array
                    itemList.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error SQL "+e.getMessage());
                }
            }

            //notifikasi adanya perubahan data pada adapter
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error SQL "+e.getMessage());
        }
    }

    private class Adapter extends BaseAdapter {

        private List<Customer> lvData;
        private Activity activity;
        LayoutInflater inflater;

        public Adapter(Activity activity, List<Customer> tampung){
            this.activity = activity;
            this.lvData = tampung;
        }

        @Override
        public int getCount() {
            return lvData.size();
        }

        @Override
        public Object getItem(int location) {
            return lvData.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (inflater == null)
                inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null)
                convertView = inflater.inflate(R.layout.lv_add_customer_khusus, null);

            TextView tvKdCust = convertView.findViewById(R.id.tv_kdcust);
            TextView tvNmCust = convertView.findViewById(R.id.tv_nmcust);
            TextView tvAlamat = convertView.findViewById(R.id.tv_alamat);

            Customer data = lvData.get(position);

            tvKdCust.setText(data.getKdcust());
            tvNmCust.setText(data.getNmcust());
            tvAlamat.setText(data.getAlamat());

            return convertView;
        }
    }

    private class Customer {
        private String kdcust, nmcust, alamat;

        public Customer() {}

        public Customer(String kdcust, String nmcust, String alamat) {
            this.kdcust = kdcust;
            this.nmcust = nmcust;
            this.alamat = alamat;
        }

        public String getKdcust() {
            return kdcust;
        }

        public void setKdcust(String kdcust) {
            this.kdcust = kdcust;
        }

        public String getNmcust() {
            return nmcust;
        }

        public void setNmcust(String nmcust) {
            this.nmcust = nmcust;
        }

        public String getAlamat() {
            return alamat;
        }

        public void setAlamat(String alamat) {
            this.alamat = alamat;
        }
    }
}
