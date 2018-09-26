package byog.lab5;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

public class HexTest {

    private static final int WIDTH = 60;  //height and width of the whole window
    private static final int HEIGHT = 40;
    //same seed meens same out come with random
    //private static final long SEED = 2873123;
    //private static final Random RANDOM = new Random(SEED);
    private static Random rand = new Random();

    //x and y are the top left most corner of the blanks space for the hex.
    public static void addHex(TETile[][] world, int size, int x, int y) {
        TETile tile = randomTile();
        topHex(world, tile, size, x, y);
        bottomHex(world, tile, size, x, y);
    }

    //Displays the top half of a hex and returns the width at center.
    private static void topHex(TETile[][] world, TETile tile, int size, int x, int y) {
        int bodyLength = size;
        int numBlanks = size - 1;
        for (int i = 0; i < size; i++) {
            displayBody(world, tile, bodyLength, numBlanks, x, y);
            y--;
            numBlanks--;
            bodyLength = bodyLength + 2;
        }
    }

    //Displays the bottom half of the hex.
    private static void bottomHex(TETile[][] world, TETile tile, int size,
                                  int x, int y) {
        int numBlanks = 0;
        y = y - size;
        int bodyLength = sizeAtCenter(size);
        //display body
        for (int i = 0; i < bodyLength; i++) {
            displayBody(world, tile, bodyLength, numBlanks, x, y);
            numBlanks++;
            bodyLength = bodyLength - 2;
            y--;
        }
    }

    //displays the body of the hex
    private static void displayBody(TETile[][] world, TETile tile, int bodyLength,
                                   int numBlanks, int x, int y) {
        //skips blanks
        for (int i = 0; i < numBlanks; i++) {
            x++;
        }
        //displays body
        for (int i = 0; i < bodyLength; i++) {
            world[x][y] = tile;
            x++;
        }
    }

    public static TETile[][] initializeWorld() {
        // Initialize background tiles.
        TETile[][] world = new TETile[WIDTH][HEIGHT];  //2 d array
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.FLOOR;  //builds the black window of (NOTHING)
            }
        }
        return world;
    }



    //Builds a world of random sized and colored hexes.
    public static TETile[][] initializeHexWorld() {
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int size = rand.nextInt(2) + 2;
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;  //builds background
            }
        }
        displayColumns(world, size);
        return world;
    }

    private static void displayColumns(TETile[][] world, int size) {
        int y = (HEIGHT - 1) - size * 2;
        int x = 0;
        int numHexes = 3;
        displayColumn(world, size, numHexes, x, y);
        y = (HEIGHT - 1) - size;
        x = x + (size * 2) - 1;
        numHexes++;
        displayColumn(world, size, numHexes, x, y);
        y = (HEIGHT - 1);
        x = x + (size * 2) - 1;
        numHexes++;
        displayColumn(world, size, numHexes, x, y);
        y = (HEIGHT - 1) - size;
        x = x + (size * 2) - 1;
        numHexes--;
        displayColumn(world, size, numHexes, x, y);
        y = (HEIGHT - 1) - size * 2;
        x = x + (size * 2) - 1;
        numHexes--;
        displayColumn(world, size, numHexes, x, y);
    }

    private static void displayColumn(TETile[][] world, int size, int numHexes, int x, int y) {
        for (int i = 0; i < numHexes; i++) {
            addHex(world, size, x, y);
            y = y - size * 2;
        }
    }

    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = rand.nextInt(8);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.GRASS;
            case 3: return Tileset.WATER;
            case 4: return Tileset.WALL;
            case 5: return Tileset.TREE;
            case 6: return Tileset.SAND;
            case 7: return Tileset.LOCKED_DOOR;
            default: return Tileset.MOUNTAIN;
        }
    }

    private static int sizeAtCenter(int size) {
        int center = size;
        for (int i = 1; i < size; i++) {
            center = center + 2;
        }
        return center;
    }

    //Must initialize the renderer before the world.
    //Initializes the renderer and sets the width and height class variables.
    public static TERenderer initializeRenderer() {
        TERenderer ter = new TERenderer();  //tile rendering engine
        ter.initialize(WIDTH, HEIGHT);
        return ter;
    }

    //Driver/////////////////////////////////////////////////////////
    public static void main(String[] args) {

        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();  //tile rendering engine
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world2 = initializeHexWorld();
        //addHex(world2, 3, 0, HEIGHT -1);

        // draws the world to the screen
        ter.renderFrame(world2);

    }
}


