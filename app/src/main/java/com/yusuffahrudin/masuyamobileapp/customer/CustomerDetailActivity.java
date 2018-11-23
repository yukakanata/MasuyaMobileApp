package com.yusuffahrudin.masuyamobileapp.customer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.adapter.AdapterExpandListHistoryPenjualan;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.data.ArrayTampung;
import com.yusuffahrudin.masuyamobileapp.data.HistoryPenjualan;
import com.yusuffahrudin.masuyamobileapp.data.User;
import com.yusuffahrudin.masuyamobileapp.sales_order.CreateSalesOrder;
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

public class CustomerDetailActivity extends AppCompatActivity {
    private String kdcust, nmcust, typecust, alm1, alm2, alm3, kota, telp1, telp2, koordinat, sales, kdkota, kdkel;
    private Double saldo;
    private TextView tv_kdcust, tv_nmcust, tv_typecust, tv_alm1, tv_telp1, tv_kota, tv_saldo, tv_sales, tv_telp2, tv_koordinat;
    private Button btn_map, btn_save_location;
    private NumberFormat nf = NumberFormat.getInstance();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private AlertDialog.Builder dialog;
    private View dialogView;
    private LayoutInflater inflater;
    private AdapterDialogSaldo adapter;
    private List<HistoryPenjualan> listData = new ArrayList<>();
    private SessionManager sessionManager;
    private static String url_select;
    private static final String TAG = CustomerDetailActivity.class.getSimpleName();
    private String tag_json_obj = "json_obj_req";
    List<User> listAkses = ArrayTampung.getListAkses();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Info Customer");
        setContentView(R.layout.activity_customer_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setupGoogleAPI();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        kdcust = sharedPreferences.getString("kdcust","");
        nmcust = sharedPreferences.getString("nmcust","");
        typecust = sharedPreferences.getString("typecust","");
        kdkel = sharedPreferences.getString("kdkel","");
        alm1 = sharedPreferences.getString("alm1","");
        alm2 = sharedPreferences.getString("alm2","");
        alm3 = sharedPreferences.getString("alm3","");
        kota = sharedPreferences.getString("kota","");
        telp1 = sharedPreferences.getString("telp1","");
        telp2 = sharedPreferences.getString("telp2","");
        saldo = Double.valueOf(sharedPreferences.getString("saldo",""));
        koordinat = sharedPreferences.getString("koordinat","");
        sales = sharedPreferences.getString("sales","");
        tv_kdcust = findViewById(R.id.tv_kdcust);
        tv_nmcust = findViewById(R.id.tv_nmcust);
        tv_typecust = findViewById(R.id.tv_typecust);
        tv_alm1 = findViewById(R.id.tv_alm1);
        tv_kota = findViewById(R.id.tv_kota);
        tv_telp1 = findViewById(R.id.tv_telp1);
        tv_telp2 = findViewById(R.id.tv_telp2);
        tv_saldo = findViewById(R.id.tv_saldo);
        tv_sales = findViewById(R.id.tv_sales);
        tv_koordinat = findViewById(R.id.tv_koordinat);
        btn_map = findViewById(R.id.btn_map);
        btn_save_location = findViewById(R.id.btn_save_location);

        setLayout();

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+koordinat);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        btn_save_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if  (koordinat == null || koordinat.equals("")){
                    Intent intent = new Intent(getApplicationContext(), SimpanLokasiActivity.class);
                    intent.putExtra("kdcust", tv_kdcust.getText());
                    intent.putExtra("nmcust", tv_nmcust.getText());
                    intent.putExtra("alm", tv_alm1.getText());
                    startActivityForResult(intent, 1);
                } else {
                    dialogPeringatan();
                }
            }
        });

        tv_saldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSaldo();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            System.out.println("Sukses");
            koordinat = data.getStringExtra("lat")+", "+data.getStringExtra("lng");
            System.out.println(koordinat);
            btn_map.setEnabled(true);
            tv_koordinat.setText(koordinat);
        }
    }

    private void setLayout(){
        tv_kdcust.setText(kdcust);
        tv_nmcust.setText(nmcust);
        tv_typecust.setText(typecust);
        tv_alm1.setText(alm1);
        tv_kota.setText(kota);
        tv_telp1.setText(telp1);
        tv_telp2.setText(telp2);
        tv_saldo.setText(nf.format(saldo));
        tv_sales.setText(sales);
        tv_koordinat.setText("Titik Koordinat : "+koordinat);
        if  (koordinat == null || koordinat.equals("")){
            btn_map.setEnabled(false);
        } else {
            btn_map.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create_sales_order) {
            Intent intent = new Intent(getApplicationContext(), CreateSalesOrder.class);
            intent.putExtra("status_order", "Create Order");
            intent.putExtra("nomor_so", "AUTO");
            intent.putExtra("kdcust", kdcust);
            intent.putExtra("nmcust", nmcust);
            intent.putExtra("kdkel", kdkel);
            intent.putExtra("alm1", alm1);
            intent.putExtra("alm2", alm2);
            intent.putExtra("alm3", alm3);
            intent.putExtra("kdsales", sales);
            startActivity(intent);
            //startActivityForResult(intent, 2);
            //Toast.makeText(CustomerDetailActivity.this, "Sedang dalam proses development", Toast.LENGTH_SHORT).show();
        }
        if (id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void dialogPeringatan(){
        dialog = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_yes_or_no, null);
        dialog.setView(dialogView);

        Button btn_yes = dialogView.findViewById(R.id.btn_yes);
        Button btn_no = dialogView.findViewById(R.id.btn_no);
        TextView tv_message = dialogView.findViewById(R.id.message);
        tv_message.setText("Titik koordinat customer sudah ada. Replace titik koordinat?");

        final AlertDialog alert = dialog.create();
        alert.show();

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SimpanLokasiActivity.class);
                intent.putExtra("kdcust", tv_kdcust.getText());
                intent.putExtra("nmcust", tv_nmcust.getText());
                intent.putExtra("alm", tv_alm1.getText());
                startActivity(intent);
                alert.dismiss();
            }
        });

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }

    // untuk menampilkan dialog
    private void DialogSaldo() {
        dialog = new AlertDialog.Builder(this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_hispenj_item, null);
        dialog.setView(dialogView);
        dialog.setTitle("Saldo Piutang");

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        kdkota = user.get(SessionManager.kdkota);

        RecyclerView rv_hispenj_item = dialogView.findViewById(R.id.rv_hispenj_item);
        rv_hispenj_item.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv_hispenj_item.setLayoutManager(layoutManager);
        adapter = new AdapterDialogSaldo(listData);
        selectSaldo();
        rv_hispenj_item.setAdapter(adapter);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //fungsi untuk select data dari database
    public void selectSaldo() {
        listData.clear();
        adapter.notifyDataSetChanged();

        Server a = new Server(kdkota);
        url_select = a.URL() + "customer/select_hutang_customer.php";

        final ProgressDialog progressDialog = ProgressDialog.show(CustomerDetailActivity.this, "", "Please Wait...");
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
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error Volley : "+ error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
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

                    item.setNofaktur(obj.getString("NoBukti"));
                    item.setHarga(obj.getDouble("Total"));

                    String dateStr = obj.getString("Tgl");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = null;
                    try {
                        date = sdf.parse(dateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    sdf = new SimpleDateFormat("dd-MM-yyyy");
                    String tgl = sdf.format(date);
                    item.setTgl(tgl);

                    //menambah item ke array
                    listData.add(item);
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

    public class AdapterDialogSaldo extends RecyclerView.Adapter <AdapterDialogSaldo.ViewHolder> {

        private List<HistoryPenjualan> rvData;
        View view;

        public AdapterDialogSaldo (List<HistoryPenjualan> tampung){
            this.rvData = tampung;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //membuat view baru
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_hutang_customer, parent, false);
            //mengeset ukuran view, margin, padding, dan parameter layout
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //mengambil elemen dari arraylist pada posisi yang ditentukan
            //dan memasukkannya sebagai isi dari view recyclerview
            for (int i=0; i<listAkses.size(); i++){
                String str = listAkses.get(i).getModul();
                String akses = str.substring(str.indexOf("-") + 1);
                String modul = str.substring(0 , str.indexOf("-")+1);

                if (akses.equalsIgnoreCase("Harga Jual")) {
                    if (listAkses.get(i).isAkses()) {
                        holder.tv_total.setVisibility(View.VISIBLE);
                    } else {
                        holder.tv_total.setVisibility(View.GONE);
                    }
                }
            }

            holder.tv_nobukti.setText(rvData.get(position).getNofaktur());
            holder.tv_tgl.setText(rvData.get(position).getTgl());
            holder.tv_total.setText(nf.format(rvData.get(position).getHarga()));

        }

        @Override
        public int getItemCount() {
            //mengembalikan jumlah data yang ada pada list recyclerview
            return rvData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tv_nobukti;
            public TextView tv_tgl;
            public TextView tv_total;

            public ViewHolder(View view) {
                super(view);
                tv_nobukti = view.findViewById(R.id.tv_nobukti);
                tv_tgl = view.findViewById(R.id.tv_tgl);
                tv_total = view.findViewById(R.id.tv_total);
            }
        }
    }

}
