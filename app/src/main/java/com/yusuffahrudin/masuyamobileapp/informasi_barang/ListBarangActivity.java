package com.yusuffahrudin.masuyamobileapp.informasi_barang;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.yusuffahrudin.masuyamobileapp.data.Data;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListBarangActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private RecyclerView recyclerView;
    private AdapterRVStok adapterRV;
    private List<Data> listData = new ArrayList<Data>();
    private LinearLayoutManager layoutManager;
    private EditText edt_nmbrg;
    private Spinner spin_kota;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button btn_search;
    private String nmbrg, tanggal, kota, user_kota, kdkota;
    private String[] kotaArray;
    private List<String> listKota;
    private ProgressDialog pDialog;
    private static String url_select_kota;
    private ArrayAdapter<String> adapter;
    private ProgressDialog progressDialog;

    private static final String TAG = ListBarangActivity.class.getSimpleName();
    private static String url_select;
    public static final String TAG_KDBRG = "KdBrg";
    public static final String TAG_NMBRG = "NmBrg";
    public static final String TAG_QTY = "Qty";
    private String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_barang);
        this.setTitle("List Barang");
        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        user_kota = user.get(SessionManager.kota);
        kdkota = user.get(SessionManager.kdkota);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //menghubungkan variabel dengan layout view dan java
        recyclerView = findViewById(R.id.rv_main);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        recyclerView.setHasFixedSize(true);
        edt_nmbrg = findViewById(R.id.edt_nmbrg);
        spin_kota = findViewById(R.id.spin_kota);
        btn_search = findViewById(R.id.btn_search);

        listKota = new ArrayList<String>();

        //mengisi spinner kota
        if (user_kota.equalsIgnoreCase("ALL")){
            new GetKota1().execute();
        } else if(user_kota.equalsIgnoreCase("MLG")){
            kotaArray = new String[]{"MLG","SBY"};
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, kotaArray);
            spin_kota.setAdapter(adapter);
        } else {
            kotaArray = new String[]{user_kota};
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, kotaArray);
            spin_kota.setAdapter(adapter);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        tanggal = sdf.format(new Date());

        /**
         * Kita menggunakan LinearLayoutManager untuk list standar
         * yang hanya berisi daftar item
         * disusun dari atas ke bawah
         */

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //untuk mengisi data dari JSON ke Adapter
        adapterRV = new AdapterRVStok(ListBarangActivity.this, listData);
        recyclerView.setAdapter(adapterRV);

        btn_search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                nmbrg = edt_nmbrg.getText().toString();
                kota = spin_kota.getSelectedItem().toString();
                selectStok();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                nmbrg = edt_nmbrg.getText().toString();
                kota = spin_kota.getSelectedItem().toString();
                selectStok();
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
    public void selectStok() {
        listData.clear();
        adapterRV.notifyDataSetChanged();

        Server a = new Server(kdkota);
        url_select = a.URL() + "masterbrg/select.php";

        progressDialog = ProgressDialog.show(ListBarangActivity.this, "", "Please Wait...");
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
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error Volley : "+ error.getMessage());
                new DialogAlert(error.getMessage(), "error", ListBarangActivity.this);
                // dismiss the progress dialog
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("namabrg", nmbrg);
                params.put("tanggal", tanggal);
                params.put("kota", kota);

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
                    item.setTanggal(tanggal);
                    item.setKota(spin_kota.getSelectedItem().toString());

                    //menambah item ke array
                    listData.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //notifikasi adanya perubahan data pada adapter
            adapterRV.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetKota1 extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ListBarangActivity.this);
            pDialog.setMessage("Fetching kota..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Server a = new Server(kdkota);
            url_select_kota = a.URL() + "tools/select_kota.php";

            StringRequest strReq = new StringRequest(Request.Method.POST, url_select_kota, new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Response : " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray result = jsonObject.getJSONArray("result");
                        listKota.clear();
                        for (int i = 0; i < result.length(); i++) {
                            try {
                                JSONObject obj = result.getJSONObject(i);
                                //menambah item ke array
                                listKota.add(obj.getString("KdKota"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        populateSpinner();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error : "+ error.getMessage());
                }
            });

            AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    private void populateSpinner() {
        // Creating adapter for spinner
        kotaArray = listKota.toArray(new String[listKota.size()]);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, kotaArray);
        spin_kota.setAdapter(adapter);
    }

    public class AdapterRVStok extends RecyclerView.Adapter <AdapterRVStok.ViewHolder> {

        private List<Data> rvData;
        private Activity activity;

        public AdapterRVStok (Activity activity, List<Data> tampung){
            this.activity = activity;
            this.rvData = tampung;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //membuat view baru
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_activity_list_barang, parent, false);
            //mengeset ukuran view, margin, padding, dan parameter layout
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //mengambil elemen dari arraylist pada posisi yang ditentukan
            //dan memasukkannya sebagai isi dari view recyclerview
            final String kdbrg = rvData.get(position).getKdbrg();
            final String tanggal = rvData.get(position).getTanggal();
            final String kota = rvData.get(position).getKota();


            holder.tvKdBrg.setText(rvData.get(position).getKdbrg());
            holder.tvNmBrg.setText(rvData.get(position).getNmbrg());
            holder.tvQty.setText(NumberFormat.getInstance().format(rvData.get(position).getQty()));

            // Set onclicklistener pada view cvMain (CardView)
            holder.cvList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, InfoBarangActivity.class);

                    intent.putExtra("kdbrg", kdbrg);
                    intent.putExtra("tanggal", tanggal);
                    intent.putExtra("kota", kota);

                    activity.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            //mengembalikan jumlah data yang ada pada list recyclerview
            return rvData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tvKdBrg;
            public TextView tvNmBrg;
            public TextView tvQty;
            public CardView cvList;

            public ViewHolder(View itemView) {
                super(itemView);
                tvKdBrg = itemView.findViewById(R.id.tv_kdbrg);
                tvNmBrg = itemView.findViewById(R.id.tv_nmbrg);
                tvQty = itemView.findViewById(R.id.tv_qty);
                cvList = itemView.findViewById(R.id.cv_main);
            }
        }
    }
}
