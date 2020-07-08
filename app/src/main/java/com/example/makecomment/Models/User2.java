package com.example.makecomment.Models;

public class User2{
    public String ad, email;
    public Integer kullaniciadi;
    public User2(String email,String sifre1) {

    }

    public User2(String ad, Integer kullaniciadi, String email) {
        this.ad = ad;
        this.kullaniciadi = kullaniciadi;
        this.email = email;
    }
}
