package com.yusuffahrudin.masuyamobileapp.update_pricelist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.adapter.AdapterLVAutoPricelist;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.data.ArrayTampung;
import com.yusuffahrudin.masuyamobileapp.data.HistoryPenjualan;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yusuf fahrudin on 17-05-2017.
 */

public class DetailAutoPricelist extends AppCompatActivity {
    private ListView listView;
    private AdapterLVAutoPricelist adapter;
    ArrayList<HistoryPenjualan> listData = ArrayTampung.getListData();
    int success;
    public String kdbrg, kdcust;
    String message, kdkota;
    AlertDialog.Builder dialog;
    SessionManager sessionManager;

    private static final String TAG = DetailAutoPricelist.class.getSimpleName();

    private static String url_select;
    private static String url_insert;

    public static final String TAG_TGL = "Tgl";
    public static final String TAG_KDBRG = "KdBrg";
    public static final String TAG_NMBRG = "NmBrg";
    public static final String TAG_SATUAN = "Satuan";
    public static final String TAG_HARGA = "Hrg";
    public static final String TAG_HRGINCPPN = "HrgIncPpn";
    public static final String TAG_DISKON1 = "PrsDisc1";
    public static final String TAG_DISKON2 = "PrsDisc2";
    public static final String TAG_DISKON3 = "PrsDisc3";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Detail AutoUpdate Pricelist");

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        kdkota = user.get(SessionManager.kdkota);
        Intent i = getIntent();
        kdcust = i.getExtras().getString("kdcust");
        this.setTitle(kdcust);
        setContentView(R.layout.activity_auto_pricelist_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab_add = findViewById(R.id.fab_add);
        FloatingActionButton fab_save = findViewById(R.id.fab_save);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailAutoPricelist.this, AddPriceCust.class);
                intent.putExtra("kdcust", kdcust);
                startActivity(intent);
                finish();
            }
        });

        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpan();
                finish();
            }
        });

        //menghubungkan variabel dengan layout view dan java
        listView = findViewById(R.id.lv_update_price_cust);

        //untuk mengisi data dari JSON ke Adapter
        adapter = new AdapterLVAutoPricelist(DetailAutoPricelist.this, listData);
        listView.setAdapter(adapter);

        if (listData.isEmpty()) {
            select();
        }

        // listview ditekan lama akan menampilkan dua pilihan edit atau delete data
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view,
                                           final int position, long id) {
                // TODO Auto-generated method stub
                final int posisi = position;

                final CharSequence[] dialogitem = {"Delete"};
                dialog = new AlertDialog.Builder(DetailAutoPricelist.this);
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        switch (which) {
                            case 0:
                                Delete(posisi);
                                break;
                        }
                    }
                }).show();
                return false;
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
        adapter.notifyDataSetChanged();
        Server a = new Server(kdkota);
        url_select = a.URL() + "updateprice/historypenj/select_auto_pricelist.php";

        final ProgressDialog progressDialog = ProgressDialog.show(DetailAutoPricelist.this, "", "Please Wait...");
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
                Log.e(TAG, "Error Volley : "+ error.getMessage());
                //swipeRefreshLayout.setRefreshing(false);
                new DialogAlert(error.getMessage(), "error", DetailAutoPricelist.this);
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("kdcust", kdcust);

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

                    HistoryPenjualan item = new HistoryPenjualan();

                    String tgl = "";

                    if (!obj.getString(TAG_TGL).equals("null")){
                        String dateStr = obj.getString(TAG_TGL);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = sdf.parse(dateStr);

                        sdf = new SimpleDateFormat("dd-MM-yyyy");
                        tgl = sdf.format(date);
                    }

                    item.setKdbrg(obj.getString(TAG_KDBRG));
                    item.setNmbrg(obj.getString(TAG_NMBRG));
                    item.setTgl(tgl);
                    item.setSatuan(obj.getString(TAG_SATUAN));
                    item.setHarga(obj.getDouble(TAG_HARGA));
                    item.setHargaincppn(obj.getDouble(TAG_HRGINCPPN));
                    item.setDiskon1(obj.getDouble(TAG_DISKON1));
                    item.setDiskon2(obj.getDouble(TAG_DISKON2));
                    item.setDiskon3(obj.getDouble(TAG_DISKON3));

                    //menambah item ke array
                    listData.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            //notifikasi adanya perubahan data pada adapter
            adapter.notifyDataSetChanged();

            //swipeRefreshLayout.setRefreshing(false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Delete(final int posisi){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(DetailAutoPricelist.this);
        builder1.setTitle("Delete");
        builder1.setMessage("Yakin untuk menghapus ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        delete(posisi);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    // fungsi untuk menghapus
    private void delete(final int posisi){
        listData.remove(posisi);
        adapter.notifyDataSetChanged();
    }

    private void simpan(){
        Server a = new Server(kdkota);
        url_insert = a.URL() + "updateprice/historypenj/insert_pricelist_auto.php";

        StringRequest strReq = new StringRequest(Request.Method.POST, url_insert, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.v(TAG, "Response : "+ response);

                try{
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    message = jObj.getString(TAG_MESSAGE);

                    //cek error node pada JSON
                    if (success == 1){
                        new DialogAlert(message, "success", DetailAutoPricelist.this);
                        listData.clear();

                        //adapter.notifyDataSetChanged();
                    } else {
                        new DialogAlert(message, "error", DetailAutoPricelist.this);
                        listData.clear();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", DetailAutoPricelist.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> param = new HashMap<String, String>();
                JSONArray jParams = new JSONArray();
                JSONObject array = null;
                try {
                    for (int i = 0; i < listData.size(); i++){
                        Double harga = listData.get(i).getHarga();
                        Double hrgincppn = listData.get(i).getHargaincppn();
                        Double diskon1 = listData.get(i).getDiskon1();
                        Double diskon2 = listData.get(i).getDiskon2();
                        Double diskon3 = listData.get(i).getDiskon3();

                        array = new JSONObject();

                        array.put("kdcust", kdcust);
                        array.put("kdbrg", listData.get(i).getKdbrg());
                        array.put("satuan", listData.get(i).getSatuan());
                        array.put("hrg", harga);
                        array.put("hrgincppn", hrgincppn);
                        array.put("diskon1", diskon1);
                        array.put("diskon2", diskon2);
                        array.put("diskon3", diskon3);

                        jParams.put(array);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                param.put("array", jParams.toString());
                Log.v("Response : ", param.toString());

                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }
}
