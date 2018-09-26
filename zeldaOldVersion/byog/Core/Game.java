package byog.Core;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;


//Zelda II rip-off, tile style layout game.
//The game scales to allow for different sized worlds.
//width can range from 45 to whatever your screen can handle (95ish for my laptop).
//height can range from 35 to whatever your screen can handle (47ish for my laptop).
public class Game implements Serializable {
    private final int windowWidth = 95;
    private final int windowHeight = 48;
    private final int worldWidth = windowWidth;
    private final int worldHeight = windowHeight - 3;
    //Adjust to change room sizes.
    private final int maxWall = Math.min(worldWidth, worldHeight) / 5;
    private TERenderer ter = new TERenderer();
    private TETile[][] world;
    //rand is re-assigned to a seeded rand for playWithInputString
    private Random rand = new Random();
    private long seed = 0;  //Initialized in playWithInputString or playWithKeyboard.
    private ArrayList<Character> inputStringCommands;  //Play with input Sting only
    private ArrayList<TETile> walkableTiles = new ArrayList<>();
    private int mouseX = 0;
    private int mouseY = 0;
    private int ghostSpeed = 4;
    private int slow = 0;
    private Pair beanStalkLocation;
    private Pair linkLocation;
    private Pair skullLoc;
    private Pair redLoc;
    private Pair sunLoc;
    private Pair kingLoc;
    private TETile linkPrevTile = Tileset.FLOOR;
    private TETile skullPrevTile = Tileset.FLOOR;
    private TETile redPrevTile = Tileset.GRASS;
    private TETile sunPrevTile = Tileset.GRASS;
    private TETile kingPrevTile = Tileset.GRASS;
    private char skullDirection = 'w';
    private char redGhostDirection = 's';
    private char sunDirection = 'e';
    private char kingDirection = 'n';
    private int skullSteps = ghostBehavior();
    private int redSteps = ghostBehavior();
    private int sunSteps = ghostBehavior();
    private int kingSteps = ghostBehavior();
    private int numberOfSkullLoops = 0;
    private int numberOfRedLoops = 0;
    private int numberOfSunLoops = 0;
    private int numberOfKingLoops = 0;
    private boolean gameLoopOn = true;
    private boolean hasTriforce = false;
    private boolean hasRope = false;
    private boolean hasSwimPotion = false;
    private boolean loadedGame = false;
    //Unique serialization number for this class.
    private static final long serialVersionUID = 1381L;

    //Scale for menu screen texts.
    private Font giantFont = new Font("Arial", Font.BOLD, (windowHeight * 2));
    private Font mediumFont = new Font("Arial", Font.PLAIN, windowHeight);
    private Font smallFont = new Font("Ariel", Font.PLAIN, (windowHeight / 4) * 3);
    //This is the size (14) that the renderFrame() uses.
    //Don't change it or the world will look funny.
    private Font defaultFontFromRenderer = new Font("Monaco", Font.BOLD, 14);
    private int titleFirstLineY = windowHeight - (windowHeight / 15) * 2;
    private int titleSecondLineY = titleFirstLineY - (windowHeight / 15) * 2;
    private int menuLine1Y = (windowHeight / 10) * 7;
    private int menuLine2Y = menuLine1Y - (windowHeight / 10);
    private int menuLine3Y = menuLine2Y - (windowHeight / 10);
    private int menuLine4Y = menuLine3Y - (windowHeight / 10);
    private int menuLine5Y = menuLine4Y - (windowHeight / 10);
    private int menuLine6Y = menuLine5Y - (windowHeight / 10);
    private int menuLine7Y = menuLine6Y - (windowHeight / 10);


    //////////////////////////////////////////////////////////////////////////////////////
    //PLAY WITH KEYBOARD METHODS  *******************************************************
    //////////////////////////////////////////////////////////////////////////////////////

    //Method used for playing a fresh game. The game starts from the main menu.
    public void playWithKeyboard() {
        //Renderer for the menu screen is full sized w*h.
        ter.initialize(windowWidth, windowHeight);
        displayStartScreen(' ');
        solicitCommandFromUser();
    }


    //Runs everything.
    //Listens for legal commands while the gameLoop is on.
    private void startGameLoop() {
        char commandChar = ' ';  //Not a legal command.
        StdDraw.enableDoubleBuffering();
        while (gameLoopOn) {
            //Listen, start workflow for a legal command.
            if (StdDraw.hasNextKeyTyped()) {
                commandChar = StdDraw.nextKeyTyped();
            }
            if (isLegalGameLoopCommand(commandChar)) {
                commandChar = Character.toUpperCase(commandChar);
                commandFlowForGameLoop(commandChar);
            }
            moveKingGhost();
            moveSunGhost();
            moveSkull();
            moveRedGhost();
            updateMouseLocation();  //Updates the HUD of the mouse moves.
            drawEverything();
            commandChar = ' ';  //update to a non-legal command until the next key stroke.
            if (slow > 0) {
                StdDraw.pause(100);
                slow--;
            }
        }
    }


    //The displayHud() will display the description
    // of the tile located at mouseX mouseY.
    private void updateMouseLocation() {
        double x = StdDraw.mouseX();
        double y = StdDraw.mouseY();
        if (x < worldWidth && x > -1 && y < worldHeight && y > -1) {
            mouseX = (int) x;
            mouseY = (int) y;
        }
    }


    //Takes care of all the things that need to happen if a saved game is loaded.
    public void startLoadedGame() {
        ter.initialize(windowWidth, windowHeight);
        drawEverything();
        loadedGame = false;
        gameLoopOn = true;
        System.out.println("From startLoadGame(), Link is at: " + linkLocation);
        startGameLoop();
    }


    //Handles work flow for main menu
    private void commandFlowMenuScreens(char commandChar) {
        commandChar = Character.toUpperCase(commandChar);
        switch (commandChar) {

            case 'N':  //New Game, display empty input String.
                displaySeedScreen("");
                seed = extractSeedFromUser();
                rand = new Random(seed);
                //Must Initialize again for the world.
                ter.initialize(windowWidth, windowHeight);
                world = initializeWorld();
                buildUpWorld();
                drawEverything();  //First time the world is drawn.
                startGameLoop();  //Every thing runs from here.
                break;

            case 'L':  //Load a saved game file.
                loadedGame = true;
                gameLoopOn = false;
                break;

            case 'Q':  //Quit and save game
                //Nothing to save when you quit from the menu screen.
                System.exit(0);
                break;

            default:
                break;
        }
    }


