package com.nebulas.io.crypto.cipher;

public interface Encrypt {

    CryptoJSON encrypt(byte[] data, byte[] passphrase) throws Exception;

    byte[] decrypt(CryptoJSON data, byte[] passphrase) throws Exception;
}
