import javax.xml.crypto.Data;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static final int NUM_BITS_IN_LONG = 64;

    public static void main(String[] args) throws Throwable {
        testBitInputAndOutputStreams();

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

            addDataToMap(bitMap, nextLong);

            // TODO wrong number of bits what to do; make tree
        }
        System.out.println(bitMap);

        // sort data
        Map<Long, Integer> sortedData = sortByValues(bitMap);
        System.out.println(sortedData);

        // build and output tree to file
        BitNode root = buildTree(sortedData);
        String treeOutputFile = "output-tree.table";
        outputTreeToFile(root, treeOutputFile);

        // use tree to compress data in file
        compressData(root, filenameInput, filenameCompressed);

        // read tree in from file


        // decode compressed file with tree
        BitInputStream inputCompressedFile = new BitInputStream(filenameCompressed);
        BitOutputStream outputDecompressedFile = new BitOutputStream(filenameDecompressed);
        decompressData(inputCompressedFile, root, filenameDecompressed);
    }

    private static void decompressData(BitInputStream inputCompressedFile, BitNode treeRoot, String outputDecompressedFile) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(outputDecompressedFile);
        DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

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
                dataOutputStream.writeLong(current.data);
                current = root;
            }
        }

        dataOutputStream.close();
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
    private static BitNode buildTree(Map<Long, Integer> sortedData) {
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

    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return -((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue()); // minus to sort largest to smallest
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    private static void addDataToMap(HashMap<Long, Integer> map, Long data) {
        if (map.get(data) == null) {
            map.put(data, 1);
        } else {
            map.put(data, map.get(data) + 1);
        }
    }

    private static void testBitInputAndOutputStreams() throws Exception {
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

