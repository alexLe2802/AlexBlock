/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * Alex on the blocks
 */ 

package blockchaintesting;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;



public class Blockchain {
    public static ArrayList<Block> blockchain = new ArrayList<>();
    private static String masterPasswordHash = null; // Hash của mật khẩu chính
    
    
    public static void printBlockchain() {
        System.out.println("\nDanh sách các khối trong blockchain:");
        for (Block block : blockchain) {
            System.out.println("Khối #" + blockchain.indexOf(block));
            System.out.println("Block Address: " + block.blockAddress);
            System.out.println("Data: " + block.getData());
            System.out.println("Hash: " + block.hash);
            System.out.println("Hash của khối trước: " + block.previousHash);
            System.out.println("-----------------------------------");
        }
        System.out.println();
    }
    public static void exportBlockchainToFile(String filename) {
        try {
            FileWriter fileWriter = new FileWriter(filename);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (Block block : blockchain) {
                bufferedWriter.write("Block #" + blockchain.indexOf(block) + "\n");
                bufferedWriter.write("Block Address: " + block.blockAddress + "\n");
                bufferedWriter.write("Data: " + block.getData() + "\n");
                bufferedWriter.write("Hash: " + block.hash + "\n");
                bufferedWriter.write("Previous Hash: " + block.previousHash + "\n");
                bufferedWriter.write("-----------------------------------\n");
            }

            bufferedWriter.close();
            System.out.println("Dữ liệu đã được xuất thành công vào file: " + filename);

        } catch (IOException e) {
            System.out.println("Đã xảy ra lỗi khi ghi vào file.");
            e.printStackTrace();
        }
    }
    
// Chuyển dữ liệu từ khối này sang khối khác
    public static void transferData(int fromBlockIndex, int toBlockIndex) {
        if (fromBlockIndex >= 0 && fromBlockIndex < blockchain.size() &&
            toBlockIndex >= 0 && toBlockIndex < blockchain.size() &&
            fromBlockIndex != toBlockIndex) {

            Block fromBlock = blockchain.get(fromBlockIndex);
            Block toBlock = blockchain.get(toBlockIndex);

            toBlock.setData(fromBlock.getData());
            blockchain.set(toBlockIndex, toBlock);
        } else {
            System.out.println("Chỉ số khối không hợp lệ hoặc không thể chuyển dữ liệu giữa cùng một khối.");
        }
    }
    
    // Thêm nhiều khối liên tiếp vào blockchain và tạo cầu nối với các khối đã đào ở mục 3
    public static void addMultipleBlocks(int numberOfBlocks) {
        String previousHash = blockchain.get(blockchain.size() - 1).hash; // Lấy hash của khối cuối cùng
        for (int i = 0; i < numberOfBlocks; i++) {
            Block newBlock = new Block("Block " + (blockchain.size()), previousHash);
            blockchain.add(newBlock);
            previousHash = newBlock.hash; // Cập nhật hash của khối mới được thêm vào
        }
        System.out.println(numberOfBlocks + " khối đã được thêm vào blockchain.");
    }

