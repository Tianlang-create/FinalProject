package ModesAndServer;
import java.io.*;
import java.net.*;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Function1_Server {
    private static final int PORT = 3000;
    private static Map<String, String> wordMap = new HashMap<>();
    public static String nickname = null;
    private static PrintWriter out;
    private static BufferedReader in;

    public Function1_Server() {
        loadWords("src/Data/words.txt");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Test:Server is running on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadWords(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    wordMap.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                int score = 10;
                while (score > 0) {
                    String[] wordPair = getRandomWord();
                    String english = wordPair[0];
                    String chinese = wordPair[1];

                    out.println(chinese);
                    out.println(getHint(english));

                    String answer = in.readLine();
                    if (answer == null) break;

                    if (answer.equals(english)) {
                        score++;
                        WriteCorrectWords(english,chinese,"src/Data/掌握单词.txt");
                        out.println("CORRECT," + score);
                    } else if (answer.isEmpty()) {
                        score--;
                        WriteUncorrectWords("未答单词",english,chinese,"src/Data/未掌握单词.txt");
                        out.println("TIMEOUT," + score + "," + english);
                    } else {
                        score -= 2;
                        WriteUncorrectWords("答错单词",english,chinese,"src/Data/未掌握单词.txt");
                        out.println("WRONG," + score + "," + english);
                    }
                }
                out.println("GAMEOVER");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String[] getRandomWord() {
            List<String> keys = new ArrayList<>(wordMap.keySet());
            String english = keys.get(new Random().nextInt(keys.size()));
            return new String[]{english, wordMap.get(english)};
        }

        private String getHint(String word) {
            return word.charAt(0) + "_".repeat(word.length() - 2) + word.charAt(word.length() - 1);
        }//getHint 接收一个 word 作为输入，并返回一个字符串，
        // 其中保留了原单词的第一个和最后一个字符
        // 中间的字符被下划线 (_) 替代。

        public static void WriteUncorrectWords(String str1, String str2, String str3, String filePath){
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                String line = str1 + ":" + str2 + " " + str3;
                if (!isDuplicate(line, filePath)) {
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error writing to file: " + e.getMessage());
            }
        }

        public static void WriteCorrectWords(String str1,String str2, String filePath){
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                if (!isDuplicate(str1, filePath)) {
                    writer.write(str1+" "+str2);
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error writing to file: " + e.getMessage());
            }
        }

        private static boolean isDuplicate(String line, String filePath) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String existingLine;
                while ((existingLine = reader.readLine()) != null) {
                    if (existingLine.equals(line)) {
                        return true;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
            return false;
        }

        }
    public static void main(String[] args) {
        Function1_Server server = new Function1_Server();}
}

