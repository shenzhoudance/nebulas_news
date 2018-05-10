package com.nebulas.io.crypto.keystore;

import com.nebulas.io.crypto.keystore.secp256k1.MemoryProvider;


public class Keystore {

    MemoryProvider provider;

    public Keystore(Algorithm algorithm) throws Exception {
        this.provider = new MemoryProvider(algorithm);
    }

    public void setKey(KeyItem item) throws Exception {
        if (this.provider == null) {
            throw new Exception("invalid provider");
        }

        this.provider.setKey(item);
    }

    public Key getKey(String address, byte[] passphrase) throws Exception {
        if (this.provider == null) {
            throw new Exception("invalid provider");
        }

        return this.provider.getKey(address, passphrase);
    }
}
