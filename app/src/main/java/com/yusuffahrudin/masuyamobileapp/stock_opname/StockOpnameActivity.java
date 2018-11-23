package com.yusuffahrudin.masuyamobileapp.stock_opname;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.data.ArrayTampung;
import com.yusuffahrudin.masuyamobileapp.data.Opname;
import com.yusuffahrudin.masuyamobileapp.data.User;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yusuffahrudin.masuyamobileapp.data.Opname.NoOpnameComparator;

public class StockOpnameActivity extends AppCompatActivity {

    private String NoOpname = "", tgl_awal="", tgl_akhir="", nmbrg="", status_opname="", status, kdkota, user_kota;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdapterListStockOpname listAdapter;
    private List<Opname> listData = ArrayTampung.getListOpname();
    private List<datacetak> listCetak = new ArrayList<>();
    private SessionManager sessionManager;
    private NumberFormat nf = NumberFormat.getInstance();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String TAG = StockOpnameActivity.class.getSimpleName();
    private static int REQUEST_ITEM = 1;
    private static int REQUEST_FILTER_STOCK = 2;
    private static String url_select;
    private static String url_filter;
    private static String url_select_datacetak;
    String tag_json_obj = "json_obj_req";
    List<User> listAkses = ArrayTampung.getListAkses();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Nomor Opname");
        setContentView(R.layout.activity_stock_opname);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        kdkota = user.get(SessionManager.kdkota);
        user_kota = user.get(SessionManager.kota);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        //status = sharedPreferences.getString("status","");
        status = "view";

        // get the listview
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        listView = findViewById(R.id.lv_opname);
        listAdapter = new AdapterListStockOpname(StockOpnameActivity.this, listData);
        Button btn_sort = findViewById(R.id.btn_sort);
        Button btn_filter = findViewById(R.id.btn_filter);

        cek_akses();

