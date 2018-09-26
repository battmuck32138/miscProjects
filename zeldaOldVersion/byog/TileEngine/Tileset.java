package byog.TileEngine;

import java.awt.Color;
import java.io.Serializable;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */


public class Tileset implements Serializable {
    //Unique serialization number for this class.
    private static final long serialVersionUID = 1386L;
    public static final TETile WALL = new TETile('#',
            new Color(216, 128, 128), Color.darkGray, "Wall");

    public static final TETile FLOOR = new TETile('·',
            new Color(128, 192, 128), Color.black, "Floor");

    public static final TETile NOTHING = new TETile(' ',
            Color.black, Color.black, "Nothing");

    public static final TETile GRASS = new TETile('"',
            Color.green, Color.black, "Grass");

    public static final TETile WATER = new TETile('≈',
            Color.blue, Color.black, "Water, Look for a bridge.");

    public static final TETile TRIFORCE = new TETile('▼',
            Color.yellow, Color.black,
            "TRIFORCE, Link needs this to save the realm!");

    public static final TETile FLOWER = new TETile('❀',
            Color.magenta, Color.black,
            "Demon Flower, if you touch these the ghosts get mad!");

    public static final TETile BEANSTALK = new TETile('֍',
            Color.black, Color.green,
            "Magic beanstalk, climb this to freedom after you "
                    + "have the Triforce.");

    public static final TETile SWIMPOTION = new TETile('Ϫ',
            Color.black, Color.blue,
            "Swim Potion, Link can swim to the temple if he drinks this.");


    public static final TETile ROPE = new TETile('Ϭ',
            Color.black, Color.orange,
            "Rope, Link can climb through the mountains with this.");


    public static final TETile WINE = new TETile('w',
            Color.black, Color.red,
            "Wine, the world seems to move a little slower after a drink.");


    public static final TETile LOCKED_DOOR = new TETile('▄',
            Color.black, Color.yellow,
            "Locked Door, you need the triforce to get in.");

    public static final TETile CAVE = new TETile('☻',
            Color.black, Color.black,
            "Cave, climb the beanstalk and escape!");

    public static final TETile BRIDGE = new TETile('▒', Color.cyan,
            Color.black, "Bridge, cross it and head for the Temple door.");

    public static final TETile MOUNTAIN = new TETile('▲', Color.gray,
            Color.black, "Misty Mountains, Link can't walk on these.");

    public static final TETile TREE = new TETile('♠', Color.green,
            Color.black, "Forrest, Link loves trees cuz he's a Woodland Elf!");

    public static final TETile LINK = new TETile('L', Color.BLACK,
            Color.green, "LINK, A brave little elf!");

    public static final TETile REDGHOST = new TETile('҈', Color.black,
            Color.red, "REDGHOST, kinda scary.");

    public static final TETile SKULL = new TETile('☠', Color.black,
            Color.white, "SKULL, super spooky!");

    public static final TETile GHOSTKING = new TETile('♛', Color.black,
            Color.blue, "HUG the Ghost King, watch out!");

    public static final TETile SUNGHOST = new TETile('Ѫ', Color.black,
            Color.orange, "Sun Ghost, he hates the dungeon.");

    public static final TETile FULLHEART = new TETile('❤', Color.red,
            Color.black, "FULLHEART");

    public static final TETile EMPTYHEART = new TETile('♡', Color.red,
            Color.black, "EMPTYHEART");

    public static final TETile TEMPLE = new TETile('ʘ', Color.black,
            Color.lightGray, "Triforce Temple, return the "
            + "Tiforce to the Knights of Highrule here.");


}

