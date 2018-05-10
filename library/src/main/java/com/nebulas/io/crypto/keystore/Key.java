package com.nebulas.io.crypto.keystore;

public interface Key {

    Algorithm algorithm();

    byte[] encode() throws Exception;

    void decode(byte[] data) throws Exception;
}
