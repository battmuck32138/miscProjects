


public class Path implements Comparable<Path> {
    double weight;
    Node end;
    int height;


    public Path(Node end, int height) {
        this.end = end;
        this.weight = end.getWeight();
        this.height = height;
    }


    @Override
    public int compareTo(Path that) {
        if (this.weight < that.weight) {
            return -1;
        }
        if (this.weight > that.weight) {
            return 1;
        }
        return 0;
    }


    public double getWeight() {
        return weight;
    }


    @Override
    public String toString() {
        return "Path{" + "weight=" + weight + ", end="
                + end + ", height=" + height + '}';
    }


    public void setWeight(double weight) {
        this.weight = weight;
    }


    public Node getEnd() {
        return end;
    }


    public void setEnd(Node end) {
        this.end = end;
    }


    public int getHeight() {
        return height;
    }


    public void setHeight(int height) {
        this.height = height;
    }


    public int[] path() {
        Node current = end;
        int[] path = new int[height];
        for (int i = height - 1; i >= 0; i--) {
            path[i] = current.getX();
            current = current.getParent();
        }
        return path;
    }


}
