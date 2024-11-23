package ModesAndServer;

import java.io.*;
import java.net.*;
import java.util.*;

public class Function2_Server {
    private static final int PORT = 5000;
    private static Map<String, String> wordMap = new HashMap<>();

    public static void main(String[] args) {
        loadWords("src/Data/words.txt");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
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
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    wordMap.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                    String[] wordPair = getRandomWord();
                    String english = wordPair[0];
                    String chinese = wordPair[1];
                    List<String> options = generateOptions(chinese);

                    out.println(english);
                    for (String option : options) {
                        out.println(option);
                    }

                    String answer = in.readLine();
                    if (answer == null) break;

                    int correctIndex = options.indexOf(chinese);
                    if (answer.equals(String.valueOf((char)('A' + correctIndex)))) {
                        score++;
                        out.println("CORRECT," + score);
                    } else if (answer.isEmpty()) {
                        score--;
                        out.println("TIMEOUT," + score + "," + chinese);
                    } else {
                        score -= 2;
                        out.println("WRONG," + score + "," + chinese);
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

        private List<String> generateOptions(String correct) {
            List<String> options = new ArrayList<>();
            options.add(correct);
            List<String> values = new ArrayList<>(wordMap.values());
            values.remove(correct);
            Collections.shuffle(values);
            options.addAll(values.subList(0, 3));
            Collections.shuffle(options);
            return options;
        }
    }
}

