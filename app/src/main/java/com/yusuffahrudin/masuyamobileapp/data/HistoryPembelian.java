package com.yusuffahrudin.masuyamobileapp.data;

/**
 * Created by yusuf fahrudin on 16-02-2017.
 */

public class HistoryPembelian {
    private String kdbrg, nmbrg, nmsup = "", tgl, satuan;
    private Double qty, harga;

    public void HistoryPenjualan(String kdbrg, String nmbrg, Double qty, String nmsup, String tgl, String satuan, Double harga){
        this.kdbrg = kdbrg;
        this.nmbrg = nmbrg;
        this.qty = qty;
        this.nmsup = nmsup;
        this.tgl = tgl;
        this.satuan = satuan;
        this.harga = harga;
    }

    public String getNmsup() {
        return nmsup;
    }

    public void setNmsup(String nmsup) {
        this.nmsup = nmsup;
    }

    public Double getHarga() {
        return harga;
    }

    public void setHarga(Double harga) {
        this.harga = harga;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getKdbrg() {
        return kdbrg;
    }

    public void setKdbrg(String kdbrg) {
        this.kdbrg = kdbrg;
    }

    public String getNmbrg() {
        return nmbrg;
    }

    public void setNmbrg(String nmbrg) {
        this.nmbrg = nmbrg;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }
}
