package Tools;

import javax.swing.*;

public class RebulidDirecter {
    String input=null;
    public Boolean NameFlag=false;
    RebulidDirecter() {
        Directer();
        //保留添加引导栏想法
    }

    public void Directer() {
        JOptionPane.showMessageDialog(null, "您输入了重复的用户名！");
        input = JOptionPane.showInputDialog("如果要保留用户数据请键入keep\n否则按回车跳过");
        //保留
        if(input.equals("keep")){
            NameFlag=true;
        }
        else {
            NameFlag=false;
        }
    }

    public static void main(String[] args) {
        RebulidDirecter rd = new RebulidDirecter();
    }
}