    //Does not stop soliciting until the user enters a legal menu command.
    private void solicitCommandFromUser() {
        char inputChar = listenForCharFromUser();
        if (inputChar != ':') {
            inputChar = Character.toUpperCase(inputChar);
        }
        if (inputChar == 'N') {
            displayStartScreen(inputChar);
            StdDraw.pause(500);  //Let user see his entry.
            commandFlowMenuScreens(inputChar);
        } else if (inputChar == 'L') {
            displayStartScreen(inputChar);
            StdDraw.pause(500);  //Let user see his entry.
            loadedGame = true;
            commandFlowMenuScreens(inputChar);
        } else if (inputChar == ':') {
            displayStartScreen(inputChar);
            StdDraw.pause(500);
            char secondChar = listenForCharFromUser();
            secondChar = Character.toUpperCase(secondChar);
            if (secondChar == 'Q') {
                displayStartScreen(secondChar);
                StdDraw.pause(500);
                commandFlowMenuScreens(secondChar);
            } else {
                displayStartScreen(' ');
                StdDraw.pause(500);
                solicitCommandFromUser();
            }
        } else {
            displayStartScreen(inputChar);
            StdDraw.pause(500);
            displayStartScreen(' ');
            StdDraw.pause(500);
            solicitCommandFromUser();
        }
    }


    //Builds the seed string from user input and stores it in the object variable.
    //Doesn't allow the user to leave until a legal command is entered.
    private long extractSeedFromUser() {
        char inputChar = ' ';
        String seedString = "";
        boolean needFirstDigit = true;
        long seedLong = 1L;
        boolean haveNoS = true;
        while (needFirstDigit) {
            displaySeedScreen(seedString);
            inputChar = listenForCharFromUser();
            displaySeedScreen(seedString + Character.toString(inputChar));
            if (Character.isDigit(inputChar)) {  //Must be a digit.
                seedString += Character.toString(inputChar);
                displaySeedScreen(seedString);
                needFirstDigit = false;
            } else {
                displaySeedScreen("Enter an integer.");
                StdDraw.pause(500);
                extractSeedFromUser();
            }
        }
        int numDigits = 0;
        while (haveNoS && numDigits < 9) {
            inputChar = listenForCharFromUser();
            if (Character.isDigit(inputChar)) {
                seedString += Character.toString(inputChar);
                displaySeedScreen(seedString);
                seedLong = Long.parseLong(seedString);
            }
            if (inputChar == 's' || inputChar == 'S') {
                haveNoS = false;
                displaySeedScreen("S");
                StdDraw.pause(500);
            }
            numDigits++;
        }
        displaySeedScreen("Find the Triforce (Gold Triangle) "
                + "then head for the green beanstalk!");
        StdDraw.pause(6000);
        return seedLong;
    }


    //Listens for a char then returns it.
    private char listenForCharFromUser() {
        boolean playersTurn = true;
        char inputChar = ' ';  //Dummy value is never used.
        while (playersTurn) {
            StdDraw.pause(100);
            if (StdDraw.hasNextKeyTyped()) {
                inputChar = StdDraw.nextKeyTyped();
                playersTurn = false;
            }
        }
        return inputChar;
    }


    //Returns true if a moving character will hit a wall on his next step based on
    //it's current direction.  False otherwise.
    //Warps Link out of the dungeon if he hit the bean stalk.
    private boolean linkHitObject(char direction) {
        int x = linkLocation.getX();
        int y = linkLocation.getY();
        switch (direction) {
            case 'n':
                if (world[x][y + 1].equals(Tileset.LOCKED_DOOR) && hasTriforce) {
                    displayVictoryScreen();  //Player wins.
                }
                if (world[x][y + 1].equals(Tileset.TRIFORCE)) {  //Link has the Triforce.
                    hasTriforce = true;
                    world[x][y + 1] = Tileset.FLOOR;
                    displayTriforceScreen();
                    return true;
                }
                if (world[x][y + 1].equals(Tileset.BEANSTALK) && hasTriforce) {
                    escapeFromDungeon();  //Escape from dungeon.
                    return true;
                }
                if (!walkableTiles.contains(world[x][y + 1])) {
                    return true;
                }
                break;
            case 's':
                if (world[x][y - 1].equals(Tileset.LOCKED_DOOR) && hasTriforce) {
                    displayVictoryScreen();
                }
                if (world[x][y - 1].equals(Tileset.TRIFORCE)) {
                    hasTriforce = true;
                    world[x][y - 1] = Tileset.FLOOR;
                    displayTriforceScreen();
                    return true;
                }
                if (world[x][y - 1].equals(Tileset.BEANSTALK) && hasTriforce) {
                    escapeFromDungeon();
                    return true;
                }
                if (!walkableTiles.contains(world[x][y - 1])) {
                    return true;
                }
                break;
            case 'e':
                if (world[x + 1][y].equals(Tileset.LOCKED_DOOR) && hasTriforce) {
                    displayVictoryScreen();
                }
                if (world[x + 1][y].equals(Tileset.TRIFORCE)) {
                    hasTriforce = true;
                    world[x + 1][y] = Tileset.FLOOR;
                    displayTriforceScreen();
                    return true;
                }
                if (world[x + 1][y].equals(Tileset.BEANSTALK) && hasTriforce) {
                    escapeFromDungeon();
                    return true;
                }
                if (!walkableTiles.contains(world[x + 1][y])) {
                    return true;
                }
                break;
            case 'w':
                if (world[x - 1][y].equals(Tileset.LOCKED_DOOR) && hasTriforce) {
                    displayVictoryScreen();
                }
                if (world[x - 1][y].equals(Tileset.TRIFORCE)) {
                    hasTriforce = true;
                    world[x - 1][y] = Tileset.FLOOR;
                    displayTriforceScreen();
                    return true;
                }
                if (world[x - 1][y].equals(Tileset.BEANSTALK) && hasTriforce) {
                    escapeFromDungeon();
                    return true;
                }
                if (!walkableTiles.contains(world[x - 1][y])) {
                    return true;
                }
                break;
            default:
        }
        return false;
    }


