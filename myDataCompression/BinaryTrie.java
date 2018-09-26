import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;



public class BinaryTrie implements Serializable {

    // alphabet size of extended ASCII
    private final int R = 256;
    private Node trie;
    private Map<Character, BitSequence> encodingMap = new HashMap<>();
    private static final long serialVersionUID = 138816335349L;

    public BinaryTrie(Map<Character, Integer> frequencyTable) {
        // initialize priority queue with singleton trees
        MinPQ<Node> pq = new MinPQ<>();

        for (char key : frequencyTable.keySet()) {
            if (frequencyTable.get(key) > 0) {
                pq.insert(new Node(key, frequencyTable.get(key), null, null));
            }
        }

        // special case in case there is only one character with a nonzero frequency
        if (pq.size() == 1) {
            if (frequencyTable.size() == 1) {
                pq.insert(new Node('\0', 0, null, null));

            } else         {
                pq.insert(new Node('\1', 0, null, null));
            }
        }

        // merge two smallest trees
        while (pq.size() > 1) {
            Node left  = pq.delMin();
            Node right = pq.delMin();
            Node parent = new Node('\0', left.freq + right.freq, left, right);
            pq.insert(parent);
        }

        this.trie = pq.delMin();
    }


    public Match longestPrefixMatch(BitSequence querySequence) {
        Node current = trie;
        Match match = new Match(null, null);
        String seq = "";
        String nextBit = "";

        for (int i = 0; i < querySequence.length(); i++) {
            int bit = querySequence.bitAt(i);

            if (bit == 0) {
                current = current.left;
                nextBit = "0";
            } else {
                current = current.right;
                nextBit = "1";
            }

            seq += nextBit;

            if (current == null) {
                return match;
            }

            if (current.isLeaf()) {
                BitSequence matchSeq = new BitSequence(seq);
                match = new Match(matchSeq, current.ch);
            }
        }

        return match;
    }




    public Map<Character, BitSequence> buildLookupTable() {
        buildCode(trie, "");
        return encodingMap;
    }


    //Helper for buildLookUpTable()
    //Performs a pre-order depth-first traversal of the trie adding
    // a 0 when visiting left children and a 1 for right children.
    private void buildCode(Node x, String s) {
        if (!x.isLeaf()) {
            buildCode(x.left,  s + '0');
            buildCode(x.right, s + '1');
        } else {
            BitSequence bs = new BitSequence(s);
            encodingMap.put(x.ch, bs);
        }
    }

    public int getR() {
        return R;
    }



    /*******************************************************************************
     * Helper Class
     *******************************************************************************/


    // Huffman trie node
    private static class Node implements Comparable<Node>, Serializable {
        private final char ch;
        private final int freq;
        private final Node left, right;
        private static final long serialVersionUID = 13888888888888L;

        Node(char ch, int freq, Node left, Node right) {
            this.ch    = ch;
            this.freq  = freq;
            this.left  = left;
            this.right = right;
        }

        // is the node a leaf node?
        private boolean isLeaf() {
            assert ((left == null) && (right == null)) || ((left != null) && (right != null));
            return (left == null) && (right == null);
        }

        // compare, based on frequency
        public int compareTo(Node that) {
            return this.freq - that.freq;
        }

    }

}


