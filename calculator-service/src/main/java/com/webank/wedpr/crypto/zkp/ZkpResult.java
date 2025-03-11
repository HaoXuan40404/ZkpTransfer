package com.webank.wedpr.crypto.zkp;

public class ZkpResult extends WedprResult {
    public boolean result;
    public byte[] check;
    public byte[] proof;
    public byte[] privatePart;
    public byte[] publicPart;
    public byte[] commitment;
    public byte[] viewkey;


    public ZkpResult expectNoError() throws WedprException {
        if (hasError()) {
            throw new WedprException(wedprErrorMessage);
        }
        return this;
    }
}
