package com.yusuffahrudin.masuyamobileapp.informasi_barang;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.controller.AppController;
import com.yusuffahrudin.masuyamobileapp.controller.DialogAlert;
import com.yusuffahrudin.masuyamobileapp.data.ArrayTampung;
import com.yusuffahrudin.masuyamobileapp.data.User;
import com.yusuffahrudin.masuyamobileapp.util.Server;
import com.yusuffahrudin.masuyamobileapp.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by yusuf fahrudin on 17-01-2018.
 */

public class BarcodeBarangFragment extends Fragment {

    private View view;
    Button btn_scan, btn_simpan_barcode;
    EditText edt_barcode;
    ImageView img_barcode;
    ProgressDialog pDialog;
    Bitmap bitmap;
    String contents, kdbrg, kdkota, barcode;
    Intent intent;
    int success;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    List<User> listAkses = ArrayTampung.getListAkses();
    SessionManager sessionManager;

    private static final String TAG = BarcodeBarangFragment.class.getSimpleName();
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_SUCCESS = "success";
    private static String url_select_barcode_produk;
    private static String url_insert_barcode_produk;
    public static final String TAG_BARCODE = "Barcode";
    String tag_json_obj = "json_obj_req";

    public BarcodeBarangFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // halaman1 merujuk pada file halaman1.xml
        view = inflater.inflate(R.layout.fragment_barcode_barang, container, false);

        sessionManager = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        kdkota = user.get(SessionManager.kdkota);

        Intent i = getActivity().getIntent();
        kdbrg = i.getExtras().getString("kdbrg");

        btn_scan = view.findViewById(R.id.btn_scan);
        btn_simpan_barcode = view.findViewById(R.id.btn_simpan_barcode);
        edt_barcode = view.findViewById(R.id.edt_barcode);
        img_barcode = view.findViewById(R.id.img_barcode);
        cekAkses();

        //action button scan barcode
        btn_scan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try{
                    intent = new Intent(ACTION_SCAN);
                    intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException anfe){
                    showDialog(getActivity(), "Tidak ditemukan scanner barcode!", "Download barcode scan dari playstore?", "Ya", "Tidak").show();
                }
            }
        });

        //action button simpan barcode
        btn_simpan_barcode.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                simpanBarcode();
            }
        });

        getData();

        return view;
    }

    //membuat dialog untuk menampilkan pilihan jika belum menginstall aplikasi scan
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence msg, CharSequence btnYes, CharSequence btnNo) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(btnYes, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:"+"com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try{
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe){

                }
            }
        });

        dialog.setNegativeButton(btnNo, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return dialog.show();
    }

    //menampilkan hasil scan barcode
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        if (requestCode == 0){
            if (resultCode == RESULT_OK){
                contents = intent.getStringExtra("SCAN_RESULT");
                edt_barcode.setText(contents);
            }
        }
    }

    public void getData() {
        Server a = new Server(kdkota);
        url_select_barcode_produk = a.URL() + "masterbrg/select_barcode_produk.php";

        StringRequest strReq = new StringRequest(Request.Method.POST, url_select_barcode_produk, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response);
                showData(response);

            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error : "+ error.getMessage());
                new DialogAlert(error.getMessage(), "error", getActivity());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("kdbrg", kdbrg);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);

    }

    private void showData(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");

            JSONObject obj = result.getJSONObject(0);

            edt_barcode.setText(obj.getString(TAG_BARCODE));
            barcode = edt_barcode.getText().toString();
            new GetBarcode().execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void simpanBarcode(){
        Server a = new Server(kdkota);
        url_insert_barcode_produk = a.URL() + "masterbrg/insert_barcode_produk.php";

        StringRequest strReq = new StringRequest(Request.Method.POST, url_insert_barcode_produk, new Response.Listener<String>(){


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response : " + response);

                try{
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    //cek error node pada JSON
                    if (success == 1){
                        new DialogAlert(jObj.getString(TAG_MESSAGE), "success", getActivity());
                    } else {
                        new DialogAlert(jObj.getString(TAG_MESSAGE), "error", getActivity());
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }

                getData();

            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogAlert(error.getMessage(), "error", getActivity());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                //posting parameter ke post url
                Map<String, String> params = new HashMap<String, String>();

                params.put("kdbrg", kdbrg);
                params.put("barcode", edt_barcode.getText().toString());

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void cekAkses(){
        for (int i=0; i<listAkses.size(); i++){
            String str = listAkses.get(i).getModul();
            String modul = str.substring(str.indexOf("-") + 1);

            if (modul.equalsIgnoreCase("Barcode")){
                if  (listAkses.get(i).isAdd()){
                    btn_simpan_barcode.setVisibility(View.VISIBLE);
                } else {
                    btn_simpan_barcode.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Anda tidak mempunyai hak akses untuk add barcode", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    private class GetBarcode extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Fetching image barcode..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                bitmap = encodeAsBitmap(barcode, BarcodeFormat.EAN_13, 600, 300);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                    img_barcode.setImageBitmap(bitmap);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }
}
