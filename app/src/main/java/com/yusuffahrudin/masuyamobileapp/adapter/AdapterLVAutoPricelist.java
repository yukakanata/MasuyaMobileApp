package com.yusuffahrudin.masuyamobileapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.data.HistoryPenjualan;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by yusuf fahrudin on 16-02-2017.
 */

public class AdapterLVAutoPricelist extends BaseAdapter {

    private List<HistoryPenjualan> lvData;
    private Activity activity;
    LayoutInflater inflater;

    public AdapterLVAutoPricelist(Activity activity, List<HistoryPenjualan> tampung){
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
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.rv_update_price_cust_layout, null);

        TextView tvKdBrg = convertView.findViewById(R.id.tv_kdbrg);
        TextView tvNmBrg = convertView.findViewById(R.id.tv_nmbrg);
        TextView tvTgl = convertView.findViewById(R.id.tv_tgl_update);
        TextView tvSatuan = convertView.findViewById(R.id.tv_satuan);
        TextView tvHrg = convertView.findViewById(R.id.tv_harga);
        TextView tvHrgIncPpn = convertView.findViewById(R.id.tv_hrg_inc_ppn);
        TextView tvDiskon1 = convertView.findViewById(R.id.tv_diskon1);
        TextView tvDiskon2 = convertView.findViewById(R.id.tv_diskon2);
        TextView tvDiskon3 = convertView.findViewById(R.id.tv_diskon3);
        CardView cvList = convertView.findViewById(R.id.cv_main);

        HistoryPenjualan data = lvData.get(position);

        NumberFormat nf = NumberFormat.getInstance();
        tvKdBrg.setText(data.getKdbrg());
        tvNmBrg.setText(data.getNmbrg());
        tvTgl.setText(data.getTgl());
        tvSatuan.setText(data.getSatuan());

        Log.v(AdapterLVAutoPricelist.class.getSimpleName(), "Harga adapter : "+ nf.format(data.getHarga()));
        tvHrg.setText(nf.format(data.getHarga()));
        tvHrgIncPpn.setText(nf.format(data.getHargaincppn()));
        tvDiskon1.setText(nf.format(data.getDiskon1()));
        tvDiskon2.setText(nf.format(data.getDiskon2()));
        tvDiskon3.setText(nf.format(data.getDiskon3()));

        return convertView;
    }
}
