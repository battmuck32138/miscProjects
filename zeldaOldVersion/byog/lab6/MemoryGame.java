package byog.lab6;

import edu.princeton.cs.introcs.StdDraw;
import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {

    private int width;
    private int height;
    private int seed;
    private int roundNum;
    private int randomNum;
    private Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) throws InterruptedException {
        //InputStreamReader ir = new InputStreamReader(System.in);
        //BufferedReader br = new BufferedReader(ir);


        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        /**
        try {
            String userInput = br.readLine();
            System.out.println("You entered: " + userInput);
        } catch (Exception e) {
            System.out.println("Caught input exception.");
        }
         */


        int seed = Integer.parseInt(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, int seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.roundNum = 1;
        this.randomNum = 0;
        this.rand = new Random(seed);
        this.playerTurn = false;
        this.gameOver = false;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public String generateRandomString(int n) {
        String s = "";
        for (int i = 0; i < n; i++) {
            int index = rand.nextInt(26);
            s += CHARACTERS[index];
        }
        return s;
    }

    public void drawFrame(String s) {
        //Set up screen.
        String line = "";
        for (int i = 0; i < width * 3; i++) {
            line += "_";
        }
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font bigFont = new Font("Arial", Font.BOLD, 30);
        Font smallFont = new Font("Ariel", Font.PLAIN, 20);
        StdDraw.setFont(smallFont);

        //Draw header.
        StdDraw.text(5, height - 2, "Round: " + roundNum);
        StdDraw.text(0, height - 4, line);
        StdDraw.text(30, height - 2, ENCOURAGEMENT[randomNum]);
        StdDraw.setPenColor(Color.RED);
        if (playerTurn) {
            StdDraw.text(18, height - 2, "Enter Sequence");
        } else {
            StdDraw.setPenColor(Color.BLUE);
            StdDraw.text(18, height - 2, "Watch Carefully");
        }

        //Draw sequence in the center.
        StdDraw.setFont(bigFont);
        StdDraw.text(width / 2, height / 2, s);
        StdDraw.show();
    }

    public void flashSequence(String letters) throws InterruptedException {
        playerTurn = false;
        char[] charArr = letters.toCharArray();
        for (char letter : charArr) {
            String s = Character.toString(letter);
            drawFrame(s);
            Thread.sleep(1500);
            StdDraw.clear(Color.BLACK);
            Thread.sleep(500);
        }
        playerTurn = true;
        drawFrame(letters);
    }

    public String solicitNCharsInput(int n) {
        int count = 0;
        String s = "";
        while (count < n) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                s += c;
                drawFrame(s);
                count++;
            }
        }
        return s;
    }

    public void startGame() throws InterruptedException {
        String round = "Round: ";
        while (!gameOver) {
            drawFrame(round + roundNum);
            String target = generateRandomString(roundNum);
            flashSequence(target);
            String answer = solicitNCharsInput(roundNum);
            if (!answer.equals(target)) {
                drawFrame("Game Over! You made it to round: " + roundNum);
                gameOver = true;
            }
            randomNum = rand.nextInt(ENCOURAGEMENT.length);
            playerTurn = true;
            roundNum++;
        }
    }

}
