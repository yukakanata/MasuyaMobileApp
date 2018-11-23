package com.yusuffahrudin.masuyamobileapp.sales_order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.controller.MyCallBack;
import com.yusuffahrudin.masuyamobileapp.data.Data;
import com.yusuffahrudin.masuyamobileapp.data.SalesOrder;
import com.yusuffahrudin.masuyamobileapp.util.Server;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TroliActivity extends AppCompatActivity {
    private View view;
    private ListView lv_barang;
    private LinearLayout btn_simpan_item_order;
    private TextView tv_subtotal, tv_total, tv_discfak_total, tv_ppn_total;
    private EditText edt_ppn_persen, edt_discfak_persen;
    private SwipeRefreshLayout swipe_refresh;
    private AdapterLV adapter;
    private ArrayList<Data> listBarang;
    private ArrayList<SalesOrder> listHeader;
    private String status_pajak;
    private NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_troli);
        Toolbar toolbar = findViewById(R.id.toolbar_troli);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity = this;

        Intent i = this.getIntent();
        listHeader = (ArrayList<SalesOrder>) i.getExtras().getSerializable("listHeader");
        listBarang = (ArrayList<Data>) i.getExtras().getSerializable("listCart");
        this.setTitle("Troli ("+listBarang.size()+")");

        //menghubungkan variabel dengan layout view dan java
        swipe_refresh = findViewById(R.id.swipe_refresh);
        lv_barang = findViewById(R.id.lv_barang);
        tv_subtotal = findViewById(R.id.tv_subtotal);
        tv_total = findViewById(R.id.tv_total);
        edt_discfak_persen = findViewById(R.id.edt_discfak_persen);
        tv_discfak_total = findViewById(R.id.tv_discfak_total);
        edt_ppn_persen = findViewById(R.id.edt_ppn_persen);
        tv_ppn_total = findViewById(R.id.tv_ppn_total);
        btn_simpan_item_order = findViewById(R.id.btn_simpan_item_order);

        edt_discfak_persen.setText(String.valueOf(listHeader.get(0).getDisc()));
        status_pajak = listHeader.get(0).getKodeTax();

        //untuk mengisi data dari JSON ke Adapter
        System.out.println("listBarang item "+listBarang.size());
        adapter = new AdapterLV(this);
        lv_barang.setAdapter(adapter);

        edt_ppn_persen.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Double subtotal = 0.0, ppn = 0.0, discfak = 0.0, total = 0.0;
                String angkaYangDiubah;
                //==================== Subtotal ======================
                angkaYangDiubah = ubahAngka(tv_subtotal.getText().toString());
                subtotal = Double.valueOf(angkaYangDiubah);
                //==================== Discfak ======================
                angkaYangDiubah = ubahAngka(tv_discfak_total.getText().toString());
                discfak = Double.valueOf(angkaYangDiubah);
                subtotal = subtotal - discfak;
                //==================== Ppn ======================
                ppn = Double.valueOf(edt_ppn_persen.getText().toString())/100;
                ppn = ppn*subtotal;
                tv_ppn_total.setText(nf.format(ppn));
                //==================== Total ======================
                total = subtotal + ppn;
                tv_total.setText(nf.format(total));
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        edt_discfak_persen.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Double subtotal = 0.0, ppn = 0.0, discfak = 0.0, total = 0.0;
                String angkaYangDiubah;
                //==================== Subtotal ======================
                angkaYangDiubah = ubahAngka(tv_subtotal.getText().toString());
                subtotal = Double.valueOf(angkaYangDiubah);
                //==================== Discfak ======================
                discfak = Double.valueOf(edt_discfak_persen.getText().toString())/100;
                discfak = discfak*subtotal;
                tv_discfak_total.setText(nf.format(discfak));
                subtotal = subtotal - discfak;
                //tv_subtotal.setText(nf.format(subtotal));
                //==================== Ppn ======================
                ppn = Double.valueOf(edt_ppn_persen.getText().toString())/100;
                ppn = ppn*subtotal;
                tv_ppn_total.setText(nf.format(ppn));
                //==================== Total ======================
                total = subtotal + ppn;
                tv_total.setText(nf.format(total));
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        btn_simpan_item_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listHeader.get(0).setSubtotal(Double.valueOf(ubahAngka(tv_subtotal.getText().toString())));
                listHeader.get(0).setDisc(Double.valueOf(edt_discfak_persen.getText().toString()));
                listHeader.get(0).setJmldisc1(Double.valueOf(ubahAngka(tv_discfak_total.getText().toString())));
                listHeader.get(0).setPrsppn(Double.valueOf(edt_ppn_persen.getText().toString()));
                listHeader.get(0).setPpn(Double.valueOf(ubahAngka(tv_ppn_total.getText().toString())));
                listHeader.get(0).setTotal(Double.valueOf(ubahAngka(tv_total.getText().toString())));

                Intent i = new Intent(TroliActivity.this, DrawSignature.class);
                i.putExtra("listHeader", listHeader);
                i.putExtra("listDetail", listBarang);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public class AdapterLV extends BaseAdapter {

        private Activity activity;
        LayoutInflater inflater;
        double qty;

        public AdapterLV(Activity activity){
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return listBarang.size();
        }

        @Override
        public Object getItem(int location) {
            return listBarang.get(location);
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
                convertView = inflater.inflate(R.layout.lv_activity_troli, null);

            TextView tvKdBrg = convertView.findViewById(R.id.tv_kdbrg);
            TextView tvNmBrg = convertView.findViewById(R.id.tv_nmbrg);
            final TextView tvHarga = convertView.findViewById(R.id.tv_harga);
            final EditText edtQty = convertView.findViewById(R.id.edt_qty);
            ImageView btnAdd = convertView.findViewById(R.id.btn_tambah);
            ImageView btnMin = convertView.findViewById(R.id.btn_kurang);
            ImageView btnRemove = convertView.findViewById(R.id.btn_hapus);
            ImageView img_brg = convertView.findViewById(R.id.img_brg);
            final TextView tvDiskon1 = convertView.findViewById(R.id.tv_diskon1);
            final TextView tvDiskon2 = convertView.findViewById(R.id.tv_diskon2);
            final TextView tvDiskon3 = convertView.findViewById(R.id.tv_diskon3);
            final TextView tvSubtotal = convertView.findViewById(R.id.tv_subtotal);

            tvKdBrg.setText(listBarang.get(position).getKdbrg());
            tvNmBrg.setText(listBarang.get(position).getNmbrg());
            edtQty.setText(nf.format(listBarang.get(position).getQty()));
            Double harga = listBarang.get(position).getHarga()*listBarang.get(position).getQty();
            tvHarga.setText(nf.format(harga));
            tvDiskon1.setText(nf.format(listBarang.get(position).getDiskon1())+" %");
            tvDiskon2.setText(nf.format(listBarang.get(position).getDiskon2())+" %");
            tvDiskon3.setText(nf.format(listBarang.get(position).getDiskon3())+" %");
            Double subtotal = harga*(1-listBarang.get(position).getDiskon1()/100)*(1-listBarang.get(position).getDiskon2()/100)*(1-listBarang.get(position).getDiskon3()/100);
            tvSubtotal.setText(nf.format(subtotal));
            listBarang.get(position).setSubtotal(subtotal);

            Server a = new Server("");
            Picasso.get().load(a.URL_IMAGE()+listBarang.get(position).getKdbrg()+".jpg")
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.drawable.img_not_found)
                    .resize(150, 150)
                    .onlyScaleDown()
                    .centerInside()
                    .into(img_brg);

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    qty = Double.parseDouble(edtQty.getText().toString()) + 1;
                    edtQty.setText(nf.format(qty));
                    tvHarga.setText(nf.format(listBarang.get(position).getHarga()*qty));
                    tvSubtotal.setText(nf.format(HitungSubtotalItem(listBarang.get(position).getHarga(), qty, listBarang.get(position).getDiskon1(), listBarang.get(position).getDiskon2(), listBarang.get(position).getDiskon3())));
                    listBarang.get(position).setSubtotal(Double.valueOf(ubahAngka(tvSubtotal.getText().toString())));
                    listBarang.get(position).setQty(qty);
                    adapter.notifyDataSetChanged();
                    HitungTotal(listBarang);
                }
            });
            btnMin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    qty = Double.parseDouble(edtQty.getText().toString()) - 1;
                    edtQty.setText(nf.format(qty));
                    tvHarga.setText(nf.format(listBarang.get(position).getHarga()*qty));
                    tvSubtotal.setText(nf.format(HitungSubtotalItem(listBarang.get(position).getHarga(), qty, listBarang.get(position).getDiskon1(), listBarang.get(position).getDiskon2(), listBarang.get(position).getDiskon3())));
                    listBarang.get(position).setSubtotal(Double.valueOf(ubahAngka(tvSubtotal.getText().toString())));
                    listBarang.get(position).setQty(qty);
                    adapter.notifyDataSetChanged();
                    HitungTotal(listBarang);
                }
            });
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listBarang.remove(position);
                    adapter.notifyDataSetChanged();
                    TroliActivity.this.setTitle("Troli ("+listBarang.size()+")");
                    PilihBarangActivity pilih = ((PilihBarangActivity)getApplicationContext());
                    //pilih.UpdateNotification(listBarang.size());
                    //ListItemOrderActivity.tabLayout.getTabAt(1).setText("Detail Item Order("+String.valueOf(lvBarang.size())+")");
                }
            });
            HitungTotal(listBarang);
            //ListItemOrderActivity.tabLayout.getTabAt(1).setText("Detail Item Order("+String.valueOf(lvBarang.size())+")");

            return convertView;
        }
    }

    private double HitungSubtotalItem(double harga, double qty, double disc1, double disc2, double disc3){
        double subtotal = 0.0;
        harga = harga*(1-disc1/100)*(1-disc2/100)*(1-disc3/100);
        subtotal = harga * qty;
        return subtotal;
    }

    private void HitungTotal(List<Data> listTemp){
        Double subtotal = 0.0, ppn = 0.0, discfak = 0.0, total = 0.0, harga = 0.0;
        //==================== Subtotal ======================
        for (int i=0; i<listTemp.size(); i++){
            //harga = listTemp.get(i).getHarga()*listTemp.get(i).getQty();
            subtotal = subtotal + listTemp.get(i).getSubtotal();
        }
        tv_subtotal.setText(nf.format(subtotal));
        //==================== Discfak ======================
        discfak = Double.valueOf(edt_discfak_persen.getText().toString())/100;
        discfak = discfak*subtotal;
        tv_discfak_total.setText(nf.format(discfak));
        subtotal = subtotal - discfak;
        //==================== Ppn ======================
        if (status_pajak.equalsIgnoreCase("01")){
            edt_ppn_persen.setText("10.0");
            ppn = Double.valueOf(edt_ppn_persen.getText().toString())/100;
            ppn = ppn*subtotal;
            tv_ppn_total.setText(nf.format(ppn));
        } else {
            edt_ppn_persen.setText("0.0");
            ppn = Double.valueOf(edt_ppn_persen.getText().toString())/100;
            ppn = ppn*subtotal;
            tv_ppn_total.setText(nf.format(ppn));
        }
        //==================== Total ======================
        total = subtotal + ppn;
        tv_total.setText(nf.format(total));
    }

    private String ubahAngka (String angka){
        String buangRibuan = angka.replace(".","");
        String gantiKoma = buangRibuan.replace(",",".");

        return gantiKoma;
    }
}
