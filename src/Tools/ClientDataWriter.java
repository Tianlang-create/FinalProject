package Tools;

import java.io.IOException;

public class ClientDataWriter {
    public ClientDataWriter(String username) {
        // File path
        String filePath = "src/Data/userName_data.txt";

        try {
            // Create a FileWriter object
            java.io.FileWriter writer = new java.io.FileWriter(filePath, true);
            // Write user data to the file
            writer.write("Username: " + username + "\n");
            // Close the FileWriter
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ClientDataWriter writer = new ClientDataWriter("Tianlang222");
    }
}