import java.util.ArrayList;

public class HuffmanDecoder {

    public static void main(String[] args) {


        //1: Read the Huffman coding trie.
        ObjectReader or = new ObjectReader(args[0]);
        Object bt = or.readObject();
        BinaryTrie trie = (BinaryTrie) bt;

        //3: Read the massive bit sequence corresponding to the original txt.
        Object mbs = or.readObject();  //Read second object from file.
        BitSequence massiveBitSequence = (BitSequence) mbs;

        /*
        4: Repeat until the master bit-sequence is length 0:
            4a: Perform a longest prefix match on the massive sequence.
            4b: Record the symbol in some data structure.
            4c: Create a new bit sequence containing the remaining unmatched bits.
        */
        ArrayList<Character> symbols = new ArrayList<>();
        while (massiveBitSequence.length() != 0) {
            Match m1 = trie.longestPrefixMatch(massiveBitSequence);
            int n = m1.getSequence().length();
            symbols.add(m1.getSymbol());
            massiveBitSequence = massiveBitSequence.allButFirstNBits(n);
        }

        //5: Write the symbols in some data structure to the specified file.
        char[] syms = new char[symbols.size()];
        for (int i = 0; i < symbols.size(); i++) {
            syms[i] = symbols.get(i);
        }
        writeCharArray(args[1], syms);


    }

    /** Utility method for HuffmanDecoder. */
    public static void writeCharArray(String filename, char[] chars) {
        BinaryOut out = new BinaryOut(filename);
        for (char c : chars) {
            out.write(c);
        }
        out.close();
    }

}


