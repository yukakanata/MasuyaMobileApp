package com.yusuffahrudin.masuyamobileapp.history_penjualan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.adapter.AdapterExpandListHistoryPenjualan;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
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
import java.util.List;
import java.util.Map;

public class ListHistoryPenjualanActivity extends AppCompatActivity {

    String nmbrg, customer, sales, from_tgl, to_tgl, nmcust = "", kdkota;
    SessionManager sessionManager;
    AdapterExpandListHistoryPenjualan listAdapter;
    ExpandableListView expListView;
    List<HistoryPenjualan> listData = new ArrayList<>();
    List<String> listDataHeader = new ArrayList<String>();
    HashMap<String, List<HistoryPenjualan>> listDataChild = new HashMap<String, List<HistoryPenjualan>>();

    private static final String TAG = ListHistoryPenjualanActivity.class.getSimpleName();

    private static String url_select;

    public static final String TAG_NMCUST = "NmCust";
    public static final String TAG_TGL = "Tgl";
    public static final String TAG_NOFAKTUR = "NoBukti";
    public static final String TAG_CETAK = "Cetak";
    public static final String TAG_KIRIM = "Kirim";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("List History Penjualan");
        setContentView(R.layout.activity_list_history_penjualan);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        kdkota = user.get(SessionManager.kdkota);

        //get parameter dari intent
        Intent i = getIntent();

        nmbrg = i.getExtras().getString("nmbrg");
        customer = i.getExtras().getString("customer");
        sales = i.getExtras().getString("sales");
        from_tgl = i.getExtras().getString("from_tgl");
        to_tgl = i.getExtras().getString("to_tgl");

        // get the listview
        expListView = findViewById(R.id.lv_exp);

        listAdapter = new AdapterExpandListHistoryPenjualan(ListHistoryPenjualanActivity.this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // preparing list data
        selectHistoryPenjualan();
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
    public void selectHistoryPenjualan() {
        listDataHeader.clear();
        listDataChild.clear();
        listAdapter.notifyDataSetChanged();

        Server a = new Server(kdkota);
        url_select = a.URL() + "historypenj/select_history_penjualan.php";

        final ProgressDialog progressDialog = ProgressDialog.show(ListHistoryPenjualanActivity.this, "", "Please Wait...");
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
                listAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error Volley : "+ error.getMessage());
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    String errorString = new String(response.data);
                    Log.i("log error", errorString);
                }
                new DialogAlert(error.getMessage(), "error", ListHistoryPenjualanActivity.this);
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("namabrg", nmbrg);
                params.put("customer", customer);
                params.put("sales", sales);
                params.put("from_tgl", from_tgl);
                params.put("to_tgl", to_tgl);

                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }

    //fungsi untuk memasukkan data dari database ke dalam arraylist
    private void setRV(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");
            int headercount = 0;
            for (int i = 0; i < result.length(); i++) {
                try {
                    JSONObject obj = result.getJSONObject(i);

                    if  (!obj.getString(TAG_NMCUST).equals(nmcust)) {
                        listDataHeader.add(obj.getString(TAG_NMCUST));
                        nmcust = obj.getString(TAG_NMCUST);
                        listData = new ArrayList<>();

                        for (int j = 0; j < result.length(); j++){
                            JSONObject jobj = result.getJSONObject(j);
                            HistoryPenjualan item = new HistoryPenjualan();

                            if (jobj.getString(TAG_NMCUST).equals(nmcust)){

                                String dateStr = jobj.getString(TAG_TGL);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = sdf.parse(dateStr);

                                sdf = new SimpleDateFormat("dd-MM-yyyy");
                                String tgl = sdf.format(date);

                                item.setNofaktur(jobj.getString(TAG_NOFAKTUR));
                                item.setTgl(tgl);
                                item.setCetak(jobj.getString(TAG_CETAK));
                                item.setKirim(jobj.getString(TAG_KIRIM));
                                item.setPenyiapan(jobj.getString("Penyiapan"));
                                item.setDiterima(jobj.getString("Diterima"));
                                item.setKembali(jobj.getString("Kembali"));
                                item.setKdbrg(jobj.getString("KdBrg"));
                                item.setNmbrg(jobj.getString("NmBrg"));
                                item.setQty(jobj.getDouble("Qty"));
                                item.setHarga(jobj.getDouble("Hrg"));
                                item.setDiskon1(jobj.getDouble("PrsDisc"));
                                item.setDiskon2(jobj.getDouble("PrsDisc2"));
                                item.setDiskon3(jobj.getDouble("PrsDisc3"));
                                item.setDiscfak(jobj.getDouble("PrsDisc1"));
                                item.setNopo(jobj.getString("NoPO"));

                                //menambah item ke array
                                listData.add(item);
                            }
                        }
                        listDataChild.put(listDataHeader.get(headercount), listData);
                        headercount++;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            //notifikasi adanya perubahan data pada adapter
            listAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
