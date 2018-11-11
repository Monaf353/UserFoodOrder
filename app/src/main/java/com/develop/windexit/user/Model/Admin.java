package com.develop.windexit.user.Model;

/**
 * Created by WINDEX IT on 02-Jun-18.
 */

public class Admin {
    private String email;
    private String Name;
    private String Phone;
    private String shopAddres;

    public Admin() {
    }

    public Admin(String email, String name, String phone, String shopAddres) {
        this.email = email;
        Name = name;
        Phone = phone;
        this.shopAddres = shopAddres;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getShopAddres() {
        return shopAddres;
    }

    public void setShopAddres(String shopAddres) {
        this.shopAddres = shopAddres;
    }
}
