package com.nebulas.io.account;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.nebulas.io.core.Address;
import com.nebulas.io.core.Transaction;
import com.nebulas.io.crypto.Crypto;
import com.nebulas.io.crypto.cipher.Cipher;
import com.nebulas.io.crypto.cipher.CryptoJSON;
import com.nebulas.io.crypto.keystore.Algorithm;
import com.nebulas.io.crypto.keystore.Key;
import com.nebulas.io.crypto.keystore.KeyItem;
import com.nebulas.io.crypto.keystore.Keystore;
import com.nebulas.io.crypto.keystore.PrivateKey;
import com.nebulas.io.crypto.keystore.Signature;
import com.nebulas.io.crypto.keystore.secp256k1.ECPrivateKey;
import com.nebulas.io.util.JSONUtils;
import com.nebulas.io.util.SharedPreferenceUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountManager {

    static {
        init();
    }

    private static void init() {
        instance().getCurrentAccount();
    }

    private Keystore keystore;


    private Algorithm encryptAlg = Algorithm.SCRYPT;
    private Algorithm signatureAlg = Algorithm.SECP256K1;

    private Map<String, Account> accountMap;

    private static AccountManager accountManager;
    private Account currentAccount;

    public static synchronized AccountManager instance() {
        if (accountManager == null) {
            accountManager = new AccountManager();
        }
        return accountManager;
    }

    public void setCurrentAccount(Account currentAccount, KeyItem item) {
        try {
            this.currentAccount = currentAccount;
            String address = currentAccount.getAddress().string();
            SharedPreferenceUtils.getUserSharedPreferences(ApplicationInstance.instance).edit().putString(currentAccount.getAddress().string(), new Gson().toJson(currentAccount)).apply();
            SharedPreferenceUtils.getCurrentUserSP(ApplicationInstance.instance).edit().putString("currentUser", new Gson().toJson(currentAccount)).apply();
            SharedPreferenceUtils.getKeyMapSP(ApplicationInstance.instance).edit().putString(currentAccount.getAddress().string(), new Gson().toJson(item)).apply();

            initKeystore();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AccountManager() {
        try {
            this.keystore = initKeystore();
            this.accountMap = initAccountMap();
            this.currentAccount = getCurrentAccount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ConcurrentHashMap initAccountMap() {
        ConcurrentHashMap<String,Account> hashMap = new ConcurrentHashMap();
        Map<String, String> map = (Map<String, String>) SharedPreferenceUtils.getUserSharedPreferences(ApplicationInstance.instance).getAll();
        for (String address : map.keySet()) {
            hashMap.put(address, new Gson().fromJson(map.get(address), Account.class));
        }
        return hashMap;
    }

    private Keystore initKeystore() {
        Keystore keystore = null;
        try {
            keystore = new Keystore(this.encryptAlg);
            Map<String, String> map = (Map<String, String>) SharedPreferenceUtils.getKeyMapSP(ApplicationInstance.instance).getAll();
            for (String address : map.keySet()) {
                keystore.setKey(new Gson().fromJson(map.get(address), KeyItem.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keystore;
    }


    public Address newAccount(byte[] passphrase) throws Exception {
        ECPrivateKey privateKey = (ECPrivateKey) Crypto.NewPrivateKey(this.signatureAlg, null);
        return updateAccount(privateKey, passphrase);
    }

    public Account getCurrentAccount() {
        if (currentAccount == null) {
            String accountStr = SharedPreferenceUtils.getCurrentUserSP(ApplicationInstance.instance).getString("currentUser", null);
            if (!TextUtils.isEmpty(accountStr)) {
                Account account = new Gson().fromJson(accountStr, Account.class);
                currentAccount = account;
            }
        }
        return currentAccount;
    }

    private Address updateAccount(ECPrivateKey privateKey, byte[] passphrase) throws Exception {
        byte[] pub = privateKey.publickey().encode();
        Address address = Address.NewAddressFromPubKey(pub);
        KeyItem keyItem = new KeyItem(address.string(), privateKey, passphrase);
        this.keystore.setKey(keyItem);
        privateKey.clear();

        Account account = this.accountMap.get(address.string());
        if (account == null) {
            account = new Account(address);
            account.setKeyJson((export(address, passphrase)));
            this.accountMap.put(address.string(), account);
        }

        setCurrentAccount(account,keyItem);
        return address;
    }

    public Address load(byte[] keyData, byte[] passphrase)  {
        try {
            String keyStr = new String(keyData);
            KeyJSON keyJSON = JSONUtils.Parse(keyStr, KeyJSON.class);

            Cipher cipher = new Cipher(this.encryptAlg);
            byte[] key = cipher.decrypt(keyJSON.crypto, passphrase);
            ECPrivateKey privateKey = (ECPrivateKey) Crypto.NewPrivateKey(this.signatureAlg, key);
            return updateAccount(privateKey, passphrase);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String export(Address address, byte[] passphrase)  {
        try {
            Key key = keystore.getKey(address.string(), passphrase);
            Cipher cipher = new Cipher(this.encryptAlg);
            CryptoJSON cryptoJSON = cipher.encrypt(key.encode(), passphrase);

            KeyJSON keyJSON = new KeyJSON(address.string(), cryptoJSON);
            return JSONUtils.Stringify(keyJSON);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void signTransaction(Transaction transaction, byte[] passphrase) throws Exception {
        Key key = keystore.getKey(transaction.getFrom().string(), passphrase);
        Signature signature = Crypto.NewSignature(this.signatureAlg);
        signature.initSign((PrivateKey) key);
        transaction.sign(signature);
    }

}