    // Phương thức để nhập dữ liệu cho một khối mới từ bàn phím
    public static void inputDataFromConsole() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập dữ liệu cho khối mới: ");
        String data = scanner.nextLine();
        addMultipleBlocks(1); // Thêm một khối mới vào blockchain
        blockchain.get(blockchain.size() - 1).setData(data);
        System.out.println("Khối mới đã được thêm vào blockchain.");
    }

    // Phương thức chỉnh sửa dữ liệu trong khối
    public static void editBlockData(String blockAddress, String newData) {
        Block blockToEdit = null;

        for (Block block : blockchain) {
            if (block.blockAddress.equals(blockAddress)) {
                blockToEdit = block;
                break;  
            }
        }

    if (blockToEdit != null) {
        if ("admin".equals(blockToEdit.blockAddress)) {
            System.out.println("Không thể chỉnh sửa dữ liệu của khối admin.");
        } 
        else {
            blockToEdit.setData(newData);
            System.out.println("Dữ liệu của khối " + blockAddress + " đã được cập nhật.");
        }
    } 
        else {
            System.out.println("Địa chỉ khối không hợp lệ.");
        }
    }

          // Kiểm tra tính hợp lệ của blockchain
    public static boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            // So sánh hash hiện tại và hash được tính toán lại
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }

            // So sánh previous hash và hash của khối trước
            if (!currentBlock.previousHash.equals(previousBlock.hash)) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
        }
        return true;
    }
    
    public static void createAdminBlock(String password) {
    if (blockchain.size() > 0) {
        masterPasswordHash = StringUtil.applySha256(password);
        Block adminBlock = new Block(password, blockchain.get(blockchain.size() - 1).hash);
        adminBlock.blockAddress = "admin";
        blockchain.add(adminBlock);
        System.out.println("Mật khẩu chính đã được tạo và lưu vào block có địa chỉ 'admin'.");
    } else {
        System.out.println("Không thể tạo mật khẩu chính vì blockchain chưa có block genesis.");
    }
    
}

    // Xoá tất cả các khối trong blockchain
    public static void deleteAllBlocks() {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Nhập mật khẩu để xoá tất cả các khối: ");
    String password = scanner.nextLine();

    if (checkMasterPassword(password)) {
        Block adminBlock = null;
        for (Block block : blockchain) {
            if ("admin".equals(block.blockAddress)) {
                adminBlock = block;
                break;
            }
        }

        blockchain.clear();

        if (adminBlock != null) {
            blockchain.add(adminBlock);
        }

        System.out.println("Tất cả các khối đã được xoá ngoại trừ khối 'admin'.");
    } else {
        System.out.println("Mật khẩu không chính xác. Không thể xoá các khối.");
    }
}
     // Xác nhận mật khẩu chính
    public static boolean checkMasterPassword(String password) {
        if (masterPasswordHash == null) {
            System.out.println("Mật khẩu chính chưa được tạo.");
            return false;
        }

        String inputHash = StringUtil.applySha256(password);
        return inputHash.equals(masterPasswordHash);
    }

    // Phương thức main để thực thi chương trình
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Tạo khối genesis
        blockchain.add(new Block("Genesis Block", "0"));
        System.out.println("Khối Genesis được tạo... ");
        blockchain.get(0).hash = blockchain.get(0).calculateHash();

        int choice;
        do {
            System.out.println("\n------ MENU ------");
            System.out.println("1. Chỉnh sửa dữ liệu trong mỗi khối");
            System.out.println("2. Chuyển dữ liệu từ khối này sang khối khác");
            System.out.println("3. Thêm nhiều khối liên tiếp vào blockchain");
            System.out.println("4. Nhập dữ liệu cho một khối mới từ bàn phím");
            System.out.println("5. In ra danh sách các khối trong blockchain");
            System.out.println("6. Kiểm tra tính hợp lệ của blockchain");
            System.out.println("7. Xuất các khối và dữ liệu của chúng ra file txt");
            System.out.println("8. Tạo mật khẩu chính và lưu vào khối có địa chỉ: admin");
            System.out.println("9. Xoá tất cả các khối trong blockchain");
            System.out.println("0. Thoát chương trình");
            System.out.println("Số lượng khối hiện có trong blockchain: " + blockchain.size());
            System.out.print("Chọn chức năng: ");
            choice = scanner.nextInt();

            scanner.nextLine(); // Đọc dòng thừa sau khi đọc số nguyên

            switch (choice) {
                case 1:
                    System.out.print("Nhập địa chỉ của khối cần chỉnh sửa: ");
                    String blockAddress = scanner.nextLine();
                    System.out.print("Nhập dữ liệu mới cho khối: ");
                    String newData = scanner.nextLine();
                    editBlockData(blockAddress, newData);
                    break;
                case 2:
                    System.out.print("Nhập địa của khối nguồn: ");
                    int fromBlockIndex = scanner.nextInt();
                    System.out.print("Nhập địa chỉ của khối đích: ");
                    int toBlockIndex = scanner.nextInt();
                    transferData(fromBlockIndex, toBlockIndex);
                    break;
                case 3:
                    System.out.print("Nhập số lượng khối cần thêm: ");
                    int numberOfBlocks = scanner.nextInt();
                    addMultipleBlocks(numberOfBlocks);
                    break;
                case 4:
                    inputDataFromConsole();
                    break;
                case 5:
                    printBlockchain();
                    break;
                case 6:
                    System.out.println("Blockchain có hợp lệ không? " + isChainValid());
                    break;
                case 7:
                    System.out.print("Nhập tên file xuất ra (bao gồm đuôi .txt): ");
                    String filename = scanner.nextLine();
                    exportBlockchainToFile(filename);
                    break;
                case 8:
                    System.out.print("Nhập mật khẩu chính: ");
                    String masterPassword = scanner.nextLine();
                    createAdminBlock(masterPassword);
                    break;
                case 9:
                    deleteAllBlocks();
                    break;
                case 0:
                    System.out.println("Đã thoát chương trình.");
                    break;
                default:
                    System.out.println("Chức năng không hợp lệ. Vui lòng chọn lại.");
                    break;
            }

        } while (choice != 0);

        scanner.close();
    }
}



    

