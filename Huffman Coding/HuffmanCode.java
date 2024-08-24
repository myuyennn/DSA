// This program compresses data that can be stored and it can
// also translate the same data back to its original form

import java.util.*;
import java.io.*;

public class HuffmanCode {
    private HuffmanNode overallRoot;

    // Parameters: array of frequencies
    // Behavior: Creates a new HuffmanCode from the given array of 
    // frequencies where frequencies[i] is the count for each
    // ASCII value
    public HuffmanCode(int[] frequencies) {
        Queue<HuffmanNode> count = new PriorityQueue<HuffmanNode>();
        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] > 0) {
                HuffmanNode nodeCount = new HuffmanNode(i, frequencies[i]);
                count.add(nodeCount);
            }
        }
        while (count.size() >= 2) {
            HuffmanNode one = count.remove();
            HuffmanNode two = count.remove();
            HuffmanNode added = new HuffmanNode(0, one.frequency + two.frequency, one, two);
            count.add(added);
        }
        overallRoot = count.remove();
    }

    // Parameters: Scanner input to scan over files
    // Behavior: Builds a new HuffmanCode object from the given file.
    // We can assume the file is not empty and has correct format.
    public HuffmanCode(Scanner input) {
        while (input.hasNextLine()) {
            int value = Integer.parseInt(input.nextLine());
            String code = input.nextLine();
            overallRoot = add(overallRoot, value, code);
        }
    }

    // Parameters: overallRoot of tree, ASCII value, and the corresponding
    // binary code to that ASCII value
    // Behavior: Helper method to build a tree from a given tree using 
    // the ASCII values and binary code from given file
   public HuffmanNode add(HuffmanNode root, int value, String code) {
        if (root == null) {
            root = new HuffmanNode(0, 0);
        }
        if (code.length() == 1) {
            if (code.equals("0")) {
                root.left = new HuffmanNode(value, 0);
            } else {
                root.right = new HuffmanNode(value, 0);
            }
        } else {
            String current = code.substring(0, 1);
            code = code.substring(1, code.length());
            if (current.equals("0")) {
                root.left = add(root.left, value, code);
            } else {
                root.right = add(root.right, value, code);
            }
        }
        return root;
    }

    // Parameters: Scanner input to scan files and PrintStream output
    // to print outputs to
    // Behavior: Reads one bit at a time from the given input file and output
    // the corresponding characters to the output file
    public void translate(Scanner input, PrintStream output) {
        HuffmanNode root = overallRoot;
        while (input.hasNext()) {
            while (root.left != null && root.right != null) {
                char temp = input.next().charAt(0);
                if (temp == '0') {
                    root = root.left;
                } else if (temp == '1') {
                    root = root.right;
                }
            }
            output.write(root.data);
            root = overallRoot;
        }
    }

    // Parameters: Takes in a PrintStream output to print outputs to
    // Behavior: Stores the current Huffman codes to output file
    // in standard format
    public void save(PrintStream output) {
        saveHelper(overallRoot, output, "");
    }

    // Parameters: overallRoot of tree, output file, and an string to
    // record the code
    // Behavior: The helper method to save and it stores the current
    // Huffman codes to given output file by traversing the tree(pre-order).
    // One it reaches a leaf node (null), traversal stops and the data
    // is printed to output file
    private void saveHelper(HuffmanNode root, PrintStream output, String code) {
        if (root != null) {
            if (root.left == null && root.right == null) {
                output.println(root.data);
                output.println(code);
            } else {
                saveHelper(root.left, output, code + 0);
                saveHelper(root.right, output, code + 1);
            }
        }
    }
    
    public class HuffmanNode implements Comparable<HuffmanNode> {
        public HuffmanNode left;
        public HuffmanNode right;
        public int data;
        public int frequency;

        // Behavior: Constructs HuffmanNode for the given data with data
        // for left and right node
        public HuffmanNode(int data, int frequency, HuffmanNode left, HuffmanNode right) {
            this.data = data;
            this.frequency = frequency;
            this.left = left; 
            this.right = right;
        }

        // Behavior: Constructs HuffmanNode for given data with
        // left and right being null
        public HuffmanNode(int data, int frequency) {
            this(data, frequency, null, null);
        }

        // Computes the difference between the frequency of one HuffmanNode 
        // to another HuffmanNode
        public int compareTo(HuffmanNode other) {
            return this.frequency - other.frequency;
        }
    }
}