    //If Link finds the beanStalk, and he has the Triforce then he
    // escapes up the beanstalk and outside the dungeon.
    private void escapeFromDungeon() {
        if (hasTriforce) {
            int x = linkLocation.getX();
            int y = linkLocation.getY();
            world[x][y] = Tileset.FLOOR;
            x = beanStalkLocation.getX() + 1;
            y = beanStalkLocation.getY();
            linkLocation = new Pair(x, y);
            world[x][y] = Tileset.LINK;
        }
    }


    //Displays the game title and menu to the screen.
    private void displayStartScreen(char inputChar) {
        StdDraw.enableDoubleBuffering();
        //Always clear the screen before drawing.
        StdDraw.clear(Color.BLACK);
        //Draw the Title.
        StdDraw.setFont(giantFont);
        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text(worldWidth / 2, titleFirstLineY, "The Legend of Zelda ▲");
        StdDraw.text(worldWidth / 2, titleSecondLineY, "Quest for the Triforce");
        //Draw the menu options.
        StdDraw.setFont(smallFont);
        StdDraw.setPenColor(Color.GREEN);  //green for go, red for something is wrong.
        StdDraw.text(worldWidth / 2, menuLine1Y, "New Game: (N)");
        StdDraw.text(worldWidth / 2, menuLine2Y, "Load Game: (L)");
        StdDraw.text(worldWidth / 2, menuLine3Y, "Quit: (:Q)");
        StdDraw.text(worldWidth / 2, menuLine4Y, Character.toString(inputChar));
        //Display the menu frame.
        StdDraw.show();
    }


    //Give instructions on how to seed the game.
    private void displaySeedScreen(String inputSt) {
        StdDraw.enableDoubleBuffering();
        //Always clear the screen before drawing.
        StdDraw.clear(Color.BLACK);
        //Draw the user instructions.
        StdDraw.setFont(mediumFont);
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.text(worldWidth / 2, titleFirstLineY,
                "The Triforce has been stolen from the Temple by the Ghost King!");
        StdDraw.text(worldWidth / 2, titleSecondLineY,
                "Without the power of the Triforce, your realm will surely fall into chaos!");
        StdDraw.setFont(smallFont);
        StdDraw.text(worldWidth / 2, menuLine1Y,
                "Link has tracked the Triforce to a dungeon deep beneath the Misty Mountains,");
        StdDraw.text(worldWidth / 2, menuLine2Y,
                "but the exit cave is very high up in a Mountain. Link can't reach it unless he");
        StdDraw.text(worldWidth / 2, menuLine3Y,
                "can grow a giant beanstalk from a magic seed that Princess Zelda gave him.");
        StdDraw.text(worldWidth / 2, menuLine4Y,
                "Find the Triforce!  "
                        + "But first, how many feet do you think the beanstalk will grow?");
        StdDraw.setPenColor(Color.GREEN);  //green for go, red for something is wrong.
        StdDraw.text(worldWidth / 2, menuLine5Y,
                "Enter an integer followed by an \"s\" for seed.");
        StdDraw.text(worldWidth / 2, menuLine6Y,
                "Example: 138s");
        StdDraw.text(worldWidth / 2, menuLine7Y, inputSt);
        StdDraw.show();  //Display the screen
    }


    //Displays the heads up display by drawing over the blank space at the
    //top of the world.  renderFrame() clears everything so whenever I
    //need to update the HUD, be sure to renderFrame(world) then draw
    //the HUD on top of it.  This method doesn't StdDraw.clear().
    private void displayHud() {
        StdDraw.enableDoubleBuffering();
        //Font must be the same as the default for the renderer class.
        //If not the world (rendered by renderFrame()) will look funny.
        StdDraw.setFont(defaultFontFromRenderer);
        StdDraw.setPenColor(Color.GREEN);
        StdDraw.text(3, windowHeight - 2, "Items: ");
        StdDraw.text((windowWidth / 10) * 9, windowHeight - 2,
                ":Q Save    UP 'W'     DOWN 'S'     LEFT 'A'     RIGHT 'D'");
        StdDraw.setPenColor(Color.white);
        StdDraw.text(3, windowHeight - 2, "Items: ");
        StdDraw.text((windowWidth / 10) * 6, windowHeight - 2,
                world[mouseX][mouseY].description());  //Displays info about the tiles.
        if (hasTriforce) {
            StdDraw.setPenColor(Color.yellow);
            StdDraw.text((worldWidth / 10), windowHeight - 2,
                    "Triforce ▲");
        }
        if (hasRope) {
            StdDraw.setPenColor(Color.orange);
            StdDraw.text((worldWidth / 10) * 2, windowHeight - 2,
                    "Rope");
        }
        if (hasSwimPotion) {
            StdDraw.setPenColor(Color.blue);
            StdDraw.text((worldWidth / 10) * 3, windowHeight - 2,
                    "Swim Potion Ϫ");
        }

        StdDraw.show();
    }


    //Displays the final screen after the player has won!
    private void displayVictoryScreen() {
        StdDraw.enableDoubleBuffering();
        //Always clear the screen before drawing.
        StdDraw.clear(Color.BLACK);
        //Draw the Title.
        StdDraw.setFont(giantFont);
        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text(worldWidth / 2, titleFirstLineY, "You Returned the TRIFORCE ▲");
        StdDraw.text(worldWidth / 2, titleSecondLineY, "The Realm is Saved!");
        //Draw the menu options.
        StdDraw.setFont(smallFont);
        StdDraw.setPenColor(Color.GREEN);  //green for go, red for something is wrong.
        StdDraw.text(worldWidth / 2, menuLine1Y, "With the power of the Triforce, the Knights");
        StdDraw.text(worldWidth / 2, menuLine2Y, "of Highrule will have no problem defeating");
        StdDraw.text(worldWidth / 2, menuLine3Y, "the evil Ghost King and his minions!");
        StdDraw.text(worldWidth / 2, menuLine4Y, "Your work here is done. Now you can go back");
        StdDraw.text(worldWidth / 2, menuLine5Y, "to Princess Zelda and enjoy the wine you got");
        StdDraw.text(worldWidth / 2, menuLine6Y, "from the Knights for a job well done!");
        //Display the menu frame.
        StdDraw.show();
        StdDraw.pause(50000);
        System.exit(0);
    }


