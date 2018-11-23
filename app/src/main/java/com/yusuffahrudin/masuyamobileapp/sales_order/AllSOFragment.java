package com.yusuffahrudin.masuyamobileapp.sales_order;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.data.SalesOrder;
import com.yusuffahrudin.masuyamobileapp.informasi_barang.ListBarangActivity;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yusuf fahrudin on 17-01-2018.
 */

public class AllSOFragment extends Fragment {
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<SalesOrder> listSO = new ArrayList<>();
    private AdapterRVSO adapter;
    private RecyclerView rv_so;
    private LinearLayoutManager layoutManager;
    private SessionManager sessionManager;
    private TextView section_label;
    private static String name, level, kdkota;
    private static final String TAG = SalesOrderActivity.class.getSimpleName();
    private static String url_select;
    private String tag_json_obj = "json_obj_req";

    public AllSOFragment() {
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
        view = inflater.inflate(R.layout.fragment_so_all, container, false);

        sessionManager = new SessionManager(this.getActivity().getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        name = user.get(SessionManager.kunci_email);
        level = user.get(SessionManager.level);
        kdkota = user.get(SessionManager.kdkota);

        //menghubungkan variabel dengan layout view dan java
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        rv_so = view.findViewById(R.id.rv_so);
        rv_so.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        rv_so.setLayoutManager(layoutManager);
        section_label = view.findViewById(R.id.section_label);
        //untuk mengisi data dari JSON ke Adapter
        adapter = new AdapterRVSO(this.getActivity(), listSO);
        rv_so.setAdapter(adapter);

        selectSO();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                selectSO();
            }
        });
        return view;
    }

    private class AdapterRVSO extends RecyclerView.Adapter <AllSOFragment.AdapterRVSO.ViewHolder> {
        private List<SalesOrder> list_tampung;
        private Activity activity;
        LayoutInflater inflater;

        public AdapterRVSO(Activity activity, List<SalesOrder> tampung){
            this.activity = activity;
            this.list_tampung = tampung;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //membuat view baru
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_so_all, parent, false);
            //mengeset ukuran view, margin, padding, dan parameter layout
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            if (list_tampung.get(position).getStatusorder().equalsIgnoreCase("OPEN")){
                holder.img_status.setImageResource(R.drawable.data_completed);
            } else if (list_tampung.get(position).getStatusorder().equalsIgnoreCase("CLOSE")){
                holder.img_status.setImageResource(R.drawable.data_closed);
            } else {
                holder.img_status.setImageResource(R.drawable.data_pending);
            }

            String dateStr = list_tampung.get(position).getTgl_create();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sdf = new SimpleDateFormat("dd-MM-yyyy");
            String tgl = sdf.format(date);

            holder.tvNoBukti.setText(list_tampung.get(position).getNobukti());
            holder.tvStatusOrder.setText(list_tampung.get(position).getStatusorder());
            holder.tvNmCust.setText(list_tampung.get(position).getNmcust());
            holder.tvTgl.setText(tgl);

            holder.cv_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), DetailSalesOrderActivity.class);
                    i.putExtra("nobukti", list_tampung.get(position).getNobukti());
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            //mengembalikan jumlah data yang ada pada list recyclerview
            return list_tampung.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tvNoBukti;
            public TextView tvStatusOrder;
            public TextView tvNmCust;
            public TextView tvTgl;
            public CardView cv_main;
            public ImageView img_status;

            public ViewHolder(View itemView) {
                super(itemView);
                tvNoBukti = itemView.findViewById(R.id.tv_nobukti);
                tvStatusOrder = itemView.findViewById(R.id.tv_status_order);
                tvNmCust = itemView.findViewById(R.id.tv_nmcust);
                tvTgl = itemView.findViewById(R.id.tv_tgl);
                cv_main = itemView.findViewById(R.id.cv_main);
                img_status = itemView.findViewById(R.id.img_status);
            }
        }
    }

    private void selectSO(){
        listSO.clear();
        adapter.notifyDataSetChanged();

        Server a = new Server(kdkota);
        url_select = a.URL() + "salesorder/select_sales_order_all.php";

        final ProgressDialog progressDialog = ProgressDialog.show(this.getActivity(), "", "Please Wait...");
        new Thread() {
            public void run() {
                try{
                    sleep(1000);
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
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (success == 1){
                        setRV(jObj);
                        // dismiss the progress dialog
                        progressDialog.dismiss();
                        section_label.setVisibility(View.GONE);

                        //notifikasi adanya perubahan data pada adapter
                        adapter.notifyDataSetChanged();
                    } else {
                        progressDialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        section_label.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", getActivity());
                progressDialog.dismiss();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    //fungsi untuk memasukkan data dari database ke dalam arraylist
    private void setRV(JSONObject jsonObject){
        try {
            JSONArray result = jsonObject.getJSONArray("result");
            for (int i = 0; i < result.length(); i++) {
                try {
                    JSONObject obj = result.getJSONObject(i);

                    SalesOrder item = new SalesOrder();

                    item.setNobukti(obj.getString("NoBukti"));
                    item.setStatusorder(obj.getString("StatusOrder"));
                    item.setKdcust(obj.getString("KdCust"));
                    item.setNmcust(obj.getString("NmCust"));
                    item.setTgl_create(obj.getString("TglOrder"));

                    //menambah item ke array
                    listSO.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //notifikasi adanya perubahan data pada adapter
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
