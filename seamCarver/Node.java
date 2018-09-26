

public class Node implements Comparable<Node> {

    private int x;
    private int y;
    private double energy;
    private Node parent = null;
    private double weight;


    public Node(int x, int y, double energy) {
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.weight = energy;
    }

    public int getX() {
        return x;
    }

    @Override
    public String toString() {
        return "Node{" + "x=" + x + ", y=" + y + ", energy="
                + energy + ", parent=" + parent + '}';
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public void setParent(Node parent) {
        this.parent = parent;
        if (parent != null) {
            weight += parent.weight;
        }
    }

    public double getWeight() {
        return weight;
    }
    public int getY() {

        return y;
    }

    public double getEnergy() {
        return energy;
    }

    public Node getParent() {
        return parent;
    }


    @Override
    public int compareTo(Node that) {
        if (this.energy < that.energy) {
            return -1;
        }
        if (this.energy > that.energy) {
            return 1;
        }
        return 0;
    }

}

