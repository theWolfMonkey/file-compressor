import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

        String filename = "test-data.txt";

        try {
            BitInputStream bis = new BitInputStream(filename);

            int bit;
            boolean isEOF = false;

            long numBits = 0;


            while (!isEOF) {
                bit = bis.readBit();

                isEOF = bit == -1;
                if (!isEOF) {
                    numBits++;
                    System.out.print(bit);
                    if (numBits % 8 == 0) {
                        System.out.println();
                    }
                }
            }

            System.out.println();
            System.out.println(numBits);

        } catch (IOException e) {
            e.printStackTrace();
        }


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

