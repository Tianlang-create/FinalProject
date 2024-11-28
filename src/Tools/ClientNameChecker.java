package Tools;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ClientNameChecker {
        public boolean NameFlag=false;
        public Boolean IsDuplicate=false;
        public Boolean IsNull=false;
        public ClientNameChecker(String username) {
        String filePath = "src/Data/UserName_data.txt";
        Set<String> uniqueNames = new HashSet<>();
        //判断用户名是否重复
        if(username=="\n"|| Objects.equals(username, "")){
            JOptionPane.showMessageDialog(null, "您什么都没有输入！");
            IsNull=true;
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String name = parts[1].trim();
                    if (name.equals(username)) {
                        System.out.println("重复用户名: " + name);
                        RebulidDirecter rd=new RebulidDirecter();
                        this.NameFlag=rd.NameFlag;
                        this.IsDuplicate=true;
                    }
                    else {
                       IsDuplicate=false;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}