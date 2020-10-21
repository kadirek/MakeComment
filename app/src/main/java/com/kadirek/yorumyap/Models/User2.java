package com.kadirek.yorumyap.Models;

public class User2{
    public String ad, email;
    public String instaUserName;
    public User2(String email,String sifre1) {

    }

    public User2(String ad, String instaUserName, String email) {
        this.ad = ad;
        this.instaUserName = instaUserName;
        this.email = email;
    }
}
