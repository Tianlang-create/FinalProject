package ModesAndServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileCopy {
    public static void main(String[] args) throws IOException {
        Path srcFile = Paths.get("src/Data/未掌握单词.txt");
        Path destFile = Paths.get("destination.txt");
        Files.copy(srcFile, destFile);
    }
}