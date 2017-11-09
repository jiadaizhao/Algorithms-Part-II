/******************************************************************************
 *  Compilation:  javac BaseballElimination.java
 *  Execution:    java BaseballElimination input.txt
 *  Dependencies:
 *  
 *  BaseballElimination module API
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.StdOut;
import java.util.HashMap;
import java.util.Arrays;

public class BaseballElimination {
    private static final double EPSILON = 0.00001;
    private final int num;
    private final String[] teams;
    private final int[] wins;
    private final int[] losses;
    private final int[] remainings;
    private final int[][] g;
    private final HashMap<String, Integer> teamToIndex;
    private final HashMap<Integer, String> indexToTeam;
    private final HashMap<String, Bag<String>> certificates;
    private int maxWin;
    private String maxWinTeam;
    
    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename is null.");
        }
        
        In in = new In(filename);
        num = in.readInt();
        teams = new String[num];
        wins = new int[num];
        losses = new int[num];
        remainings = new int[num];        
        g = new int[num][num];
        teamToIndex = new HashMap<String, Integer>();
        indexToTeam = new HashMap<Integer, String>();
        certificates = new HashMap<String, Bag<String>>();
        maxWin = 0;
        maxWinTeam = null;
        for (int i = 0; i < num; ++i) {
            teams[i] = in.readString();
            teamToIndex.put(teams[i], i);
            indexToTeam.put(i, teams[i]);
            wins[i] = in.readInt();
            if (wins[i] > maxWin) {
                maxWin = wins[i];
                maxWinTeam = teams[i];
            }
            losses[i] = in.readInt();
            remainings[i] = in.readInt();
            for (int j = 0; j < num; ++j) {
                g[i][j] = in.readInt();
            }
        }
        
    }
    
    // number of teams
    public int numberOfTeams() {
        return num;
    }
    
    // all teams
    public Iterable<String> teams() {
        return Arrays.asList(teams);
    }
    
    // number of wins for given team
    public int wins(String team) {
        if (team == null || teamToIndex.get(team) == null) {
            throw new IllegalArgumentException("team is invalid.");
        }
        return wins[teamToIndex.get(team)];
    }
    
    // number of losses for given team
    public int losses(String team) {
        if (team == null || teamToIndex.get(team) == null) {
            throw new IllegalArgumentException("team is invalid.");
        }
        return losses[teamToIndex.get(team)];
    }
    
    // number of remaining games for given team
    public int remaining(String team) {
        if (team == null || teamToIndex.get(team) == null) {
            throw new IllegalArgumentException("team is invalid.");
        }
        return remainings[teamToIndex.get(team)];
    }
    
    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (team1 == null || team2 == null || 
            teamToIndex.get(team1) == null || teamToIndex.get(team2) == null) {
            throw new IllegalArgumentException("team is invalid.");
        }
        return g[teamToIndex.get(team1)][teamToIndex.get(team2)];
    }
    
    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (team == null || teamToIndex.get(team) == null) {
            throw new IllegalArgumentException("team is invalid.");
        }
        
        if (!certificates.containsKey(team)) {
            checkEliminated(team);
        }

        return certificates.get(team) != null;
    }
    
    private FlowNetwork createFlowNetwork(int index) {
        int numGames = num * (num - 1) / 2;
        int numV = numGames + num + 2;
        int s = numV - 2;
        int t = numV - 1;
        
        int sum = wins[index] + remainings[index];
        FlowNetwork fn = new FlowNetwork(numV);
        for (int i = 0; i < num; ++i) {
            fn.addEdge(new FlowEdge(i, t, sum - wins[i]));
        }
        
        for (int i = 0, v = num; i < num; ++i) {
            for (int j = i + 1; j < num; ++j) {
                fn.addEdge(new FlowEdge(s, v, g[i][j]));
                fn.addEdge(new FlowEdge(v, i, Double.POSITIVE_INFINITY));
                fn.addEdge(new FlowEdge(v, j, Double.POSITIVE_INFINITY));
                ++v;
            }
        }
        
        return fn;
    }
    
    private boolean isFullFromSource(FlowNetwork fn, int s) {
        for (FlowEdge fe : fn.adj(s)) {
            if (Math.abs(fe.flow() - fe.capacity()) > EPSILON) {
                return false;
            }
        }
        return true;
    }
    
    private void checkEliminated(String team) {
        int index = teamToIndex.get(team);
        if (wins[index] + remainings[index] < maxWin) {
            Bag<String> b = new Bag<String>();
            b.add(maxWinTeam);
            certificates.put(team, b);
        }
        else {
            FlowNetwork fn = createFlowNetwork(index);
            int s = fn.V() - 2;
            int t = fn.V() - 1;
            FordFulkerson ff = new FordFulkerson(fn, s, t);
            if (isFullFromSource(fn, s)) {
                certificates.put(team, null);
            }
            else {                
                Bag<String> b = new Bag<String>();
                for (int i = 0; i < num; ++i) {
                    if (ff.inCut(i)) {
                        b.add(indexToTeam.get(i));
                    }
                }
                certificates.put(team, b);
            }
        }
    }
    
    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (team == null || teamToIndex.get(team) == null) {
            throw new IllegalArgumentException("team is invalid.");
        }
        
        if (!certificates.containsKey(team)) {
            checkEliminated(team);
        }
        
        return certificates.get(team);
    }
    
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
