

public class BitNode {
//    public String bitSeq;
    public long data;
    public BitNode left;
    public BitNode right;
    public String bitSequence;

    public BitNode() {
        this.bitSequence = "";
        this.data = Long.MIN_VALUE;
    }

    public BitNode(long data) {
        this.data = data;
        this.bitSequence = "";
    }

    public BitNode(String bitSequence) {
        this.bitSequence = bitSequence;
    }

    public BitNode(long data, String bitSequence) {
        this.data = data;
        this.bitSequence = bitSequence;
    }

    /**
     * Adds a bit to the sequence of bits that represents this node's position in the tree.
     *
     * True = 1, False = 0
     *
     * @param bit
     */
    public void addBit(boolean bit) {

    }
}