    private void displayTriforceScreen() {
        StdDraw.enableDoubleBuffering();
        //Always clear the screen before drawing.
        StdDraw.clear(Color.BLACK);
        //Draw the Title.
        StdDraw.setFont(giantFont);
        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text(worldWidth / 2, titleFirstLineY, "You found the TRIFORCE ▲");
        StdDraw.text(worldWidth / 2, titleSecondLineY, "But it's not safe yet!");
        //Draw the menu options.
        StdDraw.setFont(smallFont);
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.text(worldWidth / 2, menuLine1Y,
                "Get the Triforce back to the Temple");
        StdDraw.text(worldWidth / 2, menuLine2Y,
                "where it will be guarded by the Knights of");
        StdDraw.text(worldWidth / 2, menuLine3Y,
                "High Rule and you can refill your wine flask.");
        StdDraw.setPenColor(Color.green);  //green for go, red for something is wrong.
        StdDraw.text(worldWidth / 2, menuLine4Y,
                "Look for the green beanstalk and climb up to the exit cave!");
        StdDraw.text(worldWidth / 2, menuLine5Y,
                "(Try holding the mouse over objects for hints.)");
        //Display the menu frame.
        StdDraw.show();
        StdDraw.pause(15000);
    }


    //Displays instructions if the user doesn't enter an appropriate command.
    private void displayDeadLinkScreen() {
        //Always clear the screen before drawing.
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(Color.BLACK);
        //Draw the user instructions.
        StdDraw.setFont(mediumFont);
        StdDraw.setPenColor(Color.RED);
        StdDraw.text(worldWidth / 2, titleFirstLineY,
                "Sorry, your quest has failed.");
        StdDraw.text(worldWidth / 2, titleSecondLineY,
                "You have been killed by a ghost.");
        StdDraw.text(worldWidth / 2, menuLine1Y,
                "The realm will fall into chaos and");
        StdDraw.text(worldWidth / 2, menuLine2Y,
                "Princess Zelda will cry at your funeral.");
        StdDraw.show();
        StdDraw.pause(30000);
        System.exit(0);
    }


    //Moves the ghost through the world randomly.
    //skull index 0, red index 1, sun index 2, king index 3.
    private void moveSkull() {
        int x = skullLoc.getX();
        int y = skullLoc.getY();
        char[] directions = new char[] {'n', 's', 'e', 'w'};
        TETile tmp;
        //Adjust this to speed up or slow down the ghost.
        if (numberOfSkullLoops < ghostSpeed) {
            numberOfSkullLoops++;
            return;
        }
        if (!ghostNoCollision(skullDirection, skullLoc) || skullSteps <= 0) {
            skullDirection = directions[rand.nextInt(4)];
        }
        if (skullSteps <= 0) {
            skullSteps = ghostBehavior();
        }
        switch (skullDirection) {

            //The first 4 cases move Link;
            case 'n':  //north
                if (ghostNoCollision('n', skullLoc)) {
                    tmp = world[x][y + 1];
                    world[x][y + 1] = Tileset.SKULL;
                    skullLoc = new Pair(x, y + 1);
                    world[x][y] = skullPrevTile;
                    skullPrevTile = tmp;
                    numberOfSkullLoops = 0;
                }
                break;

            case 's':  //south
                if (ghostNoCollision('s', skullLoc)) {
                    tmp = world[x][y - 1];
                    world[x][y - 1] = Tileset.SKULL;
                    skullLoc = new Pair(x, y - 1);
                    world[x][y] = skullPrevTile;
                    skullPrevTile = tmp;
                    numberOfSkullLoops = 0;
                }
                break;

            case 'w':  //west
                if (ghostNoCollision('w', skullLoc)) {
                    tmp = world[x - 1][y];
                    world[x - 1][y] = Tileset.SKULL;
                    skullLoc = new Pair(x - 1, y);
                    world[x][y] = skullPrevTile;
                    skullPrevTile = tmp;
                    numberOfSkullLoops = 0;
                }
                break;

            case 'e':  //east
                if (ghostNoCollision('e', skullLoc)) {
                    tmp = world[x + 1][y];
                    world[x + 1][y] = Tileset.SKULL;
                    skullLoc = new Pair(x + 1, y);
                    world[x][y] = skullPrevTile;
                    skullPrevTile = tmp;
                    numberOfSkullLoops = 0;
                }
                break;

            default:
                break;
        }
        skullSteps--;
        numberOfSkullLoops = 0;
    }


    //Moves the ghost through the world randomly.
    private void moveSunGhost() {
        int x = sunLoc.getX();
        int y = sunLoc.getY();
        char[] directions = new char[] {'n', 's', 'e', 'w'};
        TETile tmp;
        if (numberOfSunLoops < ghostSpeed) {
            numberOfSunLoops++;
            return;
        }
        if (!ghostNoCollision(sunDirection, sunLoc) || sunSteps <= 0) {
            sunDirection = directions[rand.nextInt(4)];
        }
        if (sunSteps <= 0) {
            sunSteps = ghostBehavior();
        }
        switch (sunDirection) {

            //The first 4 cases move Link;
            case 'n':  //north
                if (ghostNoCollision('n', sunLoc)) {
                    tmp = world[x][y + 1];
                    world[x][y + 1] = Tileset.SUNGHOST;
                    sunLoc = new Pair(x, y + 1);
                    world[x][y] = sunPrevTile;
                    sunPrevTile = tmp;
                    numberOfSunLoops = 0;
                }
                break;

            case 's':  //south
                if (ghostNoCollision('s', sunLoc)) {
                    tmp = world[x][y - 1];
                    world[x][y - 1] = Tileset.SUNGHOST;
                    sunLoc = new Pair(x, y - 1);
                    world[x][y] = sunPrevTile;
                    sunPrevTile = tmp;
                    numberOfSunLoops = 0;
                }
                break;

            case 'w':  //west
                if (ghostNoCollision('w', sunLoc)) {
                    tmp = world[x - 1][y];
                    world[x - 1][y] = Tileset.SUNGHOST;
                    sunLoc = new Pair(x - 1, y);
                    world[x][y] = sunPrevTile;
                    sunPrevTile = tmp;
                    numberOfSunLoops = 0;
                }
                break;

            case 'e':  //east
                if (ghostNoCollision('e', sunLoc)) {
                    tmp = world[x + 1][y];
                    world[x + 1][y] = Tileset.SUNGHOST;
                    sunLoc = new Pair(x + 1, y);
                    world[x][y] = sunPrevTile;
                    sunPrevTile = tmp;
                    numberOfSunLoops = 0;
                }
                break;

            default:
                break;
        }
        sunSteps--;
        numberOfSunLoops = 0;
    }


