package byog.Core;

import byog.TileEngine.TETile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;


/** This is the main entry point for the program. This class simply parses
 *  the command line inputs, and lets the byog.Core.Game class take over
 *  in either keyboard or input string mode.
 */
public class Main {

    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.println("Can only have one argument - the input string");
            System.exit(0);
        } else if (args.length == 1) {  //playWithInputString()
            Game game = new Game();
            String argString = args[0];
            char[] argsChars = argString.toCharArray();
            if (argsChars[0] == 'l' || argsChars[0] == 'L') {
                game = loadGame();
            }
            TETile[][] worldState = game.playWithInputString(args[0]);
            for (char command : argsChars) {
                if (command == 'q' || command == 'Q') {
                    saveGame(game);
                }
            }
            System.out.println(TETile.toString(worldState));
        } else {  //playWithKeyBoard()
            Game game = new Game();
            game.playWithKeyboard();
            if (game.getLoadedGame()) {
                game = loadGame();
                game.startLoadedGame();
            }
            saveGame(game);
            System.exit(0);
        }
    }


    //Loads the saved game if there is one.  If not, the program ends.
    public static Game loadGame() {
        File f = new File("./game.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                Game loadGame = (Game) os.readObject();
                os.close();
                return loadGame;
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
        //In the case no Game has been saved yet, end program.
        System.out.println("From loadGame(): There is no saved game to load.");
        System.exit(0);
        return new Game();  //Dummy return is unreachable.
    }


    public static void saveGame(Game game) {
        File f = new File("./game.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(game);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

}


