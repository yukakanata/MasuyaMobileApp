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
import com.yusuffahrudin.masuyamobileapp.data.ArrayTampung;
import com.yusuffahrudin.masuyamobileapp.data.HistoryPenjualan;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yusuf fahrudin on 17-05-2017.
 */

public class AddPriceCust extends AppCompatActivity {

    private int success;
    public String kdbrg, kdcustx, kdkota;
    private static Double hpp = 0.0;
    private List<String> listSatuan = new ArrayList<>();
    private ArrayList<HistoryPenjualan> listData;
    private ArrayAdapter<String> adapter;
    private Spinner spin_satuan;
    private EditText edt_kdbrg, edt_nmbrg, edt_hrg, edt_hrg_inc_ppn, edt_diskon1, edt_diskon2, edt_diskon3;
    private Button btn_simpan, btn_search;
    private SessionManager sessionManager;

    private static final String TAG = AddPriceCust.class.getSimpleName();
    private static String url_select;
    private static String url_cek_hpp;
    private static final int PICK_CONTACT_REQUEST = 1;
    private String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Input Pricelist Barang");
        Intent i = getIntent();
        kdcustx = i.getExtras().getString("kdcust");
        setContentView(R.layout.create_price_cust);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        kdkota = user.get(SessionManager.kdkota);

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

    //fungsi untuk select data dari database
    public void select() {
        listSatuan.clear();
        adapter.notifyDataSetChanged();

        Server a = new Server(kdkota);
        url_select = a.URL() + "select_satuan.php";

        StringRequest strReq = new StringRequest(Request.Method.POST, url_select, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1){
                        setRV(jObj);
                        //notifikasi adanya perubahan data pada adapter
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AddPriceCust.this, "Kode Barang tidak ada!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", AddPriceCust.this);
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

    //fungsi untuk select data dari database
    public void insert() throws ParseException {

        listData = ArrayTampung.getListData();
        HistoryPenjualan item = new HistoryPenjualan();

        String tgl = "";

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        tgl = sdf.format(new Date());

        Number hrg = NumberFormat.getInstance().parse(edt_hrg.getText().toString());
        Number hrgincppn = NumberFormat.getInstance().parse(edt_hrg_inc_ppn.getText().toString());
        Number diskon1 = NumberFormat.getInstance().parse(edt_diskon1.getText().toString());
        Number diskon2 = NumberFormat.getInstance().parse(edt_diskon2.getText().toString());
        Number diskon3 = NumberFormat.getInstance().parse(edt_diskon3.getText().toString());

        Double hpp50 = (0.5 * hpp) + hpp;

        if (hpp > Double.valueOf(hrg.toString())){
            Toast.makeText(AddPriceCust.this, "Error! Harga dibawah HPP", Toast.LENGTH_LONG).show();
        } else if (hpp50 < Double.valueOf(hrg.toString())){
            Toast.makeText(AddPriceCust.this, "Error! Harga 50% diatas HPP", Toast.LENGTH_LONG).show();
        } else if (Double.valueOf(diskon1.toString()) >= 100 || Double.valueOf(diskon2.toString()) >= 100 ||Double.valueOf(diskon3.toString()) >= 100) {
            Toast.makeText(AddPriceCust.this, "Error! Diskon 1,2,3 tidak boleh >= 100%", Toast.LENGTH_LONG).show();
        } else {
            item.setHarga(Double.valueOf(hrg.toString()));
            item.setHargaincppn(Double.valueOf(hrgincppn.toString()));
            item.setDiskon1(Double.valueOf(diskon1.toString()));
            item.setDiskon2(Double.valueOf(diskon2.toString()));
            item.setDiskon3(Double.valueOf(diskon3.toString()));
            item.setKdbrg(kdbrg);
            item.setNmbrg(edt_nmbrg.getText().toString());
            item.setTgl(tgl);
            item.setSatuan(spin_satuan.getSelectedItem().toString());

            //menambah item ke array
            listData.add(item);
            new DialogAlert("Data berhasil ditambahkan", "success", AddPriceCust.this);
            Intent intent = new Intent(AddPriceCust.this, DetailAutoPricelist.class);
            intent.putExtra("kdcust", kdcustx);
            startActivityForResult(intent, PICK_CONTACT_REQUEST);
            finish();
        }
    }

    private void cekHPP() {
        Server a = new Server(kdkota);
        url_cek_hpp = a.URL() + "select_hpp.php";
        final StringRequest strReq = new StringRequest(Request.Method.POST, url_cek_hpp, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.v(TAG, "Response : "+ response);

                try{
                    JSONObject jObj = new JSONObject(response);
                    hpp = jObj.getDouble("hpp");

                    insert();
                } catch (JSONException e){
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", AddPriceCust.this);
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