    //Moves the ghost through the world randomly.
    private void moveKingGhost() {
        int x = kingLoc.getX();
        int y = kingLoc.getY();
        char[] directions = new char[] {'n', 's', 'e', 'w'};
        TETile tmp;
        if (numberOfKingLoops < ghostSpeed) {
            numberOfKingLoops++;
            return;
        }
        if (!ghostNoCollision(kingDirection, kingLoc) || kingSteps <= 0) {
            kingDirection = directions[rand.nextInt(4)];
        }
        if (kingSteps <= 0) {
            kingSteps = ghostBehavior();
        }
        switch (kingDirection) {

            //The first 4 cases move Link;
            case 'n':  //north
                if (ghostNoCollision('n', kingLoc)) {
                    tmp = world[x][y + 1];
                    world[x][y + 1] = Tileset.GHOSTKING;
                    kingLoc = new Pair(x, y + 1);
                    world[x][y] = kingPrevTile;
                    kingPrevTile = tmp;
                    numberOfKingLoops = 0;
                }
                break;

            case 's':  //south
                if (ghostNoCollision('s', kingLoc)) {
                    tmp = world[x][y - 1];
                    world[x][y - 1] = Tileset.GHOSTKING;
                    kingLoc = new Pair(x, y - 1);
                    world[x][y] = kingPrevTile;
                    kingPrevTile = tmp;
                    numberOfKingLoops = 0;
                }
                break;

            case 'w':  //west
                if (ghostNoCollision('w', kingLoc)) {
                    tmp = world[x - 1][y];
                    world[x - 1][y] = Tileset.GHOSTKING;
                    kingLoc = new Pair(x - 1, y);
                    world[x][y] = kingPrevTile;
                    kingPrevTile = tmp;
                    numberOfKingLoops = 0;
                }
                break;

            case 'e':  //east
                if (ghostNoCollision('e', kingLoc)) {
                    tmp = world[x + 1][y];
                    world[x + 1][y] = Tileset.GHOSTKING;
                    kingLoc = new Pair(x + 1, y);
                    world[x][y] = kingPrevTile;
                    kingPrevTile = tmp;
                    numberOfKingLoops = 0;
                }
                break;

            default:
                break;
        }
        kingSteps--;
        numberOfKingLoops = 0;
    }


    //Moves the ghost through the world randomly.
    //skull index 0, red index 1, sun index 2, king index 3.
    private void moveRedGhost() {
        int x = redLoc.getX();
        int y = redLoc.getY();
        char[] directions = new char[] {'n', 's', 'e', 'w'};
        TETile tmp;
        if (numberOfRedLoops < ghostSpeed) {
            numberOfRedLoops++;
            return;
        }
        if (!ghostNoCollision(redGhostDirection, redLoc) || redSteps <= 0) {
            redGhostDirection = directions[rand.nextInt(4)];
        }
        if (redSteps <= 0) {
            redSteps = ghostBehavior();
        }
        switch (redGhostDirection) {

            //The first 4 cases move Link;
            case 'n':  //north
                if (ghostNoCollision('n', redLoc)) {
                    tmp = world[x][y + 1];
                    world[x][y + 1] = Tileset.REDGHOST;
                    redLoc = new Pair(x, y + 1);
                    world[x][y] = redPrevTile;
                    redPrevTile = tmp;
                    numberOfRedLoops = 0;
                }
                break;

            case 's':  //south
                if (ghostNoCollision('s', redLoc)) {
                    tmp = world[x][y - 1];
                    world[x][y - 1] = Tileset.REDGHOST;
                    redLoc = new Pair(x, y - 1);
                    world[x][y] = redPrevTile;
                    redPrevTile = tmp;
                    numberOfRedLoops = 0;
                }
                break;

            case 'w':  //west
                if (ghostNoCollision('w', redLoc)) {
                    tmp = world[x - 1][y];
                    world[x - 1][y] = Tileset.REDGHOST;
                    redLoc = new Pair(x - 1, y);
                    world[x][y] = redPrevTile;
                    redPrevTile = tmp;
                    numberOfRedLoops = 0;
                }
                break;

            case 'e':  //east
                if (ghostNoCollision('e', redLoc)) {
                    tmp = world[x + 1][y];
                    world[x + 1][y] = Tileset.REDGHOST;
                    redLoc = new Pair(x + 1, y);
                    world[x][y] = redPrevTile;
                    redPrevTile = tmp;
                    numberOfRedLoops = 0;
                }
                break;

            default:
                break;
        }
        redSteps--;
        numberOfRedLoops = 0;
    }


    public boolean getLoadedGame() {
        return loadedGame;
    }


    //Always draw the world, then the HUD over it.
    private void drawEverything() {
        StdDraw.enableDoubleBuffering();
        ter.renderFrame(world);
        displayHud();
    }


    //Picks the number of steps before the ghost changes direction.
    private int ghostBehavior() {
        int coin = rand.nextInt(100);
        int steps;
        if (coin < 25) {  //25%
            steps = rand.nextInt(25);
        } else if (coin < 50) {  //25%
            steps = rand.nextInt(3);
        } else {  //50%
            steps = rand.nextInt(1);  //Adjust low to make ghost change directions often.
        }
        return steps;
    }


