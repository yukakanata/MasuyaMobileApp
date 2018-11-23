package com.yusuffahrudin.masuyamobileapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.data.Customer;
import com.yusuffahrudin.masuyamobileapp.update_pricelist.UpdatePriceCustActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yusuf fahrudin on 16-02-2017.
 */

public class AdapterRVCustUpdatePricelist extends RecyclerView.Adapter <AdapterRVCustUpdatePricelist.ViewHolder> implements Filterable {

    private List<Customer> rvCust;
    private List<Customer> rvFilter;
    private Activity activity;

    public AdapterRVCustUpdatePricelist(Activity activity, List<Customer> tampung){
        this.activity = activity;
        this.rvCust = tampung;
        this.rvFilter = tampung;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //membuat view baru
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_customer_layout, parent, false);
        //mengeset ukuran view, margin, padding, dan parameter layout
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //mengambil elemen dari arraylist pada posisi yang ditentukan
        //dan memasukkannya sebagai isi dari view recyclerview
        final String kdcust = rvCust.get(position).getKdcust();


        holder.tvKdCust.setText(rvCust.get(position).getKdcust());
        holder.tvNmCust.setText(rvCust.get(position).getNmcust());

        // Set onclicklistener pada view cvMain (CardView)
        holder.cvList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, UpdatePriceCustActivity.class);

                intent.putExtra("kdcust", kdcust);

                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        //mengembalikan jumlah data yang ada pada list recyclerview
        return rvCust.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvKdCust;
        public TextView tvNmCust;
        public CardView cvList;

        public ViewHolder(View itemView) {
            super(itemView);
            tvKdCust = itemView.findViewById(R.id.tv_kdcust);
            tvNmCust = itemView.findViewById(R.id.tv_nmcust);
            cvList = itemView.findViewById(R.id.cv_main);
        }
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    rvCust = rvFilter;
                } else {

                    ArrayList<Customer> filteredList = new ArrayList<>();

                    for (Customer customer : rvCust) {

                        if (customer.getKdcust().toLowerCase().contains(charString) || customer.getNmcust().toLowerCase().contains(charString)) {

                            filteredList.add(customer);
                        }
                    }

                    rvCust = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = rvCust;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                rvCust = (ArrayList<Customer>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