        btn_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = { "A-Z", "Z-A" };
                AlertDialog.Builder builder = new AlertDialog.Builder(StockOpnameActivity.this);
                builder.setTitle("Sorting");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("A-Z")){
                            Collections.sort(listData, NoOpnameComparator);
                            listAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        } else {
                            Collections.reverse(listData);
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
                editor.putString("tgl_awal", tgl_awal);
                editor.putString("tgl_akhir", tgl_akhir);
                editor.putString("nmbrg", nmbrg);
                editor.putString("status_opname", status_opname);
                editor.commit();
                Intent intent = new Intent(StockOpnameActivity.this, FilterStockOpname.class);
                startActivityForResult(intent, REQUEST_FILTER_STOCK);
            }
        });

        // setting list adapter
        listView.setAdapter(listAdapter);

        if  (listData.isEmpty()) {
            select_opname();
        } else {
            filter();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                select_opname();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check if the requestCode is the wanted one and if the result is what we are expecting
        if (requestCode == REQUEST_ITEM && resultCode == RESULT_OK) {
            select_opname();
        } else if (requestCode == REQUEST_FILTER_STOCK && resultCode == RESULT_OK) {
            tgl_awal = data.getStringExtra(FilterStockOpname.TGL_AWAL);
            tgl_akhir = data.getStringExtra(FilterStockOpname.TGL_AKHIR);
            nmbrg = data.getStringExtra(FilterStockOpname.NMBRG);
            status_opname = data.getStringExtra(FilterStockOpname.STATUS_OPNAME);
            filter();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stock_opname, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
        }
        if (id == R.id.add){
            editor.putString("no_opname", "AUTO");
            editor.putString("status", "create");
            editor.putString("kota", "");
            editor.commit();
            Intent intent = new Intent(StockOpnameActivity.this, FilterFormStockOpname.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    //fungsi untuk select data dari database
    public void select_opname() {
        listData.clear();
        listAdapter.notifyDataSetChanged();
        Server a = new Server(kdkota);
        url_select = a.URL() + "stockopname/view/select_list_opname.php";

        final ProgressDialog progressDialog = ProgressDialog.show(StockOpnameActivity.this, "", "Please Wait...");
        new Thread() {
            public void run() {
                try{
                    sleep(10000);
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
            }
        }.start();

        StringRequest strReq = new StringRequest(Request.Method.POST, url_select, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response Select Opname : " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("result");
                    for (int i = 0; i < result.length(); i++) {
                        try {
                            JSONObject obj = result.getJSONObject(i);

                            Opname item = new Opname();

                            String dateStr = obj.getString("Tgl");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = sdf.parse(dateStr);

                            sdf = new SimpleDateFormat("dd-MM-yyyy");
                            String tgl = sdf.format(date);

                            item.setNo_opname(obj.getString("NoOpname"));
                            item.setTgl(tgl);
                            item.setKota(obj.getString("Kota"));
                            item.setUser(obj.getString("User"));

                            listData.add(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
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
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", StockOpnameActivity.this);
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("kdkota", user_kota);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }

    //fungsi untuk select data dari database
    public void filter() {
        listData.clear();
        listAdapter.notifyDataSetChanged();
        Server a = new Server(kdkota);
        url_filter = a.URL() + "stockopname/view/filter_list_opname.php";

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
                new DialogAlert(error.getMessage(), "error", StockOpnameActivity.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("nmbrg", nmbrg);
                params.put("tgl_awal", tgl_awal);
                params.put("tgl_akhir", tgl_akhir);
                params.put("status_opname", status_opname);

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

                    Opname item = new Opname();

                    item.setNo_opname(obj.getString("NoOpname"));
                    item.setTgl(obj.getString("Tgl"));
                    item.setKota(obj.getString("Kota"));
                    item.setUser(obj.getString("User"));

                    listData.add(item);

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

    private class AdapterListStockOpname extends BaseAdapter {

        private List<Opname> lvData;
        private Activity activity;
        LayoutInflater inflater;

        public AdapterListStockOpname(Activity activity, List<Opname> tampung){
            this.activity = activity;
            this.lvData = tampung;
        }

        @Override
        public int getCount() {
            return lvData.size();
        }

        @Override
        public Object getItem(int location) {
            return lvData.get(location);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (inflater == null)
                inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null)
                convertView = inflater.inflate(R.layout.lv_nomor_opname, null);

            TextView tv_nomor_opname = convertView.findViewById(R.id.tv_nomor_opname);
            TextView tv_tgl_opname = convertView.findViewById(R.id.tv_tgl_opname);
            TextView tv_kota = convertView.findViewById(R.id.tv_kota);
            TextView tv_user = convertView.findViewById(R.id.tv_user);
            Button btn_cetak = convertView.findViewById(R.id.btn_cetak);
            CardView cv_main = convertView.findViewById(R.id.cv_no_opname);

            String no_opname = lvData.get(position).getNo_opname();
            String tgl_opname = lvData.get(position).getTgl();
            String kota = listData.get(position).getKota();
            String user = listData.get(position).getUser();

            tv_nomor_opname.setText(no_opname);
            tv_tgl_opname.setText(tgl_opname);
            tv_kota.setText(kota);
            tv_user.setText(user);

            cv_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editor.putString("nmbrg", nmbrg);
                    editor.putString("status_opname", status_opname);
                    editor.putString("status", status);
                    editor.putString("user", listData.get(position).getUser());
                    editor.putString("no_opname", listData.get(position).getNo_opname());
                    editor.putString("kota", listData.get(position).getKota());
                    editor.putString("tgl", listData.get(position).getTgl());
                    editor.commit();

                    Intent intent = new Intent(StockOpnameActivity.this, ItemOpnameActivity.class);
                    startActivityForResult(intent, REQUEST_ITEM);
                }
            });

            btn_cetak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cetak(position);
                }
            });

            return convertView;
        }
    }

    //fungsi untuk select data dari database
    public void cetak(final int position) {
        listCetak.clear();
        Server a = new Server(kdkota);
        url_select_datacetak = a.URL() + "stockopname/view/select_data_cetak.php";

        final ProgressDialog pDialog = ProgressDialog.show(StockOpnameActivity.this, "Print", "Please Wait...");
        new Thread() {
            public void run() {
                try{
                    sleep(10000);
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
            }
        }.start();

        StringRequest strReq = new StringRequest(Request.Method.POST, url_select_datacetak, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("result");
                    for (int i = 0; i < result.length(); i++) {
                        try {
                            JSONObject obj = result.getJSONObject(i);

                            datacetak item = new datacetak();
                            item.setKdbrg(obj.getString("KdBrg"));
                            item.setSysgood(nf.format(obj.getDouble("SYSGOOD")));
                            item.setSysbook(nf.format(obj.getDouble("SYSBOOK")));
                            item.setSysbad(nf.format(obj.getDouble("SYSBAD")));
                            item.setHasilgood(nf.format(obj.getDouble("HASILGOOD")));
                            item.setHasilbook(nf.format(obj.getDouble("HASILBOOK")));
                            item.setHasilbad(nf.format(obj.getDouble("HASILBAD")));
                            item.setPending(nf.format(obj.getDouble("Pending")));
                            item.setSelisih(nf.format(obj.getDouble("Selisih")));

                            listCetak.add(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Thread thread = new Thread(new Runnable(){
                        @Override
                        public void run(){
                            try {

                                InputStream src = getAssets().open("Opname.pdf");
                                String dst = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                                        + "/"+listData.get(position).getNo_opname()+".pdf";
                                PdfReader reader = new PdfReader(src);
                                PdfStamper stamper = new PdfStamper(reader,
                                        new FileOutputStream(dst));
                                AcroFields form = stamper.getAcroFields();

                                form.setField("txtNoOpname", listData.get(position).getNo_opname());
                                form.setField("txtKota", listData.get(position).getKota());
                                form.setField("txtTglOpname", listData.get(position).getTgl());
                                form.setField("txtOpnameBy", listData.get(position).getUser());
                                for(int i=0; i<listCetak.size(); i++){
                                    form.setField("txtNo"+i, String.valueOf(i+1));
                                    form.setField("txtKodeBrg"+i, listCetak.get(i).getKdbrg());
                                    form.setField("txtStockGood"+i, listCetak.get(i).getSysgood());
                                    form.setField("txtStockBook"+i, listCetak.get(i).getSysbook());
                                    form.setField("txtStockBad"+i, listCetak.get(i).getSysbad());
                                    form.setField("txtHasilGood"+i, listCetak.get(i).getHasilgood());
                                    form.setField("txtHasilBook"+i, listCetak.get(i).getHasilbook());
                                    form.setField("txtHasilBad"+i, listCetak.get(i).getHasilbad());
                                    form.setField("txtPending"+i, listCetak.get(i).getPending());
                                    form.setField("txtSelisih"+i, listCetak.get(i).getSelisih());
                                }

                                stamper.close();
                                reader.close();

                                /* ======= Membaca File PDF di Folder Download INternal Storage ====== */
                                //PackageManager packageManager = getPackageManager();

                                Intent testIntent = new Intent(Intent.ACTION_VIEW);
                                testIntent.setType("application/pdf");

                                //List list = packageManager.queryIntentActivities(testIntent,
                                //PackageManager.MATCH_DEFAULT_ONLY);

                                try {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);

                                    File fileToRead = new File(
                                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                                                    + "/"+listData.get(position).getNo_opname()+".pdf");
                                    Uri uri = Uri.fromFile(fileToRead.getAbsoluteFile());

                                    pDialog.dismiss();
                                    intent.setDataAndType(uri, "application/pdf");
                                    startActivity(intent);
                                } catch (Exception ex) {
                                    Log.i(getClass().toString(), ex.toString());
                                    new DialogAlert("Cannot open your selected file, try again later", "error", StockOpnameActivity.this);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error print stock opname : " + error.getMessage());
                Toast.makeText(StockOpnameActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("no_opname", listData.get(position).getNo_opname());
                params.put("kota", listData.get(position).getKota());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }

    private class datacetak {
        String kdbrg;
        String sysgood, sysbook, sysbad, hasilgood, hasilbook, hasilbad, pending, selisih;

        public void datacetak(String kdbrg, String sysgood, String sysbook, String sysbad, String hasilgood, String hasilbook, String hasilbad, String pending, String selisih) {
            this.kdbrg = kdbrg;
            this.sysgood = sysgood;
            this.sysbook = sysbook;
            this.sysbad = sysbad;
            this.hasilgood = hasilgood;
            this.hasilbook = hasilbook;
            this.hasilbad = hasilbad;
            this.pending = pending;
            this.selisih = selisih;
        }

        public String getKdbrg() {
            return kdbrg;
        }

        public void setKdbrg(String kdbrg) {
            this.kdbrg = kdbrg;
        }

        public String getSysgood() {
            return sysgood;
        }

        public void setSysgood(String sysgood) {
            this.sysgood = sysgood;
        }

        public String getSysbook() {
            return sysbook;
        }

        public void setSysbook(String sysbook) {
            this.sysbook = sysbook;
        }

        public String getSysbad() {
            return sysbad;
        }

        public void setSysbad(String sysbad) {
            this.sysbad = sysbad;
        }

        public String getHasilgood() {
            return hasilgood;
        }

        public void setHasilgood(String hasilgood) {
            this.hasilgood = hasilgood;
        }

        public String getHasilbook() {
            return hasilbook;
        }

        public void setHasilbook(String hasilbook) {
            this.hasilbook = hasilbook;
        }

        public String getHasilbad() {
            return hasilbad;
        }

        public void setHasilbad(String hasilbad) {
            this.hasilbad = hasilbad;
        }

        public String getPending() {
            return pending;
        }

        public void setPending(String pending) {
            this.pending = pending;
        }

        public String getSelisih() {
            return selisih;
        }

        public void setSelisih(String selisih) {
            this.selisih = selisih;
        }
    }

    private void cek_akses(){
        for (int j=0; j<listAkses.size(); j++){
            String str = listAkses.get(j).getModul();
            String modul = str.substring(str.indexOf("-") + 1);
            if (modul.equalsIgnoreCase("Create") && listAkses.get(j).isAkses()){
                if (listAkses.get(j).isAkses()){
                    //fab_add_opname.setEnabled(true);
                } else {
                    //fab_add_opname.setEnabled(false);
                }
            }
        }
    }

}
