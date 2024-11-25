package testProject;

import java.io.*;
import java.net.*;
import java.util.*;

public class WordGameServer {
    private static final int PORT = 1000;
    private static final String VOCAB_FILE = "src/Data/words.txt";
    private List<String[]> vocabulary = new ArrayList<>();

    public WordGameServer() {
        loadVocabulary();
    }

    private void loadVocabulary() {
        try (BufferedReader br = new BufferedReader(new FileReader(VOCAB_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    vocabulary.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
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

    private class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private int score = 10;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (score > 0) {
                    String[] question = getRandomQuestion();
                    out.println("QUESTION:" + question[0]);
                    for (int i = 0; i < 4; i++) {
                        out.println("OPTION:" + (char)('A' + i) + ":" + getRandomOption(question[1], i == 0));
                    }

                    String answer = in.readLine();
                    boolean correct = answer != null && answer.equals("A");

                    if (answer == null) {
                        score -= 1;
                        out.println("RESULT:您没有回答，正确答案是" + question[1]);
                    } else if (correct) {
                        score += 1;
                        out.println("RESULT:恭喜回答正确");
                    } else {
                        score -= 2;
                        out.println("RESULT:回答错误，答案是" + question[1]);
                    }

                    out.println("SCORE:" + score);
                }

                out.println("GAMEOVER:游戏结束，您的最终得分是" + score);
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

        private String[] getRandomQuestion() {
            return vocabulary.get(new Random().nextInt(vocabulary.size()));
        }

        private String getRandomOption(String correctAnswer, boolean isCorrect) {
            if (isCorrect) {
                return correctAnswer;
            }
            String[] randomWord;
            do {
                randomWord = getRandomQuestion();
            } while (randomWord[1].equals(correctAnswer));
            return randomWord[1];
        }
    }

    public static void main(String[] args) {
        new WordGameServer().start();
    }
}

