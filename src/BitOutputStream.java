import java.io.FileNotFoundException;
import java.io.PrintStream;

public class BitOutputStream {

    public static final int BYTE_SIZE = 8;

    private PrintStream output;
    private int bitBuffer;
    private int numBits;

    public BitOutputStream(String filename) throws FileNotFoundException {
        output = new PrintStream(filename);
        bitBuffer = 0;
        numBits = 0;
    }

    public void writeBit(int bit) throws Exception {
        if (bit < 0 || bit > 1) {
            throw new Exception("Invalid bit given: " + bit);
        }

        bitBuffer += bit << numBits;
        numBits++;
        if (numBits == BYTE_SIZE) {
            output.write(bitBuffer);
            bitBuffer = 0;
            numBits = 0;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        // make sure we write the last few bits we have, even if we don't have a full byte
        if (bitBuffer > 0) {
            output.write(bitBuffer);
        }

        output.close();
        super.finalize();
    }
}
