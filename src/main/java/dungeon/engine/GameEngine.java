package dungeon.engine;

import dungeon.engine.cells.Empty;

import java.io.*;
import java.util.Scanner;

public class GameEngine implements Serializable {
    private Level currentLevel;
    private final Player player;
    private int difficulty;
    private int level;
    private boolean gameOver;
    private int deathType;
    private final Score scoreManager;
    private boolean isNewHS;

    // serial version UID
    @Serial
    private static final long serialVersionUID = 0L;

    // save file name
    private static final String SAVE = "md_saves.dat";

    public GameEngine(int difficulty) {
        this.difficulty = Math.min(10, Math.max(0, difficulty)); // explain
        this.level = 1;
        this.player = new Player();
        this.gameOver = false;
        this.deathType = -1; // represents no death, 0 is death due to no hp, 1 is death due to max steps reached, etc...
        this.scoreManager = new Score();
        this.isNewHS = false;

        initLevel();
    }

    private void initLevel() {
        currentLevel = new Level(level, difficulty);

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
        String output;

        // movement calculations
        switch (direction.toLowerCase()) {
            case "up":
                newPos.setY(oldPos.getY() - 1);
                output = "Moved up"; // temp
                break;
            case "down":
                newPos.setY(oldPos.getY() + 1);
                output = "Moved down"; // temp
                break;
            case "left":
                newPos.setX(oldPos.getX() - 1);
                output = "Moved left"; // temp
                break;
            case "right":
                newPos.setX(oldPos.getX() + 1);
                output = "Moved right"; // temp
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

                    currentLevel = new Level(level, difficulty);

                    currentLevel.setEntryPos(ladderPos);

                    player.startPos(ladderPos.getX(), ladderPos.getY());

                    output += " Moving onto level " + level + ". ";
                } else {
                    // player win
                    gameOver = true;
                    output += " Hey, that's the exit to the dungeon! You win!";

                    // checking if score is a new high score
                    isNewHS = scoreManager.addScore(player.getScore());
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
        return Level.getSize();
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
        return scoreManager.formatScores();
    }

    /**
     * Plays a text-based game
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameEngine engine;

        System.out.println("Welcome to the Mini Dungeon! Mwahaha...");

        // checking for saved game
        if (saveExists()) {
            System.out.print("A saved game was found. Would you like to load it? (y/n): ");
            String loadInput = scanner.nextLine().toLowerCase();

            if (loadInput.equals("y")) {
                engine = loadGame();
                if (engine != null) {
                    System.out.println("Game loaded!");
                } else {
                    System.out.println("Failed to load game. Starting a new game...");
                    engine = newGame(scanner);
                }
            } else {
                engine = newGame(scanner);
            }
        } else {
            engine = newGame(scanner);
        }

        System.out.println("The Basics: 'u' for up, 'd' for down, 'l' for left, 'r' for right, 'q' to quit and 's' to save the game.");
        System.out.println("Current level: " + engine.level);

        boolean quit = false;
        while (!quit && !engine.isGameOver()) {
            // map display
            displayMap(engine);

            // player info
            System.out.println("HP: " + engine.getPlayer().getHp());
            System.out.println("Steps: " + engine.getPlayer().getSteps());
            System.out.println("Score: " + engine.getPlayer().getScore());

            // grabbing input
            System.out.print("Enter command: ");
            String input = scanner.nextLine().toLowerCase();

            if (input.equals("q")) {
                quit = true;
            } else if (input.equals("s")) {
                String saveOutput = engine.saveGame();
                System.out.println(saveOutput);
            } else {
                String result;
                switch (input) {
                    case "u":
                        result = engine.moveUp();
                        break;
                    case "d":
                        result = engine.moveDown();
                        break;
                    case "l":
                        result = engine.moveLeft();
                        break;
                    case "r":
                        result = engine.moveRight();
                        break;
                    default:
                        System.out.println("Invalid command.");
                        continue;
                }

                System.out.println(result);
            }
        }

        // need to improve engine responsiveness to score tracking with mutants

        if (engine.isGameOver()){
            int deathType = engine.getDeathType();
            if (deathType == 0) {
                System.out.println("Game Over. You died.");
            } else if (deathType == 1) {
                System.out.println("Game Over. You walked your last step.");
            }
        }

        // score display
        // should not appear if no scores are present
        System.out.println("\n--High Scores--");
        System.out.println(engine.getHighscores());

        // close
        System.out.println("Thanks for playing! (until next time...)");
        scanner.close();
    }

    // game creation
    private static GameEngine newGame(Scanner scanner) {
        System.out.print("Please enter your preferred difficulty (0-10, default 3): ");
        String inputDifficulty = scanner.nextLine().trim();

        int difficulty = 3;
        if (!inputDifficulty.isEmpty()) {
            try {
                difficulty = Integer.parseInt(inputDifficulty);
                if (difficulty < 0 || difficulty > 10) {
                    System.out.println("Invalid input, using default difficulty...");
                    difficulty = 3;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, using default difficulty...");
            }
        }

        return new  GameEngine(difficulty);
    }

    // displaying map in console
    private static void displayMap(GameEngine engine) {
        Cell[][] map = engine.getMap();
        Position playerPos = engine.getPlayer().getPosition();

        for (int y = 0; y < engine.getSize(); y++) {
            for (int x = 0; x < engine.getSize(); x++) {
                if (x== playerPos.getX() && y == playerPos.getY()) {
                    System.out.print('P');
                } else {
                    System.out.print(map[y][x].cellGetSymbol());
                }
            }
            System.out.println();
        }
    }

    // save / load functionality
    public String saveGame() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(SAVE)))) {
            out.writeObject(this);
            return "Game saved!";
        } catch (IOException e) {
            return "Error saving game: " + e.getMessage();
        }
    }

    public static GameEngine loadGame() {
        File file = new File(SAVE);
        if (!file.exists()) {
            return null; // no save file
        }

        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(SAVE)))) {
            Object obj = in.readObject();
            if (obj instanceof GameEngine) {
                return (GameEngine) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game: " + e.getMessage());
        }

        return null;
    }

    public static boolean saveExists() {
        File file = new File(SAVE);
        return file.exists();
    }
}
