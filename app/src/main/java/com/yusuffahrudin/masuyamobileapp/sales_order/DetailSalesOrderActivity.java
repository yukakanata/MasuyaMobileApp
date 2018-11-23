package com.yusuffahrudin.masuyamobileapp.sales_order;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.adapter.AdapterExpandListHistoryPenjualan;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.data.Data;
import com.yusuffahrudin.masuyamobileapp.data.SalesOrder;
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
import java.util.Locale;
import java.util.Map;

public class DetailSalesOrderActivity extends AppCompatActivity {

    private TextView tv_customer, tv_tgl_order, tv_tgl_kirim, tv_noPO, tv_cetak_note, tv_keterangan, tv_order_by;
    private TextView tv_subtotal, tv_discfak, tv_ppn, tv_total;
    private TextView tv_status_order, tv_status_kirim;
    private RecyclerView rv_item_order;
    private LinearLayoutManager layoutManager;
    private AdapterRVItem adapterRVItem;
    private List<SalesOrder> listData = new ArrayList<>();
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    private String user_kota, kdkota, nobukti, tgl_create, tgl_kirim;
    private static String url_select;
    private boolean partial = false, open = true;
    private static final String TAG = DetailSalesOrderActivity.class.getSimpleName();
    private String tag_json_obj = "json_obj_req";
    private NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sales_order);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        user_kota = user.get(SessionManager.kota);
        kdkota = user.get(SessionManager.kdkota);

        Intent i = this.getIntent();
        nobukti = i.getExtras().getString("nobukti");
        this.setTitle(nobukti);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_customer = findViewById(R.id.tv_customer);
        tv_tgl_order = findViewById(R.id.tv_tgl_order);
        tv_tgl_kirim = findViewById(R.id.tv_tgl_kirim);
        tv_noPO = findViewById(R.id.tv_noPO);
        tv_cetak_note = findViewById(R.id.tv_cetak_note);
        tv_keterangan = findViewById(R.id.tv_keterangan);
        tv_order_by = findViewById(R.id.tv_order_by);
        tv_subtotal = findViewById(R.id.tv_subtotal);
        tv_discfak = findViewById(R.id.tv_discfak);
        tv_ppn = findViewById(R.id.tv_ppn);
        tv_total = findViewById(R.id.tv_total);
        tv_status_order = findViewById(R.id.tv_status_order);
        tv_status_kirim = findViewById(R.id.tv_status_kirim);
        rv_item_order = findViewById(R.id.rv_item_order);

        rv_item_order.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rv_item_order.setLayoutManager(layoutManager);

        //untuk mengisi data dari JSON ke Adapter
        adapterRVItem = new AdapterRVItem(DetailSalesOrderActivity.this, listData);
        rv_item_order.setAdapter(adapterRVItem);

        selectSalesOrder();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public class AdapterRVItem extends RecyclerView.Adapter <AdapterRVItem.ViewHolder> {

        private List<SalesOrder> rvData;
        private Activity activity;

        public AdapterRVItem (Activity activity, List<SalesOrder> tampung){
            this.activity = activity;
            this.rvData = tampung;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //membuat view baru
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_activity_sales_order, parent, false);
            //mengeset ukuran view, margin, padding, dan parameter layout
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //mengambil elemen dari arraylist pada posisi yang ditentukan
            //dan memasukkannya sebagai isi dari view recyclerview
            final String kdbrg = rvData.get(position).getKdbrg();
            final String nmbrg = rvData.get(position).getNmbrg();

            holder.tvKdBrg.setText(rvData.get(position).getKdbrg());
            holder.tvNmBrg.setText(rvData.get(position).getNmbrg());
            holder.tvQty.setText("Qty Kirim : "+nf.format(rvData.get(position).getQtykirim())+"/"+nf.format(rvData.get(position).getQtyorder()));
            holder.tvHarga.setText("Harga : Rp "+nf.format(rvData.get(position).getHrgnet()));
            holder.tvJumlah.setText("Jumlah : Rp "+nf.format(rvData.get(position).getJumlah()));
            Server a = new Server("");
            Picasso.get().load(a.URL_IMAGE()+rvData.get(position).getKdbrg()+".jpg")
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.drawable.img_not_found)
                    .resize(100, 200)
                    .onlyScaleDown()
                    .centerInside()
                    .into(holder.imgBrg);

            // Set onclicklistener pada view cvMain (CardView)
            holder.cvList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, DetailItemSalesOrderActivity.class);

                    intent.putExtra("kdbrg", kdbrg);
                    intent.putExtra("nmbrg", nmbrg);
                    intent.putExtra("nobukti", nobukti);

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
            public TextView tvHarga;
            public TextView tvJumlah;
            private ImageView imgBrg;
            public CardView cvList;

            public ViewHolder(View itemView) {
                super(itemView);
                imgBrg = itemView.findViewById(R.id.img_brg);
                tvKdBrg = itemView.findViewById(R.id.tv_kdbrg);
                tvNmBrg = itemView.findViewById(R.id.tv_nmbrg);
                tvQty = itemView.findViewById(R.id.tv_qty);
                tvHarga = itemView.findViewById(R.id.tv_harga);
                tvJumlah = itemView.findViewById(R.id.tv_jumlah);
                cvList = itemView.findViewById(R.id.cv_main);
            }
        }
    }

    //fungsi untuk select data dari database
    public void selectSalesOrder() {
        listData.clear();
        adapterRVItem.notifyDataSetChanged();

        Server a = new Server(kdkota);
        url_select = a.URL() + "salesorder/select_sales_order_detail.php";

        progressDialog = ProgressDialog.show(DetailSalesOrderActivity.this, "", "Please Wait...");
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
                adapterRVItem.notifyDataSetChanged();
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", DetailSalesOrderActivity.this);
                // dismiss the progress dialog
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("no_order", nobukti);

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

                    SalesOrder item = new SalesOrder();

                    item.setNobukti(obj.getString("NoBukti"));
                    item.setStatusorder(obj.getString("StatusOrder"));
                    item.setNmcust(obj.getString("NmCust"));
                    item.setTgl_create(obj.getString("TglOrder"));
                    item.setTgl_kirim(obj.getString("TglKirim"));
                    item.setNoPO(obj.getString("NoPO"));
                    item.setKet1(obj.getString("Ket1"));
                    item.setKet2(obj.getString("Ket2"));
                    item.setOrderby(obj.getString("OrderBy"));
                    item.setSubtotal(obj.getDouble("SubTotal"));
                    item.setDisc(obj.getDouble("Discount"));
                    item.setPpn(obj.getDouble("Ppn"));
                    item.setTotal(obj.getDouble("Total"));
                    item.setKdbrg(obj.getString("KdBrg"));
                    item.setNmbrg(obj.getString("NmBrg"));
                    item.setQtykirim(obj.getDouble("QtyKirim"));
                    item.setQtyorder(obj.getDouble("QtyOrder"));
                    item.setHrgnet(obj.getDouble("HrgNet"));
                    item.setJumlah(obj.getDouble("Jumlah"));

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date dateCreate = null;
                    Date dateKirim = null;
                    try {
                        dateCreate = sdf.parse(item.getTgl_create());
                        dateKirim = sdf.parse(item.getTgl_kirim());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    sdf = new SimpleDateFormat("dd-MM-yyyy");
                    tgl_create = sdf.format(dateCreate);
                    tgl_kirim = sdf.format(dateKirim);

                    tv_customer.setText(item.getNmcust());
                    tv_status_order.setText(item.getStatusorder());
                    tv_tgl_order.setText(tgl_create);
                    tv_tgl_kirim.setText(tgl_kirim);
                    tv_noPO.setText(item.getNoPO());
                    tv_cetak_note.setText(item.getKet1());
                    tv_keterangan.setText(item.getKet2());
                    tv_order_by.setText(item.getOrderby());
                    tv_subtotal.setText("Rp "+nf.format(item.getSubtotal()));
                    tv_discfak.setText("Rp "+nf.format(item.getDisc()));
                    tv_ppn.setText("Rp "+nf.format(item.getPpn()));
                    tv_total.setText("Rp "+nf.format(item.getTotal()));
                    if  (item.getQtykirim() < item.getQtyorder()){
                        partial = true;
                    }
                    if ((item.getQtyorder() - item.getQtykirim()) > 0.0){
                        open = false;
                    }

                    //menambah item ke array
                    listData.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (partial){
                tv_status_kirim.setText("Partially shipped");
            } else {
                tv_status_kirim.setText("Fully shipped");
            }
            if (open){
                tv_status_kirim.setText("not yet sent");
            }

            //notifikasi adanya perubahan data pada adapter
            adapterRVItem.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
