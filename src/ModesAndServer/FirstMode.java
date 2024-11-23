package ModesAndServer;

//合并设计方案
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

import static Tools.CountUserScore.saveUserData;

public class FirstMode extends JFrame {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 0000;
    //public JFrame_Design jfd=new JFrame_Design();
    private JLabel chineseLabel;
    private JLabel hintLabel;
    private JTextField answerField;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private JLabel messageLabel;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    public static String nick=null;
    private Timer timer;
    private int timeLeft;

    public FirstMode(String nickname) {
        nick=nickname;
        System.out.println("打开了模块一");
        setTitle("Welcome, " + nickname);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 1));

        chineseLabel = new JLabel("", SwingConstants.CENTER);
        hintLabel = new JLabel("", SwingConstants.CENTER);
        answerField = new JTextField();
        answerField.setLocation(50, 50);
        scoreLabel = new JLabel("得分: 10", SwingConstants.CENTER);
        timerLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel = new JLabel("", SwingConstants.CENTER);
        add(chineseLabel);
        add(hintLabel);
        add(answerField);
        add(scoreLabel);
        add(timerLabel);
        add(messageLabel);
        this.setVisible(true);
        answerField.addActionListener(e -> submitAnswer());
        timer = new Timer(1000, e -> updateTimer());
        connectToServer();
    }

    private void connectToServer() {
        try {
            System.out.println("开始了链接");
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
                    JOptionPane.showMessageDialog(this, "游戏结束！");
                    System.exit(0);
                }
                String hint = in.readLine();
                String finalLine = line;
                SwingUtilities.invokeLater(() -> {
                    chineseLabel.setText(finalLine);
                    hintLabel.setText(hint);
                    answerField.setText("");
                    answerField.setEnabled(true);
                    messageLabel.setText("");
                    timeLeft = 10;
                    timerLabel.setText("剩余时间: " + timeLeft + "秒");
                    timer.start();
                });

                String result = in.readLine();
                String[] parts = result.split(",");
                SwingUtilities.invokeLater(() -> {
                    timer.stop();
                    answerField.setEnabled(false);
                    scoreLabel.setText("得分: " + parts[1]);
                    if (parts[0].equals("CORRECT")) {
                        saveUserData(nick,1,"src/Data/User_Score_Data.txt");//统计用户回答数目
                        messageLabel.setText("恭喜回答正确！");
                    } else if (parts[0].equals("WRONG")) {
                        messageLabel.setText("回答错误，答案是 " + parts[2]);
                    } else if (parts[0].equals("TIMEOUT")) {
                        messageLabel.setText("您没有回答，答案是 " + parts[2]);
                    }
                });

                Thread.sleep(2000);  // 暂停2秒，显示结果
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void submitAnswer() {
        String answer = answerField.getText();
        out.println(answer);
        timer.stop();
        answerField.setEnabled(false);
    }

    private void updateTimer() {
        timeLeft--;
        timerLabel.setText("剩余时间: " + timeLeft + "秒");
        if (timeLeft == 0) {
            timer.stop();
            out.println("");  // 发送空字符串表示超时
            answerField.setEnabled(false);
        }
    }

    public static void main(String[] args) {
        String test = "admin";
        new FirstMode(test).setVisible(true);
    }
}

