package byog.Core;

import java.io.Serializable;

public class Pair implements Serializable {
    private int x;
    private int y;
    //Unique serialization number for this class.
    private static final long serialVersionUID = 1382L;



    //Constructor
    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public int getX() {
        return x;
    }


    public int getY() {
        return y;
    }


    public String toString() {
        return "(" + x + ", " + y + ") ";
    }

}
