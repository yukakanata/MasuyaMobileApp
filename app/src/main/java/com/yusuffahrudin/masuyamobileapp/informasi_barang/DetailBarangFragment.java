package com.yusuffahrudin.masuyamobileapp.informasi_barang;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.adapter.AdapterVPFotoProdukKecil;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.data.ArrayTampung;
import com.yusuffahrudin.masuyamobileapp.data.User;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by yusuf fahrudin on 17-01-2018.
 */

public class DetailBarangFragment extends Fragment {
    private View view;
    private String kdbrg, nmbrg, kdkota, password, hrgjualmin_excppn, hrgjualmin_incppn, user, tanggal, kota;
    private int success;
    private EditText edt_jenis, edt_nmtype, edt_packing3, edt_hrgjualmin_excppn, edt_hrgjualmin_incppn;
    private TextView tv_kdbrg, tv_nmbrg;
    private Button btn_harga;
    private AlertDialog.Builder dialog;
    private View dialogView;
    private LayoutInflater inflater;
    private ViewPager vp_image;
    private AdapterVPFotoProdukKecil adapterVPFotoProdukKecil;
    private CircleIndicator indicator;
    private ArrayList<String> arrayFoto;
    private List<User> listAkses = ArrayTampung.getListAkses();
    private SessionManager sessionManager;

    private static final String TAG = DetailBarangFragment.class.getSimpleName();
    private static String url_select_detail_produk;
    private static String url_login;
    public static final String TAG_NMBRG = "NmBrg";
    public static final String TAG_JENIS = "Jenis";
    public static final String TAG_NMTYPE = "NmType";
    public static final String TAG_PACKING3 = "Packing3";
    public static final String TAG_HRGJUALMINEXCPPN = "HrgJualMinExcPPN";
    public static final String TAG_HRGJUALMININCPPN = "HrgJualMinIncPPN";
    public static final String TAG_QTY_GOOD = "Qty_GOOD";
    public static final String TAG_QTY_TITIPAN = "Qty_TITIPAN";
    public static final String TAG_QTY_BOOKING = "Qty_BOOKING";
    public static final String TAG_QTY_BAD = "Qty_BAD";
    String tag_json_obj = "json_obj_req";

