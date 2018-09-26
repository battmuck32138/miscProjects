package byog.Core;

import java.io.Serializable;
import java.util.ArrayList;

public class Path implements Serializable {
    private String orientation;
    private int roomA;
    private int roomB;
    private ArrayList<Pair> pathList = new ArrayList<>();
    //Unique serialization number for this class.
    private static final long serialVersionUID = 1383L;


    //Finds the biggest x and Y and returns them as a pair.
    protected Pair getBiggest() {
        int biggestX = 0;
        int biggestY = 0;
        Pair xy;
        for (int i = 0; i < pathList.size(); i++) {
            xy = pathList.get(i);
            if (xy.getX() > biggestX) {
                biggestX = xy.getX();
            }
            if (xy.getY() > biggestY) {
                biggestY = xy.getY();
            }
        }
        Pair biggest = new Pair(biggestX, biggestY);
        return biggest;
    }


    //Finds the smallest x and Y and returns them as a pair.
    protected Pair getSmallest() {
        int smallestX = pathList.get(0).getX();
        int smallestY = pathList.get(0).getY();
        Pair xy;
        for (int i = 0; i < pathList.size(); i++) {
            xy = pathList.get(i);
            if (xy.getX() < smallestX) {
                smallestX = xy.getX();
            }
            if (xy.getY() < smallestY) {
                smallestY = xy.getY();
            }
        }
        Pair smallest = new Pair(smallestX, smallestY);
        return smallest;
    }


    public String getOrientation() {
        return orientation;
    }


    public int getRoomA() {
        return roomA;
    }


    public int getRoomB() {
        return roomB;
    }


    public ArrayList<Pair> getList() {
        return pathList;
    }


    public void addPair(Pair pair) {
        pathList.add(pair);
    }


    public void setRoomA(int roomNumber) {
        roomA = roomNumber;
    }


    public void setRoomB(int roomNumber) {
        roomB = roomNumber;
    }


    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }


    public String toString() {
        String s = "roomA: " + roomA + "  roomB: " + roomB
                        + "  orientation: " + orientation + "  Path: ";
        for (Pair xy : pathList) {
            s += xy.toString() + " ";
        }
        return s;
    }


}

