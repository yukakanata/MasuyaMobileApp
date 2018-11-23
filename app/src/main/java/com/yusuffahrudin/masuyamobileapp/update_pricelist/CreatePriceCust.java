package com.yusuffahrudin.masuyamobileapp.update_pricelist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.controller.NumberTextWatcher;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yusuf fahrudin on 17-05-2017.
 */

public class CreatePriceCust extends AppCompatActivity {

    int success;
    public String kdbrg, kdcustx, kdbrgx, nmbrgx, satuanx, hrgx, hrgincppnx, diskon1x, diskon2x, diskon3x;
    private static Double hpp = 0.0, harga, hargaincppn, disc1, disc2, disc3;
    String message, kdkota;
    List<String> listSatuan = new ArrayList<>();
    ArrayAdapter<String> adapter;
    Spinner spin_satuan;
    EditText edt_kdbrg, edt_nmbrg, edt_hrg, edt_hrg_inc_ppn, edt_diskon1, edt_diskon2, edt_diskon3;
    Button btn_simpan, btn_search;
    SessionManager sessionManager;

    private static final String TAG = CreatePriceCust.class.getSimpleName();

    private static String url_select;
    private static String url_insert;
    private static String url_update;
    private static String url_cek_hpp;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_price_cust);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        kdkota = user.get(SessionManager.kdkota);
        Intent i = getIntent();
        kdcustx = i.getExtras().getString("kdcust");
        kdbrgx = i.getExtras().getString("kdbrg");
        nmbrgx = i.getExtras().getString("nmbrg");
        satuanx = i.getExtras().getString("satuan");
        this.setTitle(kdcustx);

        NumberFormat nf = NumberFormat.getInstance();
        hrgx = nf.format(i.getExtras().getDouble("hrg"));
        hrgincppnx = nf.format(i.getExtras().getDouble("hrgincppn"));
        diskon1x = nf.format(i.getExtras().getDouble("diskon1"));
        diskon2x = nf.format(i.getExtras().getDouble("diskon2"));
        diskon3x = nf.format(i.getExtras().getDouble("diskon3"));

        spin_satuan = findViewById(R.id.spin_satuan);
        edt_kdbrg = findViewById(R.id.edt_kdbrg);
        edt_nmbrg = findViewById(R.id.edt_nmbrg);
        edt_hrg = findViewById(R.id.edt_harga);
        edt_hrg_inc_ppn = findViewById(R.id.edt_hrg_inc_ppn);
        edt_diskon1 = findViewById(R.id.edt_diskon1);
        edt_diskon2 = findViewById(R.id.edt_diskon2);
        edt_diskon3 = findViewById(R.id.edt_diskon3);
        btn_simpan = findViewById(R.id.btn_simpan);
        btn_search = findViewById(R.id.btn_search);

        edt_hrg.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // you can call or do what you want with your EditText here
                Number harga = 0;
                try {
                    harga = NumberFormat.getInstance().parse(edt_hrg.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Double hrg = Double.valueOf(harga.toString())*1.1;
                String hargaincppn = NumberFormat.getInstance().format(hrg);
                edt_hrg_inc_ppn.setText(hargaincppn);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        edt_hrg.addTextChangedListener(new NumberTextWatcher(edt_hrg));
        edt_hrg_inc_ppn.addTextChangedListener(new NumberTextWatcher(edt_hrg_inc_ppn));
        edt_diskon1.addTextChangedListener(new NumberTextWatcher(edt_diskon1));
        edt_diskon2.addTextChangedListener(new NumberTextWatcher(edt_diskon2));
        edt_diskon3.addTextChangedListener(new NumberTextWatcher(edt_diskon3));

        //mengisi spinner kota
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listSatuan);
        spin_satuan.setAdapter(adapter);

        isi();

        edt_kdbrg.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return actionId == EditorInfo.IME_ACTION_NEXT;
            }
        });

        edt_diskon3.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btn_simpan.performClick();
                    return true;
                }
                return false;
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kdbrg = edt_kdbrg.getText().toString().toUpperCase();
                select();
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kdbrg = edt_kdbrg.getText().toString().toUpperCase();
                cekHPP();
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

    private void isi() {
        edt_kdbrg.setText(kdbrgx);
        edt_nmbrg.setText(nmbrgx);
        listSatuan.clear();
        listSatuan.add(satuanx);
        adapter.notifyDataSetChanged();
        edt_hrg.setText(hrgx);
        edt_hrg_inc_ppn.setText(hrgincppnx);
        edt_diskon1.setText(diskon1x);
        edt_diskon2.setText(diskon2x);
        edt_diskon3.setText(diskon3x);
    }

    //fungsi untuk select data dari database
    public void select() {
        listSatuan.clear();
        adapter.notifyDataSetChanged();

        Server a = new Server(kdkota);
        url_select = a.URL() + "updateprice/select_satuan.php";

        StringRequest strReq = new StringRequest(Request.Method.POST, url_select, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1){
                        setRV(jObj);
                        //notifikasi adanya perubahan data pada adapter
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CreatePriceCust.this, "Kode Barang tidak ada!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", CreatePriceCust.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("kdbrg", kdbrg);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }

    //fungsi untuk memasukkan data dari database ke dalam arraylist
    private void setRV(JSONObject jsonObject){
        try {
            JSONArray result = jsonObject.getJSONArray("result");
            for (int i = 0; i < result.length(); i++) {
                try {
                    JSONObject obj = result.getJSONObject(i);

                    String harga = NumberFormat.getInstance().format(obj.getDouble("HrgJualMinExcPPN"));
                    String hargaincppn = NumberFormat.getInstance().format(obj.getDouble("HrgJualMinIncPPN"));

                    edt_hrg.setText(harga);
                    edt_nmbrg.setText(obj.getString("NmBrg"));
                    edt_hrg_inc_ppn.setText(hargaincppn);
                    edt_diskon1.setText("0");
                    edt_diskon2.setText("0");
                    edt_diskon3.setText("0");

                    //menambah item ke array
                    listSatuan.add(obj.getString("Satuan"));
                    listSatuan.add(obj.getString("Satuan2"));
                    listSatuan.add(obj.getString("Satuan3"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //notifikasi adanya perubahan data pada adapter
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //fungsi button simpan
    private void simpan(){

        Number hrg = 0,hrgincppn = 0,diskon1 = 0,diskon2 = 0,diskon3 = 0;
        try {
            hrg = NumberFormat.getInstance().parse(edt_hrg.getText().toString());
            hrgincppn = NumberFormat.getInstance().parse(edt_hrg_inc_ppn.getText().toString());
            diskon1 = NumberFormat.getInstance().parse(edt_diskon1.getText().toString());
            diskon2 = NumberFormat.getInstance().parse(edt_diskon2.getText().toString());
            diskon3 = NumberFormat.getInstance().parse(edt_diskon3.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        harga = Double.valueOf(hrg.toString());
        hargaincppn = Double.valueOf(hrgincppn.toString());
        disc1 = Double.valueOf(diskon1.toString());
        disc2 = Double.valueOf(diskon2.toString());
        disc3 = Double.valueOf(diskon3.toString());

        Double hpp50 = (0.5 * hpp) + hpp;

        if (hpp > harga){
            new DialogAlert("Harga dibawah HPP", "error", CreatePriceCust.this);
        } else if (hpp50 < harga){
            new DialogAlert("Harga 50% diatas HPP", "error", CreatePriceCust.this);
        } else if (disc1 >= 100 || disc2 >= 100 || disc3 >= 100) {
            new DialogAlert("Diskon 1,2,3 tidak boleh >= 100%", "error", CreatePriceCust.this);
        } else {
            if (kdbrgx.isEmpty()){
                insert();
            } else {
                update();
            }

            Intent intent = new Intent(CreatePriceCust.this, UpdatePriceCustActivity.class);
            intent.putExtra("kdcust", kdcustx);
            startActivity(intent);
            finish();
        }
    }

    //fungsi untuk select data dari database
    public void insert() {
        Server a = new Server(kdkota);
        url_insert = a.URL() + "updateprice/customer/insert_pricelist_produk_cust.php";

        StringRequest strReq = new StringRequest(Request.Method.POST, url_insert, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : "+ response);

                try{
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    message = jObj.getString(TAG_MESSAGE);

                    //cek error node pada JSON
                    if (success == 1){
                        new DialogAlert(message, "success", CreatePriceCust.this);
                    } else {
                        new DialogAlert(message, "error", CreatePriceCust.this);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
                //notifikasi adanya perubahan data pada adapter
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", CreatePriceCust.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("kdcust", kdcustx);
                params.put("kdbrg", kdbrg);
                params.put("satuan", spin_satuan.getSelectedItem().toString());
                params.put("hrg", harga.toString());
                params.put("hrgincppn", hargaincppn.toString());
                params.put("diskon1", disc1.toString());
                params.put("diskon2", disc2.toString());
                params.put("diskon3", disc3.toString());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }

    //fungsi untuk select data dari database
    public void update() {
        Server a = new Server(kdkota);
        url_update = a.URL() + "updateprice/customer/update_pricelist_produk_cust.php";

        StringRequest strReq = new StringRequest(Request.Method.POST, url_update, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : "+ response);

                try{
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    message = jObj.getString(TAG_MESSAGE);

                    //cek error node pada JSON
                    if (success == 1){
                        new DialogAlert(message, "success", CreatePriceCust.this);
                        //adapter.notifyDataSetChanged();
                    } else {
                        new DialogAlert(message, "error", CreatePriceCust.this);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
                //notifikasi adanya perubahan data pada adapter
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", CreatePriceCust.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("kdcust", kdcustx);
                params.put("kdbrg", edt_kdbrg.getText().toString());
                params.put("satuan", spin_satuan.getSelectedItem().toString());
                params.put("hrg", harga.toString());
                params.put("hrgincppn", hargaincppn.toString());
                params.put("diskon1", disc1.toString());
                params.put("diskon2", disc2.toString());
                params.put("diskon3", disc3.toString());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }

    private void cekHPP() {
        Server a = new Server(kdkota);
        url_cek_hpp = a.URL() + "updateprice/select_hpp.php";
        final StringRequest strReq = new StringRequest(Request.Method.POST, url_cek_hpp, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.v(TAG, "Response : "+ response);

                try{
                    JSONObject jObj = new JSONObject(response);
                    hpp = jObj.getDouble("hpp");

                    simpan();
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", CreatePriceCust.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //Posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("kdbrg", kdbrg);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
}
