package blockchaintesting;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

class Block implements Serializable {
    public String hash;
    public String previousHash;
    private String data; // dữ liệu của khối
    public long timeStamp; // thời gian tạo khối
    public int nonce;
    public String blockAddress; // địa chỉ khối

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = System.currentTimeMillis();
        this.hash = calculateHash();
        this.blockAddress = StringUtil.applySha256(Long.toString(timeStamp) + data); // Tạo địa chỉ của khối
    }

    public String calculateHash() {
        String calculatedhash = StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        data
        );
        return calculatedhash;
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0'); // Tạo chuỗi đích với độ khó yêu cầu
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        this.hash = calculateHash(); // Cập nhật lại hash sau khi thay đổi dữ liệu
    }

}