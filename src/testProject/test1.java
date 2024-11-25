package testProject;

import java.util.Map;
import static Tools.CountUserNumber.*;

public class test1 {
    private static final String FILE_PATH = "src/Data/User_Score_Data.txt";
    public static void main(String[] args) {
        //测试点一:针对用户数据的操作
        String nickname = "Tianlang";
        Integer score1 = 20;
        saveUserData(nickname, score1, FILE_PATH);
        Map<String, Integer> userData = readUserData();
        printUserData(userData);
        Integer score2 = 40;
        saveUserData(nickname, score2,FILE_PATH);
        Map<String, Integer> userData1 = readUserData();
        printUserData(userData1);
    }
}
