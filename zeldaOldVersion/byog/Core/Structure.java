package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;


//A structure is a series of 1 or more rooms, connected by hallways.
//After all the rooms have been drawn, the hallways are drawn to
//connect the rooms.
public class Structure implements Serializable {
    private final double roomsToSpace = 0.3;  //Adjust for a denser structure.
    private int roomId = 1;  //Each room has unique id
    private int floorTilesUsed = 0;
    //Contains all possible paths between rooms.
    private ArrayList<Path> hallways = new ArrayList<>();
    private boolean inside = false;
    private boolean drawingHallway = false;
    private TETile[][] world;
    private Random rand;
    private int maxWall;
    private final int structureArestaWidth;
    private final int height;
    private int[][] rooms;  //(x, y) contains the roomNumber of the room drawn there.
    //Unique serialization number for this class.
    private static final long serialVersionUID = 1384L;

    //Constructor
    public Structure(TETile[][] world, Random rand, int maxWall,
                     int width, int height, TERenderer ter) {
        this.world = world;
        this.rand = rand;
        this.maxWall = maxWall;
        this.structureArestaWidth = (width / 3);  //Leave half the world for other stuff;
        this.height = height - 1;  //Leave header at top.
        this.rooms = new int[width][height];  //Leave half the world for other stuff;
    }


    //Adds all of the features to the world.
    public void buildSingleStructure() {
        //Build dungeon structure.
        while (!hasEnoughRooms(roomsToSpace)) { //bug?
            drawSingleRoom();
            roomId++;
        }
        connectRooms();
    }

    
    //Returns true if the world is saturated with structures.
    //False if there is still more room to build based on rate.
    private boolean hasEnoughRooms(double rate) {
        double roomToSpace = floorTilesUsed / (double) (structureArestaWidth * height);
        return roomToSpace > rate;
    }



    //Draws a room object on the world.
    private int[][] drawSingleRoom() {
        int x;
        int y;
        int roomWidth;
        int roomHeight;
        do {
            Pair location = findLocation();
            Pair dimensions = roomDimensions();
            x = location.getX();
            y = location.getY();
            roomWidth = dimensions.getX();
            roomHeight = dimensions.getY();
        } while (!goodLocation(x, y, roomWidth, roomHeight));
        drawRoomWalls(x, y, roomWidth, roomHeight);
        drawRoomFloor(x, y, roomWidth, roomHeight);
        return rooms;
    }


    //Draws hallways between rooms randomly until all of the rooms
    //that CAN BE CONNECTED are connected using union find.
    private HashSet<Integer> connectRooms() {
        UnionFind uf;
        HashSet<Integer> connectedRooms = new HashSet<>();
        //Try paths at random until all rooms are connected;
        //Throw away the path after i've tried it.
        for (int i = 0; i < 2; i++) {
            findPaths();
            uf = new UnionFind(roomId);
            while (hallways.size() > 0) {
                int index = rand.nextInt(hallways.size());
                Path path = hallways.get(index);
                int roomA = path.getRoomA();
                int roomB = path.getRoomB();
                //If the rooms aren't already connected in someway, draw the hallway.
                if (uf.union(roomA, roomB)) {
                    drawSingleHallway(path);
                    connectedRooms.add(path.getRoomA());
                    connectedRooms.add(path.getRoomB());
                } else {
                    hallways.remove(index);
                }
            }
        }
        return connectedRooms;
    }


    //Finds all paths between all rooms and stores them in
    //the hallways class variable.
    private ArrayList<Path> findPaths() {
        findHorizontalPaths();
        findVerticalPaths();
        return hallways;
    }


    //Draws full block for walls.
    private void drawRoomWalls(int x, int y, int roomWidth, int roomHeight) {
        int bottomOfRoom = y - roomHeight;
        int hz = x;
        int vert = y;
        while (vert > bottomOfRoom) {
            drawRow(hz, vert, roomWidth, Tileset.WALL);
            vert--;
        }
    }


