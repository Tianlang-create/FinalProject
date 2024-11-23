package Tools;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class CountUserScore {
    private static String FILE_PATH = "";
    public static void saveUserData(String userName, int score,String filePath) {
        FILE_PATH = filePath;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String existingScore = getExistingScore(userName);
            if (existingScore != null) {
                updateScore(userName,(parseInt(existingScore) + score));
            } else {
                writer.write(userName + ":" + score);
            }
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }

    private static String getExistingScore(String userName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].equals(userName)) {
                    return parts[1];
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading user data: " + e.getMessage());
        }
        return null;
    }

    public static Map<String, Integer> readUserData() {
        Map<String, Integer> userData = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String userName = parts[0];
                    int score = parseInt(parts[1]);
                        userData.put(userName, score);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading user data: " + e.getMessage());
        }
        return userData;
    }

    public static void printUserData(Map<String, Integer> userData) {
        for (Map.Entry<String, Integer> entry : userData.entrySet()) {
            System.out.println("用户名：" + entry.getKey() + ", 分数：" + entry.getValue());
        }
    }

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
