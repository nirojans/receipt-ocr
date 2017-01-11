import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.channels.Pipe;

/**
 * Created by niro273 on 1/4/17.
 */
public class TemplateEngine {

    public static String getRetaurantName() {
        try {
            BufferedReader br = FileHelper.readFile("images/sharp_clean_resize_result.txt");

            // Run a for loop, until we reach the Restaurant Name or the Max lines
            String line;
            while ((line = br.readLine()) != null) {
                int distance = StringHelper.distance("TESCO",line);
                if (distance == 0) {
                    System.out.println("Super market has been found, the restaurant name is TESO");
                    //System.out.println(line);
                } else {
                    System.out.println("not found in" + line);
                }

            }

//            StringHelper.distance()
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Not Found";
    }
}
