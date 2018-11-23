package com.yusuffahrudin.masuyamobileapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.yusuffahrudin.masuyamobileapp.R;

import java.util.ArrayList;

/**
 * Created by yusuf fahrudin on 05-05-2017.
 */

public class AdapterVPFotoProdukBesar extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    ArrayList<String> arrayFoto = new ArrayList<>();

    public AdapterVPFotoProdukBesar(Context context, ArrayList<String> arrayFoto) {
        this.context = context;
        this.arrayFoto = arrayFoto;
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
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, container, false);
        PhotoView imageView = view.findViewById(R.id.imageView);

        Picasso.get().load(arrayFoto.get(position))
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.img_not_found)
                .resize(768, 1024)
                .onlyScaleDown()
                .centerInside()
                .into(imageView);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }
}
