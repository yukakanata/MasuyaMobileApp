package com.yusuffahrudin.masuyamobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crash.FirebaseCrash;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.data.ArrayTampung;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    int success;
    EditText edt_user, edt_pass;
    Button btn_login;
    CheckBox cb_show_password;
    Spinner spin_kota;
    String user, pass, message, level, kota, kdkota;
    Intent intent;
    SessionManager sessionManager;
    ArrayAdapter<String> adapterkota;
    List<String> listKota = ArrayTampung.getListKota();

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static String url_login;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(getApplicationContext());
        FirebaseApp.initializeApp(getApplicationContext());
        //FirebaseCrash.report(new Exception("My first Android non-fatal error"));

        edt_user = findViewById(R.id.edt_username);
        edt_pass = findViewById(R.id.edt_password);
        btn_login = findViewById(R.id.btn_login);
        cb_show_password = findViewById(R.id.cb_show_password);
        spin_kota = findViewById(R.id.spin_kota);


        String [] kotaArray = new String[]{"SBY","MLG","MKS","BPN","SMG","YGY","JKT","TES"};
        adapterkota = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, kotaArray);
        spin_kota.setAdapter(adapterkota);

        cb_show_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    edt_pass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    edt_pass.setInputType(129);
                }
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            LogIn();
            }
        });
    }

    private void LogIn(){
        user  = edt_user.getText().toString();
        pass = edt_pass.getText().toString();
        kdkota = spin_kota.getSelectedItem().toString().toLowerCase();
        Server a = new Server(kdkota);
        url_login = a.URL() + "tools/login.php";
        Log.v(TAG, "url login : "+url_login);

        final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "", "Please Wait...");
        new Thread() {
            public void run() {
                try{
                    sleep(10000);
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
            }
        }.start();

        final StringRequest strReq = new StringRequest(Request.Method.POST, url_login, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : "+ response);

                try{
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    message = jObj.getString(TAG_MESSAGE);
                    level = jObj.getString("Level");
                    kota = jObj.getString("KdKota");
                    // dismiss the progress dialog
                    progressDialog.dismiss();

                    //cek error node pada JSON
                    if (success == 1){
                        Log.v("login ", jObj.toString());
                        sessionManager.createSession(user, level, kota, kdkota);
                        listKota.clear();
                        intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        new DialogAlert(message, "error", LoginActivity.this);

                    }
                } catch (JSONException e){
                    e.printStackTrace();
                    new DialogAlert(e.getMessage(), "error", LoginActivity.this);
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                new DialogAlert(error.getMessage(), "error", LoginActivity.this);
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                //Posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", user);
                params.put("pass", pass);

                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit..",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
}
