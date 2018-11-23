package com.yusuffahrudin.masuyamobileapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.data.Customer;

import java.util.List;

/**
 * Created by yusuf fahrudin on 16-02-2017.
 */

public class AdapterLVUpdatePriceProduk extends BaseAdapter {

    private List<Customer> lvData;
    private Activity activity;
    LayoutInflater inflater;

    public AdapterLVUpdatePriceProduk(Activity activity, List<Customer> tampung){
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
            convertView = inflater.inflate(R.layout.rv_customer_layout, null);

        TextView tvKdCust = convertView.findViewById(R.id.tv_kdcust);
        TextView tvNmCust = convertView.findViewById(R.id.tv_nmcust);
        CardView cvList = convertView.findViewById(R.id.cv_main);

        Customer data = lvData.get(position);

        tvKdCust.setText(data.getKdcust());
        tvNmCust.setText(data.getNmcust());

        return convertView;
    }
}
