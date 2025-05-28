package dungeon.engine;

import dungeon.engine.cells.Interaction;
import dungeon.engine.cells.set.Empty;

import java.io.*;

/**
 * Main class for handling game functionality
 *
 * Handles:
 * - Game state management (level, player, difficulty and game status updates)
 * - Movement processing
 * - Level progression
 * - Save/load functionality
 * - Score tracking
 */
public class GameEngine implements Serializable {
    private Level currentLevel;
    private final Player player;
    private int difficulty;
    private int level;
    private boolean gameOver;
    private int deathType;
    private final Score scoreImport;
    private boolean isNewHS;

    // serial version UID
    @Serial
    private static final long serialVersionUID = 0L;

    // save file name
    private final String savePath;

    /**
     * Constructor with full dependencies
     *
     * @param difficulty initial game difficulty (0-10)
     * @param scoreHandler communicates with the ScoreHandler interface for score-related tasks
     * @param savePath gamesave file storage path
     */
    public GameEngine(int difficulty, ScoreHandler scoreHandler, String savePath) {
        this.difficulty = Math.min(10, Math.max(0, difficulty)); // difficulty between 0 and 10
        this.level = 1;
        this.player = new Player(10, 100); // Injecting max health and steps
        this.gameOver = false;
        this.deathType = -1; // represents no death, 0 is death due to no hp, 1 is death due to max steps reached, etc...
        this.scoreImport = (Score) scoreHandler;
        this.isNewHS = false;
        this.savePath = savePath;

        initLevel();
    }

    /**
     * Default constructor
     *
     * @param difficulty initial game difficulty (0-10)
     */
    public GameEngine(int difficulty) {
        this(difficulty, new Score("md_highscores.dat"), "saves.dat");
    }

    /**
     * - Initialises a new level with initial difficulty
     * - Places player at the default entry point
     */
    private void initLevel() {
        currentLevel = new Level(level, difficulty, 10);

        // setting player pos to entry
        Position entryPos = currentLevel.getEntryPos();
        player.startPos(entryPos.getX(), entryPos.getY());
    }

    //------------------------------------------------------------------------------------------- MOVEMENT

    /**
     * Moves player up
     * @return string of movement result
     */
    public String moveUp() {
        return processMove("up");
    }

    /**
     * Moves player down
     * @return string of movement result
     */
    public String moveDown() {
        return processMove("down");
    }

    /**
     * Moves player left
     * @return string of movement result
     */
    public String moveLeft() {
        return processMove("left");
    }

    /**
     * Moves player right
     * @return string of movement result
     */
    public String moveRight() {
        return processMove("right");
    }

    /**
     * Movement processing method
     *
     * Handles position calculation, collision checks, player/cell interactions,
     * level transitions and ranged attack checks
     *
     * @param direction to move
     * @return string of movement results (output)
     */
    private String processMove(String direction) {
        Position oldPos = new Position(player.getPosition());
        Position newPos = new Position(oldPos);
        String output = "Moved " + direction;

        // movement calculations
        switch (direction.toLowerCase()) {
            case "up":
                newPos.setY(oldPos.getY() - 1);
                break;
            case "down":
                newPos.setY(oldPos.getY() + 1);
                break;
            case "left":
                newPos.setX(oldPos.getX() - 1);
                break;
            case "right":
                newPos.setX(oldPos.getX() + 1);
                break;
            default:
                return "Invalid move.";
        }

        // checking if move is valid
        Cell targetCell = currentLevel.getCell(newPos);
        if (targetCell.cellCanWalk()) {
            output += " one step.";

            // updating player position
            switch (direction.toLowerCase()) {
                case "up":
                    player.moveUp();
                    break;
                case "down":
                    player.moveDown();
                    break;
                case "left":
                    player.moveLeft();
                    break;
                case "right":
                    player.moveRight();
                    break;
            }

            // player-cell interaction
            if (targetCell instanceof Interaction interaction) {
                String interactionResult = player.interact(interaction);
                output += " " + interactionResult;

                if (interaction.cellRemoveOnUse()) {
                    // replaces cell with empty cell
                    currentLevel.setCell(newPos, new Empty());
                }
            }

            if (currentLevel.isLadder(player.getPosition())) {
                if (level == 1) {
                    // next level
                    level = 2;
                    difficulty += 2;
                    Position ladderPos = currentLevel.getLadderPos();

                    currentLevel = new Level(level, difficulty, 10);

                    currentLevel.setEntryPos(ladderPos);

                    player.startPos(ladderPos.getX(), ladderPos.getY());

                    output += " Moving onto level " + level + ". ";
                } else {
                    // player win
                    gameOver = true;
                    output += " Hey, that's the exit to the dungeon! You win!";

                    // checking if score is a new high score
                    isNewHS = scoreImport.addScore(player.getScore());
                    if (isNewHS) {
                        output += " Congratulations! You got a new high score!";
                    }
                }

                return output; // early output to skip ranged attack checks on level transitions
            }

            // check for ranged attacks
            int rangedDamage = currentLevel.checkRange(player);
            if (rangedDamage > 0) {
                player.hurt(rangedDamage);
                output += " A flurry of arrows approached you and dealt " + rangedDamage + " damage, ouch!";
            }

            // check game over conditions
            checkGameOver();
        } else {
            output += " hit a wall.";
        }

        return output;
    }

