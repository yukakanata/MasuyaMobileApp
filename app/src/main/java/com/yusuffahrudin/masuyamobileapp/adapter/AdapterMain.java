package com.yusuffahrudin.masuyamobileapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.customer.CustomerActivity;
import com.yusuffahrudin.masuyamobileapp.data.ArrayTampung;
import com.yusuffahrudin.masuyamobileapp.data.Opname;
import com.yusuffahrudin.masuyamobileapp.data.User;
import com.yusuffahrudin.masuyamobileapp.history_pembelian.HistoryPembelianActivity;
import com.yusuffahrudin.masuyamobileapp.history_penjualan.HistoryPenjualanActivity;
import com.yusuffahrudin.masuyamobileapp.informasi_barang.ListBarangActivity;
import com.yusuffahrudin.masuyamobileapp.sales_order.SalesOrderActivity;
import com.yusuffahrudin.masuyamobileapp.stock_opname.StockOpnameActivity;
import com.yusuffahrudin.masuyamobileapp.update_pricelist.UpdatePricelistActivity;
import com.yusuffahrudin.masuyamobileapp.user_manage.UserManageActivity;

import java.util.List;

/**
 * Created by yusuf fahrudin on 14-07-2017.
 */

public class AdapterMain extends RecyclerView.Adapter<AdapterMain.ViewHolder> {
    private Activity activity;
    private String[] gridViewString;
    private TypedArray gridViewImageId;
    List<User> listAkses = ArrayTampung.getListAkses();
    Intent intent;
    int defValue = -1;

    public AdapterMain(Activity activity, String[] gridViewString, TypedArray gridViewImageId) {
        this.activity = activity;
        this.gridViewImageId = gridViewImageId;
        this.gridViewString = gridViewString;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //membuat view baru
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_activity_main, parent, false);
        //mengeset ukuran view, margin, padding, dan parameter layout
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //mengambil elemen dari arraylist pada posisi yang ditentukan
        //dan memasukkannya sebagai isi dari view recyclerview

        holder.img_main.setImageResource(gridViewImageId.getResourceId(position, defValue));
        holder.tv_main.setText(gridViewString[position]);

        // Set onclicklistener pada view cvMain (CardView)
        holder.cv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("tes "+listAkses.get(0).getModul());
                boolean cek = false;
                for (int j=0; j<listAkses.size(); j++){
                    if (listAkses.get(j).getModul().equals(gridViewString[position]) && listAkses.get(j).isAkses()){
                        switch (position){
                            case 0:
                                intent = new Intent(activity, ListBarangActivity.class);
                                activity.startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(activity, CustomerActivity.class);
                                activity.startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(activity, HistoryPenjualanActivity.class);
                                activity.startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(activity, HistoryPembelianActivity.class);
                                activity.startActivity(intent);
                                break;
                            case 4:
                                //intent = new Intent(activity, SalesOrderActivity.class);
                                //activity.startActivity(intent);
                                Toast.makeText(activity, "Sedang dalam proses development", Toast.LENGTH_SHORT).show();
                                break;
                            case 5:
                                intent = new Intent(activity, UpdatePricelistActivity.class);
                                activity.startActivity(intent);
                                break;
                            case 6:
                                List<Opname> listData = ArrayTampung.getListOpname();
                                listData.clear();
                                intent = new Intent(activity, StockOpnameActivity.class);
                                activity.startActivity(intent);
                                break;
                            case 7:
                                intent = new Intent(activity, UserManageActivity.class);
                                activity.startActivity(intent);
                                break;
                            case 8:
                                //intent = new Intent(activity, UserManageActivity.class);
                                //activity.startActivity(intent);
                                break;
                        }
                        cek = true;
                    }
                }

                if (cek == false){
                    Toast.makeText(activity, "Anda tidak mempunyai hak akses", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        //mengembalikan jumlah data yang ada pada list recyclerview
        return gridViewString.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView img_main;
        public TextView tv_main;
        public CardView cv_main;

        public ViewHolder(View itemView) {
            super(itemView);
            img_main = itemView.findViewById(R.id.img_main);
            tv_main = itemView.findViewById(R.id.tv_main);
            cv_main = itemView.findViewById(R.id.cv_main);
        }
    }
}
