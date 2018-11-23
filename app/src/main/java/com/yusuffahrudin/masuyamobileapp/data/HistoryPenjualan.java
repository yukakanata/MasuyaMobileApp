package com.yusuffahrudin.masuyamobileapp.data;

/**
 * Created by yusuf fahrudin on 16-02-2017.
 */

public class HistoryPenjualan {
    private String kdbrg, nmbrg, nmcust = "", tgl, cetak, kirim, nofaktur, satuan, penyiapan, diterima, kembali, nopo;
    private Double qty, harga, hargaincppn, diskon1, diskon2, diskon3, discfak;

    public void HistoryPenjualan(String kdbrg, String nmbrg, Double qty, String nmcust, String tgl, String cetak, String noPO,
                                 String kirim, String kembali, String nofaktur, String satuan, Double harga, Double hargaincppn,
                                 Double diskon1, Double diskon2, Double diskon3, Double discfak, String penyiapan, String diterima){
        this.kdbrg = kdbrg;
        this.nmbrg = nmbrg;
        this.qty = qty;
        this.satuan = satuan;
        this.nmcust = nmcust;
        this.tgl = tgl;
        this.nofaktur = nofaktur;
        this.cetak = cetak;
        this.kirim = kirim;
        this.harga = harga;
        this.hargaincppn = hargaincppn;
        this.diskon1 = diskon1;
        this.diskon2 = diskon2;
        this.diskon3 = diskon3;
        this.discfak = discfak;
        this.penyiapan = penyiapan;
        this.diterima = diterima;
        this.kembali = kembali;
        this.nopo = noPO;
    }

    public String getNopo() {
        return nopo;
    }

    public void setNopo(String nopo) {
        this.nopo = nopo;
    }

    public String getPenyiapan() {
        return penyiapan;
    }

    public void setPenyiapan(String penyiapan) {
        this.penyiapan = penyiapan;
    }

    public String getDiterima() {
        return diterima;
    }

    public void setDiterima(String diterima) {
        this.diterima = diterima;
    }

    public String getKembali() {
        return kembali;
    }

    public void setKembali(String kembali) {
        this.kembali = kembali;
    }

    public String getNmcust() {
        return nmcust;
    }

    public void setNmcust(String nmcust) {
        this.nmcust = nmcust;
    }

    public String getCetak() {
        return cetak;
    }

    public void setCetak(String cetak) {
        this.cetak = cetak;
    }

    public String getKirim() {
        return kirim;
    }

    public void setKirim(String kirim) {
        this.kirim = kirim;
    }

    public String getNofaktur() {
        return nofaktur;
    }

    public void setNofaktur(String nofaktur) {
        this.nofaktur = nofaktur;
    }

    public Double getHarga() {
        return harga;
    }

    public void setHarga(Double harga) {
        this.harga = harga;
    }

    public Double getDiskon1() {
        return diskon1;
    }

    public void setDiskon1(Double diskon1) {
        this.diskon1 = diskon1;
    }

    public Double getDiskon2() {
        return diskon2;
    }

    public void setDiskon2(Double diskon2) {
        this.diskon2 = diskon2;
    }

    public Double getDiskon3() {
        return diskon3;
    }

    public void setDiskon3(Double diskon3) {
        this.diskon3 = diskon3;
    }

    public Double getDiscfak() {
        return discfak;
    }

    public void setDiscfak(Double discfak) {
        this.discfak = discfak;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
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

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public Double getHargaincppn() {
        return hargaincppn;
    }

    public void setHargaincppn(Double hargaincppn) {
        this.hargaincppn = hargaincppn;
    }
}
