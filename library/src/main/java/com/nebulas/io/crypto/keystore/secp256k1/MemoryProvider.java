package com.nebulas.io.crypto.keystore.secp256k1;

import com.nebulas.io.crypto.cipher.Cipher;
import com.nebulas.io.crypto.cipher.CryptoJSON;
import com.nebulas.io.crypto.keystore.Algorithm;
import com.nebulas.io.crypto.keystore.Key;
import com.nebulas.io.crypto.keystore.KeyItem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryProvider {

    Map<String, Entry> entries;

    Cipher cipher;

    private class Entry {

        Key key;
        CryptoJSON data;

        public Entry(Key key, CryptoJSON data) {
            this.key = key;
            this.data = data;
        }

        public Key getKey() {
            return key;
        }

        public CryptoJSON getData() {
            return data;
        }
    }

    public MemoryProvider(Algorithm algorithm) throws Exception {
        this.cipher = new Cipher(algorithm);
        this.entries = new ConcurrentHashMap<>();
    }

    public void setKey(KeyItem item) throws Exception {
        if (item.address.length() == 0) {
            throw new Exception("invalid key alias");
        }
        if (item.passphrase.length == 0) {
            throw new Exception("invalid passphrase");
        }
        CryptoJSON data = this.cipher.encrypt(item.key.encode(), item.passphrase);

        Entry entry = new Entry(item.key, data);

        this.entries.put(item.address, entry);
    }

    public Key getKey(String address, byte[] passphrase) throws Exception {
        if (address.length() == 0) {
            throw new Exception("invalid key alias");
        }
        if (passphrase.length == 0) {
            throw new Exception("invalid passphrase");
        }

        Entry entry = this.entries.get(address);
        if (entry == null) {
            throw new Exception("key not found");
        }
        byte[] data = this.cipher.decrypt(entry.getData(), passphrase);
        entry.getKey().decode(data);
        return entry.getKey();
    }


    public void clear() {
        this.entries.clear();
    }
}