    /**
     * Checks if game is over based on game-over requirements and sets accordingly if valid
     */
    private void checkGameOver() {
        if (!player.isAlive()) {
            gameOver = true;
            deathType = 0; // hp = 0
            player.setScore(-1);
        } else if (player.checkSteps()) {
            gameOver = true;
            deathType = 1; // max steps reached
            player.setScore(-1);
        }
    }

    //------------------------------------------------------------------------------------------- GAME STATE

    /**
     * Current level size
     * @return level size (width/height)
     */
    public int getSize() {
        return currentLevel.getSize();
    }

    /**
     * Gets the 2d cell array level representation of the current level
     * @return 2d array of cells
     */
    public Cell[][] getMap() {
        return currentLevel.getMap();
    }

    /**
     * Gets the player
     * @return player instance
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Checks if game is over
     * @return true if game is over
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Checks the type of game over instance that occured
     * @return death type (-1 = no death, 0 = no hp, 1 = max steps)
     */
    public int getDeathType() {
        return deathType;
    }

    /**
     * Gets formatted highscores
     * @return string of formatted highscores
     */
    public String getHighscores() {
        return scoreImport.formatScores();
    }

    /**
     * Gets the current level number
     * @return current level
     */
    public int getLevel() {
        return level;
    }

    //------------------------------------------------------------------------------------------- SAVE/LOAD

    /**
     * Saves the current game to file
     *
     * @return String of save result status
     */
    public String saveGame() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(savePath)))) {
            out.writeObject(this);
            return "Game saved!";
        } catch (IOException e) {
            return "Error saving game: " + e.getMessage();
        }
    }

    /**
     * Loads a saved game instance from file
     *
     * @return true if load successful
     */
    public boolean loadGame() {
        if (!saveExists()) {
            return false; // no save file
        }

        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(savePath)))) {
            GameEngine loaded = (GameEngine) in.readObject();

            this.currentLevel = loaded.currentLevel;
            this.player.copyFrom(loaded.player);
            this.difficulty = loaded.difficulty;
            this.level = loaded.level;
            this.gameOver = loaded.gameOver;
            this.deathType = loaded.deathType;
            this.isNewHS = loaded.isNewHS;

            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a save instance exists
     *
     * @return true if save file exists
     */
    public boolean saveExists() {
        File file = new File(savePath);
        return file.exists();
    }

    //------------------------------------------------------------------------------------------- TESTING UTILS

    /**
     * Gets the current level object for testing
     * @return current level object
     */
    public Level getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Sets the current level
     * @param level number to set
     */
    public void setLevel(int level) {
        this.level = level;
    }

    //------------------------------------------------------------------------------------------- START METHOD


    // Creates a ConsoleUI instance and starts a console version of the game
    public static void main(String[] args) {
        // creating console ui and starting game
        ConsoleUI consoleUI = new ConsoleUI();
        consoleUI.start();
    }
}
