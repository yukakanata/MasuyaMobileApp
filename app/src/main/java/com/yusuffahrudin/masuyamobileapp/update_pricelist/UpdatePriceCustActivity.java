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
import com.yusuffahrudin.masuyamobileapp.adapter.AdapterLVUpdatePriceCust;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.data.ArrayTampung;
import com.yusuffahrudin.masuyamobileapp.data.HistoryPenjualan;
import com.yusuffahrudin.masuyamobileapp.data.User;
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

public class UpdatePriceCustActivity extends AppCompatActivity {
    private ListView listView;
    private AdapterLVUpdatePriceCust adapter;
    List<HistoryPenjualan> listData = new ArrayList<>();
    int success;
    public String kdbrg, kdcust;
    String message, name, level, kdkota;
    boolean isadd = false, isedit = false, isdelete = false;
    FloatingActionButton fab;
    AlertDialog.Builder dialog;
    SessionManager sessionManager;
    List<User> listAkses = ArrayTampung.getListAkses();

    private static final String TAG = UpdatePriceCustActivity.class.getSimpleName();

    private static String url_select;
    private static String url_delete;
    //private static String url_edit = Server.URL + "updateprice/customer/edit_pricelist_produk_cust.php";

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
        Intent i = getIntent();
        kdcust = i.getExtras().getString("kdcust");
        this.setTitle(kdcust);
        setContentView(R.layout.activity_update_price_cust);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        name = user.get(SessionManager.kunci_email);
        level = user.get(SessionManager.level);
        kdkota = user.get(SessionManager.kdkota);

        //menghubungkan variabel dengan layout view dan java
        fab = findViewById(R.id.fab_add);
        listView = findViewById(R.id.lv_update_price_cust);

        cekAkses();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (isadd == true){
                Intent intent = new Intent(UpdatePriceCustActivity.this, CreatePriceCust.class);
                intent.putExtra("kdcust", kdcust);
                intent.putExtra("kdbrg", "");
                intent.putExtra("satuan", "");
                intent.putExtra("hrg", "0");
                intent.putExtra("hrgincppn", "0");
                intent.putExtra("diskon1", "0");
                intent.putExtra("diskon2", "0");
                intent.putExtra("diskon3", "0");
                startActivity(intent);
                finish();
            } else {
                new DialogAlert("anda tidak mempunyai hak akses", "error", UpdatePriceCustActivity.this);
            }

            }
        });

        //swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        //untuk mengisi data dari JSON ke Adapter
        adapter = new AdapterLVUpdatePriceCust(UpdatePriceCustActivity.this, listData);
        listView.setAdapter(adapter);

        selectPricelistCust();

        // listview ditekan lama akan menampilkan dua pilihan edit atau delete data
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view,
                                           final int position, long id) {
                // TODO Auto-generated method stub
                final String kdbrgx = listData.get(position).getKdbrg();
                final String nmbrgx = listData.get(position).getNmbrg();
                final String satuanx = listData.get(position).getSatuan();
                final Double hrgx = listData.get(position).getHarga();
                final Double hrgincppnx = listData.get(position).getHargaincppn();
                final Double diskon1x = listData.get(position).getDiskon1();
                final Double diskon2x = listData.get(position).getDiskon2();
                final Double diskon3x = listData.get(position).getDiskon3();

                final CharSequence[] dialogitem = {"Edit", "Delete"};
                dialog = new AlertDialog.Builder(UpdatePriceCustActivity.this);
                dialog.setCancelable(true);
                dialog.setItems(dialogitem, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        switch (which) {
                            case 0:
                                if(isedit == true){
                                    edit(kdbrgx,nmbrgx,satuanx,hrgx,hrgincppnx,diskon1x,diskon2x,diskon3x);
                                } else {
                                    new DialogAlert("anda tidak mempunyai hak akses", "error", UpdatePriceCustActivity.this);
                                }
                                break;
                            case 1:
                                if(isdelete == true){
                                    Delete(kdbrgx);
                                } else {
                                    new DialogAlert("anda tidak mempunyai hak akses", "error", UpdatePriceCustActivity.this);
                                }
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
    public void selectPricelistCust() {
        listData.clear();
        adapter.notifyDataSetChanged();
        Server a = new Server(kdkota);
        url_select = a.URL() + "updateprice/customer/select_pricelist_cust.php";

        final ProgressDialog progressDialog = ProgressDialog.show(UpdatePriceCustActivity.this, "", "Please Wait...");
        new Thread() {
            public void run() {
                try{
                    sleep(10000);
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
            }
        }.start();
        //swipeRefreshLayout.setRefreshing(true);

        StringRequest strReq = new StringRequest(Request.Method.POST, url_select, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response);
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
                new DialogAlert(error.getMessage(), "error", UpdatePriceCustActivity.this);
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

                    //DecimalFormat df = new DecimalFormat("#,###.##");
                    NumberFormat nf = NumberFormat.getInstance();
                    String harga = nf.format(obj.getDouble(TAG_HARGA));
                    String diskon1 = nf.format(obj.getDouble(TAG_DISKON1));
                    String diskon2 = nf.format(obj.getDouble(TAG_DISKON2));
                    String diskon3 = nf.format(obj.getDouble(TAG_DISKON3));
                    String hrgincppn = nf.format(obj.getDouble(TAG_HRGINCPPN));

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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Delete(final String kdbrgx){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(UpdatePriceCustActivity.this);
        builder1.setTitle("Delete");
        builder1.setMessage("Yakin untuk menghapus "+kdbrgx+" ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        delete(kdbrgx);
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
    private void delete(final String kdbrgx){
        Server a = new Server(kdkota);
        url_delete = a.URL() + "updateprice/customer/delete_pricelist_produk_cust.php";
        StringRequest strReq = new StringRequest(Request.Method.POST, url_delete, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("delete", jObj.toString());

                        selectPricelistCust();
                        new DialogAlert(jObj.getString(TAG_MESSAGE), "success", UpdatePriceCustActivity.this);
                        adapter.notifyDataSetChanged();

                    } else {
                        new DialogAlert(jObj.getString(TAG_MESSAGE), "error", UpdatePriceCustActivity.this);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", UpdatePriceCustActivity.this);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("kdcust", kdcust);
                params.put("kdbrg", kdbrgx);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    // fungsi untuk get edit data
    private void edit(final String kdbrgx,final String nmbrgx,final String satuanx,final Double hrgx,final Double hrgincppnx,final Double diskon1x,final Double diskon2x,final Double diskon3x){
        Intent intent = new Intent(UpdatePriceCustActivity.this, CreatePriceCust.class);
        intent.putExtra("kdcust", kdcust);
        intent.putExtra("kdbrg", kdbrgx);
        intent.putExtra("nmbrg", nmbrgx);
        intent.putExtra("satuan", satuanx);
        intent.putExtra("hrg", hrgx);
        intent.putExtra("hrgincppn", hrgincppnx);
        intent.putExtra("diskon1", diskon1x);
        intent.putExtra("diskon2", diskon2x);
        intent.putExtra("diskon3", diskon3x);
        startActivity(intent);
        finish();
    }

    private void cekAkses(){
        for (int i=0; i<listAkses.size(); i++){
            String str = listAkses.get(i).getModul();
            String modul = str.substring(str.indexOf("-") + 1);

            if (modul.equalsIgnoreCase("Customer")){
                if  (listAkses.get(i).isAdd()){
                    isadd = true;
                }
                if  (listAkses.get(i).isEdit()){
                    isedit = true;
                }
                if  (listAkses.get(i).isDelete()){
                    isdelete = true;
                }
            }
        }
    }
}
