package UI;

import ModesAndServer.*;
import Tools.*;
import javax.swing.*;

import static java.lang.System.exit;

public class JFrame_Design extends JFrame {
    ClientNameChecker cnc=null;
    public static String nickName = null;
    public JFrame_Design() {
        nickName = JOptionPane.showInputDialog(this, "输入昵称");
        setNickName();
        if(cnc.IsDuplicate==false) {
            new ClientDataWriter(nickName);
        }
        this.setTitle("你好，" + nickName+ "欢迎学习英语!");
        this.setSize(800, 600);
        SelectMode();
        this.setVisible(false);
        this.getDefaultCloseOperation();
    }

    public void setNickName() {
        cnc=new ClientNameChecker(nickName);
        if(cnc.NameFlag==true) {
            System.out.println("用户保留了名字"+nickName);
        }else if (cnc.IsDuplicate==true) {
            nickName = JOptionPane.showInputDialog(this, "输入昵称");
            setNickName();
        }}

    public void SelectMode() {
        while(true) {
            String[] options = {"模式一", "模式二","导出我错的词","退出"};
            int option = JOptionPane.showOptionDialog(null, "请选择游戏模式", "游戏模式选择", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (option == 0) {
                System.out.println("选择了模式一");
                set(1);
                new FirstMode(nickName).setVisible(true);
                break;
            } else if (option == 1) {
                System.out.println("选择了模式二");
                set(2);
                new SecondMode(nickName).setVisible(true);
                break;
            }
            else if (option == 2) {
                System.out.println("选择了导出我错的词");
                FileCopy fc=new FileCopy();
                System.out.println("导出成功");
            }
            else if (option == 3) {
                System.out.println("选择了退出");
                exit(0);
            }
    }}

    public void set(int i) {
         new Thread(()->{
             if(i==1)
                new Function1_Server();
             else if(i==2)
                 new Function2_Server();;
         }).start();
         try {
             Thread.sleep(1000);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
    }


    public static void main(String[] args) {
        JFrame_Design frame = new JFrame_Design();
    }

}