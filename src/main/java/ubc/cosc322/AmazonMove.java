package ubc.cosc322;

import java.util.ArrayList;

/**
 * Custom class to store a complete Amazon move (0-indexed).
 * Includes helper methods to convert back to 1-indexed server format.
 */
public class AmazonMove {
    public int[] qStart;
    public int[] qEnd;
    public int[] arrow;

    public AmazonMove(int[] start, int[] end, int[] arr) {
        this.qStart = start;
        this.qEnd = end;
        this.arrow = arr;
    }

    // Method to convert coordinates back to 1-10 format for server sending
    public ArrayList<Integer> getServerQStart() { return toServerList(qStart); }
    public ArrayList<Integer> getServerQEnd()   { return toServerList(qEnd); }
    public ArrayList<Integer> getServerArrow()  { return toServerList(arrow); }

    private ArrayList<Integer> toServerList(int[] coord) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(coord[0] + 1); // Apply +1 offset for server
        list.add(coord[1] + 1);
        return list;
    }
}