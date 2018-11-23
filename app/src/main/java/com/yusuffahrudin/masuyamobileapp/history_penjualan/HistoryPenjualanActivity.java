package com.yusuffahrudin.masuyamobileapp.history_penjualan;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class HistoryPenjualanActivity extends AppCompatActivity {

    private LinearLayout btn_date_dari, btn_date_sampai;
    private static TextView tv_tgl_awal, tv_tgl_akhir;
    private Button btn_cari;
    private EditText edt_nmbrg, edt_cust, edt_sales;
    private String nmbrg, customer, sales, from_tgl, to_tgl, name, level;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("History Penjualan");
        setContentView(R.layout.activity_history_penjualan);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        name = user.get(SessionManager.kunci_email);
        level = user.get(SessionManager.level);

        //set date pada button
        btn_date_dari = findViewById(R.id.btn_date_dari);
        btn_date_sampai = findViewById(R.id.btn_date_sampai);
        tv_tgl_awal = findViewById(R.id.tv_tgl_awal);
        tv_tgl_akhir = findViewById(R.id.tv_tgl_akhir);

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        tv_tgl_awal.setText(year + "/" + (month + 1) + "/" + day);
        tv_tgl_akhir.setText(year + "/" + (month + 1) + "/" + day);

        //menghubungkan variabel dengan layout view dan java
        edt_nmbrg = findViewById(R.id.edt_nmbrg);
        edt_cust = findViewById(R.id.edt_cust);
        edt_sales = findViewById(R.id.edt_sales);
        btn_cari = findViewById(R.id.btn_cari);

        if (level.equalsIgnoreCase("Sales")){
            edt_sales.setText(name);
            edt_sales.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    //fungsi untuk membuat dialog datepicker dari
    public static class DatePickerFragmentFrom extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            tv_tgl_awal.setText(year + "/" + (month + 1) + "/" + day);
        }
    }

    //fungsi untuk menampilkan dialog date picker dari
    public void showDatePickerDialogFrom(View v) {
        DialogFragment newFragment = new DatePickerFragmentFrom();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //fungsi untuk membuat dialog datepicker sampai
    public static class DatePickerFragmentTo extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            tv_tgl_akhir.setText(year + "/" + (month + 1) + "/" + day);
        }
    }

    //fungsi untuk menampilkan dialog date picker dari
    public void showDatePickerDialogTo(View v) {
        DialogFragment newFragment = new DatePickerFragmentTo();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //fungsi onclick search
    public void cariHistoryClick(View view){
        nmbrg = edt_nmbrg.getText().toString();
        customer = edt_cust.getText().toString();
        sales = edt_sales.getText().toString();
        from_tgl = tv_tgl_awal.getText().toString();
        to_tgl = tv_tgl_akhir.getText().toString();

        Intent intent = new Intent(this, ListHistoryPenjualanActivity.class);

        intent.putExtra("nmbrg", nmbrg);
        intent.putExtra("customer", customer);
        intent.putExtra("sales", sales);
        intent.putExtra("from_tgl", from_tgl);
        intent.putExtra("to_tgl", to_tgl);

        startActivity(intent);
    }
}
