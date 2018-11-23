package com.yusuffahrudin.masuyamobileapp.history_pembelian;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.adapter.AdapterExpandListHistoryPembelian;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.data.HistoryPembelian;
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

public class ListHistoryPembelianActivity extends AppCompatActivity {

    String nmbrg, supplier, from_tgl, to_tgl, nmsup = "", kdkota;
    SessionManager sessionManager;
    AdapterExpandListHistoryPembelian listAdapter;
    ExpandableListView expListView;
    List<HistoryPembelian> listData = new ArrayList<>();
    List<String> listDataHeader = new ArrayList<String>();
    HashMap<String, List<HistoryPembelian>> listDataChild = new HashMap<String, List<HistoryPembelian>>();

    private static final String TAG = ListHistoryPembelianActivity.class.getSimpleName();

    private static String url_select;

    public static final String TAG_KDBRG = "KdBrg";
    public static final String TAG_NMBRG = "NmBrg";
    public static final String TAG_QTY = "Qty";
    public static final String TAG_HARGA = "Hrg";
    public static final String TAG_NMSUP = "NmSup";
    public static final String TAG_SATUAN = "Satuan";
    public static final String TAG_TGL = "Tgl";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("List History Pembelian");
        setContentView(R.layout.activity_list_pembelian);
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
        supplier = i.getExtras().getString("supplier");
        from_tgl = i.getExtras().getString("from_tgl");
        to_tgl = i.getExtras().getString("to_tgl");

        // get the listview
        expListView = findViewById(R.id.lv_exp);

        listAdapter = new AdapterExpandListHistoryPembelian(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // preparing list data
        selectHistoryPembelian();
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
    public void selectHistoryPembelian() {
        listDataHeader.clear();
        listDataChild.clear();
        listAdapter.notifyDataSetChanged();

        Server a = new Server(kdkota);
        url_select = a.URL() + "historypemb/select_history_pembelian.php";

        final ProgressDialog progressDialog = ProgressDialog.show(ListHistoryPembelianActivity.this, "", "Please Wait...");
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
                new DialogAlert(error.getMessage(), "error", ListHistoryPembelianActivity.this);
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("namabrg", nmbrg);
                params.put("supplier", supplier);
                params.put("from_tgl", from_tgl);
                params.put("to_tgl", to_tgl);

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
            int headercount = 0;
            for (int i = 0; i < result.length(); i++) {
                try {
                    JSONObject obj = result.getJSONObject(i);

                    if  (!obj.getString(TAG_NMSUP).equals(nmsup)) {
                        listDataHeader.add(obj.getString(TAG_NMSUP));
                        nmsup = obj.getString(TAG_NMSUP);
                        listData = new ArrayList<>();

                        for (int j = 0; j < result.length(); j++){
                            JSONObject jobj = result.getJSONObject(j);
                            HistoryPembelian item = new HistoryPembelian();

                            if (jobj.getString(TAG_NMSUP).equals(nmsup)){

                                String dateStr = jobj.getString(TAG_TGL);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = sdf.parse(dateStr);

                                sdf = new SimpleDateFormat("dd-MM-yyyy");
                                String tgl = sdf.format(date);

                                item.setKdbrg(jobj.getString(TAG_KDBRG));
                                item.setNmbrg(jobj.getString(TAG_NMBRG));
                                item.setQty(jobj.getDouble(TAG_QTY));
                                item.setTgl(tgl);
                                item.setSatuan(jobj.getString(TAG_SATUAN));
                                item.setHarga(jobj.getDouble(TAG_HARGA));

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
