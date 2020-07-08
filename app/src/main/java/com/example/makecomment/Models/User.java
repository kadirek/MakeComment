package com.example.iddaa.models;

public class User {
    public String ad, soyad, email, sifre1;
    public Integer kullaniciadi;
    public User(String email, String sifre1) {

    }

    public User(String ad, String soyad, Integer kullaniciadi, String email, String sifre1) {
        this.ad = ad;
        this.soyad = soyad;
        this.kullaniciadi = kullaniciadi;
        this.email = email;
        this.sifre1 = sifre1;
    }
}
