package com.yusuffahrudin.masuyamobileapp.stock_opname;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.clans.fab.FloatingActionButton;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.data.ArrayTampung;
import com.yusuffahrudin.masuyamobileapp.data.Opname;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemOpnameActivity extends AppCompatActivity {

    private String NoOpname = "", status_opname, status, kota, tgl, user, kdkota, nmbrg;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdapterListItemOpname listAdapter;
    private List<String> listItem = new ArrayList<>();
    private List<String> listNmBrg = new ArrayList<>();
    private List<String> listStatusOpname = new ArrayList<>();
    private List<String> listSatuan = new ArrayList<>();
    private int jumlah_item = 0;
    private SessionManager sessionManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String TAG = ItemOpnameActivity.class.getSimpleName();
    private static int REQUEST_FORM = 1;
    private static int REQUEST_FILTER_ITEM = 2;
    private static String url_select;
    private static String url_filter;
    private String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_opname);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> cache = sessionManager.getUserDetails();
        kdkota = cache.get(SessionManager.kdkota);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        status_opname = sharedPreferences.getString("status_opname","");
        status = sharedPreferences.getString("status","");
        NoOpname = sharedPreferences.getString("no_opname","");
        kota = sharedPreferences.getString("kota","");
        tgl = sharedPreferences.getString("tgl","");
        user = sharedPreferences.getString("user","");
        nmbrg = sharedPreferences.getString("nmbrg","");
        this.setTitle(NoOpname);

        // get the listview
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        listView = findViewById(R.id.lv_item);
        listAdapter = new AdapterListItemOpname(ItemOpnameActivity.this, listItem, listNmBrg, listStatusOpname);

        // setting list adapter
        listView.setAdapter(listAdapter);

        if (status_opname.equalsIgnoreCase("")){
            select_item();
        } else {
            filter();
        }

        FloatingActionButton fab_nomor = findViewById(R.id.fab_sub1);
        Button btn_sort = findViewById(R.id.btn_sort);
        Button btn_filter = findViewById(R.id.btn_filter);

        btn_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = { "A-Z", "Z-A" };
                AlertDialog.Builder builder = new AlertDialog.Builder(ItemOpnameActivity.this);
                builder.setTitle("Sorting");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("A-Z")){
                            Collections.sort(listItem);
                            listAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        } else {
                            Collections.reverse(listItem);
                            listAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("nmbrg", nmbrg);
                editor.putString("status_opname", status_opname);
                editor.commit();

                Intent intent = new Intent(ItemOpnameActivity.this, FilterItemOpname.class);
                startActivityForResult(intent, REQUEST_FILTER_ITEM);
            }
        });

        fab_nomor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jumlah_item > 10){
                    Toast.makeText(ItemOpnameActivity.this, " Tambah Nomor Opname baru...!!! \n Jumlah item opname sudah mencapai 25 item", Toast.LENGTH_LONG).show();
                } else {
                    editor.putString("status", "create");
                    editor.putString("no_opname", NoOpname);
                    editor.putString("kota", kota);
                    editor.putString("tgl", tgl);
                    editor.putString("user", user);
                    editor.commit();

                    Intent intent = new Intent(ItemOpnameActivity.this, FilterFormStockOpname.class);
                    startActivityForResult(intent, REQUEST_FORM);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editor.putString("status", status);
                editor.putString("no_opname", NoOpname);
                editor.putString("kota", kota);
                editor.putString("tgl", tgl);
                editor.putString("user", user);
                editor.putString("kdbrg", listItem.get(position));
                editor.putString("nmbrg", listNmBrg.get(position));
                editor.putString("status_opname", listStatusOpname.get(position));
                editor.commit();

                Intent intent = new Intent(ItemOpnameActivity.this, FormStockOpnameActivity.class);
                startActivityForResult(intent, REQUEST_FORM);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                select_item();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FORM && resultCode == RESULT_OK) {
            select_item();
        } else if(requestCode == REQUEST_FILTER_ITEM && resultCode == RESULT_OK){
            status_opname = data.getStringExtra(FilterItemOpname.STATUS_OPNAME);
            nmbrg = data.getStringExtra(FilterItemOpname.NMBRG);
            filter();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            List<Opname> listData = ArrayTampung.getListOpname();
            listData.clear();
            editor.putString("tgl_awal", "");
            editor.putString("tgl_akhir", "");
            editor.putString("nmbrg", "");
            editor.putString("status_opname", "");
            editor.putString("status", "view");
            editor.commit();
            Intent intent = new Intent(ItemOpnameActivity.this, StockOpnameActivity.class);
            setResult(Activity.RESULT_OK, intent);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    //fungsi untuk select data dari database
    public void select_item() {
        listItem.clear();
        listNmBrg.clear();
        listSatuan.clear();
        listStatusOpname.clear();
        listAdapter.notifyDataSetChanged();
        Server a = new Server(kdkota);
        url_select = a.URL() + "stockopname/view/select_list_item_opname.php";

        final ProgressDialog progressDialog = ProgressDialog.show(ItemOpnameActivity.this, "", "Please Wait...");
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
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("result");
                    for (int i = 0; i < result.length(); i++) {
                        try {
                            JSONObject obj = result.getJSONObject(i);

                            listItem.add(obj.getString("KdBrg"));
                            listNmBrg.add(obj.getString("NmBrg"));
                            listStatusOpname.add(obj.getString("StatusOpname"));
                            listSatuan.add(obj.getString("Satuan"));
                            jumlah_item = jumlah_item + 1;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    progressDialog.dismiss();
                    //notifikasi adanya perubahan data pada adapter
                    listAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", ItemOpnameActivity.this);
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("no_opname", NoOpname);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private class AdapterListItemOpname extends BaseAdapter {

        private List<String> lvItem;
        private List<String> lvNmBrg;
        private List<String> lvStatusOpname;
        private Activity activity;
        LayoutInflater inflater;

        public AdapterListItemOpname(Activity activity, List<String> lvItem, List<String> lvNmBrg, List<String> lvStatusOpname){
            this.activity = activity;
            this.lvItem = lvItem;
            this.lvNmBrg = lvNmBrg;
            this.lvStatusOpname = lvStatusOpname;
        }

        @Override
        public int getCount() {
            return lvItem.size();
        }

        @Override
        public Object getItem(int location) {
            return lvItem.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (inflater == null)
                inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null)
                convertView = inflater.inflate(R.layout.lv_item_opname, null);

            TextView tv_item_opname = convertView.findViewById(R.id.tv_item_opname);
            TextView tv_nmbrg_opname = convertView.findViewById(R.id.tv_nmbrg_opname);
            TextView tv_status_opname = convertView.findViewById(R.id.tv_status_opname);

            String item = lvItem.get(position);
            String nmbrg = lvNmBrg.get(position);
            String status_opname = lvStatusOpname.get(position);

            tv_item_opname.setText(item);
            tv_nmbrg_opname.setText(nmbrg);
            tv_status_opname.setText(status_opname);

            return convertView;
        }
    }

    //fungsi untuk select data dari database
    public void filter() {
        listItem.clear();
        listNmBrg.clear();
        listSatuan.clear();
        listStatusOpname.clear();
        listAdapter.notifyDataSetChanged();
        Server a = new Server(kdkota);
        url_filter = a.URL() + "stockopname/view/filter_list_item.php";

        StringRequest strReq = new StringRequest(Request.Method.POST, url_filter, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.v(TAG, "Response : " + response);
                setRV(response);

                //notifikasi adanya perubahan data pada adapter
                listAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", ItemOpnameActivity.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("nmbrg", nmbrg);
                params.put("status_opname", status_opname);
                params.put("no_opname", NoOpname);

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

                    listItem.add(obj.getString("KdBrg"));
                    listNmBrg.add(obj.getString("NmBrg"));
                    listStatusOpname.add(obj.getString("StatusOpname"));
                    listSatuan.add(obj.getString("Satuan"));

                } catch (JSONException e) {
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