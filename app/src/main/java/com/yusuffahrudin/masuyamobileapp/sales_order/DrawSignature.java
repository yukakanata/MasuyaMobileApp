package com.yusuffahrudin.masuyamobileapp.sales_order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.data.Data;
import com.yusuffahrudin.masuyamobileapp.data.SalesOrder;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yusuf fahrudin on 07-02-2018.
 */

public class DrawSignature extends AppCompatActivity {
    private GestureOverlayView signaturePad;
    private LinearLayout btn_cancel, btn_done;
    public static final String BITMAP_SIGN = "bitmap_sign";
    private String tag_json_obj = "json_obj_req", kdkota;
    private static String url_insert;
    private static final String TAG = DrawSignature.class.getSimpleName();
    private ArrayList<Data> listBarang;
    private ArrayList<SalesOrder> listHeader;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_signature);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        kdkota = user.get(SessionManager.kdkota);

        Intent i = this.getIntent();
        listHeader = (ArrayList<SalesOrder>) i.getExtras().getSerializable("listHeader");
        listBarang = (ArrayList<Data>) i.getExtras().getSerializable("listDetail");

        signaturePad = findViewById(R.id.signaturePad);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_done = findViewById(R.id.btn_done);

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signaturePad.setDrawingCacheEnabled(true);
                Bitmap bm = Bitmap.createBitmap(signaturePad.getDrawingCache());
                String base64 = bitmapToBase64(bm);

                //Intent intent = new Intent(DrawSignature.this, CreateSalesOrder.class);
                //intent.putExtra(BITMAP_SIGN, base64);
                //setResult(RESULT_OK, intent);
                SaveOrder(base64);
                signaturePad.setDrawingCacheEnabled(false);
                finish();
                TroliActivity.activity.finish();
                PilihBarangActivity.activity.finish();
                CreateSalesOrder.activity.finish();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signaturePad.clear(true);
                Intent intent = new Intent(DrawSignature.this, CreateSalesOrder.class);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }



    private void SaveOrder(final String base64) {
        Server a = new Server(kdkota);
        url_insert = a.URL() + "salesorder/insert_sales_order.php";

        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Please Wait...");
        new Thread() {
            public void run() {
                try{
                    sleep(1000);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }.start();

        StringRequest strReq = new StringRequest(Request.Method.POST, url_insert, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.v(TAG, "Response : " + response);
                try{
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    String message = jObj.getString("message");
                    //cek error node pada JSON
                    if (success == 1){
                        new DialogAlert(message, "success", DrawSignature.this);
                        progressDialog.dismiss();
                    } else {
                        new DialogAlert(message, "error", DrawSignature.this);
                        progressDialog.dismiss();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", DrawSignature.this);
                progressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();
                Double totalM3 = 0.0;

                //JSONArray List Item Order
                JSONArray paramItem = new JSONArray();
                JSONObject arrayItem = null;
                try {
                    for (int i = 0; i < listBarang.size(); i++){

                        arrayItem = new JSONObject();
                        arrayItem.put("kdbrg", listBarang.get(i).getKdbrg());
                        System.out.println(listBarang.get(i).getNmbrg().replace("'","''"));
                        listBarang.get(i).getNmbrg().replace("'","''");
                        arrayItem.put("nmbrg", listBarang.get(i).getNmbrg().replace("'","''"));
                        arrayItem.put("satuan", listBarang.get(i).getSatuan());
                        arrayItem.put("satuan3", listBarang.get(i).getSatuan3());
                        arrayItem.put("qty", listBarang.get(i).getQty());
                        arrayItem.put("qtykvs3", listBarang.get(i).getQtykvs3());
                        arrayItem.put("harga", listBarang.get(i).getHarga());
                        arrayItem.put("diskon1", listBarang.get(i).getDiskon1());
                        arrayItem.put("diskon2", listBarang.get(i).getDiskon2());
                        arrayItem.put("diskon3", listBarang.get(i).getDiskon3());
                        arrayItem.put("m3", listBarang.get(i).getM3());
                        paramItem.put(arrayItem);

                        totalM3 = totalM3 + (listBarang.get(i).getQty()*listBarang.get(i).getM3());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                params.put("no_order", listHeader.get(0).getNobukti());
                params.put("kdcust", listHeader.get(0).getKdcust());
                params.put("nmcust", listHeader.get(0).getNmcust());
                params.put("kdkel", listHeader.get(0).getKdkel());
                params.put("alm1", listHeader.get(0).getAlm1());
                params.put("alm2", listHeader.get(0).getAlm2());
                params.put("alm3", listHeader.get(0).getAlm3());
                params.put("kdsales", listHeader.get(0).getKdsales());
                params.put("pajak", listHeader.get(0).getJnsjualtax());
                params.put("tgl_create", listHeader.get(0).getTgl_create());
                params.put("tgl_kirim", listHeader.get(0).getTgl_kirim());
                params.put("cetak_note", listHeader.get(0).getKet1());
                params.put("kodePO", listHeader.get(0).getNoPO());
                params.put("keterangan", listHeader.get(0).getKet2());
                params.put("kdgd", listHeader.get(0).getKdgd());
                params.put("createby", listHeader.get(0).getCreateby());
                params.put("orderby", listHeader.get(0).getOrderby());
                params.put("subtotal", String.valueOf(listHeader.get(0).getSubtotal()));
                params.put("discfak_persen", String.valueOf(listHeader.get(0).getDisc()));
                params.put("discfak_total", String.valueOf(listHeader.get(0).getJmldisc1()));
                params.put("ppn_persen", String.valueOf(listHeader.get(0).getPrsppn()));
                params.put("ppn_total", String.valueOf(listHeader.get(0).getPpn()));
                params.put("total", String.valueOf(listHeader.get(0).getTotal()));
                if (base64 == null){
                    params.put("signature", "");
                } else {
                    params.put("signature", base64);
                }
                params.put("listItem", paramItem.toString());
                params.put("totalM3", totalM3.toString());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
}
