package com.nebulas.io.account;


import com.nebulas.io.crypto.cipher.CryptoJSON;

import java.util.UUID;

public class KeyJSON {
    public String address;
    public CryptoJSON crypto;
    public String id;
    public int version;

    public KeyJSON(String address, CryptoJSON crypto) {
        this.address = address;
        this.crypto = crypto;
        this.id = UUID.randomUUID().toString();
        this.version = CryptoJSON.VERSION;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CryptoJSON getCrypto() {
        return crypto;
    }

    public void setCrypto(CryptoJSON crypto) {
        this.crypto = crypto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
