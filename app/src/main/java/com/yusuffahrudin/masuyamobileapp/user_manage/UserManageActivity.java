package com.yusuffahrudin.masuyamobileapp.user_manage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManageActivity extends AppCompatActivity {
    private Button btn_simpan, btn_detail;
    private RadioButton radio_level, radio_admin, radio_manager, radio_supervisor, radio_sales, radio_warehouse, radio_bd;
    private RadioGroup radio_group;
    private String user="", level="", message, kdkota, user_kota;
    private int success;
    private Spinner spin_user, spin_kota;
    private ArrayList<String> data;
    private ArrayAdapter adapter; ArrayAdapter<String> adapterKota;
    private Intent intent;
    private String [] kotaArray;
    private List<String> listKota;
    private ProgressDialog pDialog;
    private static String url_select_kota;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    private static final String TAG = UserManageActivity.class.getSimpleName();
    private static String url_select_user;
    private static String url_insert_level;

    String tag_json_obj = "json_obj_req";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("User Management");
        setContentView(R.layout.activity_user_manage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> cache = sessionManager.getUserDetails();
        kdkota = cache.get(SessionManager.kdkota);
        user_kota = cache.get(SessionManager.kota);

        spin_user = findViewById(R.id.spin_user);
        spin_kota = findViewById(R.id.spin_kota);
        radio_group = findViewById(R.id.radio_group);
        radio_admin = findViewById(R.id.radio_admin);
        radio_manager = findViewById(R.id.radio_manager);
        radio_supervisor = findViewById(R.id.radio_supervisor);
        radio_bd = findViewById(R.id.radio_bd);
        radio_sales = findViewById(R.id.radio_sales);
        radio_warehouse = findViewById(R.id.radio_warehouse);
        btn_simpan = findViewById(R.id.btn_simpan);
        btn_detail = findViewById(R.id.btn_detail);

        //mengisi spinner kota
        listKota = new ArrayList<String>();
        data = new ArrayList<>();
        if (user_kota.equalsIgnoreCase("ALL")){
            new GetKota1().execute();
        } else {
            kotaArray = new String[]{user_kota};
            adapterKota = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, kotaArray);
            spin_kota.setAdapter(adapterKota);
        }

        adapter = new ArrayAdapter(UserManageActivity.this, android.R.layout.simple_list_item_1, data);
        spin_user.setAdapter(adapter);

        getUser();

        spin_user.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            public void onItemSelected( AdapterView<?> parent, View view, int position, long id) {
                user = spin_user.getSelectedItem().toString();
                getLevel();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(UserManageActivity.this, "tidak terselect", Toast.LENGTH_SHORT).show();
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get selected radio button from radioGroup
                int selectedId = radio_group.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radio_level = findViewById(selectedId);
                level = radio_level.getText().toString();
                Log.v(TAG, "level : "+ level);
                simpanUserLevel();
            }
        });

        btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get selected radio button from radioGroup
                int selectedId = radio_group.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radio_level = findViewById(selectedId);
                Toast.makeText(UserManageActivity.this,
                        radio_level.getText(), Toast.LENGTH_SHORT).show();
                intent = new Intent(UserManageActivity.this, UserAksesActivity.class);
                intent.putExtra("level", radio_level.getText());
                startActivity(intent);
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

    public void getUser() {
        data.clear();
        adapter.notifyDataSetChanged();
        Server a = new Server(kdkota);
        url_select_user = a.URL() + "usermanagement/select_user.php";

        progressDialog = ProgressDialog.show(UserManageActivity.this, "", "Please Wait...");
        new Thread() {
            public void run() {
                try{
                    sleep(10000);
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
            }
        }.start();

        StringRequest strReq = new StringRequest(Request.Method.POST, url_select_user, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.v(TAG, "Response : " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("result");
                    for (int i = 0; i < result.length(); i++) {
                        try {
                            JSONObject obj = result.getJSONObject(i);
                            data.add(obj.getString("UID"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //notifikasi adanya perubahan data pada adapter
                    adapter.notifyDataSetChanged();
                    getLevel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", UserManageActivity.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", user);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    public void getLevel() {
        Server a = new Server(kdkota);
        url_select_user = a.URL() + "usermanagement/select_user.php";
        StringRequest strReq = new StringRequest(Request.Method.POST, url_select_user, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.v(TAG, "Response : " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("result");
                    for (int i = 0; i < result.length(); i++) {
                        try {
                            JSONObject obj = result.getJSONObject(i);
                            level = obj.getString("Level");
                            if(level.equalsIgnoreCase("Administrator"))
                            {
                                radio_admin.setChecked(true);
                            }
                            else if(level.equalsIgnoreCase("Manager")){

                                radio_manager.setChecked(true);
                            }
                            else if(level.equalsIgnoreCase("Supervisor"))
                            {
                                radio_supervisor.setChecked(true);
                            }
                            else if(level.equalsIgnoreCase("BD"))
                            {
                                radio_bd.setChecked(true);
                            }
                            else if(level.equalsIgnoreCase("Sales"))
                            {
                                radio_sales.setChecked(true);
                            }
                            else if(level.equalsIgnoreCase("Warehouse"))
                            {
                                radio_warehouse.setChecked(true);
                            } else {
                                radio_admin.setChecked(false);
                                radio_manager.setChecked(false);
                                radio_supervisor.setChecked(false);
                                radio_bd.setChecked(false);
                                radio_sales.setChecked(false);
                                radio_warehouse.setChecked(false);
                            }
                            int spinnerPosition = adapter.getPosition(obj.getString("KdKota"));
                            spin_kota.setSelection(spinnerPosition);
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", UserManageActivity.this);
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", user);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    //menyimpan data user level
    private void simpanUserLevel(){
        Server a = new Server(kdkota);
        url_insert_level = a.URL() + "usermanagement/insert_user_level.php";

        progressDialog = ProgressDialog.show(UserManageActivity.this, "", "Please Wait...");
        new Thread() {
            public void run() {
                try{
                    sleep(10000);
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
            }
        }.start();

        StringRequest strReq = new StringRequest(Request.Method.POST, url_insert_level, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.v(TAG, "Response Hasil Opname : "+ response);

                try{
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt("success");
                    message = jObj.getString("message");

                    //cek error node pada JSON
                    if (success == 1){
                        new DialogAlert(message, "success", UserManageActivity.this);
                        progressDialog.dismiss();
                    } else {
                        new DialogAlert(message, "error", UserManageActivity.this);
                        progressDialog.dismiss();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", UserManageActivity.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> param = new HashMap<String, String>();
                param.put("level", level);
                param.put("user", user);
                param.put("kota", spin_kota.getSelectedItem().toString());

                return param;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private class GetKota1 extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserManageActivity.this);
            pDialog.setMessage("Fetching kota..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            sessionManager = new SessionManager(getApplicationContext());
            HashMap<String, String> user = sessionManager.getUserDetails();
            kdkota = user.get(SessionManager.kdkota);

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
                        listKota.add("ALL");
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
        adapterKota = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, kotaArray);
        spin_kota.setAdapter(adapterKota);
    }

}
