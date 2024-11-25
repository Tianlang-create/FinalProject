package ModesAndServer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import UI.*;

import static ModesAndServer.Function1_Server.nickname;
import static Tools.CountUserNumber.saveUserData;

public class SecondMode extends JFrame implements ActionListener {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JLabel wordLabel;
    private JButton[] optionButtons = new JButton[4];
    private int currentScore = 0;
    private String correctAnswer = "";

    private String nick="";
    public SecondMode(String nickname) {
        this.nick = nickname;
        setTitle(nickname+"欢迎来到模式二");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 创建并设置UI组件
        wordLabel = new JLabel("等待服务器发送单词...", SwingConstants.CENTER);
        add(wordLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4));
        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i] = new JButton("选项 " + (char)('A' + i + 1));
            optionButtons[i].setFont(new Font("Dialog", Font.PLAIN, 18));
            optionButtons[i].setPreferredSize(new Dimension(150, 50));
            optionButtons[i].addActionListener(this);
            buttonPanel.add(optionButtons[i]);
        }
        add(buttonPanel, BorderLayout.CENTER);
        this.getDefaultCloseOperation();

        // 连接到服务器
        try {
            socket = new Socket("localhost", 5000);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            // 启动一个线程来监听服务器的消息
            new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readLine();
                        if (message == null) break;
                        handleMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "与服务器断开连接！");
                    System.exit(1);
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "无法连接到服务器！");
            System.exit(1);
        }

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void handleMessage(String message) throws IOException {
        if (message.startsWith("CORRECT,")) {
            InitButtons();
            String[] parts = message.split(",");
            saveUserData(nick,1,"src/Data/User_Score_Data.txt");//统计用户回答数目
            currentScore = Integer.parseInt(parts[1]);
            JOptionPane.showMessageDialog(this, "回答正确！当前得分：" + currentScore);
        } else if (message.startsWith("TIMEOUT,")) {
            InitButtons();
            String[] parts = message.split(",");
            currentScore = Integer.parseInt(parts[1]);
            correctAnswer = parts[2];
            JOptionPane.showMessageDialog(this, "回答超时！正确答案：" + correctAnswer + "。当前得分：" + currentScore);
        } else if (message.startsWith("WRONG,")) {
            InitButtons();
            String[] parts = message.split(",");
            currentScore = Integer.parseInt(parts[1]);
            correctAnswer = parts[2];
            JOptionPane.showMessageDialog(this, "回答错误！正确答案：" + correctAnswer + "。当前得分：" + currentScore);
        } else if (!message.equals("GAMEOVER")) {
            // 这里处理服务器发送的单词和选项
            if (wordLabel.getText().equals("等待服务器发送单词...")) {
                // 第一个消息是英文单词
                wordLabel.setText(message);
                wordLabel.setFont(new Font("Dialog", Font.PLAIN, 30));
            } else {
                // 后续的消息是选项
                for (int i = 0; i < optionButtons.length; i++) {
                    if (optionButtons[i].getText().startsWith("选项 ")) {
                        optionButtons[i].setText(message);
                        break;
                    }
                }
            }
        } else {
            // 游戏结束
            JOptionPane.showMessageDialog(this, "游戏结束！最终得分：" + currentScore);
            // 返回主菜单
            setVisible(false);
            JFrame_Design.SelectMode();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < optionButtons.length; i++) {
            if (e.getSource() == optionButtons[i]) {
                String selectedAnswer = optionButtons[i].getText();
                out.println(selectedAnswer);
                break;
            }
        }
    }
    public void InitButtons(){
        wordLabel.setText("等待服务器发送单词...");
        for (int i = 0; i < optionButtons.length; i++) {
            optionButtons[i].setText("选项 " + (char)('A' + i));
        }
    }

    public static void main(String[] args) {
        new SecondMode("admin");
    }
}