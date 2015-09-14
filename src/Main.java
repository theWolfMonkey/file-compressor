import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static final int NUM_BITS_IN_LONG = 64;

    public static void main(String[] args) throws Throwable {
        Tests.testBitInputAndOutputStreams();

        String filenameInput = "test-data.txt";
        String filenameCompressed = "test-data.compressed";
        String filenameDecompressed = "test-data.decompressed";
        BitInputStream bis = new BitInputStream(filenameInput);

        // map of <bit sequence, count>
        HashMap<Long, Integer> bitMap = new HashMap<>();

        while (true) {
            long nextLong = getNextLong(bis);

            if (nextLong == -1) {
                break;
            }

            Util.addDataToMap(bitMap, nextLong);

            // TODO wrong number of bits what to do; make tree
        }
        System.out.println(bitMap);

        // sort data
        Map<Long, Integer> sortedData = Util.sortByValues(bitMap);
        System.out.println(sortedData);

        // build and output tree to file
        BitNode root = buildImbalancedTree(sortedData);
        String treeOutputFile = "output-tree.table";
        outputTreeToFile(root, treeOutputFile);

        // use tree to compress data in file
        compressData(root, filenameInput, filenameCompressed);

        // read tree in from file


        // decode compressed file with tree
        BitInputStream inputCompressedFile = new BitInputStream(filenameCompressed);
//        BitOutputStream outputDecompressedFile = new BitOutputStream(filenameDecompressed);
        decompressData(inputCompressedFile, root, filenameDecompressed);


        // write long test
//        writeLongTest();
    }

    private static void writeLongTest() throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream("writelongtest.txt");
        DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

        long testLong = 62L;
        System.out.println(Long.toBinaryString(testLong));

        BitOutputStream bitOutputStream = new BitOutputStream("writelongtest.txt");
        writeLong(bitOutputStream, testLong);

//        fileOutputStream.write();
//        dataOutputStream.writeLong(testLong);


        BitInputStream bitInputStream = new BitInputStream("writelongtest.txt");
        while (true) {
            int bit = bitInputStream.readBit();

            if (bit != -1) {
                System.out.print(bit);
            } else {
                break;
            }

        }
    }

    private static void writeLong(BitOutputStream bitOutputStream, long data) throws Exception {
        String binaryString = Util.padLongBinaryString(data);

        for (int i = 0; i < binaryString.length(); i++) {
            int nextBit = binaryString.charAt(i) == '0' ? 0 : 1;
            bitOutputStream.writeBit(nextBit);
        }
    }

    private static void decompressData(BitInputStream inputCompressedFile, BitNode treeRoot, String outputDecompressedFile) throws Exception {

        BitOutputStream bitOutputStream = new BitOutputStream(outputDecompressedFile);

        boolean isEOF = false;

        BitNode root = treeRoot;
        BitNode current = root;

        while (!isEOF) {
            int nextBit = inputCompressedFile.readBit();

            if (nextBit == -1) {
                // TODO end of file -- what to do? special cases? leftover data that didn't fit into long?
                isEOF = true;
                break;
            }

            if (nextBit == 0) {
                current = current.left;
            } else {
                current = current.right;
            }

            if (current.left == null && current.right == null) {
                // found a leaf, output and reset current
                writeLong(bitOutputStream, current.data);
                current = root;
            }
        }

    }

    private static void compressData(BitNode treeRoot, String fileToCompress, String compressedFile) throws Throwable {
        BitInputStream bitInputStream = new BitInputStream(fileToCompress);
        BitOutputStream bitOutputStream = new BitOutputStream(compressedFile);

        while (true) {
            long nextLong = getNextLong(bitInputStream);

            if (nextLong == -1) {
                break;
            }

            String compressedBitRepresentation = getCompressedBitRepresentation(treeRoot, nextLong);
            System.out.println(compressedBitRepresentation);

            writeBitRepresentation(compressedBitRepresentation, bitOutputStream);
        }

    }

    private static void writeBitRepresentation(String bitSequence, BitOutputStream bitOutputStream) throws Exception {
        for (int i = 0; i < bitSequence.length(); i++) {
            int bit = bitSequence.charAt(i) == '1' ? 1 : 0;
            bitOutputStream.writeBit(bit);
//            System.out.print(bit);
        }
    }

    // TODO not hanndling big enough numbers for how big the tree will get..?
    private static String getCompressedBitRepresentation(BitNode treeRoot, long data) {
//        String bitSequence;


        return inOrderTraversalSearch(treeRoot, data);
    }

    private static String inOrderTraversalSearch(BitNode root, long data) {
        if (root == null) {
            return "NOT FOUND";
        }

        if (root.data == data) {
            return root.bitSequence;
        }

        // DEBUG
//        if (root.left != null) {
//            System.out.println("Left: " + root.left.bitSequence);
//        }
//
//        if (root.right != null) {
//            System.out.println("Right: " + root.right.bitSequence);
//        }

        String leftResult = inOrderTraversalSearch(root.left, data);

        if (!leftResult.equals("NOT FOUND")) {
            return leftResult;
        } else {
            return inOrderTraversalSearch(root.right, data);
        }
    }

    private static long getNextLong(BitInputStream bis) throws IOException {
        long bitBuffer = 0;
        int numBits = 0;

        while (numBits < NUM_BITS_IN_LONG) {
            int bit = bis.readBit();

            if (bit == -1) {
                return -1;
            }

            bitBuffer += bit << numBits;
            numBits++;
        }

        return bitBuffer;
    }

    private static void outputTreeToFile(BitNode root, String treeOutputFile) throws IOException {
        FileWriter writer = new FileWriter(treeOutputFile);

        printNode(root.left, "0", writer);
        printNode(root.right, "1", writer);

        writer.close();
    }

    private static void printNode(BitNode root, String bitSequence, FileWriter writer) throws IOException {
        if (root == null) {
            return;
        }

        if (root.left == null && root.right == null) {
            // hit a leaf, output bit sequence
            writer.write(bitSequence + ":" + root.data + "\n");
        } else {
            printNode(root.left, bitSequence + "0", writer);
            printNode(root.right, bitSequence + "1", writer);
        }
    }

    /**
     * Builds an inefficient tree to try to highly favor most frequent sequences.
     *
     * Data is stored in '0' leaves.
     *
     *         root
     *       /      \
     *      0        1
     *              / \
     *             0   1
     *                / \
     *               0   1
     *
     * @param sortedData
     * @return
     */
    private static BitNode buildImbalancedTree(Map<Long, Integer> sortedData) {
        BitNode root = new BitNode();
        BitNode current = root;

        for (long data : sortedData.keySet()) {
            String previousBitSeq = current.bitSequence;
            BitNode leftLeaf = new BitNode(data);
            leftLeaf.bitSequence = previousBitSeq + "0";
            BitNode rightLeaf = new BitNode();
            rightLeaf.bitSequence = previousBitSeq + "1";
            current.left = leftLeaf;
            current.right = rightLeaf;
            current = current.right;
        }

        return root;
    }

}

