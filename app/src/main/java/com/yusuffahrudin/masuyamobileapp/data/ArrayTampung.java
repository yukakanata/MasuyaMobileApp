package com.yusuffahrudin.masuyamobileapp.data;

import java.util.ArrayList;

/**
 * Created by yusuf fahrudin on 06-06-2017.
 */

public class ArrayTampung {
    private static ArrayList<HistoryPenjualan> listData;
    private static ArrayList<Opname> listOpname;
    private static ArrayList<User> listAkses;
    private static ArrayList<String> listKota;
    private static ArrayList<Data> listItem;
    private static ArrayList<Data> listItemOrder;

    public static ArrayList<Opname> getListOpname() {
        if(listOpname == null) {
            listOpname = new ArrayList<>();
        }
        return listOpname;
    }

    public static ArrayList<HistoryPenjualan> getListData() {
        if(listData == null) {
            listData = new ArrayList<>();
        }
        return listData;
    }

    public static ArrayList<User> getListAkses() {
        if(listAkses == null) {
            listAkses = new ArrayList<>();
        }
        return listAkses;
    }

    public static ArrayList<String> getListKota() {
        if(listKota == null) {
            listKota = new ArrayList<>();
        }
        return listKota;
    }

    public static ArrayList<Data> getListItemOrder() {
        if(listItemOrder == null) {
            listItemOrder = new ArrayList<>();
        }
        return listItemOrder;
    }

    public static ArrayList<Data> getListItem() {
        if(listItem == null) {
            listItem = new ArrayList<>();
        }
        return listItem;
    }
}
