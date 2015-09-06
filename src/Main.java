import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        File file = new File("test-data.txt");

        byte[] bytes = readContentIntoByteArray(file);

        System.out.println(Arrays.toString(bytes));
    }

    private static byte[] readContentIntoByteArray(File file) {
        FileInputStream fileInputStream;
        byte[] bFile = new byte[(int) file.length()];
        try {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bFile;
    }

}

