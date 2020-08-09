package com.example.makecomment.Models;

public class User {
    public String ad, soyad, email, sifre1;
    public String instaUserName;
    public User(String email, String sifre1) {

    }

    public User(String ad, String soyad, String instaUserName, String email, String sifre1) {
        this.ad = ad;
        this.soyad = soyad;
        this.instaUserName = instaUserName;
        this.email = email;
        this.sifre1 = sifre1;
    }
}
