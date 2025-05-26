package dungeon.engine;

import dungeon.engine.cells.Empty;

import java.io.*;

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

    // constructor with dependencies
    public GameEngine(int difficulty, ScoreHandler scoreHandler, String savePath) {
        this.difficulty = Math.min(10, Math.max(0, difficulty)); // explain
        this.level = 1;
        this.player = new Player(10, 100); // Injecting MAX_HP and MAX_STEPS
        this.gameOver = false;
        this.deathType = -1; // represents no death, 0 is death due to no hp, 1 is death due to max steps reached, etc...
        this.scoreImport = (Score) scoreHandler;
        this.isNewHS = false;
        this.savePath = savePath;

        initLevel();
    }

    // default constructor
    public GameEngine(int difficulty) {
        this(difficulty, new Score("md_highscores.dat"), "saves.dat");
    }

    private void initLevel() {
        currentLevel = new Level(level, difficulty, 10);

        // setting player pos to entry
        Position entryPos = currentLevel.getEntryPos();
        player.startPos(entryPos.getX(), entryPos.getY());
    }

    // movement
    public String moveUp() {
        return processMove("up");
    }

    public String moveDown() {
        return processMove("down");
    }

    public String moveLeft() {
        return processMove("left");
    }

    public String moveRight() {
        return processMove("right");
    }

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

    // could optimise with OR condition as only two checks
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

    // map and game status getters
    public int getSize() {
        return currentLevel.getSize();
    }

    public Cell[][] getMap() {
        return currentLevel.getMap();
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getDeathType() {
        return deathType;
    }

    public String getHighscores() {
        return scoreImport.formatScores();
    }

    public int getLevel() {
        return level;
    }

    // save / load functionality
    public String saveGame() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(savePath)))) {
            out.writeObject(this);
            return "Game saved!";
        } catch (IOException e) {
            return "Error saving game: " + e.getMessage();
        }
    }

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

    public boolean saveExists() {
        File file = new File(savePath);
        return file.exists();
    }

    // testing
    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Plays a text-based game
     */
    public static void main(String[] args) {
        // creating console ui and starting game
        ConsoleUI consoleUI = new ConsoleUI();
        consoleUI.start();
    }
}
