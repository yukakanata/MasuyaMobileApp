package com.yusuffahrudin.masuyamobileapp.data;

/**
 * Created by yusuf fahrudin on 16-05-2017.
 */

public class Customer {
    private String kdcust, nmcust, typecust, kdkel, alm1, alm2, alm3, kota, telp1, telp2, koordinat, sales, kdsales;
    private Double saldo;

    public void Customer(String kdcust, String nmcust, String typecust, String kdkel, String alm1, String alm2, String alm3, String kota, String telp1, String telp2, String koordinat, Double saldo, String sales, String kdsales) {
        this.kdcust = kdcust;
        this.nmcust = nmcust;
        this.typecust = typecust;
        this.kdkel = kdkel;
        this.alm1 = alm1;
        this.alm2 = alm2;
        this.alm3 = alm3;
        this.kota = kota;
        this.telp1 = telp1;
        this.telp2 = telp2;
        this.koordinat = koordinat;
        this.saldo = saldo;
        this.sales = sales;
        this.kdsales = kdsales;
    }

    public String getKdkel() {
        return kdkel;
    }

    public void setKdkel(String kdkel) {
        this.kdkel = kdkel;
    }

    public String getAlm2() {
        return alm2;
    }

    public void setAlm2(String alm2) {
        this.alm2 = alm2;
    }

    public String getAlm3() {
        return alm3;
    }

    public void setAlm3(String alm3) {
        this.alm3 = alm3;
    }

    public String getKdsales() {
        return kdsales;
    }

    public void setKdsales(String kdsales) {
        this.kdsales = kdsales;
    }

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }

    public String getTelp2() {
        return telp2;
    }

    public void setTelp2(String telp2) {
        this.telp2 = telp2;
    }

    public String getKdcust() {
        return kdcust;
    }

    public void setKdcust(String kdcust) {
        this.kdcust = kdcust;
    }

    public String getNmcust() {
        return nmcust;
    }

    public void setNmcust(String nmcust) {
        this.nmcust = nmcust;
    }

    public String getTypecust() {
        return typecust;
    }

    public void setTypecust(String typecust) {
        this.typecust = typecust;
    }

    public String getAlm1() {
        return alm1;
    }

    public void setAlm1(String alm1) {
        this.alm1 = alm1;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getTelp1() {
        return telp1;
    }

    public void setTelp1(String telp1) {
        this.telp1 = telp1;
    }

    public String getKoordinat() {
        return koordinat;
    }

    public void setKoordinat(String koordinat) {
        this.koordinat = koordinat;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }
}
