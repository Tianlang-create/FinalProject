package testProject;

import java.io.*;

public class test2 {
    public static void updateScore(String userName, int newScore) {
        String FILE_NAME = "src/Data/User_Score_Data.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            StringBuilder updatedFile = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].equals(userName)) {
                    updatedFile.append(userName).append(":").append(newScore).append("\n");
                } else {
                    updatedFile.append(line).append("\n");
                }
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                writer.write(updatedFile.toString());
            }
        } catch (IOException e) {
            System.err.println("Error updating score: " + e.getMessage());
        }
    }
}
