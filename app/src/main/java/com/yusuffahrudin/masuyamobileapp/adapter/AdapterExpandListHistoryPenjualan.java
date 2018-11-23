package com.yusuffahrudin.masuyamobileapp.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.data.ArrayTampung;
import com.yusuffahrudin.masuyamobileapp.data.HistoryPenjualan;
import com.yusuffahrudin.masuyamobileapp.data.User;
import com.yusuffahrudin.masuyamobileapp.history_penjualan.ListHistoryPenjualanActivity;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yusuf fahrudin on 19-04-2017.
 */

public class AdapterExpandListHistoryPenjualan extends BaseExpandableListAdapter {

    private Activity activity;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<HistoryPenjualan>> _listDataChild;
    List<User> listAkses = ArrayTampung.getListAkses();
    AlertDialog.Builder dialog;
    View dialogView;
    LayoutInflater inflater;
    private String nofak, kdkota;
    NumberFormat nf = NumberFormat.getInstance();
    SessionManager sessionManager;
    private static String url_select;
    private static final String TAG = ListHistoryPenjualanActivity.class.getSimpleName();
    String tag_json_obj = "json_obj_req";
    List<HistoryPenjualan> listData = new ArrayList<>();
    AdapterRVHisPenjItem adapter;

    public AdapterExpandListHistoryPenjualan(Activity activity, List<String> listDataHeader,
                                             HashMap<String, List<HistoryPenjualan>> listChildData) {
        this.activity = activity;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        HistoryPenjualan data = (HistoryPenjualan) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_history, null);
        }

        RelativeLayout line_hispenj_nofak = convertView.findViewById(R.id.line_hispenj_nofak);
        TextView tv_tgl_item = convertView.findViewById(R.id.tv_tgl_item);
        final TextView tv_nofaktur_item = convertView.findViewById(R.id.tv_nofaktur_item);
        ImageView img_cetak = convertView.findViewById(R.id.img_cetak);
        ImageView img_kirim = convertView.findViewById(R.id.img_kirim);
        ImageView img_penyiapan = convertView.findViewById(R.id.img_penyiapan);
        ImageView img_diterima = convertView.findViewById(R.id.img_terima);
        ImageView img_kembali = convertView.findViewById(R.id.img_kembali);
        TextView tv_kdbrg_item = convertView.findViewById(R.id.tv_kdbrg_item);
        TextView tv_nmbrg_item = convertView.findViewById(R.id.tv_nmbrg_item);
        TextView tv_qty = convertView.findViewById(R.id.tv_qty);
        TextView tv_harga_item = convertView.findViewById(R.id.tv_harga_item);
        TextView tv_diskon1_header = convertView.findViewById(R.id.tv_diskon1_header);
        TextView tv_diskon1_item = convertView.findViewById(R.id.tv_diskon1_item);
        TextView tv_diskon2_header = convertView.findViewById(R.id.tv_diskon2_header);
        TextView tv_diskon2_item = convertView.findViewById(R.id.tv_diskon2_item);
        TextView tv_diskon3_header = convertView.findViewById(R.id.tv_diskon3_header);
        TextView tv_diskon3_item = convertView.findViewById(R.id.tv_diskon3_item);
        TextView tv_discfak_header = convertView.findViewById(R.id.tv_discfak_header);
        TextView tv_discfak_item = convertView.findViewById(R.id.tv_discfak_item);
        TextView tv_cetak = convertView.findViewById(R.id.tv_cetak);
        TextView tv_penyiapan = convertView.findViewById(R.id.tv_penyiapan);
        TextView tv_kirim = convertView.findViewById(R.id.tv_kirim);
        TextView tv_terima = convertView.findViewById(R.id.tv_terima);
        TextView tv_kembali = convertView.findViewById(R.id.tv_kembali);
        TextView tv_nopo = convertView.findViewById(R.id.tv_nopo);

        tv_nofaktur_item.setText(data.getNofaktur());
        tv_nopo.setText(data.getNopo());
        tv_tgl_item.setText(data.getTgl());

        //set status cetak
        if (data.getCetak().equals("0")){
            img_cetak.setImageResource(R.drawable.cetak0);
            tv_cetak.setTextColor(ContextCompat.getColor(activity, R.color.flatui_concrete));
        } else {
            img_cetak.setImageResource(R.drawable.cetak1);
            tv_cetak.setTextColor(ContextCompat.getColor(activity, R.color.flatui_green_sea));
        }

        //set status kirim
        if (data.getKirim().equals("0")){
            img_kirim.setImageResource(R.drawable.kirim0);
            tv_kirim.setTextColor(ContextCompat.getColor(activity, R.color.flatui_concrete));
        } else {
            img_kirim.setImageResource(R.drawable.kirim1);
            tv_kirim.setTextColor(ContextCompat.getColor(activity, R.color.flatui_green_sea));
        }

        //set status penyiapan
        if (data.getPenyiapan().equals("0")){
            img_penyiapan.setImageResource(R.drawable.penyiapan0);
            tv_penyiapan.setTextColor(ContextCompat.getColor(activity, R.color.flatui_concrete));
        } else {
            img_penyiapan.setImageResource(R.drawable.penyiapan1);
            tv_penyiapan.setTextColor(ContextCompat.getColor(activity, R.color.flatui_green_sea));
        }

        //set status diterima
        if (data.getDiterima().equals("0")){
            img_diterima.setImageResource(R.drawable.diterima0);
            tv_terima.setTextColor(ContextCompat.getColor(activity, R.color.flatui_concrete));
        } else {
            img_diterima.setImageResource(R.drawable.diterima1);
            tv_terima.setTextColor(ContextCompat.getColor(activity, R.color.flatui_green_sea));
        }

        //set status kembali
        if (data.getKembali().equals("0")){
            img_kembali.setImageResource(R.drawable.dokumen0);
            tv_kembali.setTextColor(ContextCompat.getColor(activity, R.color.flatui_concrete));
        } else {
            img_kembali.setImageResource(R.drawable.dokumen1);
            tv_kembali.setTextColor(ContextCompat.getColor(activity, R.color.flatui_green_sea));
        }

        tv_kdbrg_item.setText(data.getKdbrg());
        tv_nmbrg_item.setText(data.getNmbrg());
        tv_qty.setText(nf.format(data.getQty()));
        tv_harga_item.setText(nf.format(data.getHarga()));

        //set diskon 1
        tv_diskon1_item.setText(nf.format(data.getDiskon1())+" %");
        if (nf.format(data.getDiskon1()).equals("0")){
            tv_diskon1_item.setTextColor(Color.BLACK);
        } else {
            tv_diskon1_item.setTextColor(Color.RED);
        }

        //set diskon 2
        tv_diskon2_item.setText(nf.format(data.getDiskon2())+" %");
        if (nf.format(data.getDiskon2()).equals("0")){
            tv_diskon2_item.setTextColor(Color.BLACK);
        } else {
            tv_diskon2_item.setTextColor(Color.RED);
        }

        //set diskon 3
        tv_diskon3_item.setText(nf.format(data.getDiskon3())+" %");
        if (nf.format(data.getDiskon3()).equals("0")){
            tv_diskon3_item.setTextColor(Color.BLACK);
        } else {
            tv_diskon3_item.setTextColor(Color.RED);
        }

        //set diskon faktur
        tv_discfak_item.setText(nf.format(data.getDiscfak())+" %");
        if (nf.format(data.getDiscfak()).equals("0")){
            tv_discfak_item.setTextColor(Color.BLACK);
        } else {
            tv_discfak_item.setTextColor(Color.RED);
        }

        line_hispenj_nofak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nofak = tv_nofaktur_item.getText().toString();
                DialogItem();
            }
        });

        for (int i=0; i<listAkses.size(); i++){
            String str = listAkses.get(i).getModul();
            String akses = str.substring(str.indexOf("-") + 1);
            String modul = str.substring(0 , str.indexOf("-")+1);

            if (akses.equalsIgnoreCase("Harga Jual")) {
                if (listAkses.get(i).isAkses()) {
                    tv_harga_item.setVisibility(View.VISIBLE);
                } else {
                    tv_harga_item.setVisibility(View.GONE);
                }
            }
            if (akses.equalsIgnoreCase("Diskon")) {
                if (listAkses.get(i).isAkses()) {
                    tv_discfak_item.setVisibility(View.VISIBLE);
                    tv_diskon1_item.setVisibility(View.VISIBLE);
                    tv_diskon2_item.setVisibility(View.VISIBLE);
                    tv_diskon3_item.setVisibility(View.VISIBLE);
                    tv_discfak_header.setVisibility(View.VISIBLE);
                    tv_diskon1_header.setVisibility(View.VISIBLE);
                    tv_diskon2_header.setVisibility(View.VISIBLE);
                    tv_diskon3_header.setVisibility(View.VISIBLE);
                } else {
                    tv_discfak_item.setVisibility(View.GONE);
                    tv_diskon1_item.setVisibility(View.GONE);
                    tv_diskon2_item.setVisibility(View.GONE);
                    tv_diskon3_item.setVisibility(View.GONE);
                    tv_discfak_header.setVisibility(View.GONE);
                    tv_diskon1_header.setVisibility(View.GONE);
                    tv_diskon2_header.setVisibility(View.GONE);
                    tv_diskon3_header.setVisibility(View.GONE);
                }
            }
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int childPosition) {
        return this._listDataChild.get(this._listDataHeader.get(childPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_history, null);
        }

        TextView tv_cust_header = convertView
                .findViewById(R.id.tv_cust_header);

        tv_cust_header.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    // untuk menampilkan dialog password
    private void DialogItem() {
        dialog = new AlertDialog.Builder(activity);
        inflater = activity.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_hispenj_item, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.masuyalogo);
        dialog.setTitle(nofak);

        sessionManager = new SessionManager(activity.getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        kdkota = user.get(SessionManager.kdkota);

        RecyclerView rv_hispenj_item = dialogView.findViewById(R.id.rv_hispenj_item);
        rv_hispenj_item.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        rv_hispenj_item.setLayoutManager(layoutManager);
        adapter = new AdapterRVHisPenjItem(listData);
        selectHisPenjItem();
        rv_hispenj_item.setAdapter(adapter);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //fungsi untuk select data dari database
    public void selectHisPenjItem() {
        listData.clear();
        adapter.notifyDataSetChanged();

        Server a = new Server(kdkota);
        url_select = a.URL() + "historypenj/select_history_penjualan_item.php";

        final ProgressDialog progressDialog = ProgressDialog.show(activity, "", "Please Wait...");
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
                progressDialog.dismiss();

                //notifikasi adanya perubahan data pada adapter
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error Volley : "+ error.getMessage());
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("nofak", nofak);

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

                    HistoryPenjualan item = new HistoryPenjualan();

                    item.setKdbrg(obj.getString("KdBrg"));
                    item.setNmbrg(obj.getString("NmBrg"));
                    item.setQty(obj.getDouble("Qty"));
                    item.setHarga(obj.getDouble("Hrg"));
                    item.setDiskon1(obj.getDouble("PrsDisc"));
                    item.setDiskon2(obj.getDouble("PrsDisc2"));
                    item.setDiskon3(obj.getDouble("PrsDisc3"));
                    item.setDiscfak(obj.getDouble("PrsDisc1"));

                    //menambah item ke array
                    listData.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //notifikasi adanya perubahan data pada adapter
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class AdapterRVHisPenjItem extends RecyclerView.Adapter <AdapterRVHisPenjItem.ViewHolder> {

        private List<HistoryPenjualan> rvData;
        View view;

        public AdapterRVHisPenjItem (List<HistoryPenjualan> tampung){
            this.rvData = tampung;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //membuat view baru
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_hispenj_item, parent, false);
            //mengeset ukuran view, margin, padding, dan parameter layout
            ViewHolder viewHolder = new ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //mengambil elemen dari arraylist pada posisi yang ditentukan
            //dan memasukkannya sebagai isi dari view recyclerview
            for (int i=0; i<listAkses.size(); i++){
                String str = listAkses.get(i).getModul();
                String akses = str.substring(str.indexOf("-") + 1);
                String modul = str.substring(0 , str.indexOf("-")+1);

                if (akses.equalsIgnoreCase("Harga Jual")) {
                    if (listAkses.get(i).isAkses()) {
                        holder.tv_harga_item.setVisibility(View.VISIBLE);
                    } else {
                        holder.tv_harga_item.setVisibility(View.GONE);
                    }
                }
                if (akses.equalsIgnoreCase("Diskon")) {
                    if (listAkses.get(i).isAkses()) {
                        holder.tv_discfak_item.setVisibility(View.VISIBLE);
                        holder.tv_diskon1_item.setVisibility(View.VISIBLE);
                        holder.tv_diskon2_item.setVisibility(View.VISIBLE);
                        holder.tv_diskon3_item.setVisibility(View.VISIBLE);
                        holder.tv_discfak_header.setVisibility(View.VISIBLE);
                        holder.tv_diskon1_header.setVisibility(View.VISIBLE);
                        holder.tv_diskon2_header.setVisibility(View.VISIBLE);
                        holder.tv_diskon3_header.setVisibility(View.VISIBLE);
                    } else {
                        holder.tv_discfak_item.setVisibility(View.GONE);
                        holder.tv_diskon1_item.setVisibility(View.GONE);
                        holder.tv_diskon2_item.setVisibility(View.GONE);
                        holder.tv_diskon3_item.setVisibility(View.GONE);
                        holder.tv_discfak_header.setVisibility(View.GONE);
                        holder.tv_diskon1_header.setVisibility(View.GONE);
                        holder.tv_diskon2_header.setVisibility(View.GONE);
                        holder.tv_diskon3_header.setVisibility(View.GONE);
                    }
                }
            }

            holder.tv_kdbrg_item.setText(rvData.get(position).getKdbrg());
            holder.tv_nmbrg_item.setText(rvData.get(position).getNmbrg());
            holder.tv_qty.setText(nf.format(rvData.get(position).getQty()));
            holder.tv_harga_item.setText(nf.format(rvData.get(position).getHarga()));

            //set diskon 1
            holder.tv_diskon1_item.setText(nf.format(rvData.get(position).getDiskon1())+" %");
            if (nf.format(rvData.get(position).getDiskon1()).equals("0")){
                holder.tv_diskon1_item.setTextColor(Color.BLACK);
            } else {
                holder.tv_diskon1_item.setTextColor(Color.RED);
            }

            //set diskon 2
            holder.tv_diskon2_item.setText(nf.format(rvData.get(position).getDiskon2())+" %");
            if (nf.format(rvData.get(position).getDiskon2()).equals("0")){
                holder.tv_diskon2_item.setTextColor(Color.BLACK);
            } else {
                holder.tv_diskon2_item.setTextColor(Color.RED);
            }

            //set diskon 3
            holder.tv_diskon3_item.setText(nf.format(rvData.get(position).getDiskon3())+" %");
            if (nf.format(rvData.get(position).getDiskon3()).equals("0")){
                holder.tv_diskon3_item.setTextColor(Color.BLACK);
            } else {
                holder.tv_diskon3_item.setTextColor(Color.RED);
            }

            //set diskon faktur
            holder.tv_discfak_item.setText(nf.format(rvData.get(position).getDiscfak())+" %");
            if (nf.format(rvData.get(position).getDiscfak()).equals("0")){
                holder.tv_discfak_item.setTextColor(Color.BLACK);
            } else {
                holder.tv_discfak_item.setTextColor(Color.RED);
            }
        }

        @Override
        public int getItemCount() {
            //mengembalikan jumlah data yang ada pada list recyclerview
            return rvData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tv_kdbrg_item;
            public TextView tv_nmbrg_item;
            public TextView tv_qty;
            public TextView tv_harga_item;
            public TextView tv_diskon1_header;
            public TextView tv_diskon1_item;
            public TextView tv_diskon2_header;
            public TextView tv_diskon2_item;
            public TextView tv_diskon3_header;
            public TextView tv_diskon3_item;
            public TextView tv_discfak_header;
            public TextView tv_discfak_item;

            public ViewHolder(View view) {
                super(view);
                tv_kdbrg_item = view.findViewById(R.id.tv_kdbrg_item);
                tv_nmbrg_item = view.findViewById(R.id.tv_nmbrg_item);
                tv_qty = view.findViewById(R.id.tv_qty);
                tv_harga_item = view.findViewById(R.id.tv_harga_item);
                tv_diskon1_header = view.findViewById(R.id.tv_diskon1_header);
                tv_diskon1_item = view.findViewById(R.id.tv_diskon1_item);
                tv_diskon2_header = view.findViewById(R.id.tv_diskon2_header);
                tv_diskon2_item = view.findViewById(R.id.tv_diskon2_item);
                tv_diskon3_header = view.findViewById(R.id.tv_diskon3_header);
                tv_diskon3_item = view.findViewById(R.id.tv_diskon3_item);
                tv_discfak_header = view.findViewById(R.id.tv_discfak_header);
                tv_discfak_item = view.findViewById(R.id.tv_discfak_item);
            }
        }
    }

}
