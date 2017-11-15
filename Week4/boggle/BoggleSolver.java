/******************************************************************************
 *  Compilation:  javac BoggleSolver.java
 *  Execution:    java BoggleSolver input1.txt input2.txt
 *  Dependencies: BoggleBoard.java
 *  
 *  BoggleSolver module API
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.HashSet;

public class BoggleSolver {
    private static final int[] DR = {-1, 0, 1, -1, 1, -1, 0, 1};
    private static final int[] DC = {-1, -1, -1, 0, 0, 1, 1, 1};    
    private final TrieNode root;
    private class TrieNode {
        public TrieNode[] next = new TrieNode[26];
        public boolean isEnd = false;
    }

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        root = new TrieNode();
        for (String s : dictionary) {
            insert(s);
        }
    }

    private void insert(String s) {
        TrieNode node = root;
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (node.next[c - 'A'] == null) {
                node.next[c - 'A'] = new TrieNode();
            }
            node = node.next[c - 'A'];
        }
        node.isEnd = true;
    }
    
    private int getScore(String s) {
        int len = s.length();
        
        if (len <= 2) {
            return 0;
        }
        else if (len <= 4) {
            return 1;
        }
        else if (len == 5) {
            return 2;
        }
        else if (len == 6) {
            return 3;
        }
        else if (len == 7) {
            return 5;
        }
        else {
            return 11;
        }        
    }

    private void dfs(BoggleBoard board, int i, int j, TrieNode curr, String path, 
                     HashSet<String> words, boolean[][] visited) {
        char c = board.getLetter(i, j);
        if (curr == null || curr.next[c - 'A'] == null) {
            return;
        }
        
        String str;
        if (c == 'Q') {
            str = path + "QU";
            curr = curr.next['Q' - 'A'];
            if (curr.next['U' - 'A'] == null) {
                return;
            }
            curr = curr.next['U' - 'A'];
        }
        else {
            str = path + c;
            curr = curr.next[c - 'A'];
        }

        if (curr.isEnd && str.length() > 2) {
            words.add(str);
        }
        
        visited[i][j] = true;
        int m = board.rows();
        int n = board.cols();
        for (int k = 0; k < DR.length; ++k) {
            int row = i + DR[k];
            int col = j + DC[k];
            if (row >= 0 && row < m && col >= 0 && col < n && !visited[row][col]) {
                dfs(board, row, col, curr, str, words, visited);
            }
        }        
        visited[i][j] = false;
    }
    
    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        int m = board.rows();
        int n = board.cols();
        HashSet<String> words = new HashSet<String>();
        boolean[][] visited = new boolean[m][n];

        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                dfs(board, i, j, root, "", words, visited);
            }
        }
        
        return words;
    }

    private boolean contains(String s) {
        TrieNode node = root;
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (node.next[c - 'A'] == null) {
                return false;
            }
            node = node.next[c - 'A'];
        }
        return node.isEnd;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (contains(word)) {
            return getScore(word);
        }
        else {
            return 0;
        }
    }
    
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        int count = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            ++count;
            score += solver.scoreOf(word);
        }
        StdOut.println("Count = " + count);
        StdOut.println("Score = " + score);
    }
}
