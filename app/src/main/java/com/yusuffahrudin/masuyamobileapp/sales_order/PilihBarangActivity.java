package com.yusuffahrudin.masuyamobileapp.sales_order;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.controller.MyCallBack;
import com.yusuffahrudin.masuyamobileapp.data.Data;
import com.yusuffahrudin.masuyamobileapp.data.SalesOrder;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PilihBarangActivity extends AppCompatActivity implements MyCallBack {
    private SwipeRefreshLayout swipe_refresh;
    private RecyclerView rv_barang;
    private GridLayoutManager layoutManager;
    private ImageView img_cart;
    public NotificationBadge not_count;
    private Toast toast;
    private AdapterBarang adapter;
    private NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
    private SessionManager sessionManager;
    private String kdkota, kdcust, status_pajak, kdgd;
    private List<Data> listBarang = new ArrayList<>();
    private ArrayList<Data> listCart = new ArrayList<>();
    private ArrayList<SalesOrder> listHeader;
    private String[] satuanArray;
    private List<String> listSatuan;
    private ArrayAdapter<String> adapterSatuan;
    private AlertDialog.Builder dialog;
    private View dialogView;
    public int count = 0;
    private String kdbrg, nmbrg, satuan, satuan3, harga, diskon1, diskon2, diskon3;
    private double stok, qty, qtykvs3, m3;
    public static Activity activity;
    private static final String TAG = PilihBarangActivity.class.getSimpleName();
    private static String url_select;
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Pilih Barang");
        setContentView(R.layout.activity_pilih_barang);
        Toolbar toolbar = findViewById(R.id.toolbar_pilih_barang);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessionManager = new SessionManager(this.getApplicationContext());
        HashMap<String, String> cache = sessionManager.getUserDetails();
        kdkota = cache.get(SessionManager.kdkota);
        activity = this;

        Intent i = this.getIntent();
        listHeader = (ArrayList<SalesOrder>) i.getSerializableExtra("listHeader");
        kdcust = listHeader.get(0).getKdcust();
        status_pajak = listHeader.get(0).getKodeTax();
        kdgd = listHeader.get(0).getKdgd();

        cekParameter();

        swipe_refresh = findViewById(R.id.swipe_refresh);
        rv_barang = findViewById(R.id.list_barang);

        layoutManager = new GridLayoutManager(this, 2);
        rv_barang.setHasFixedSize(true);
        rv_barang.setLayoutManager(layoutManager);
        adapter = new AdapterBarang(this, listBarang);
        rv_barang.setAdapter(adapter);

        getData();
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.pilih_barang, menu);
        MenuItem search = menu.findItem(R.id.search);
        MenuItem cart = menu.findItem(R.id.cart);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);

        View actionView = cart.getActionView();
        if (actionView != null){
            img_cart = actionView.findViewById(R.id.img_cart);
            not_count = actionView.findViewById(R.id.not_count);
        }

        img_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Jumlah Number "+count, Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), TroliActivity.class);
                i.putExtra("listHeader",listHeader);
                i.putExtra("listCart",listCart);
                startActivity(i);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void cekParameter(){
        if (kdcust.equalsIgnoreCase("")){
            new DialogAlert("Customer tidak boleh kosong", "attention", PilihBarangActivity.this);
        }
        if  (status_pajak.equalsIgnoreCase("")){
            new DialogAlert("Status Pajak tidak boleh kosong", "attention", PilihBarangActivity.this);
        }
    }

    public void getData() {
        listBarang.clear();
        adapter.notifyDataSetChanged();

        Server a = new Server(kdkota);
        url_select = a.URL() + "salesorder/select_barang.php";

        final ProgressDialog progressDialog = ProgressDialog.show(this, "Pilih Barang", "Please Wait...");
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
                try{
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    //cek error node pada JSON
                    if (success == 1){
                        showData(jObj);
                    } else {
                        new DialogAlert("Error pengambilan data", "error", PilihBarangActivity.this);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
                progressDialog.dismiss();

            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                new DialogAlert(error.getMessage(), "error", PilihBarangActivity.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("kdcust", kdcust);
                params.put("kdgd", kdgd);
                params.put("status_pajak", status_pajak);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showData(JSONObject jsonObject){
        try {
            JSONArray result = jsonObject.getJSONArray("result");
            for (int i=0; i<result.length(); i++){
                JSONObject obj = result.getJSONObject(i);

                Data item = new Data();
                item.setKdbrg(obj.getString("KdBrg"));
                item.setNmbrg(obj.getString("NmBrg"));
                item.setQty(obj.getDouble("Qty"));
                item.setSatuan(obj.getString("Satuan"));
                item.setSatuan3(obj.getString("Satuan3"));
                item.setQtykvs3(obj.getDouble("QtyKvs3"));
                item.setHarga(obj.getDouble("Harga"));
                item.setDiskon1(obj.getDouble("Diskon1"));
                item.setDiskon2(obj.getDouble("Diskon2"));
                item.setDiskon3(obj.getDouble("Diskon3"));
                item.setM3(obj.getDouble("MKubik1"));
                listBarang.add(item);
            }

            adapter.notifyDataSetChanged();
            swipe_refresh.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void UpdateTextView(String str) {

    }

    @Override
    public void UpdateNotification(int i) {
        synchronized(not_count){
            not_count.setNumber(i);
            not_count.notify();
        }
    }

    private class AdapterBarang extends RecyclerView.Adapter<AdapterBarang.ViewHolder> implements Filterable {
        private List<Data> list_tampung;
        private List<Data> lvFilter;
        private Activity activity;
        LayoutInflater inflater;

        public AdapterBarang(Activity activity, List<Data> tampung){
            this.activity = activity;
            this.list_tampung = tampung;
            this.lvFilter = tampung;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //membuat view baru
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_activity_pilihbarang, parent, false);
            //mengeset ukuran view, margin, padding, dan parameter layout
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            //mengambil elemen dari arraylist pada posisi yang ditentukan
            //dan memasukkannya sebagai isi dari view recyclerview

            Server a = new Server("");
            Picasso.get().load(a.URL_IMAGE()+list_tampung.get(position).getKdbrg()+".jpg")
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.drawable.img_not_found)
                    .resize(300, 400)
                    .onlyScaleDown()
                    .centerInside()
                    .into(holder.img_brg);
            holder.tv_stokbrg.setText(nf.format(list_tampung.get(position).getQty()));
            holder.tv_namabrg.setText(list_tampung.get(position).getNmbrg());
            holder.tv_hargabrg.setText(nf.format(list_tampung.get(position).getHarga()));

            holder.btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    kdbrg = list_tampung.get(position).getKdbrg();
                    nmbrg = list_tampung.get(position).getNmbrg();
                    stok = list_tampung.get(position).getQty();
                    satuan = list_tampung.get(position).getSatuan();
                    satuan3 = list_tampung.get(position).getSatuan3();
                    qtykvs3 = list_tampung.get(position).getQtykvs3();
                    harga = list_tampung.get(position).getHarga().toString();
                    diskon1 = list_tampung.get(position).getDiskon1().toString();
                    diskon2 = list_tampung.get(position).getDiskon2().toString();
                    diskon3 = list_tampung.get(position).getDiskon3().toString();
                    m3 = list_tampung.get(position).getM3();

                    listSatuan = new ArrayList<>();
                    listSatuan.add(satuan);
                    listSatuan.add("LOAF");
                    listSatuan.add("EKOR");
                    satuanArray = listSatuan.toArray(new String[listSatuan.size()]);
                    DialogQty();
                }
            });
        }

        @Override
        public int getItemCount() {
            //mengembalikan jumlah data yang ada pada list recyclerview
            return list_tampung.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView img_brg;
            public TextView tv_stokbrg;
            public TextView tv_namabrg;
            public TextView tv_hargabrg;
            public Button btn_add_to_cart;
            public CardView cv_main;

            public ViewHolder(View itemView) {
                super(itemView);
                img_brg = itemView.findViewById(R.id.img_brg);
                tv_stokbrg = itemView.findViewById(R.id.tv_stokbrg);
                tv_namabrg = itemView.findViewById(R.id.tv_namabrg);
                tv_hargabrg = itemView.findViewById(R.id.tv_hargabrg);
                btn_add_to_cart = itemView.findViewById(R.id.btn_add_to_cart);
                cv_main = itemView.findViewById(R.id.cv_main);
            }
        }

        @Override
        public Filter getFilter() {

            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {

                    String charString = charSequence.toString();

                    if (charString.isEmpty()) {

                        list_tampung = lvFilter;
                    } else {

                        ArrayList<Data> filteredList = new ArrayList<>();

                        for (Data item : list_tampung) {

                            if (item.getKdbrg().toLowerCase().contains(charString) || item.getNmbrg().toLowerCase().contains(charString)) {

                                filteredList.add(item);
                            }
                        }

                        list_tampung = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = list_tampung;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    list_tampung = (ArrayList<Data>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

        // untuk menampilkan dialog password
        private void DialogQty() {
            dialog = new AlertDialog.Builder(activity);
            inflater = activity.getLayoutInflater();
            dialogView = inflater.inflate(R.layout.dialog_so_qty_item, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);
            //dialog.setIcon(R.drawable.masuyalogo);
            //dialog.setTitle("Qty Barang");

            final ImageView img_brg = dialogView.findViewById(R.id.img_brg);
            final EditText edt_qty = dialogView.findViewById(R.id.edt_qty);
            final ImageView btn_minus = dialogView.findViewById(R.id.btn_minus);
            final ImageView btn_plus = dialogView.findViewById(R.id.btn_plus);
            final TextView tv_harga = dialogView.findViewById(R.id.tv_harga);
            final TextView tv_diskon1 = dialogView.findViewById(R.id.tv_diskon1);
            final TextView tv_diskon2 = dialogView.findViewById(R.id.tv_diskon2);
            final TextView tv_diskon3 = dialogView.findViewById(R.id.tv_diskon3);
            final Spinner spin_satuan = dialogView.findViewById(R.id.spin_satuan);

            Server a = new Server("");
            Picasso.get().load(a.URL_IMAGE()+kdbrg+".jpg")
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.drawable.img_not_found)
                    .resize(150, 150)
                    .onlyScaleDown()
                    .centerInside()
                    .into(img_brg);
            adapterSatuan = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, satuanArray);
            spin_satuan.setAdapter(adapterSatuan);

            spin_satuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected( AdapterView<?> parent, View view, int position, long id) {
                    if (id > 0){
                        tv_harga.setText("0");
                    } else {
                        tv_harga.setText(nf.format(Float.parseFloat(harga)));
                        tv_diskon1.setText(nf.format(Double.parseDouble(diskon1)));
                        tv_diskon2.setText(nf.format(Double.parseDouble(diskon2)));
                        tv_diskon3.setText(nf.format(Double.parseDouble(diskon3)));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            btn_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Double qty = Double.valueOf(edt_qty.getText().toString());
                    if (qty > 0){
                        edt_qty.setText(String.valueOf(qty-1));
                    }
                }
            });

            btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Double qty = Double.valueOf(edt_qty.getText().toString());
                    edt_qty.setText(String.valueOf(qty+1));
                }
            });

            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    qty = Double.parseDouble(edt_qty.getText().toString());
                    diskon1 = tv_diskon1.getText().toString();
                    diskon2 = tv_diskon2.getText().toString();
                    diskon3 = tv_diskon3.getText().toString();
                    if (qty > stok || stok == 0.0){
                        new DialogAlert("Stok tidak mencukupi..!", "attention", PilihBarangActivity.this);
                    } else if (Double.parseDouble(harga) == 0.0){
                        new DialogAlert("Harga Barang Rp0 \nInput harga barang di modul Update Pricelist", "attention", PilihBarangActivity.this);
                    } else {
                        Data item = new Data();
                        item.setKdbrg(kdbrg);
                        item.setNmbrg(nmbrg);
                        item.setSatuan(satuan);
                        item.setSatuan3(satuan3);
                        item.setQtykvs3(qtykvs3);
                        item.setQty(qty);
                        item.setHarga(Double.valueOf(harga));
                        item.setDiskon1(Double.valueOf(diskon1));
                        item.setDiskon2(Double.valueOf(diskon2));
                        item.setDiskon3(Double.valueOf(diskon3));
                        item.setM3(m3);
                        double hrg = 0.0;
                        hrg = item.getHarga()*(1-item.getDiskon1()/100)*(1-item.getDiskon2()/100)*(1-item.getDiskon3()/100);
                        item.setSubtotal(hrg*qty);
                        listCart.add(item);

                        synchronized(not_count){
                            not_count.setNumber(++count);
                            not_count.notify();
                        }
                        dialog.dismiss();
                    }

                }
            });

            dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }
}
