import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by niro273 on 1/4/17.
 */
public class FileHelper {

    public static BufferedReader readFile(String fileLocation) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(fileLocation));
//        String line;
//        while ((line = br.readLine()) != null) {
//            System.out.println(line);
//        }
        return br;
    }

    public static void getAllFileNames() {
        File folder = new File("images");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
    }
}
