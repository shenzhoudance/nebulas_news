package com.nebulas.io.crypto.keystore.secp256k1;

import com.nebulas.io.crypto.keystore.Algorithm;
import com.nebulas.io.crypto.keystore.PublicKey;
import com.nebulas.io.crypto.util.Utils;


public class ECPublicKey implements PublicKey {
    byte[] pubKey;

    public ECPublicKey(byte[] pub) {
        this.pubKey = pub;
    }

    @Override
    public Algorithm algorithm() {
        return Algorithm.SECP256K1;
    }

    @Override
    public byte[] encode() throws Exception {
        return this.pubKey;
    }

    @Override
    public void decode(byte[] data) throws Exception {
        this.pubKey = data;
    }

    @Override
    public void clear() {
        Utils.ClearBytes(this.pubKey);
    }

    @Override
    public boolean verify(byte[] data, byte[] signature) throws Exception {
        return Secp256k1.Verify(data, signature, pubKey);
    }
}
