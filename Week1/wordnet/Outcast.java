/******************************************************************************
 *  Compilation:  javac Outcast.java
 *  Execution:    java Outcast input
 *  Dependencies: WordNet.java
 *  
 *  Outcast module API
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        wordNet = wordnet;
    }
    
    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int[] distance = new int[nouns.length];  
        for (int i = 0; i < nouns.length; ++i) {  
            for (int j = i + 1; j < nouns.length; ++j) {  
                int dist = wordNet.distance(nouns[i], nouns[j]);  
                distance[i] += dist;  
                distance[j] += dist;  
            }  
        }  
        int maxDist = 0;  
        String result = null; 
        for (int i = 0; i < distance.length; ++i) {  
            if (distance[i] > maxDist) {  
                maxDist = distance[i];  
                result = nouns[i];  
            }  
        }  
        return result; 
    }
    
    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
