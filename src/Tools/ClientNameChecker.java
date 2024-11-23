package Tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ClientNameChecker {
        public boolean NameFlag=false;
        public Boolean IsDuplicate=false;
        public ClientNameChecker(String username) {
        String filePath = "src/Data/UserName_data.txt";
        Set<String> uniqueNames = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String name = parts[1].trim();
                    String Flag =parts[0].trim();
                    if (name.equals(username) && Flag.equals("Username")) {
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