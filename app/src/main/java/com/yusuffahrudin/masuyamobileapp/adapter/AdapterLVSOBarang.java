package com.yusuffahrudin.masuyamobileapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.data.ArrayTampung;
import com.yusuffahrudin.masuyamobileapp.data.Data;
import com.yusuffahrudin.masuyamobileapp.data.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by yusuf fahrudin on 16-02-2017.
 */

public class AdapterLVSOBarang extends BaseAdapter{

    private List<Data> lvBarang;
    private List<Data> lvTemp = ArrayTampung.getListItemOrder();
    private String[] satuanArray;
    private List<String> listSatuan;
    private ArrayAdapter<String> adapterSatuan;
    private Activity activity;
    AlertDialog.Builder dialog;
    View dialogView;
    LayoutInflater inflater;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    String kdbrg, nmbrg, satuan, satuan3, harga, diskon1, diskon2, diskon3;
    double stok, qty, qtykvs3, m3;
    private NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
    private Toast toast;

    public AdapterLVSOBarang(Activity activity, List<Data> tampung){
        this.activity = activity;
        this.lvBarang = tampung;
    }

    @Override
    public int getCount() {
        return lvBarang.size();
    }

    @Override
    public Object getItem(int location) {
        return lvBarang.get(location);
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
            convertView = inflater.inflate(R.layout.lv_barang, null);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        editor = sharedPreferences.edit();

        TextView tvKdBrg = convertView.findViewById(R.id.tv_kdbrg);
        TextView tvNmBrg = convertView.findViewById(R.id.tv_nmbrg);
        TextView tvQty = convertView.findViewById(R.id.tv_qty);
        CardView cv_main = convertView.findViewById(R.id.cv_main);

        tvKdBrg.setText(lvBarang.get(position).getKdbrg());
        tvNmBrg.setText(lvBarang.get(position).getNmbrg());
        String qty = lvBarang.get(position).getQty().toString();
        tvQty.setText(nf.format(Float.parseFloat(qty)));

        cv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kdbrg = lvBarang.get(position).getKdbrg();
                nmbrg = lvBarang.get(position).getNmbrg();
                stok = lvBarang.get(position).getQty();
                satuan = lvBarang.get(position).getSatuan();
                satuan3 = lvBarang.get(position).getSatuan3();
                qtykvs3 = lvBarang.get(position).getQtykvs3();
                harga = lvBarang.get(position).getHarga().toString();
                diskon1 = lvBarang.get(position).getDiskon1().toString();
                diskon2 = lvBarang.get(position).getDiskon2().toString();
                diskon3 = lvBarang.get(position).getDiskon3().toString();
                m3 = lvBarang.get(position).getM3();

                listSatuan = new ArrayList<>();
                listSatuan.add(satuan);
                listSatuan.add("LOAF");
                listSatuan.add("EKOR");
                satuanArray = listSatuan.toArray(new String[listSatuan.size()]);
                DialogQty();
            }
        });

        return convertView;
    }

    // untuk menampilkan dialog password
    private void DialogQty() {
        dialog = new AlertDialog.Builder(activity);
        inflater = activity.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_so_qty_item, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.masuyalogo);
        dialog.setTitle("Qty Item");

        final EditText edt_qty = dialogView.findViewById(R.id.edt_qty);
        final TextView tv_kdbrg = dialogView.findViewById(R.id.tv_kdbrg_item);
        final TextView tv_harga = dialogView.findViewById(R.id.tv_harga_item);
        final Spinner spin_satuan = dialogView.findViewById(R.id.spin_satuan);

        adapterSatuan = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, satuanArray);
        spin_satuan.setAdapter(adapterSatuan);
        tv_kdbrg.setText(kdbrg);
        tv_harga.setText(nf.format(Float.parseFloat(harga)));

        spin_satuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected( AdapterView<?> parent, View view, int position, long id) {
                if (id > 0){
                    tv_harga.setText("0");
                } else {
                    tv_harga.setText(nf.format(Float.parseFloat(harga)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                qty = Double.parseDouble(edt_qty.getText().toString());
                if (qty > stok || stok == 0.0){
                    toast = Toast.makeText(activity, "Stok tidak mencukupi..!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (Double.parseDouble(harga) == 0.0){
                    toast = Toast.makeText(activity, "Harga Barang Rp0 \nInput harga barang di modul Update Pricelist", Toast.LENGTH_LONG);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    if( v != null) v.setGravity(Gravity.CENTER);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Data item = new Data();
                    item.setKdbrg(kdbrg);
                    item.setNmbrg(nmbrg);
                    item.setSatuan(satuan);
                    item.setSatuan3(satuan3);
                    item.setQtykvs3(qtykvs3);
                    item.setQty(qty);
                    item.setHarga(Double.valueOf(harga));
                    item.setDiskon1(Double.valueOf(diskon1));
                    item.setDiskon2(Double.valueOf(diskon2));
                    item.setDiskon3(Double.valueOf(diskon3));
                    item.setM3(m3);

                    lvTemp.add(item);
                    try {
                        EventBus.getDefault().post(new MessageEvent(1));
                    }catch (Exception e){
                        EventBus.getDefault().post(new MessageEvent(0));
                    }
                    dialog.dismiss();
                }
            }
        });

        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
