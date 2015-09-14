import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by Nick on 9/13/2015.
 */
public class Tests {

    public static void testBitInputAndOutputStreams() throws Exception {
        String filenameInput = "test-data.txt";
        String filenameOutput = "test-data-out.txt";

        ArrayList<Integer> bits = new ArrayList<>();
        BitInputStream bis = new BitInputStream(filenameInput);
        BitOutputStream bos;
        int bit;
        boolean isEOF = false;
        long numBits = 0;

        // test input bit stream
        while (!isEOF) {
            bit = bis.readBit();
            isEOF = bit == -1;

            if (!isEOF) {
                numBits++;
                System.out.print(bit);
                bits.add(bit);
                if (numBits % 8 == 0) {
                    System.out.println();
                }
            }
        }

        System.out.println();
        System.out.println(numBits);

        // test output bit stream
        bos = new BitOutputStream(filenameOutput);
        for (int outBit : bits) {
            bos.writeBit(outBit);
        }

        // compare output and input files
        File inFile = new File(filenameInput);
        File outFile = new File(filenameInput);

        System.out.println();
        if (inFile.hashCode() == outFile.hashCode()) {
            System.out.println("Read and write of data without alteration was successful!");
        } else {
            System.out.println("Error reading or writing data with BitInput/OutputStreams");
        }
    }

    public static byte[] readContentIntoByteArray(File file) {
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
