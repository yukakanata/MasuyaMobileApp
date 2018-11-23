package com.yusuffahrudin.masuyamobileapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.yusuffahrudin.masuyamobileapp.R;
import com.yusuffahrudin.masuyamobileapp.data.ArrayTampung;
import com.yusuffahrudin.masuyamobileapp.data.HistoryPembelian;
import com.yusuffahrudin.masuyamobileapp.data.User;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yusuf fahrudin on 19-04-2017.
 */

public class AdapterExpandListHistoryPembelian extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<HistoryPembelian>> _listDataChild;
    List<User> listAkses = ArrayTampung.getListAkses();

    public AdapterExpandListHistoryPembelian(Context context, List<String> listDataHeader,
                                             HashMap<String, List<HistoryPembelian>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        HistoryPembelian data = (HistoryPembelian) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_historypemb, null);
        }

        TextView tv_tgl_item = convertView
                .findViewById(R.id.tv_tgl_item);
        TextView tv_kdbrg_item = convertView
                .findViewById(R.id.tv_kdbrg_item);
        TextView tv_nmbrg_item = convertView
                .findViewById(R.id.tv_nmbrg_item);
        TextView tv_qty = convertView
                .findViewById(R.id.tv_qty);
        TextView tv_harga = convertView
                .findViewById(R.id.tv_harga_item);
        TextView tv_satuan = convertView
                .findViewById(R.id.tv_satuan);

        for (int i=0; i<listAkses.size(); i++){
            String str = listAkses.get(i).getModul();
            String akses = str.substring(str.indexOf("-") + 1);
            String modul = str.substring(0 , str.indexOf("-")+1);

            if (akses.equalsIgnoreCase("Harga Beli")){
                if  (listAkses.get(i).isAkses()){
                    tv_harga.setVisibility(View.VISIBLE);
                } else {
                    tv_harga.setVisibility(View.GONE);
                }
            }
        }

        NumberFormat nf = NumberFormat.getInstance();
        tv_kdbrg_item.setText(data.getKdbrg());
        tv_qty.setText(nf.format(data.getQty()));
        tv_harga.setText("Rp "+nf.format(data.getHarga()));
        tv_tgl_item.setText(data.getTgl());
        tv_nmbrg_item.setText(data.getNmbrg());
        tv_satuan.setText(data.getSatuan());

        return convertView;
    }

    @Override
    public int getChildrenCount(int childPosition) {
        return this._listDataChild.get(this._listDataHeader.get(childPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_historypemb, null);
        }

        TextView tv_sup_header = convertView
                .findViewById(R.id.tv_sup_header);

        tv_sup_header.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
