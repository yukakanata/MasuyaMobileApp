package com.yusuffahrudin.masuyamobileapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.customer.CustomerDetailActivity;
import com.yusuffahrudin.masuyamobileapp.data.Customer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yusuf fahrudin on 16-02-2017.
 */

public class AdapterLVCust extends BaseAdapter implements Filterable {

    private List<Customer> lvCust;
    private List<Customer> lvFilter;
    private Activity activity;
    LayoutInflater inflater;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public AdapterLVCust(Activity activity, List<Customer> tampung){
        this.activity = activity;
        this.lvCust = tampung;
        this.lvFilter = tampung;
    }

    @Override
    public int getCount() {
        return lvCust.size();
    }

    @Override
    public Object getItem(int location) {
        return lvCust.get(location);
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
            convertView = inflater.inflate(R.layout.rv_customer, null);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        editor = sharedPreferences.edit();

        TextView tvKdCust = convertView.findViewById(R.id.tv_kdcust);
        TextView tvNmCust = convertView.findViewById(R.id.tv_nmcust);
        CardView cv_main = convertView.findViewById(R.id.cv_main);

        String kdcust = lvCust.get(position).getKdcust();
        String nmcust = lvCust.get(position).getNmcust();

        tvKdCust.setText(kdcust);
        tvNmCust.setText(nmcust);

        cv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("kdcust", lvCust.get(position).getKdcust());
                editor.putString("nmcust", lvCust.get(position).getNmcust());
                editor.putString("typecust", lvCust.get(position).getTypecust());
                editor.putString("kdkel", lvCust.get(position).getKdkel());
                editor.putString("alm1", lvCust.get(position).getAlm1());
                editor.putString("alm2", lvCust.get(position).getAlm2());
                editor.putString("alm3", lvCust.get(position).getAlm3());
                editor.putString("kota", lvCust.get(position).getKota());
                editor.putString("telp1", lvCust.get(position).getTelp1());
                editor.putString("saldo", lvCust.get(position).getSaldo().toString());
                editor.putString("koordinat", lvCust.get(position).getKoordinat());
                editor.putString("sales", lvCust.get(position).getSales());
                editor.commit();
                Intent intent = new Intent(activity, CustomerDetailActivity.class);
                activity.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    lvCust = lvFilter;
                } else {

                    ArrayList<Customer> filteredList = new ArrayList<>();

                    for (Customer customer : lvCust) {

                        if (customer.getKdcust().toLowerCase().contains(charString) || customer.getNmcust().toLowerCase().contains(charString)) {

                            filteredList.add(customer);
                        }
                    }

                    lvCust = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = lvCust;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                lvCust = (ArrayList<Customer>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