    private boolean ghostNoCollision(char direction, Pair ghostLocation) {
        int x = ghostLocation.getX();
        int y = ghostLocation.getY();

        switch (direction) {
            case 'n':
                if (world[x][y + 1].equals(Tileset.LINK)) {
                    displayDeadLinkScreen();
                }
                if (walkableTiles.contains(world[x][y + 1])) {
                    return true;
                }
                break;

            case 's':
                if (world[x][y - 1].equals(Tileset.LINK)) {
                    displayDeadLinkScreen();
                }
                if (walkableTiles.contains(world[x][y - 1])) {
                    return true;
                }
                break;

            case 'e':
                if (world[x + 1][y].equals(Tileset.LINK)) {
                    displayDeadLinkScreen();
                }
                if (walkableTiles.contains(world[x + 1][y])) {
                    return true;
                }
                break;

            case 'w':
                if (world[x - 1][y].equals(Tileset.LINK)) {
                    displayDeadLinkScreen();
                }
                if (walkableTiles.contains(world[x - 1][y])) {
                    return true;
                }
                break;

            default:
                break;
        }
        return false;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    //PLAY WITH INPUT STRING ONLY METHODS *******  NO StdDraw ALLOWED  *************************
    ////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        // Method to run the game using the input passed in, from command line args.
        // Returns a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().
        if (isNewGame(input)) {  //Extract the seed and build a new world.
            seed = extractSeedPlayWithInputString(inputStringCommands);
            rand = new Random(seed);  //Seeded Random will build the same world every time.
            world = initializeWorld();
            buildUpWorld();  //World is built but nothing moves.
            ter.initialize(windowWidth, windowHeight);  ///////first thing you do is initialize
            ter.renderFrame(world);  /////////////second thing is renderFrame
            runInputStringCommands();  //Run the commands left after extracting the seed.
        } else {  //It's a loaded Game
            ter.initialize(windowWidth, windowHeight);  //comment out for auto-grader////
            ter.renderFrame(world);  ////////comment out for auto-grader/////////////
            runInputStringCommands();  //Run the new commands.
        }
        TETile[][] finalWorldFrame = world;
        return finalWorldFrame;
    }


    //Runs the commands left over after extracting the seed.
    //i.e.  N543sWWWWAAQ would run the commands W, W, W, W, A, A, Q.
    private void runInputStringCommands() {
        for (char c : inputStringCommands) {
            commandFlowForGameLoop(c);
        }
    }


    //Adds mountains at random locations on the west 1/3 of the world.
    //The density of the mountains gets lighter as you approach
    //the center of the world.
    public void addMountains() {
        int coinToss;
        //Blend grass and mountains 50% mountain.
        //Can adjust density % (coinToss < _____).
        //From 0 to structure area + 5
        for (int x = 0; x < (worldWidth / 3) + 5; x++) {
            for (int y = worldHeight - 2; y > 0; y--) {
                coinToss = rand.nextInt(100);
                if (coinToss < 50) {
                    world[x][y] = Tileset.MOUNTAIN;
                }
            }
        }
        for (int x = (worldWidth / 3) + 5; x < (worldWidth / 3) + 10; x++) {
            for (int y = worldHeight - 2; y >= 0; y--) {
                coinToss = rand.nextInt(100);
                if (coinToss < 30) {
                    world[x][y] = Tileset.MOUNTAIN;
                }
            }
        }
        for (int x = (worldWidth / 3) + 10; x < (worldWidth / 3) + 15; x++) {
            for (int y = worldHeight - 2; y >= 0; y--) {
                coinToss = rand.nextInt(100);
                if (coinToss < 10) {
                    world[x][y] = Tileset.MOUNTAIN;
                }
            }
        }
        for (int x = (worldWidth / 3) + 15; x < (worldWidth / 3) + 20; x++) {
            for (int y = worldHeight - 2; y >= 0; y--) {
                coinToss = rand.nextInt(100);
                if (coinToss < 1) {
                    world[x][y] = Tileset.MOUNTAIN;
                }
            }
        }
    }


    //Sprinkle in the trees on the east 1/3 of the world.
    //Can adjust density with (coinToss < ____).
    public void addTrees() {
        int coinToss;
        //Blend grass and 2% trees.
        for (int x = (worldWidth / 3) + 30; x < (worldWidth / 3) + 35; x++) {
            for (int y = worldHeight - 2; y > 0; y--) {
                coinToss = rand.nextInt(100);
                if (coinToss < 2) {
                    world[x][y] = Tileset.TREE;
                }
            }
        }
        //Blend grass and 10% trees.
        for (int x = (worldWidth / 3) + 35; x < (worldWidth / 3) + 40; x++) {
            for (int y = worldHeight - 2; y >= 0; y--) {
                coinToss = rand.nextInt(100);
                if (coinToss < 10) {
                    world[x][y] = Tileset.TREE;
                }
            }
        }
        //Blend grass and 20% trees.
        for (int x = (worldWidth / 3) + 40; x < (worldWidth / 3) + 45; x++) {
            for (int y = worldHeight - 2; y >= 0; y--) {
                coinToss = rand.nextInt(100);
                if (coinToss < 20) {
                    world[x][y] = Tileset.TREE;
                }
            }
        }
        //Blend grass and 40% trees.
        for (int x = (worldWidth / 3) + 45; x < worldWidth; x++) {
            for (int y = worldHeight - 2; y >= 0; y--) {
                coinToss = rand.nextInt(100);
                if (coinToss < 40) {
                    world[x][y] = Tileset.TREE;
                }
            }
        }
    }


