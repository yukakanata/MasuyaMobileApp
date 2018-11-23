package com.yusuffahrudin.masuyamobileapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.informasi_barang.ShowProduk;

import java.util.ArrayList;

/**
 * Created by yusuf fahrudin on 05-05-2017.
 */

public class AdapterVPFotoProdukKecil extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    ArrayList<String> arrayFoto = new ArrayList<>();
    String kdbrg;

    public AdapterVPFotoProdukKecil(Context context, ArrayList<String> arrayFoto, String kdbrg) {
        this.context = context;
        this.arrayFoto = arrayFoto;
        this.kdbrg = kdbrg;
    }

    @Override
    public int getCount() {
        return arrayFoto.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, container, false);
        PhotoView imageView = view.findViewById(R.id.imageView);

        Picasso.get().load(arrayFoto.get(position))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.img_not_found)
                .resize(300, 400)
                .onlyScaleDown()
                .centerInside()
                .into(imageView);

        container.addView(view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowProduk.class);
                intent.putExtra("kdbrg", kdbrg);
                intent.putExtra("posisi", position);
                context.startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }
}