    public DetailBarangFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_detail_barang, container, false);
        sessionManager = new SessionManager(this.getActivity().getApplicationContext());
        HashMap<String, String> cache = sessionManager.getUserDetails();
        kdkota = cache.get(SessionManager.kdkota);
        user = cache.get(SessionManager.kunci_email);

        Intent i = this.getActivity().getIntent();
        kdbrg = i.getExtras().getString("kdbrg");
        tanggal = i.getExtras().getString("tanggal");
        kota = i.getExtras().getString("kota");

        tv_kdbrg = view.findViewById(R.id.tv_kdbrg);
        tv_nmbrg = view.findViewById(R.id.tv_nmbrg);
        edt_jenis = view.findViewById(R.id.edt_jenis);
        edt_nmtype = view.findViewById(R.id.edt_nmtype);
        edt_packing3 = view.findViewById(R.id.edt_packing3);
        btn_harga = view.findViewById(R.id.btn_harga);
        vp_image = view.findViewById(R.id.vp_image);
        indicator = view.findViewById(R.id.indicator);

        setFoto();

        adapterVPFotoProdukKecil = new AdapterVPFotoProdukKecil(getParentFragment().getContext(), arrayFoto, kdbrg);
        vp_image.setAdapter(adapterVPFotoProdukKecil);
        indicator.setViewPager(vp_image);

        getData();
        //loadImage();
        cekAkses();

        //action button harga
        btn_harga.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                DialogPassword();
            }
        });

        return view;
    }

    public void getData() {
        Server a = new Server(kdkota);
        url_select_detail_produk = a.URL() + "masterbrg/select_detail_barang.php";

        final ProgressDialog progressDialog = ProgressDialog.show(this.getActivity(), "", "Please Wait...");
        new Thread() {
            public void run() {
                try{
                    sleep(10000);
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
            }
        }.start();

        StringRequest strReq = new StringRequest(Request.Method.POST, url_select_detail_produk, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response);
                showData(response);
                progressDialog.dismiss();

            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error : "+ error.getMessage());
                new DialogAlert(error.getMessage(), "error", getActivity());
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("kdbrg", kdbrg);
                params.put("tanggal", tanggal);
                params.put("kota", kota);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }

    private void showData(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");

            JSONObject obj = result.getJSONObject(0);

            nmbrg = obj.getString(TAG_NMBRG);

            tv_kdbrg.setText(kdbrg);
            tv_nmbrg.setText(obj.getString(TAG_NMBRG));
            edt_jenis.setText(obj.getString(TAG_JENIS));
            edt_nmtype.setText(obj.getString(TAG_NMTYPE));
            edt_packing3.setText(obj.getString(TAG_PACKING3));

            hrgjualmin_excppn = NumberFormat.getInstance().format(obj.getDouble(TAG_HRGJUALMINEXCPPN));
            hrgjualmin_incppn = NumberFormat.getInstance().format(obj.getDouble(TAG_HRGJUALMININCPPN));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void cekAkses(){
        for (int i=0; i<listAkses.size(); i++){
            String str = listAkses.get(i).getModul();
            String modul = str.substring(str.indexOf("-") + 1);

            if (modul.equalsIgnoreCase("Harga Jual Min")){
                if  (listAkses.get(i).isAkses()){
                    btn_harga.setVisibility(View.VISIBLE);
                    //edt_hrgjualmin_incppn.setVisibility(View.VISIBLE);
                } else {
                    btn_harga.setVisibility(View.GONE);
                    //edt_hrgjualmin_incppn.setVisibility(View.GONE);
                }
            }
        }
    }

    private void setFoto(){
        Server a = new Server(kdkota);
        arrayFoto = new ArrayList<>();
        arrayFoto.add(a.URL_IMAGE()+kdbrg+".jpg");
        arrayFoto.add(a.URL_IMAGE()+kdbrg+"_1.jpg");
        arrayFoto.add(a.URL_IMAGE()+kdbrg+"_2.jpg");
    }

    // untuk menampilkan dialog password
    private void DialogPassword() {
        dialog = new AlertDialog.Builder(getActivity());
        inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_password, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.masuyalogo);
        dialog.setTitle("Password");

        final EditText edt_password = dialogView.findViewById(R.id.edt_password);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                password = edt_password.getText().toString();

                CekPassword();
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                edt_password.setText(null);
            }
        });

        dialog.show();
    }

    // untuk menampilkan dialog harga
    private void DialogHarga() {
        dialog = new AlertDialog.Builder(getActivity());
        inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_hargajualmin, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.masuyalogo);
        dialog.setTitle("Harga Jual Minimum");

        edt_hrgjualmin_excppn = dialogView.findViewById(R.id.edt_hrgjualmin_excppn);
        edt_hrgjualmin_incppn = dialogView.findViewById(R.id.edt_hrgjualmin_incppn);
        edt_hrgjualmin_excppn.setText(hrgjualmin_excppn);
        edt_hrgjualmin_incppn.setText(hrgjualmin_incppn);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void CekPassword(){
        Server a = new Server(kdkota);
        url_login = a.URL() + "tools/login.php";
        Log.v(TAG, "url login : "+url_login);

        final StringRequest strReq = new StringRequest(Request.Method.POST, url_login, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : "+ response);

                try{
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt("success");

                    //cek error node pada JSON
                    if (success == 1){
                        DialogHarga();
                    } else {
                        Toast.makeText(getActivity(), "Password yang anda masukkan salah!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                    new DialogAlert(e.toString(), "error", getActivity());
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error Volley : "+error.getMessage());
                new DialogAlert(error.getMessage(), "error", getActivity());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                //Posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", user);
                params.put("pass", password);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
}