    //Draws smaller block for floor.
    private void drawRoomFloor(int x, int y, int roomWidth, int roomHeight) {
        inside = true;
        int hz = x + 1;
        roomWidth = roomWidth - 2;
        int vert = y - 1;
        roomHeight = roomHeight - 1;
        int bottomOfRoom = y - roomHeight;
        while (vert > bottomOfRoom) {
            drawRow(hz, vert, roomWidth, Tileset.FLOOR);
            vert--;
        }
        inside = false;
    }


    //Draws one row of room or hallway object.
    //Does NOT draw over floor tiles.
    private void drawRow(int x, int y, int rowWidth, TETile tile) {
        int rightCorner = x + rowWidth;
        while (x < rightCorner) {
            //Don't draw over floor tiles.
            if (!world[x][y].equals(Tileset.FLOOR)) {
                world[x][y] = tile;
                if (!drawingHallway) {
                    rooms[x][y] = roomId;  //Add number for room location.
                }
                if (!inside) {
                    floorTilesUsed++;
                }
            }
            x++;
        }
    }


    //Draws a Hallway object.
    private void drawSingleHallway(Path hallway) {
        drawingHallway = true;
        String direction = hallway.getOrientation();
        int roomA = hallway.getRoomA();
        int roomB = hallway.getRoomB();
        int x;
        int y;
        int roomWidth;
        int roomHeight;

        //Set up for horizontal hallway.
        if (direction.equals("hz")) {
            x = hallway.getSmallest().getX();
            y = hallway.getSmallest().getY() + 1;  //+1 for wall thickness.
            roomWidth = hallway.getBiggest().getX() - x + 1;
            roomHeight = 3;
        } else {  //Set up for vertical hallway.
            x = hallway.getBiggest().getX() - 1;  //-1 for wall thickness.
            y = hallway.getBiggest().getY();
            roomWidth = 3;
            roomHeight = y - hallway.getSmallest().getY() + 1;
        }
        //Draw block for walls.
        int bottomOfRoom = y - roomHeight;
        int hz = x;
        int vert = y;
        while (vert > bottomOfRoom) {
            drawRow(hz, vert, roomWidth, Tileset.WALL);
            vert--;
        }
        //Draw smaller block for floor.
        inside = true;
        //Hallway object is horizontal.
        if (direction.equals("hz")) {
            vert = y - 1;
            drawRow(hz, vert, roomWidth, Tileset.FLOOR);
        } else {  //Hallway object is vertical.
            hz = x + 1;
            roomWidth = 1;
            vert = y;
            bottomOfRoom = y - roomHeight;
            while (vert > bottomOfRoom) {
                drawRow(hz, vert, roomWidth, Tileset.FLOOR);
                vert--;
            }
        }
        inside = false;
        drawingHallway = false;
        roomId++; 
    }


    //Chooses a random location for the structure.
    private Pair findLocation() {
        int x;
        int y;
        Pair location;
        x = rand.nextInt(structureArestaWidth - maxWall) + 2;
        y = rand.nextInt(height - maxWall - 4) + maxWall + 2;
        location = new Pair(x, y);
        return location;
    }


    //Calculates random room dimensions;
    //Minimum wall size is currently 4, i.e. (+ 4).
    //maxWall size is set in Game.java.
    private Pair roomDimensions() {
        int roomWidth = rand.nextInt(maxWall - 3) + 4;
        int roomHeight = rand.nextInt(maxWall - 3) + 4;
        Pair dimensions = new Pair(roomWidth, roomHeight);
        return dimensions;
    }


    //Returns false if there is another room in the same
    //area as the proposed room.
    private boolean goodLocation(int x, int y,
                                 int roomWidth, int roomHeight) {
        int hz = x;
        int vert = y;
        int stopX = x + roomWidth;
        int stopY = y - roomHeight;
        while (hz < stopX) {
            vert = y;
            while (vert > stopY) {
                if (rooms[hz][vert] != 0) {
                    return false;
                }
                vert--;
            }
            hz++;
        }
        return true;
    }


