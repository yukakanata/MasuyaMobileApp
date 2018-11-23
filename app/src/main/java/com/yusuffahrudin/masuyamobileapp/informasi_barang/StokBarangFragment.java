package com.yusuffahrudin.masuyamobileapp.informasi_barang;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by yusuf fahrudin on 17-01-2018.
 */

public class StokBarangFragment extends Fragment {
    private View view;
    private String kdkota, user, kdbrg, tanggal, kota;
    private List<Data> listStok = new ArrayList<>();
    private SessionManager sessionManager;
    private SwipeRefreshLayout swipe_refresh;
    private NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
    private AdapterStok adapter;

    private RecyclerView rv_fragment_stok;
    private GridLayoutManager layoutManager;

    private static final String TAG = StokBarangFragment.class.getSimpleName();
    private static String url_select_stok;
    String tag_json_obj = "json_obj_req";

    public StokBarangFragment() {
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
        view = inflater.inflate(R.layout.fragment_stok_barang, container, false);
        sessionManager = new SessionManager(this.getActivity().getApplicationContext());
        HashMap<String, String> cache = sessionManager.getUserDetails();
        kdkota = cache.get(SessionManager.kdkota);
        user = cache.get(SessionManager.kunci_email);

        Intent i = this.getActivity().getIntent();
        kdbrg = i.getExtras().getString("kdbrg");
        tanggal = i.getExtras().getString("tanggal");
        kota = i.getExtras().getString("kota");

        swipe_refresh = view.findViewById(R.id.swipe_refresh);

        rv_fragment_stok = view.findViewById(R.id.list_stok);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        rv_fragment_stok.setHasFixedSize(true);
        rv_fragment_stok.setLayoutManager(layoutManager);
        adapter = new AdapterStok(getActivity(), listStok);
        rv_fragment_stok.setAdapter(adapter);

        getData();
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        return view;
    }

    public void getData() {
        listStok.clear();
        adapter.notifyDataSetChanged();

        Server a = new Server(kdkota);
        url_select_stok = a.URL() + "masterbrg/select_stok_barang.php";

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

        StringRequest strReq = new StringRequest(Request.Method.POST, url_select_stok, new Response.Listener<String>(){

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
                        Toast.makeText(getActivity(), "Error pengambilan data", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e){
                    new DialogAlert(e.getMessage(), "error", getActivity());
                }
                progressDialog.dismiss();

            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", getActivity());
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("kdbrg", kdbrg);
                params.put("kota", kota);

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
                item.setKdgd(obj.getString("KdGd"));
                item.setNmgd(obj.getString("NmGd"));
                item.setQty(obj.getDouble("Qty"));
                listStok.add(item);
            }

            adapter.notifyDataSetChanged();
            swipe_refresh.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class AdapterStok extends RecyclerView.Adapter<AdapterStok.ViewHolder> {
        private List<Data> list_tampung;
        private Activity activity;
        LayoutInflater inflater;

        public AdapterStok(Activity activity, List<Data> tampung){
            this.activity = activity;
            this.list_tampung = tampung;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //membuat view baru
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_fragment_stok, parent, false);
            //mengeset ukuran view, margin, padding, dan parameter layout
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            //mengambil elemen dari arraylist pada posisi yang ditentukan
            //dan memasukkannya sebagai isi dari view recyclerview

            holder.tv_stok.setText(nf.format(list_tampung.get(position).getQty()));
            holder.tv_kdgd.setText(list_tampung.get(position).getKdgd());
            holder.tv_nmgd.setText(list_tampung.get(position).getNmgd());

        }

        @Override
        public int getItemCount() {
            //mengembalikan jumlah data yang ada pada list recyclerview
            return list_tampung.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tv_stok;
            public TextView tv_kdgd;
            public TextView tv_nmgd;
            public CardView cv_main;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_stok = itemView.findViewById(R.id.tv_stok);
                tv_kdgd = itemView.findViewById(R.id.tv_kdgd);
                tv_nmgd = itemView.findViewById(R.id.tv_nmgd);
                cv_main = itemView.findViewById(R.id.cv_main);
            }
        }
    }
}
