/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blockchaintesting;
import java.util.Date;

public class Block {
    public String hash;
    public String previousHash;
    String data; // dữ liệu khối
    private long timeStamp; // thời gian khối được tạo
    public String blockAddress; // Địa chỉ của khối

    // Constructor
    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash(); // Hash của khối này
        this.blockAddress = StringUtil.applySha256(Long.toString(timeStamp) + data); // Tạo địa chỉ của khối
    }


    // Tính toán hash của khối
    public String calculateHash() {
        String input = previousHash + Long.toString(timeStamp) + data;
        return StringUtil.applySha256(input);
    }
    
    // Getter cho dữ liệu
    public String getData() {
        return data;
    }
    
    // Setter cho dữ liệu
    public void setData(String data) {
        this.data = data;
        this.hash = calculateHash(); // Tính lại hash khi dữ liệu thay đổi
    }
}
