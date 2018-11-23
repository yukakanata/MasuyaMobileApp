package com.yusuffahrudin.masuyamobileapp.sales_order;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.adapter.AdapterLVSOCust;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.data.SalesOrder;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateSalesOrder extends AppCompatActivity {
    private EditText edt_cust, edt_nomor_so, edt_orderby, edt_noPO, edt_cetak_note, edt_ket;
    private LinearLayout btn_tgl_create, btn_browse_cust, btn_tgl_kirim, btn_pilih_brg, btn_draw_sign;
    private TextView tv_tgl_create;
    private Spinner spin_kdgd;
    private static TextView tv_tgl_kirim;
    private RadioGroup radioGroup;
    private RadioButton radio_ppn, radio_pnbkp, radio_pbbs, radio_selected;
    private SessionManager sessionManager;
    private String kdbrg, nmbrg, status_pajak, satuan, nomor_so, message;
    private String subtotal, discfak_persen = "0", discfak_total, ppn_persen, ppn_total, total;
    private String kdcust, nmcust, kdkel, alm1, alm2, alm3, kdsales, user_kota, kdkota, name;
    private String[] kdgdArray;
    private List<String> listGD;
    private Double qty, harga, diskon1, diskon2, diskon3;
    private static int REQUEST_CUST = 1;
    private static int REQUEST_SO = 2;
    private ArrayAdapter<String> adapterKdGd;
    private ArrayList<SalesOrder> listHeader;
    private ProgressDialog pDialog;
    private static String url_select_kota;
    private static final String TAG = CreateSalesOrder.class.getSimpleName();
    private String tag_json_obj = "json_obj_req";
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Create Sales Order");
        setContentView(R.layout.activity_create_sales_order);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity = this;

        edt_cust = findViewById(R.id.edt_cust);
        edt_nomor_so = findViewById(R.id.edt_nomor_so);
        edt_orderby = findViewById(R.id.edt_orderby);
        edt_noPO = findViewById(R.id.edt_noPO);
        edt_cetak_note = findViewById(R.id.edt_cetak_note);
        edt_ket = findViewById(R.id.edt_ket);
        tv_tgl_create = findViewById(R.id.tv_tgl_create);
        tv_tgl_kirim = findViewById(R.id.tv_tgl_kirim);
        btn_browse_cust = findViewById(R.id.btn_browse_cust);
        btn_tgl_create = findViewById(R.id.btn_tgl_create);
        btn_tgl_kirim = findViewById(R.id.btn_tgl_kirim);
        btn_pilih_brg = findViewById(R.id.btn_pilih_brg);
        radioGroup = findViewById(R.id.radio_group);
        radio_ppn = findViewById(R.id.radio_ppn);
        radio_pnbkp = findViewById(R.id.radio_pnbkp);
        radio_pbbs = findViewById(R.id.radio_pbbs);
        spin_kdgd = findViewById(R.id.spin_kdgd);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                nomor_so = "";
                kdcust = "";
                nmcust = "";
                kdkel = "";
                alm1 = "";
                alm2 = "";
                alm3 = "";
                kdsales = "";
            } else {
                nomor_so = extras.getString("nomor_so");
                kdcust = extras.getString("kdcust");
                nmcust = extras.getString("nmcust");
                kdkel = extras.getString("kdkel");
                alm1 = extras.getString("alm1");
                alm2 = extras.getString("alm2");
                alm3 = extras.getString("alm3");
                kdsales = extras.getString("kdsales");
            }
        } else {
            nomor_so = (String) savedInstanceState.getSerializable("nomor_so");
        }

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        name = user.get(SessionManager.kunci_email);
        user_kota = user.get(SessionManager.kota);
        kdkota = user.get(SessionManager.kdkota);

        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //editor = sharedPreferences.edit();
        edt_cust.setText(kdcust);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        tv_tgl_create.setText(year + "/" + (month + 1) + "/" + day);
        edt_nomor_so.setText(nomor_so);

        listGD = new ArrayList<String>();
        new GetKdGd().execute();

        btn_browse_cust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateSalesOrder.this, SOListCustomerActivity.class);
                startActivityForResult(intent, REQUEST_CUST);
            }
        });

        btn_pilih_brg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getRadio();
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radio_selected = findViewById(selectedId);

                listHeader = new ArrayList<>();
                SalesOrder header = new SalesOrder();
                header.setNobukti(edt_nomor_so.getText().toString());
                header.setKdcust(kdcust);
                header.setNmcust(nmcust);
                header.setKdkel(kdkel);
                header.setAlm1(alm1);
                header.setAlm2(alm2);
                header.setAlm3(alm3);
                header.setKdsales(kdsales);
                header.setTgl_create(tv_tgl_create.getText().toString());
                header.setTgl_kirim(tv_tgl_kirim.getText().toString());
                header.setKet1(edt_cetak_note.getText().toString());
                header.setKet2(edt_ket.getText().toString());
                header.setNoPO(edt_noPO.getText().toString());
                header.setJnsjualtax(radio_selected.getText().toString());
                header.setKodeTax(status_pajak);
                header.setOrderby(edt_orderby.getText().toString());
                header.setCreateby(name);
                header.setKdgd(spin_kdgd.getSelectedItem().toString());
                listHeader.add(header);

                if (kdcust == null || kdcust.equalsIgnoreCase("")){
                    new DialogAlert("Customer belum diisi...!", "attention", CreateSalesOrder.this);
                    //Toast.makeText(getApplicationContext(), "Customer belum diisi...!", Toast.LENGTH_LONG).show();
                } else if(tv_tgl_kirim.getText().toString().equalsIgnoreCase("tgl kirim")){
                    new DialogAlert("Tanggal kirim belum diisi...!", "attention", CreateSalesOrder.this);
                    //Toast.makeText(getApplicationContext(), "Tanggal kirim belum diisi...!", Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(CreateSalesOrder.this, PilihBarangActivity.class);
                    i.putExtra("listHeader",listHeader);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CUST && resultCode == RESULT_OK) {
            kdcust = data.getStringExtra(AdapterLVSOCust.KDCUST);
            nmcust = data.getStringExtra(AdapterLVSOCust.NMCUST);
            kdkel = data.getStringExtra(AdapterLVSOCust.KDKEL);
            alm1 = data.getStringExtra(AdapterLVSOCust.ALM1);
            alm2 = data.getStringExtra(AdapterLVSOCust.ALM2);
            alm3 = data.getStringExtra(AdapterLVSOCust.ALM3);
            kdsales = data.getStringExtra(AdapterLVSOCust.KDSALES);
            edt_cust.setText(kdcust);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    //fungsi untuk membuat dialog datepicker dari
    public static class DatePickerFragmentKirimSO extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            //datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            tv_tgl_kirim.setText(year + "/" + (month + 1) + "/" + day);
        }
    }

    //fungsi untuk menampilkan dialog date picker dari
    public void showDatePickerDialogKirimSO(View v) {
        DialogFragment newFragment = new DatePickerFragmentKirimSO();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void getRadio(){
        if (radio_ppn.isChecked()){
            status_pajak = "01";
        } else if (radio_pnbkp.isChecked()){
            status_pajak = "02";
        } else if (radio_pbbs.isChecked()){
            status_pajak = "03";
        } else {
            Toast.makeText(getApplicationContext(), "Status pajak belum dipilih...!", Toast.LENGTH_LONG).show();
        }
    }

    private class GetKdGd extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateSalesOrder.this);
            pDialog.setMessage("Fetching kota...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            sessionManager = new SessionManager(getApplicationContext());
            HashMap<String, String> user = sessionManager.getUserDetails();
            kdkota = user.get(SessionManager.kdkota);

            Server a = new Server(kdkota);
            url_select_kota = a.URL() + "tools/select_kdgd.php";

            StringRequest strReq = new StringRequest(Request.Method.POST, url_select_kota, new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Response : " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray result = jsonObject.getJSONArray("result");
                        listGD.clear();
                        for (int i = 0; i < result.length(); i++) {
                            try {
                                JSONObject obj = result.getJSONObject(i);
                                //menambah item ke array
                                listGD.add(obj.getString("KdGd"));
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
                    new DialogAlert(error.getMessage(), "error", CreateSalesOrder.this);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    //posting parameter ke post url
                    Map<String, String> params = new HashMap<String, String>();

                    if (user_kota.equalsIgnoreCase("ALL")){
                        params.put("user_kota", "'SBY','MLG','MKS','BPN','SMG','YGY'");
                    } else if(user_kota.equalsIgnoreCase("MLG")){
                        params.put("user_kota", "'SBY','MLG'");
                    } else {
                        params.put("user_kota", "'"+user_kota+"'");
                    }

                    return params;
                }
            };

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
        kdgdArray = listGD.toArray(new String[listGD.size()]);
        adapterKdGd = new ArrayAdapter<String>(this, R.layout.spinner_item, kdgdArray);
        spin_kdgd.setAdapter(adapterKdGd);
    }
}
