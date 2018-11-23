package com.yusuffahrudin.masuyamobileapp.sales_order;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
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
import com.yusuffahrudin.masuyamobileapp.adapter.AdapterLVSOBarang;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.data.Data;
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

public class SOListBarangActivity extends AppCompatActivity implements View.OnClickListener {

    private AdapterLVSOBarang adapterRV;
    ListView lv_barang;
    List<Data> listBarang = new ArrayList<>();
    SessionManager sessionManager;
    Map<String, Integer> mapIndex;
    private static String name, level, kdkota;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String status_pajak, kdcust;

    private static final String TAG = SOListBarangActivity.class.getSimpleName();
    private static String url_select;

    public static final String TAG_KDBRG = "KdBrg";
    public static final String TAG_NMBRG = "NmBrg";
    public static final String TAG_QTY = "Qty";
    public static final String TAG_SATUAN = "Satuan";
    public static final String TAG_HARGA = "Harga";
    public static final String TAG_DISKON1 = "Diskon1";
    public static final String TAG_DISKON2 = "Diskon2";
    public static final String TAG_DISKON3 = "Diskon3";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("List Barang");
        setContentView(R.layout.activity_so_list_barang);
        Toolbar toolbar = findViewById(R.id.toolbar_barang);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        name = user.get(SessionManager.kunci_email);
        level = user.get(SessionManager.level);
        kdkota = user.get(SessionManager.kdkota);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        kdcust = sharedPreferences.getString("kdcust","");
        status_pajak = sharedPreferences.getString("status_pajak","");

        cekParameter();

        //menghubungkan variabel dengan layout view dan java
        lv_barang = findViewById(R.id.lv_barang);

        //untuk mengisi data dari JSON ke Adapter
        adapterRV = new AdapterLVSOBarang(SOListBarangActivity.this, listBarang);
        lv_barang.setAdapter(adapterRV);

        selectBarang();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search, menu);
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

    private void cekParameter(){
        System.out.println("================== status pajak "+status_pajak);
        System.out.println("================== kdcust "+kdcust);
        if (kdcust.equalsIgnoreCase("")){
            Toast.makeText(SOListBarangActivity.this, "Customer tidak boleh kosong", Toast.LENGTH_LONG).show();
        }
        if  (status_pajak.equalsIgnoreCase("")){
            Toast.makeText(SOListBarangActivity.this, "Status Pajak tidak boleh kosong", Toast.LENGTH_LONG).show();
        }
    }

    //fungsi untuk select data dari database
    public void selectBarang() {
        listBarang.clear();
        adapterRV.notifyDataSetChanged();
        Server a = new Server(kdkota);
        url_select = a.URL() + "salesorder/select_barang.php";

        final ProgressDialog progressDialog = ProgressDialog.show(SOListBarangActivity.this, "", "Please Wait...");
        new Thread() {
            public void run() {
                try{
                    sleep(1000);
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
                new DialogAlert(error.getMessage(), "error", SOListBarangActivity.this);
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("kdcust", kdcust);
                params.put("status_pajak", status_pajak);
                params.put("kota", kdkota);

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

                    Data item = new Data();

                    item.setKdbrg(obj.getString(TAG_KDBRG));
                    item.setNmbrg(obj.getString(TAG_NMBRG));
                    item.setQty(obj.getDouble(TAG_QTY));
                    item.setSatuan(obj.getString(TAG_SATUAN));
                    item.setHarga(obj.getDouble(TAG_HARGA));
                    item.setDiskon1(obj.getDouble(TAG_DISKON1));
                    item.setDiskon2(obj.getDouble(TAG_DISKON2));
                    item.setDiskon3(obj.getDouble(TAG_DISKON3));

                    //menambah item ke array
                    listBarang.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //notifikasi adanya perubahan data pada adapter
            adapterRV.notifyDataSetChanged();
            getIndexList(listBarang);

            displayIndex();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getIndexList(List<Data> barang) {
        mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < barang.size(); i++) {
            String brg = barang.get(i).getKdbrg();
            String index = barang.get(i).getKdbrg().substring(0, 1);

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
        lv_barang.setSelection(mapIndex.get(selectedIndex.getText()));
    }
}
