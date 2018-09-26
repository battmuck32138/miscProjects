import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HuffmanEncoder {


    public static Map<Character, Integer> buildFrequencyTable(char[] inputSymbols) {
        HashMap<Character, Integer> frequencyTable = new HashMap<>();
        int[] counts = new int[256];
        for (char sym : inputSymbols) {
            counts[sym]++;
        }

        for (int i = 0; i < counts.length; i++) {
            if (counts[i] != 0) {
                char c = (char) i;
                frequencyTable.put(c, counts[i]);
            }
        }
        return frequencyTable;
    }


    /** Utility method for HuffmanEncoder. */
    public static char[] readFile(String filename) {
        BinaryIn in = new BinaryIn(filename);
        ArrayList<Character> chars = new ArrayList<>();
        while (!in.isEmpty()) {
            chars.add(in.readChar());
        }
        char[] input = new char[chars.size()];
        for (int i = 0; i < input.length; i += 1) {
            input[i] = chars.get(i);
        }
        return input;
    }


    /** Utility method for HuffmanDecoder. */
    public static void writeCharArray(String filename, char[] chars) {
        BinaryOut out = new BinaryOut(filename);
        for (char c : chars) {
            out.write(c);
        }
        out.close();
    }



    public static void main(String[] args) {

        //1: Read the file as 8 bit symbols.
        char[] in = readFile(args[0]);

        //2: Build frequency table.
        Map<Character, Integer> frequencyTable = buildFrequencyTable(in);

        //3: Use frequency table to construct a binary decoding trie.
        BinaryTrie bt1 = new BinaryTrie(frequencyTable);


        //4: Write the binary decoding trie to the .huf file.
        ObjectWriter ow = new ObjectWriter(args[0] + ".huf");
        ow.writeObject(bt1);

        //6: Use binary trie to create lookup table for encoding.
        Map<Character, BitSequence> lookUPTable = bt1.buildLookupTable();

        //7: Create a list of bit-sequences.
        ArrayList<BitSequence> bitSequences = new ArrayList<>();

        /*
        8: For each 8 bit symbol in the original file:
        Lookup that symbol in the lookup table.
        Add the appropriate bit sequence to the list of bit-sequences.
        */
        for (char c : in) {
            BitSequence b = lookUPTable.get(c);
            bitSequences.add(b);
        }

        //9: Assemble all bit sequences into one huge bit sequence.
        BitSequence masterSequence = BitSequence.assemble(bitSequences);

        //10: Write the huge bit sequence to the .huf file.
        ow.writeObject(masterSequence);




    }

}


