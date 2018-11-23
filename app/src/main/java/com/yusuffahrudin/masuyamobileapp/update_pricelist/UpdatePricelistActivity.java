package com.yusuffahrudin.masuyamobileapp.update_pricelist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.data.ArrayTampung;
import com.yusuffahrudin.masuyamobileapp.data.User;

import java.util.List;

public class UpdatePricelistActivity extends AppCompatActivity {

    List<User> listAkses = ArrayTampung.getListAkses();
    Button btn_update_price_cust, btn_update_price_brg, btn_update_price_auto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Update Pricelist");
        setContentView(R.layout.activity_update_pricelist);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_update_price_cust = findViewById(R.id.btn_update_price_cust);
        btn_update_price_brg = findViewById(R.id.btn_update_price_brg);
        btn_update_price_auto = findViewById(R.id.btn_update_price_auto);
        cekAkses();

        btn_update_price_cust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdatePricelistActivity.this, ListCustomerActivity.class);
                startActivity(intent);
            }
        });
        btn_update_price_brg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdatePricelistActivity.this, AddPriceProduk.class);
                startActivity(intent);
            }
        });
        btn_update_price_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdatePricelistActivity.this, AutoPricelistActivity.class);
                startActivity(intent);
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

    private void cekAkses(){
        for (int i=0; i<listAkses.size(); i++){
            String str = listAkses.get(i).getModul();
            String modul = str.substring(str.indexOf("-") + 1);

            if (modul.equalsIgnoreCase("Customer")){
                if  (listAkses.get(i).isAkses()){
                    btn_update_price_cust.setEnabled(true);
                } else {
                    btn_update_price_cust.setEnabled(false);
                    //Toast.makeText(this, "Anda tidak mempunyai hak akses", Toast.LENGTH_LONG).show();
                }
            }
            if (modul.equalsIgnoreCase("Produk")){
                if  (listAkses.get(i).isAkses()){
                    btn_update_price_brg.setEnabled(true);
                } else {
                    btn_update_price_brg.setEnabled(false);
                    //Toast.makeText(this, "Anda tidak mempunyai hak akses", Toast.LENGTH_LONG).show();
                }
            }
            if (modul.equalsIgnoreCase("History Penjualan")){
                if  (listAkses.get(i).isAkses()){
                    btn_update_price_auto.setEnabled(true);
                } else {
                    btn_update_price_auto.setEnabled(false);
                    //Toast.makeText(this, "Anda tidak mempunyai hak akses", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
