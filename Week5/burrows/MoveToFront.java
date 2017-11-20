/******************************************************************************
 *  Compilation:  javac MoveToFront.java
 *  Execution:    java MoveToFront -/+ input
 *  Dependencies:
 *  
 *  MoveToFront module API
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import java.util.LinkedList;

public class MoveToFront {
    private static LinkedList<Character> createASCIIList() {
        LinkedList<Character> asciiList = new LinkedList<Character>();
        for (int i = 255; i >= 0; --i) {
            asciiList.addFirst((char) i);
        }
        return asciiList;
    }
    
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        LinkedList<Character> asciiList = createASCIIList();
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int index = asciiList.indexOf(c);
            asciiList.remove(index);
            asciiList.addFirst(c);
            BinaryStdOut.write(index, 8);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        LinkedList<Character> asciiList = createASCIIList();
        while (!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readChar();
            char c = asciiList.get(index);
            asciiList.remove(index);
            asciiList.addFirst(c);
            BinaryStdOut.write(c, 8);
        }
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Illegal command line argument");
        }
        if (args[0].equals("-")) { 
            encode();
        }
        else if (args[0].equals("+")) {
            decode();
        }
        else {
            throw new IllegalArgumentException("Illegal command line argument");
        }
    }
}
