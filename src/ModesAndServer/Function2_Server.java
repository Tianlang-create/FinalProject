package ModesAndServer;

import com.sun.tools.attach.VirtualMachineDescriptor;
import Tools.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Function2_Server {
    private static final int PORT = 5000;
    private static Map<String, String> wordMap = new HashMap<>();

    public Function2_Server() {
        // 加载单词数据
        loadWords("src/Data/words.txt ");
        if (wordMap.isEmpty()) {
            System.out.println("Error: wordMap is empty");
            return;
        }
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            while (true) {
                // 等待客户端连接
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
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    // 将单词数据存储到wordMap中
                    wordMap.put(parts[0].trim(), parts[1].trim());
                    System.out.println("Loaded word: " + parts[0].trim() + "#" + parts[1].trim());
                } else {
                    System.out.println("Error: invalid word format - " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: unable to load word data - " + e.getMessage());
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

                int score = 10;
                while (score > 0) {
                    // 获取随机单词
                    String[] wordPair = getRandomWord();
                    if (wordPair == null) {
                        break;
                    }
                    String english = wordPair[0];
                    String chinese = wordPair[1];
                    // 生成选项
                    List<String> options = generateOptions(chinese);

                    // 发送单词和选项给客户端
                    out.println(english);
                    for (String option : options) {
                        out.println(option);
                    }

                    // 接收客户端的答案
                    String answer = in.readLine();
                    if (answer == null) break;

                    // 判断答案是否正确
                    int correctIndex = options.indexOf(chinese);
                    if (answer.equals(chinese)) {
                        score++;
                        out.println("CORRECT," + score);
                        WriteCorrectWords(english,"src/Data/掌握单词.txt");
                    } else if (answer.isEmpty()) {
                        score--;
                        out.println("TIMEOUT," + score + "," + chinese);
                        WriteUncorrectWords("未答单词",english,"src/Data/未掌握单词.txt");
                    } else {
                        score -= 2;
                        out.println("WRONG," + score + "," + chinese);
                        WriteUncorrectWords("答错单词",english,"src/Data/未掌握单词.txt");
                    }
                }
                // 游戏结束，发送结束消息给客户端
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
            if (keys.isEmpty()) {
                // Handle the case when the keys list is empty
                // For example, you can return a default value or throw a custom exception
                return null;
            }
            String english = keys.get(new Random().nextInt(keys.size()));
            return new String[]{english, wordMap.get(english)};
        }

        private List<String> generateOptions(String correct) {
            // 生成选项列表
            List<String> options = new ArrayList<>();
            options.add(correct);
            // 获取其他单词列表
            List<String> values = new ArrayList<>(wordMap.values());
            values.remove(correct);
            // 随机选择三个单词作为选项
            Collections.shuffle(values);
            options.addAll(values.subList(0, 3));
            // 打乱选项顺序
            Collections.shuffle(options);
            return options;
        }
    }

    public static void WriteUncorrectWords(String str1, String str2, String filePath){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(str1+":"+str2);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
    public static void WriteCorrectWords(String str1, String filePath){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(str1);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

}