    //Sprinkles in a few flowers here and there.
    public void AddSpecialItems() {
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight - 1; y++) {
                int coin = rand.nextInt(400);
                if (coin < 1) {
                    world[x][y] = Tileset.FLOWER;
                }
            }
        }
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight - 1; y++) {
                int coin = rand.nextInt(800);
                if (coin < 1) {
                    world[x][y] = Tileset.WINE;
                }
            }
        }
    }


    //Builds a plain world with mountains in the west and forrest in the east.
    public TETile[][] initializeWorld() {
        world = new TETile[worldWidth][worldHeight];  //Set up double array.
        //Start with all walkable GRASS TILES.
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                world[x][y] = Tileset.GRASS;
            }
        }
        addMountains();  //Sprinkle the MOUNTAINS over the GRASS.
        addTrees();  //Sprinkle in the TREES over the GRASS.
        AddSpecialItems();
        //Draw a row of nothing at the top of the world to usa as a barrier that
        //nothing can walk on.
        for (int i = 0; i < windowWidth; i++) {
            world[i][worldHeight - 1] = Tileset.NOTHING;
        }
        for (int i = 0; i < windowWidth; i++) {
            world[i][0] = Tileset.NOTHING;
        }
        for (int i = worldHeight - 1; i > 0; i--) {
            world[worldWidth - 1][i] = Tileset.NOTHING;
        }
        for (int i = worldHeight - 1; i > 0; i--) {
            world[0][i] = Tileset.NOTHING;
        }
        return world;
    }


    //Adds all of the features to the world, inside and outside.
    private void buildUpWorld() {
        //Define what tiles can be walked on.
        walkableTiles.add(Tileset.FLOOR);
        walkableTiles.add(Tileset.GRASS);
        walkableTiles.add(Tileset.BRIDGE);
        walkableTiles.add(Tileset.TREE);
        walkableTiles.add(Tileset.TRIFORCE);
        walkableTiles.add(Tileset.BEANSTALK);
        walkableTiles.add(Tileset.FLOWER);
        walkableTiles.add(Tileset.TEMPLE);
        walkableTiles.add(Tileset.SWIMPOTION);
        walkableTiles.add(Tileset.ROPE);
        walkableTiles.add(Tileset.WINE);
        Structure dungeon = new Structure(world, rand, maxWall, worldWidth, worldHeight, ter);
        dungeon.buildSingleStructure();
        Hexagon hex = new Hexagon(world, rand, worldWidth, worldHeight);
        hex.addTemple();
        setUpMovingPieces();
    }


    //Adds the moving items and/or actionable items to the dungeon.
    //Saves the locations of those items.
    public void setUpDungeon() {
        int x = 0;  //Dummy values are never used.
        int y = 0;  //Dummy values are never used.
        TETile[] dungeonTiles = new TETile[4];
        dungeonTiles[0] = Tileset.LINK;
        dungeonTiles[1] = Tileset.TRIFORCE;
        dungeonTiles[2] = Tileset.BEANSTALK;
        dungeonTiles[3] = Tileset.SKULL;
        boolean inDungeon = false;
        for (int i = 0; i < dungeonTiles.length; i++) {
            while (!inDungeon) {
                x = rand.nextInt(worldWidth / 3);
                //Dungeon are is on the first 1/3 of world.
                y = rand.nextInt(worldHeight - 3);
                if (world[x][y].equals(Tileset.FLOOR)) {
                    world[x][y] = dungeonTiles[i];
                    inDungeon = true;
                }
            }
            inDungeon = false;
            if (world[x][y].equals(Tileset.LINK)) {
                linkLocation = new Pair(x, y);
            }
            if (world[x][y].equals(Tileset.SKULL)) {
                skullLoc = new Pair(x, y);
            }
        }
    }


    //Adds the moving items and/or actionable items to the outdoor areas.
    //Saves the locations of those items.
    private void setUpOutside() {
        int x = 0;  //Dummy values never used.
        int y = 0;  //Dummy values never used.

        TETile[] outsideTiles = new TETile[3];
        outsideTiles[0] = Tileset.SUNGHOST;
        outsideTiles[1] = Tileset.GHOSTKING;
        outsideTiles[2] = Tileset.REDGHOST;
        int swimPotionY = rand.nextInt(worldHeight - 10) + 3;
        int ropeY = rand.nextInt(worldHeight - 10) + 3;
        world[1][swimPotionY] = Tileset.SWIMPOTION;
        world[worldWidth - 2][ropeY] = Tileset.ROPE;
        //Add items to the outside world.
        for (int i = 0; i < outsideTiles.length; i++) {
            //Start ghosts near the center of the world.
            x = (worldWidth / 2) + i * 3;
            y = (worldHeight / 2) + i * 2;
            if (world[x][y].equals(Tileset.GRASS) || world[x][y].equals(Tileset.MOUNTAIN)) {
                world[x][y] = outsideTiles[i];
            }
            if (world[x][y].equals(Tileset.SUNGHOST)) {
                sunLoc = new Pair(x, y);
            }
            if (world[x][y].equals(Tileset.GHOSTKING)) {
                kingLoc = new Pair(x, y);
            }
            if (world[x][y].equals(Tileset.REDGHOST)) {
                redLoc = new Pair(x, y);
            }
        }
    }


    //Adds moving and interactive items to the world and saves their locations.
    private void setUpMovingPieces() {
        setUpDungeon();
        setUpOutside();
        //Add the beanstalk outside of the dungeon and clears a walkable
        //path through the mountains leading from the beanstalk.
        int x = (worldWidth / 3) + 2;
        int y = rand.nextInt(worldHeight - 11) + 5;
        world[x][y] = Tileset.BEANSTALK;
        world[x - 1][y] = Tileset.CAVE;
        beanStalkLocation = new Pair(x, y);
        for (int i = x + 1; i < x + 15; i++) {
            for (int j = y + 1; j > y - 2; j--) {
                world[i][j] = Tileset.GRASS;
            }
        }
    }


    //Extracts the seed from commandline args.
    //Method assumes that inputStringCommands is of form:
    //      ############s or ###########swwwwadQ
    //The first command char, 'n' has already been removed.
    private long extractSeedPlayWithInputString(ArrayList<Character> inputArrayList) {
        char c = ' ';  //Dummy value is never used.
        String seedString = "";
        //inputStringCommands is of form ############swasdq, I only care about the digits here.
        while (c != 's' && c != 'S') {  //Extract digits for seed.
            c = inputArrayList.get(0);
            if (Character.isDigit(c)) {  //Just a final check to be safe.
                seedString += Character.toString(c);
                inputArrayList.remove(0);
            }
        }
        long seedLong = Long.parseLong(seedString);
        inputArrayList.remove(0);  //Remove the 's' that follows the seed digit.
        return seedLong;
    }


    //Converts the input String to an ArrayList of chars.
    //Determines if the seed needs to be extracted from the input String (i.e. it's a new game).
    //Removes the first command from the ArrayList.
    public boolean isNewGame(String input) {
        char firstCommand;
        char[] inputArr = input.toCharArray();
        inputStringCommands = new ArrayList<>();  //InputStringCommands may still contain digits.
        for (char c : inputArr) {
            inputStringCommands.add(c);
        }
        firstCommand = inputStringCommands.remove(0);  //Remove the irrelevant first char.
        firstCommand = Character.toUpperCase(firstCommand);
        if (firstCommand == ':') {
            char secondCommand = inputStringCommands.get(0);
            if (secondCommand == 'q' || secondCommand == 'Q') {
                //The command string wants to save before there is anything to save.
                //Just exit without doing anything.
                System.exit(0);
            }
        }
        return firstCommand == 'N';
    }


    //Returns true for a legal command, false otherwise.
    private boolean isLegalGameLoopCommand(char commandChar) {
        ArrayList<Character> legalInputChars = new ArrayList<>();
        legalInputChars.addAll(Arrays.asList('w', 'W', 's', 'S', 'a',
                'A', 'd', 'D', 'Q', 'q', ':'));
        return legalInputChars.contains(commandChar);
    }


    //Workflow during game play.  Also used for playWithInputString.
    private void commandFlowForGameLoop(char commandChar) {
        //Takes both upper and lower case commands.
        commandChar = Character.toUpperCase(commandChar);
        int x = linkLocation.getX();
        int y = linkLocation.getY();
        TETile tmp;


        //Ghosts speed up if Link hits a flower.
        if (world[x][y].equals(Tileset.FLOWER)) {
            ghostSpeed = 1;
        }

        //Commands that can be given during the game
        //or while the game loop is running.
        switch (commandChar) {

            //The first 4 cases move Link;
            case 'W':  //north
                if (!linkHitObject('n')) {
                    tmp = world[x][y + 1];
                    if (tmp.equals(Tileset.FLOWER)) {
                        ghostSpeed = 1;
                    }
                    if (tmp.equals(Tileset.ROPE)) {
                        walkableTiles.add(Tileset.MOUNTAIN);
                        tmp = Tileset.GRASS;
                        hasRope = true;
                    }
                    if (tmp.equals(Tileset.SWIMPOTION)) {
                        walkableTiles.add(Tileset.WATER);
                        tmp = Tileset.GRASS;
                        hasSwimPotion = true;
                    }
                    if (tmp.equals(Tileset.WINE)) {
                        slow = 100;
                        tmp = Tileset.GRASS;
                    }
                    world[x][y + 1] = Tileset.LINK;
                    linkLocation = new Pair(x, y + 1);
                    world[x][y] = linkPrevTile;
                    linkPrevTile = tmp;
                }
                break;

            case 'S':  //south
                if (!linkHitObject('s')) {
                    tmp = world[x][y - 1];
                    if (tmp.equals(Tileset.FLOWER)) {
                        ghostSpeed = 1;
                    }
                    if (tmp.equals(Tileset.ROPE)) {
                        walkableTiles.add(Tileset.MOUNTAIN);
                        tmp = Tileset.GRASS;
                        hasRope = true;
                    }
                    if (tmp.equals(Tileset.SWIMPOTION)) {
                        walkableTiles.add(Tileset.WATER);
                        tmp = Tileset.GRASS;
                        hasSwimPotion = true;
                    }
                    if (tmp.equals(Tileset.WINE)) {
                        slow = 100;
                        tmp = Tileset.GRASS;
                    }
                    world[x][y - 1] = Tileset.LINK;
                    linkLocation = new Pair(x, y - 1);
                    world[x][y] = linkPrevTile;
                    linkPrevTile = tmp;
                }
                break;

            case 'A':  //west
                if (!linkHitObject('w')) {
                    tmp = world[x - 1][y];
                    if (tmp.equals(Tileset.FLOWER)) {
                        ghostSpeed = 1;
                    }
                    if (tmp.equals(Tileset.ROPE)) {
                        walkableTiles.add(Tileset.MOUNTAIN);
                        tmp = Tileset.GRASS;
                        hasRope = true;
                    }
                    if (tmp.equals(Tileset.SWIMPOTION)) {
                        walkableTiles.add(Tileset.WATER);
                        tmp = Tileset.GRASS;
                        hasSwimPotion = true;
                    }
                    if (tmp.equals(Tileset.WINE)) {
                        slow = 100;
                        tmp = Tileset.GRASS;
                    }
                    world[x - 1][y] = Tileset.LINK;
                    linkLocation = new Pair(x - 1, y);
                    world[x][y] = linkPrevTile;
                    linkPrevTile = tmp;
                }
                break;

            case 'D':  //east
                if (!linkHitObject('e')) {
                    tmp = world[x + 1][y];
                    if (tmp.equals(Tileset.FLOWER)) {
                        ghostSpeed = 1;
                    }
                    if (tmp.equals(Tileset.ROPE)) {
                        walkableTiles.add(Tileset.MOUNTAIN);
                        hasRope = true;
                        tmp = Tileset.GRASS;
                        hasRope = true;
                    }
                    if (tmp.equals(Tileset.SWIMPOTION)) {
                        walkableTiles.add(Tileset.WATER);
                        tmp = Tileset.GRASS;
                        hasSwimPotion = true;
                    }
                    if (tmp.equals(Tileset.WINE)) {
                        slow = 100;
                        tmp = Tileset.GRASS;
                    }
                    world[x + 1][y] = Tileset.LINK;
                    linkLocation = new Pair(x + 1, y);
                    world[x][y] = linkPrevTile;
                    linkPrevTile = tmp;
                }
                break;

            case 'Q':
                gameLoopOn = false;  //Stop the game loop and go back to Main class.
                break;

            default:
                break;
        }
        drawEverything();  //comment out for auto-grader//No StdDraw allowed.
    }



}

