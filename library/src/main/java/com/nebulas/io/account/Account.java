package com.nebulas.io.account;


import com.nebulas.io.core.Address;

public class Account {

    private Address address;
    public String keyJson;

    public String getKeyJson() {
        return keyJson;
    }

    public void setKeyJson(String keyJson) {
        this.keyJson = keyJson;
    }

    public Account(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
