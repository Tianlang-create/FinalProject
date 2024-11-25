package Tools;

import UI.JFrame_Design;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.*;
import java.awt.*;

public class ErrorWordsShow {
    public ErrorWordsShow() throws IOException {
        Path srcFile = Paths.get("src/Data/未掌握单词.txt");
        // Read the contents of the file
        String content = new String(java.nio.file.Files.readAllBytes(srcFile));
        // Create a new JFrame
        JFrame frame = new JFrame("未掌握单词");
        JButton button = new JButton("返回");
        button.addActionListener(e -> {
            frame.dispose();//frame.dispose(); 是一个在 JavaScript 中常用的方法，用于释放一个组件或对象的资源。
            JFrame_Design.SelectMode();
        });
        frame.add(button, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextArea textArea = new JTextArea(content);
        textArea.setFont(new Font("Dialog", Font.PLAIN, 18));
        textArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
        //将滚动面板（包含 JTextArea 组件）添加到 JFrame 的内容面板中，位于中心区域。
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //监听按钮

    }
}