package com.yusuffahrudin.masuyamobileapp.sales_order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailItemSalesOrderActivity extends AppCompatActivity {

    private TextView tv_qty_kirim, tv_qty_order, tv_nmbrg, tv_hrgnet, tv_jumlah, tv_kdbrg;
    //private List<SalesOrder> listData = new ArrayList<>();
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    private String user_kota, kdkota, nobukti, nmbrg, kdbrg;
    private static String url_select;
    private static final String TAG = DetailItemSalesOrderActivity.class.getSimpleName();
    private String tag_json_obj = "json_obj_req";
    private NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item_sales_order);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        user_kota = user.get(SessionManager.kota);
        kdkota = user.get(SessionManager.kdkota);

        Intent i = this.getIntent();
        nobukti = i.getExtras().getString("nobukti");
        nmbrg = i.getExtras().getString("nmbrg");
        kdbrg = i.getExtras().getString("kdbrg");
        this.setTitle(nmbrg);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_kdbrg = findViewById(R.id.tv_kdbrg);
        tv_nmbrg = findViewById(R.id.tv_nmbrg);
        tv_qty_order = findViewById(R.id.tv_qty_order);
        tv_qty_kirim = findViewById(R.id.tv_qty_kirim);
        tv_hrgnet = findViewById(R.id.tv_hrgnet);
        tv_jumlah = findViewById(R.id.tv_jumlah);

        selectSalesOrderItem();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    //fungsi untuk select data dari database
    public void selectSalesOrderItem() {
        Server a = new Server(kdkota);
        url_select = a.URL() + "salesorder/select_sales_order_detail_item.php";

        progressDialog = ProgressDialog.show(DetailItemSalesOrderActivity.this, "", "Please Wait...");
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
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", DetailItemSalesOrderActivity.this);
                // dismiss the progress dialog
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("no_order", nobukti);
                params.put("nmbrg", nmbrg);

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

                    tv_kdbrg.setText(kdbrg);
                    tv_nmbrg.setText(obj.getString("NmBrg"));
                    tv_qty_order.setText(nf.format(obj.getDouble("QtyOrder"))+" "+obj.getString("Satuan"));
                    tv_qty_kirim.setText(nf.format(obj.getDouble("QtyKirim"))+" "+obj.getString("Satuan"));
                    tv_hrgnet.setText("Rp "+nf.format(obj.getDouble("HrgNet")));
                    tv_jumlah.setText("Rp "+nf.format(obj.getDouble("Jumlah")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
