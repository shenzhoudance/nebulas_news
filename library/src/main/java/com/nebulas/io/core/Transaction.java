package com.nebulas.io.core;


import com.google.protobuf.ByteString;
import com.nebulas.io.crypto.hash.Hash;
import com.nebulas.io.crypto.keystore.Algorithm;
import com.nebulas.io.crypto.keystore.Signature;
import com.nebulas.io.util.ByteUtils;

import java.math.BigInteger;


public class Transaction {

    // TransactionMaxGasPrice max gasPrice:1 * 10 ** 12
    public static final BigInteger TransactionMaxGasPrice = new BigInteger("1000000000000");

    // TransactionMaxGas max gas:50 * 10 ** 9
    public static final BigInteger TransactionMaxGas = new BigInteger("50000000000");

    // TransactionGasPrice default gasPrice : 10**6
    public static final BigInteger TransactionGasPrice = new BigInteger("1000000");

    // MinGasCountPerTransaction default gas for normal transaction
    public static final BigInteger MinGasCountPerTransaction = new BigInteger("20000");

    // GasCountPerByte per byte of data attached to a transaction gas cost
    public static final BigInteger GasCountPerByte = new BigInteger("1");

    // MaxDataPayLoadLength Max data length in transaction
    public static final int MaxDataPayLoadLength = 1024 * 1024;

    // MaxDataBinPayloadLength Max data length in binary transaction
    public static final int MaxDataBinPayloadLength = 64;

    private int chainID;
    private byte[] hash;

    private Address from;
    private Address to;
    private BigInteger value;

    private long nonce;
    private long timestamp;

    private TransactionOuterClass.Data data;

    private Transaction() {
        // local constructor
    }

    private BigInteger gasPrice;
    private BigInteger gasLimit;

    private Algorithm alg;
    private byte[] sign;

    public enum PayloadType {
        BINARY("binary"),
        DEPLOY("deploy"),
        CALL("call");

        private String type;

        PayloadType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    public Transaction(int chainID, Address from, Address to, BigInteger value, long nonce, PayloadType payloadType, byte[] payload, BigInteger gasPrice, BigInteger gasLimit) throws Exception {
        if (gasPrice.compareTo(TransactionMaxGasPrice) > 0) {
            throw new Exception("invalid gasPrice");
        }
        if (gasPrice.compareTo(TransactionMaxGas) > 0) {
            throw new Exception("invalid gasLimit");
        }

        if ( payload != null && payload.length > MaxDataPayLoadLength) {
            throw new Exception("payload data length is out of max length");
        }

        this.chainID = chainID;
        this.from = from;
        this.to = to;
        this.value = value;
        this.nonce = nonce;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.timestamp = System.currentTimeMillis()/1000;

        TransactionOuterClass.Data.Builder builder = TransactionOuterClass.Data.newBuilder();
        builder.setPayloadType(payloadType.getType());
        if (payload != null && payload.length > 0) {
            builder.setPayload(ByteString.copyFrom(payload));
        }
        this.data = builder.build();
    }

    public static Transaction FromProto(byte[] msg) throws Exception {
        Transaction tx = new Transaction();
        tx.fromProto(msg);
        return tx;
    }

    public void fromProto(byte[] msg) throws Exception {
        TransactionOuterClass.Transaction t = TransactionOuterClass.Transaction.parseFrom(msg);
        this.setHash(t.getHash().toByteArray());
        this.setFrom(Address.ParseFromBytes(t.getFrom().toByteArray()));
        this.setChainID(t.getChainId());

        BigInteger gasPrice = new BigInteger(1, t.getGasPrice().toByteArray());
        if (gasPrice.compareTo(TransactionMaxGasPrice) > 0) {
            throw new Exception("invalid gasPrice");
        }
        this.setGasPrice(gasPrice);

        BigInteger gasLimit = new BigInteger(1, t.getGasLimit().toByteArray());
        if (gasPrice.compareTo(TransactionMaxGas) > 0) {
            throw new Exception("invalid gasLimit");
        }
        this.setGasLimit(gasLimit);
        this.setNonce(t.getNonce());
        this.setAlg(Algorithm.FromType(t.getAlg()));
        this.setSign(t.getSign().toByteArray());
        this.setTimestamp(t.getTimestamp());
        this.setTo(Address.ParseFromBytes(t.getTo().toByteArray()));
        this.setValue(new BigInteger(1, t.getValue().toByteArray()));

        if (t.getData() == null) {
            throw new Exception("invalid transaction data");
        }
        if (t.getData().getPayload().toByteArray().length > MaxDataPayLoadLength) {
            throw new Exception("payload data length is out of max length");
        }

        this.setData(t.getData());
    }


    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public Address getFrom() {
        return from;
    }

    public void setFrom(Address from) {
        this.from = from;
    }

    public Address getTo() {
        return to;
    }

    public void setTo(Address to) {
        this.to = to;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public int getChainID() {
        return chainID;
    }

    public void setChainID(int chainID) {
        this.chainID = chainID;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }

    public Algorithm getAlg() {
        return alg;
    }

    public void setAlg(Algorithm alg) {
        this.alg = alg;
    }

    public byte[] getSign() {
        return sign;
    }

    public void setSign(byte[] sign) {
        this.sign = sign;
    }

    public TransactionOuterClass.Data getData() {
        return data;
    }

    public void setData(TransactionOuterClass.Data data) {
        this.data = data;
    }


    public byte[] toProto() throws Exception {
        TransactionOuterClass.Transaction.Builder builder = TransactionOuterClass.Transaction.newBuilder();
        builder.setAlg(this.getAlg().getType());
        builder.setChainId(this.getChainID());
        builder.setFrom(ByteString.copyFrom(this.getFrom().bytes()));
        builder.setTo(ByteString.copyFrom(this.getTo().bytes()));
        builder.setValue(ByteString.copyFrom(ByteUtils.ToFixedSizeBytes(this.getValue().toByteArray(), 16)));
        builder.setGasLimit(ByteString.copyFrom(ByteUtils.ToFixedSizeBytes(this.getGasLimit().toByteArray(), 16)));
        builder.setGasPrice(ByteString.copyFrom(ByteUtils.ToFixedSizeBytes(this.getGasPrice().toByteArray(), 16)));
        builder.setNonce(this.getNonce());
        builder.setHash(ByteString.copyFrom(this.getHash()));
        builder.setSign(ByteString.copyFrom(this.getSign()));
        builder.setTimestamp(this.getTimestamp());
        builder.setData(this.getData());

        TransactionOuterClass.Transaction t = builder.build();
        return t.toByteArray();
    }

    public byte[] calculateHash() {
        byte[] hash = Hash.Sha3256(
                this.from.bytes(),
                this.to.bytes(),
                ByteUtils.BigIntegerToBytes(this.value, 16),
                ByteUtils.LongToBytes(this.nonce),
                ByteUtils.LongToBytes(this.timestamp),
                this.data.toByteArray(),
                ByteUtils.IntToBytes(this.chainID),
                ByteUtils.BigIntegerToBytes(this.gasPrice, 16),
                ByteUtils.BigIntegerToBytes(this.gasLimit, 16)
        );
        this.hash = hash;
        return this.hash;
    }



    public void sign(Signature signature) throws Exception {
        // calculate hash
        this.calculateHash();

        byte[] sign = signature.sign(this.hash);
        this.alg = signature.algorithm();
        this.sign = sign;
    }
}
