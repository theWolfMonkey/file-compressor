import java.io.FileInputStream;
import java.io.IOException;

public class BitInputStream {

    public static final int BYTE_SIZE = 8;

    private FileInputStream input;
    private int bitBuffer;
    private int numBits;

    public BitInputStream(String file) throws IOException {
        input = new FileInputStream(file);
        bufferNextByte();
    }

    /**
     * Read and return the next bit in the file
     *
     * @return the next bit in the file, or -1 if at the end of the file
     * @throws IOException
     */
    public int readBit() throws IOException {
        // if at eof, return -1
        if (bitBuffer == -1) {
            return -1;
        }

        int nextBit = bitBuffer % 2;
        bitBuffer /= 2;
        numBits++;

        if (numBits == BYTE_SIZE) {
            bufferNextByte();
        }

        return nextBit;
    }

    private void bufferNextByte() throws IOException {
        bitBuffer = input.read();
        numBits = 0;
    }

    @Override
    protected void finalize() throws Throwable {
        input.close();
        super.finalize();
    }
}
