package blockchaintesting;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;


public class BlockchainApp {
    private static ArrayList<Block> blockchain = new ArrayList<>();
    private static int difficulty = 5;
    private static String adminPassword = "defaultPassword";


    public static void main(String[] args) {
        String filename = "blockchain.ser";

        // Tải blockchain từ tệp hoặc khởi tạo blockchain mới nếu không tồn tại
        loadBlockchain(filename);

        // Khởi tạo blockchain nếu chưa có
        initializeBlockchain();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

      // Giao diện menu
        while (running) {
            
            System.out.println("Menu:");
            System.out.println("1. Thêm khối mới với dữ liệu nhập từ bàn phím");
            System.out.println("2. Thêm/Chỉnh sửa dữ liệu trong khối có sẵn");
            System.out.println("3. Chuyển dữ liệu từ khối này sang khối khác");
            System.out.println("4. In ra dữ liệu của một khối");
            System.out.println("5. Thêm nhiều khối liên tiếp");
            System.out.println("6. Xóa tất cả các khối (ngoại trừ khối admin)");
            System.out.println("7. Xuất blockchain ra tệp");
            System.out.println("8. Thay đổi mật khẩu admin");
            System.out.println("9. Tạo lại khối admin");
            System.out.println("0. Thoát");
            System.out.println("Số lượng khối trong blockchain: " + blockchain.size());
            System.out.print("Chọn tùy chọn: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear the newline

            switch (choice) {
                case 1:
                    inputDataFromConsole();
                    saveBlockchain(filename);
                    break;
                case 2:
                    editBlockData(filename, filename);
                    saveBlockchain(filename);
                    break;
                case 3:
                    System.out.print("Nhập địa chỉ khối nguồn: ");
                    String fromAddress = scanner.nextLine();
                    System.out.print("Nhập địa chỉ khối đích: ");
                    String toAddress = scanner.nextLine();
                    transferData(fromAddress, toAddress);
                    saveBlockchain(filename);
                    break;
                case 4:
                    displayBlockData();
                    break;
                case 5:
                    System.out.print("Nhập số lượng khối cần thêm: ");
                    int numberOfBlocks = scanner.nextInt();
                    addMultipleBlocks(numberOfBlocks);
                    saveBlockchain(filename);
                    break;
                case 6:
                    deleteAllBlocks();
                    saveBlockchain(filename);
                    break;
                case 7:
                    exportBlockchainToFile("blockchain_output.txt");
                    break;
                case 8:
                    changeAdminPassword();
                    saveBlockchain(filename);
                    break;
                case 9:
                    recreateAdminBlock();
                    saveBlockchain(filename);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng chọn lại.");
            }
        }
        scanner.close();
    }    
        // Thay đổi mật khẩu admin
    public static void changeAdminPassword() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập mật khẩu hiện tại của admin: ");
        String currentPassword = scanner.nextLine();

        Block adminBlock = null;
        for (Block block : blockchain) {
            if (block.blockAddress.equals("admin")) {
                adminBlock = block;
                break;
            }
        }

        if (adminBlock != null && adminBlock.getData().equals(currentPassword)) {
            System.out.print("Nhập mật khẩu mới: ");
            String newPassword = scanner.nextLine();
            adminBlock.setData(newPassword);
            adminPassword = newPassword; // Cập nhật mật khẩu admin trong chương trình
            System.out.println("Mật khẩu admin đã được thay đổi.");
        } else {
            System.out.println("Mật khẩu hiện tại không đúng.");
        }
    }
  
    // In ra dữ liệu của 1 khối
    public static void displayBlockData() {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Nhập địa chỉ khối cần xuất dữ liệu: ");
    String blockAddress = scanner.nextLine();

    for (Block block : blockchain) {
        if (block.blockAddress.equals(blockAddress)) {
            System.out.println("Dữ liệu khối:");
            System.out.println("Block Address: " + block.blockAddress);
            System.out.println("Data: " + block.getData());
            System.out.println("Hash: " + block.hash);
            System.out.println("Previous Hash: " + block.previousHash);
            System.out.println("Timestamp: " + block.timeStamp);
            System.out.println("Nonce: " + block.nonce);
            return;
        }
    }
    System.out.println("Không tìm thấy khối với địa chỉ này.");
}
        // Tạo khối admin
    public static void createAdminBlock(String password) {
        Block adminBlock = new Block(password, "0");
        adminBlock.blockAddress = "admin";
        adminBlock.mineBlock(difficulty);
        blockchain.add(adminBlock);
        System.out.println("Khối admin đã được tạo.");
    }

