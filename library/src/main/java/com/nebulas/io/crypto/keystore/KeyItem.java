package com.nebulas.io.crypto.keystore;

import com.nebulas.io.crypto.keystore.secp256k1.ECPrivateKey;

/**
 * Created by legend on 2018/5/6.
 */

public class KeyItem {

    public String address;
    public ECPrivateKey key;
    public byte[] passphrase;

    public KeyItem(String address, ECPrivateKey key, byte[] passphrase) {
        this.address = address;
        this.key = key;
        this.passphrase = passphrase;
    }
}
