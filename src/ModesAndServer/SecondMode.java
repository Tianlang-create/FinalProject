package ModesAndServer;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class SecondMode extends JFrame {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    private JLabel englishLabel;
    private JButton[] optionButtons;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private JLabel messageLabel;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Timer timer;
    private int timeLeft;

    public SecondMode() {
        setTitle("考研单词记忆游戏");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 1));

        englishLabel = new JLabel("", SwingConstants.CENTER);
        englishLabel.setFont(new Font("Arial", Font.BOLD, 20));
        optionButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JButton();
            optionButtons[i].addActionListener(e -> submitAnswer(e.getActionCommand()));
        }
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2));
        for (JButton button : optionButtons) {
            optionsPanel.add(button);
        }
        scoreLabel = new JLabel("得分: 10", SwingConstants.CENTER);
        timerLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel = new JLabel("", SwingConstants.CENTER);

        add(englishLabel);
        add(optionsPanel);
        add(scoreLabel);
        add(timerLabel);
        add(messageLabel);

        timer = new Timer(1000, e -> updateTimer());

        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            new Thread(this::playGame).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playGame() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equals("GAMEOVER")) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "游戏结束！");
                        System.exit(0);
                    });
                    break;
                }
                String english = line;
                String[] options = new String[4];
                for (int i = 0; i < 4; i++) {
                    options[i] = in.readLine();
                }
                SwingUtilities.invokeLater(() -> {
                    englishLabel.setText(english);
                    for (int i = 0; i < 4; i++) {
                        optionButtons[i].setText((char)('A' + i) + ". " + options[i]);
                        optionButtons[i].setActionCommand(String.valueOf((char)('A' + i)));
                        optionButtons[i].setEnabled(true);
                    }
                    messageLabel.setText("");
                    timeLeft = 10;
                    timerLabel.setText("剩余时间: " + timeLeft + "秒");
                    timer.start();
                });

                String result = in.readLine();
                String[] parts = result.split(",");
                SwingUtilities.invokeLater(() -> {
                    timer.stop();
                    for (JButton button : optionButtons) {
                        button.setEnabled(false);
                    }
                    scoreLabel.setText("得分: " + parts[1]);
                    if (parts[0].equals("CORRECT")) {
                        messageLabel.setText("恭喜回答正确！");
                    } else if (parts[0].equals("WRONG")) {
                        messageLabel.setText("回答错误，答案是 " + parts[2]);
                    } else if (parts[0].equals("TIMEOUT")) {
                        messageLabel.setText("您没有回答，正确答案是 " + parts[2]);
                    }
                });

                Thread.sleep(2000);  // 暂停2秒，显示结果
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void submitAnswer(String answer) {
        out.println(answer);
        timer.stop();
        for (JButton button : optionButtons) {
            button.setEnabled(false);
        }
    }

    private void updateTimer() {
        timeLeft--;
        timerLabel.setText("剩余时间: " + timeLeft + "秒");
        if (timeLeft == 0) {
            timer.stop();
            out.println("");  // 发送空字符串表示超时
            for (JButton button : optionButtons) {
                button.setEnabled(false);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SecondMode().setVisible(true);
        });
    }
}