    // Phương thức lưu blockchain vào tệp
    public static void saveBlockchain(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(blockchain);
            System.out.println("Blockchain đã được lưu vào tệp.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        // Tạo lại khối admin mà không cần mật khẩu cũ
    public static void recreateAdminBlock() {

        Block adminBlock = null;
        for (Block block : blockchain) {
            if (block.blockAddress.equals("admin")) {
                adminBlock = block;
                break;
            }
        }

        if (adminBlock != null) {
            adminBlock.setData("defaultPassword");
            adminBlock.mineBlock(difficulty);
            System.out.println("Mật khẩu admin đã được đặt về mặc định: defaultPassword.");
            adminPassword = "defaultPassword"; // Cập nhật mật khẩu admin trong chương trình
        } else {
            System.out.println("Khối admin không tồn tại.");
        }
    }


    // Phương thức tải blockchain từ tệp
    @SuppressWarnings("unchecked")
    public static void loadBlockchain(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            blockchain = (ArrayList<Block>) ois.readObject();
            System.out.println("Blockchain đã được tải từ tệp.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    // Chỉnh sửa dữ liệu của một khối
public static void editBlockData(String blockAddress, String newData) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Nhập địa chỉ khối cần thêm chỉnh sửa dữ liệu: ");
    blockAddress = scanner.nextLine();

    // Tìm khối admin
    Block adminBlock = null;
    for (Block block : blockchain) {
        if (block.blockAddress.equals("admin")) {
            adminBlock = block;
            break;
        }
    }

    for (Block block : blockchain) {
        if (block.blockAddress.equals(blockAddress)) {
            // Kiểm tra nếu khối cần chỉnh sửa là khối admin
            if (block == adminBlock) {
                System.out.println("Không thể chỉnh sửa dữ liệu của khối admin.");
                return;
            }
            System.out.println("Nhập dữ liệu mới");
            newData = scanner.nextLine();
            block.setData(newData);
            System.out.println("Dữ liệu khối đã được cập nhật.");
            return;
        }
    }
    System.out.println("Không tìm thấy khối với địa chỉ này.");
}

    // Chuyển dữ liệu từ khối này sang khối khác sử dụng địa chỉ khối
    public static void transferData(String fromBlockAddress, String toBlockAddress) {
        Block fromBlock = null;
        Block toBlock = null;

        for (Block block : blockchain) {
            if (block.blockAddress.equals(fromBlockAddress)) {
                fromBlock = block;
            }
            if (block.blockAddress.equals(toBlockAddress)) {
                toBlock = block;
            }
        }

        if (fromBlock != null && toBlock != null && !fromBlockAddress.equals(toBlockAddress)) {
            toBlock.setData(fromBlock.getData());
            System.out.println("Dữ liệu đã được chuyển từ khối " + fromBlockAddress + " sang khối " + toBlockAddress);
        } else {
            System.out.println("Địa chỉ khối không hợp lệ hoặc không thể chuyển dữ liệu giữa cùng một khối.");
        }
    }
        // Khởi tạo blockchain với khối Genesis nếu danh sách trống
    public static void initializeBlockchain() {
        if (blockchain.isEmpty()) {
            Block adminBlock = new Block(adminPassword, "0");
            adminBlock.blockAddress = "admin";
            adminBlock.mineBlock(difficulty);
            blockchain.add(adminBlock);
            System.out.println("Khối admin đã được tạo.");
            saveBlockchain("blockchain.ser"); // Lưu khối admin vào tệp
        }
    }

    // Thêm nhiều khối liên tiếp vào blockchain
    public static void addMultipleBlocks(int numberOfBlocks) {
    if (blockchain.isEmpty()) {
        System.out.println("Danh sách blockchain hiện tại đang trống. Vui lòng thêm khối khởi đầu trước.");
        return;
    }

    for (int i = 0; i < numberOfBlocks; i++) {
        String previousHash = blockchain.get(blockchain.size() - 1).hash;
        Block newBlock = new Block("Block #" + (blockchain.size() + 1), previousHash);
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
        System.out.println("Khối #" + (blockchain.size() - 1) + " đã được thêm vào blockchain.");
    }
}

    // Nhập dữ liệu cho một khối mới từ bàn phím
    public static void inputDataFromConsole() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập dữ liệu cho khối mới: ");
        String data = scanner.nextLine();
        String previousHash = blockchain.get(blockchain.size() - 1).hash;
        Block newBlock = new Block(data, previousHash);
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
        System.out.println("Khối mới đã được thêm vào blockchain.");
    }

  // Xuất dữ liệu của blockchain ra tệp
    public static void exportBlockchainToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(filename))) {
            for (Block block : blockchain) {
                writer.println("Block Address: " + block.blockAddress);
                writer.println("Data: " + block.getData());
                writer.println("Hash: " + block.hash);
                writer.println("Previous Hash: " + block.previousHash);
                writer.println("Timestamp: " + block.timeStamp);
                writer.println("Nonce: " + block.nonce);
                writer.println();
            }
            System.out.println("Dữ liệu của blockchain đã được xuất ra tệp.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     // Xóa tất cả các khối trong blockchain ngoại trừ khối 'admin'
    public static void deleteAllBlocks() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập mật khẩu để xóa tất cả các khối: ");
        String password = scanner.nextLine();

        Block adminBlock = null;
        for (Block block : blockchain) {
            if (block.blockAddress.equals("admin")) {
                adminBlock = block;
                break;
            }
        }

        if (adminBlock != null && adminBlock.getData().equals(password)) {
            Block adminBlockCopy = adminBlock;
            blockchain.clear();
            blockchain.add(adminBlockCopy);
            System.out.println("Tất cả các khối đã được xóa ngoại trừ khối admin.");
        } else {
            System.out.println("Mật khẩu không đúng hoặc khối admin không tồn tại.");
        }
    }
   

    // Kiểm tra tính hợp lệ của blockchain
    public static boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            // So sánh hash đã được đăng ký và hash được tính toán
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                return false;
            }

            // So sánh hash của khối trước và previousHash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                return false;
            }
        }
        return true;
    }
}

