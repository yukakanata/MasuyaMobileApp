package com.yusuffahrudin.masuyamobileapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.data.Customer;
import com.yusuffahrudin.masuyamobileapp.sales_order.CreateSalesOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yusuf fahrudin on 16-02-2017.
 */

public class AdapterLVSOCust extends BaseAdapter implements Filterable {

    private List<Customer> lvCust;
    private List<Customer> lvFilter;
    private Activity activity;
    LayoutInflater inflater;
    public static final String KDCUST = "kdcust";
    public static final String NMCUST = "NmCust";
    public static final String KDKEL = "KdKel";
    public static final String ALM1 = "Alm1";
    public static final String ALM2 = "Alm2";
    public static final String ALM3 = "Alm3";
    public static final String KDSALES = "KdSales";

    public AdapterLVSOCust(Activity activity, List<Customer> tampung){
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
                Intent intent = new Intent(activity.getApplicationContext(), CreateSalesOrder.class);
                intent.putExtra(KDCUST, lvCust.get(position).getKdcust());
                intent.putExtra(NMCUST, lvCust.get(position).getNmcust());
                intent.putExtra(KDKEL, lvCust.get(position).getKdkel());
                intent.putExtra(ALM1, lvCust.get(position).getAlm1());
                intent.putExtra(ALM2, lvCust.get(position).getAlm2());
                intent.putExtra(ALM3, lvCust.get(position).getAlm3());
                intent.putExtra(KDSALES, lvCust.get(position).getKdsales());
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
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

                    for (Customer data : lvCust) {

                        if (data.getKdcust().toLowerCase().contains(charString) || data.getNmcust().toLowerCase().contains(charString)) {

                            filteredList.add(data);
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