    //Calculates and collects the paths between rooms on x-axis.
    private void findHorizontalPaths() {
        boolean inRoom1 = false;
        boolean inRoom2 = false;
        boolean onPath = false;
        boolean lock1 = false;
        boolean lock2 = false;
        boolean lock3 = false;
        boolean lock4 = false;
        Path pathObj = new Path();
        //Find all horizontal paths.
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < structureArestaWidth; x++) {
                //In room1
                if (world[x][y].equals(Tileset.FLOOR) && !lock1) {  //&& (lock1 == false)
                    //lock1 = true;
                    inRoom1 = true;

                }
                //Leaving room1, start my path here.
                if (world[x][y].equals(Tileset.WALL) && inRoom1
                        && !lock2) {
                    pathObj.setRoomA(rooms[x][y]);
                    onPath = true;
                    lock2 = true;
                }
                //In here whenever I'm on a path.
                if (onPath && !lock3) {
                    Pair xy = new Pair(x, y);
                    pathObj.addPair(xy);  //Store locations.
                }
                //Made it to another room if I hit a wall and the next tile is
                // floor. Save the path.
                if (world[x][y].equals(Tileset.WALL) && inRoom1 && onPath
                        && world[x + 1][y].equals(Tileset.FLOOR) && !lock4) {
                    pathObj.setRoomB(rooms[x][y]);  //This is where the hall will stop.
                    pathObj.setOrientation("hz");  //Set the hz.
                    hallways.add(pathObj);  //Store the path in a static class list.
                    lock3 = true;  //Close off the path.
                    lock4 = true;
                    inRoom2 = true;
                }
                //Left the second room, reset for next path.
                if (world[x][y].equals(Tileset.WALL) && inRoom1 && inRoom2) {
                    pathObj = new Path();
                    inRoom1 = false;
                    onPath = false;
                    lock1 = false;
                    lock2 = false;
                    lock3 = false;
                    lock4 = false;
                }
            }
            //end of a row, reset and start over.
            pathObj = new Path();
            inRoom1 = false;
            inRoom2 = false;
            onPath = false;
            lock1 = false;
            lock2 = false;
            lock3 = false;
            lock4 = false;
        }
    }


    //Calculates and collects the paths between rooms on y-axis.
    private void findVerticalPaths() {
        boolean inRoom1 = false;
        boolean inRoom2 = false;
        boolean onPath = false;
        boolean lock1 = false;
        boolean lock2 = false;
        boolean lock3 = false;
        boolean lock4 = false;
        Path pathObj = new Path();
        //Find all vertical paths.
        for (int x = 0; x < structureArestaWidth; x++) {
            for (int y = height - 1; y > 0; y--) {
                //In room1
                if (world[x][y].equals(Tileset.FLOOR) && !lock1) {
                    lock1 = true;
                    inRoom1 = true;
                }
                //Leaving room1, start my path here.
                if (world[x][y].equals(Tileset.WALL) && inRoom1
                        && !lock2) {
                    pathObj.setRoomA(rooms[x][y]);
                    onPath = true;
                    lock2 = true;
                }
                //In here whenever I'm on a path.
                if (onPath && !lock3) {
                    Pair xy = new Pair(x, y);
                    pathObj.addPair(xy);  //Store locations.
                }
                //Made it to another room if I hit a wall, and the tile
                // after the wall is floor.  Save the path.
                if (world[x][y].equals(Tileset.WALL) && inRoom1 && onPath
                        && world[x][y - 1].equals(Tileset.FLOOR) && !lock4) {
                    pathObj.setRoomB(rooms[x][y]);  //This is where the hall will stop.
                    pathObj.setOrientation("vert");  //Set the hz.
                    hallways.add(pathObj);  //Store the path in a static class list.
                    lock3 = true;  //Close off the path.
                    lock4 = true;
                    inRoom2 = true;
                }
                //Left the second room, reset for next path.
                if (world[x][y].equals(Tileset.WALL) && inRoom1 && inRoom2) {
                    pathObj = new Path();
                    inRoom1 = false;
                    onPath = false;
                    lock1 = false;
                    lock2 = false;
                    lock3 = false;
                    lock4 = false;
                }
            }
            //end of a row, reset and start over.
            pathObj = new Path();
            inRoom1 = false;
            inRoom2 = false;
            onPath = false;
            lock1 = false;
            lock2 = false;
            lock3 = false;
            lock4 = false;
        }
    }










}
