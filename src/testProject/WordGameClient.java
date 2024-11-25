package testProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class WordGameClient extends JFrame implements ActionListener {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1000; // 确保与服务器端口一致

    private JLabel wordLabel;
    private JButton[] optionButtons;
    private JLabel timerLabel;
    private JLabel scoreLabel;
    private int score = 0;
    private Timer timer;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String correctAnswer;
    private List<String> options;

    public WordGameClient() {
        setTitle("单词记忆游戏");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        wordLabel = new JLabel("等待服务器发送单词...", SwingConstants.CENTER);
        add(wordLabel, BorderLayout.NORTH);

        JPanel optionPanel = new JPanel(new GridLayout(1, 4));
        optionButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JButton("");
            optionButtons[i].addActionListener(this);
            optionPanel.add(optionButtons[i]);
        }
        add(optionPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        timerLabel = new JLabel("时间: 10");
        bottomPanel.add(timerLabel);

        scoreLabel = new JLabel("得分: " + score);
        bottomPanel.add(scoreLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        startGame();
    }

    private void startGame() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String word = receiveWord();
                options = receiveOptions();
                correctAnswer = options.get(0).split(":")[1]; // 假设正确答案是第一个选项中的中文

                displayWordAndOptions(word, options);

                timer = new Timer(1000, e -> {
                    int time = Integer.parseInt(timerLabel.getText().split(":")[1]);
                    if (time > 0) {
                        timerLabel.setText("时间: " + (time - 1));
                    } else {
                        timer.stop();
                        checkAnswer(""); // 时间到，视为未回答
                    }
                });
                timer.start();

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "无法连接到服务器", "错误", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }).start();
    }

    private String receiveWord() throws IOException {
        String line = in.readLine();
        return line.split(":")[1]; // 假设服务器发送的格式为 "WORD:单词"
    }

    private List<String> receiveOptions() throws IOException {
        List<String> opts = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String line = in.readLine();
            opts.add(line); // 假设服务器发送的格式为 "OPTION:A.中文"
        }
        return opts;
    }

    private void displayWordAndOptions(String word, List<String> opts) {
        wordLabel.setText("英文单词: " + word);
        for (int i = 0; i < 4; i++) {
            String[] parts = opts.get(i).split(":");
            String optionText = parts[1].split("\\.")[1].trim();
            optionButtons[i].setText("A B C D".charAt(i) + ". " + optionText);
            optionButtons[i].setEnabled(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < 4; i++) {
            if (e.getSource() == optionButtons[i]) {
                String selectedOption = options.get(i).split(":")[1].split("\\.")[1].trim();
                timer.stop();
                checkAnswer(selectedOption);
                break;
            }
        }
    }

    private void checkAnswer(String answer) {
        if (answer.equals(correctAnswer)) {
            score++;
            JOptionPane.showMessageDialog(this, "恭喜回答正确！");
        } else {
            score -= 2;
            JOptionPane.showMessageDialog(this, "回答错误，答案是" + correctAnswer);
        }
        if (score <= 0) {
            JOptionPane.showMessageDialog(this, "游戏结束，你的分数已经扣到0分或以下。");
            System.exit(0);
        } else {
            startGame(); // 进入下一局
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WordGameClient().setVisible(true);
        });
    }
}