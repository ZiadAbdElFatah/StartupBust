import java.util.*;

class GameHelper {
    private static final String ALPHABET = "abcdefg";
    private static final int GRID_LENGTH = 7;
    private static final int GRID_SIZE = 49;
    private static final int MAX_ATTEMPTS = 200;

    static final int HORIZONTAL_INCREMENT = 1;          // A better way to represent these two
    static final int VERTICAL_INCREMENT = GRID_LENGTH;  // things is an enum (see Appendix B)

    private final int[] grid = new int[GRID_SIZE];
    private final Random random = new Random();

    private int startupCount = 0;

    public String getUserInput(String prompt) {
        System.out.print(prompt + ": ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().toLowerCase();
    } //end getUserInput

    public ArrayList<String> placeStartup(int startupSize) {
        // holds index to grid (0 - 48)
        int[] startupCoords = new int[startupSize];         // current candidate co-ordinates
        int attempts = 0;                                   // current attempts counter
        boolean success = false;                            // flag = found a good location?

        startupCount++;                                     // nth Startup to place
        int increment = getIncrement();                     // alternate vert & horiz alignment

        while (!success & attempts++ < MAX_ATTEMPTS) {      // main search loop
            int location = random.nextInt(GRID_SIZE);         // get random starting point

            for (int i = 0; i < startupCoords.length; i++) {  // create array of proposed coords
                startupCoords[i] = location;                    // put current location in array
                location += increment;                          // calculate the next location
            }
//            System.out.println("Trying: " + Arrays.toString(startupCoords));

            if (startupFits(startupCoords, increment)) {      // startup fits on the grid?
                success = coordsAvailable(startupCoords);       // ...and locations aren't taken?
            }                                                 // end loop
        }                                                   // end while

        savePositionToGrid(startupCoords);                  // coords passed checks, save
        ArrayList<String> alphaCells = convertCoordsToAlphaFormat(startupCoords);
//        System.out.println("Placed at: "+ alphaCells);
        return alphaCells;
    } //end placeStartup

    boolean startupFits(int[] startupCoords, int increment) {
        int finalLocation = startupCoords[startupCoords.length - 1];
        if (increment == HORIZONTAL_INCREMENT) {
            // check end is on same row as start
            return calcRowFromIndex(startupCoords[0]) == calcRowFromIndex(finalLocation);
        } else {
            return finalLocation < GRID_SIZE;                 // check end isn't off the bottom
        }
    } //end startupFits

    boolean coordsAvailable(int[] startupCoords) {
        for (int coord : startupCoords) {                   // check all potential positions
            if (grid[coord] != 0) {                           // this position already taken
                System.out.println("position: " + coord + " already taken.");
                return false;                                   // NO success
            }
        }
        return true;                                        // there were no clashes, yay!
    } //end coordsAvailable

    void savePositionToGrid(int[] startupCoords) {
        for (int index : startupCoords) {
            grid[index] = 1;                                  // mark grid position as 'used'
        }
    } //end savePositionToGrid

    private ArrayList<String> convertCoordsToAlphaFormat(int[] startupCoords) {
        ArrayList<String> alphaCells = new ArrayList<String>();
        for (int index : startupCoords) {                   // for each grid coordinate
            String alphaCoords = getAlphaCoordsFromIndex(index); // turn it into an "a0" style
            alphaCells.add(alphaCoords);                      // add to a list
        }
        return alphaCells;                                  // return the "a0"-style coords
    } // end convertCoordsToAlphaFormat

    String getAlphaCoordsFromIndex(int index) {
        int row = calcRowFromIndex(index);                  // get row value
        int column = index % GRID_LENGTH;                   // get numeric column value

        String letter = ALPHABET.substring(column, column + 1); // convert to letter
        return letter + row;
    } // end getAlphaCoordsFromIndex

    private int calcRowFromIndex(int index) {
        return index / GRID_LENGTH;
    } // end calcRowFromIndex

    private int getIncrement() {
        if (startupCount % 2 == 0) {                        // if EVEN Startup
            return HORIZONTAL_INCREMENT;                      // place horizontally
        } else {                                            // else ODD
            return VERTICAL_INCREMENT;                        // place vertically
        }
    } //end getIncrement
} //end class
class Startup {
    private ArrayList<String> locationCells;
    private String name;

    public void setName(String n) {
        name = n;
    }

    public void setLocationCells(ArrayList<String> loc) {
        locationCells = loc;
    }

    public String checkYourself(String guess) {
        String result = "miss";
        int idx = locationCells.indexOf(guess);
        if (idx != -1) {
            result = "hit";
            locationCells.remove(idx);
        } else {
            result = "kill";
        }
        return result;
    }
}
public class startupBust {

    private GameHelper helper = new GameHelper();
    private ArrayList<Startup> startups = new ArrayList<Startup>();
    private int numOfGuesses = 0;

    private void setUpGame() {
        Startup one = new Startup();
        one.setName("poniez");
        Startup two = new Startup();
        two.setName("hacqi");
        Startup three = new Startup();
        three.setName("cabista");
        startups.add(one);
        startups.add(two);
        startups.add(three);

        System.out.println("Your goal is to sink three Startups.");
        System.out.println("poniez, hacqi, cabista");
        System.out.println("Try to sink them all in the fewest number of guesses");

        for (Startup startup : startups) {
            ArrayList<String> newLocation = helper.placeStartup(3);
            startup.setLocationCells(newLocation);
        }
    }

    private void startPlaying() {
        while (!startups.isEmpty()) {
            String userGuess = helper.getUserInput("Enter a guess");
            checkUserGuess(userGuess);
        }
        finishGame();
    }

    private void checkUserGuess(String userGuess) {
        numOfGuesses++;
        String result = "miss"; // assume a miss until told otherwise

        for (Startup startupToTest : startups) {
            result = startupToTest.checkYourself(userGuess);

            if (result.equals("hit")) {
                break;
            }
            if (result.equals("kill")) {
                startups.remove(startupToTest); // he's gone
                break;
            }
        }

        System.out.println(result);
    }

    private void finishGame() {
        System.out.println("All Startups are dead! Your stock is now worthless");
        if (numOfGuesses <= 18) {
            System.out.println("It only took you " + numOfGuesses + " guesses.");
            System.out.println("You got out before your options sank.");
        } else {
            System.out.println("Took you long enough. " + numOfGuesses + " guesses.");
            System.out.println("Fish are dancing with your options");
        }
    }

    public static void main(String[] args) {
        startupBust game = new startupBust();
        game.setUpGame();
        game.startPlaying();
    }
}

