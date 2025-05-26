package dungeon.engine;

import java.io.File;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private GameEngine engine;
    private final String savePath;

    public ConsoleUI() {
        this("md_saves.dat");
    }

    public ConsoleUI(String savePath) {
        this.savePath = savePath;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to the Mini Dungeon! Mwahaha...");

        // checking for saved game
        File saveFile = new File(savePath);
        if (saveFile.exists()) {
            System.out.print("A saved game was found. Would you like to load it? (y/n): ");
            String loadInput = scanner.nextLine().toLowerCase();

            if (loadInput.equals("y")) {
                engine = new GameEngine(0, new Score("md_highscores.dat"), savePath);
                if (engine.loadGame()) {
                    System.out.println("Game loaded!");
                } else {
                    System.out.println("Failed to load game. Starting a new game...");
                    engine = newGame();
                }
            } else {
                engine = newGame();
            }
        } else {
            engine = newGame();
        }

        runGameLoop();

        scanner.close();
    }

    private GameEngine newGame() {
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

        return new GameEngine(difficulty, new Score("md_highscores.dat"), savePath);
    }

    private void runGameLoop() {
        System.out.println("The Basics: 'u' for up, 'd' for down, 'l' for left, 'r' for right, 'q' to quit, 'h' for help and 's' to save the game.");
        System.out.println("Current level: " + engine.getLevel());

        boolean quit = false;
        while (!quit && !engine.isGameOver()) {
            // map and player info display
            displayMap();
            displayPlayerInfo();

            // grabbing input
            System.out.print("Enter command: ");
            String input = scanner.nextLine().toLowerCase();

            if (input.equals("q")) {
                quit = true;
            } else if (input.equals("s")) {
                String saveOutput = engine.saveGame();
                System.out.println(saveOutput);
            } else {
                processInput(input);
            }
        }

        displayGameOver();
        displayHighScores();
    }

    private void displayMap() {
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

    private void displayPlayerInfo() {
        Player player = engine.getPlayer();
        System.out.println("HP: " + player.getHp());
        System.out.println("Steps: " + player.getSteps());
        System.out.println("Score: " + player.getScore());
    }

    private void processInput(String input) {
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
            case "h":
                displayHelp();
                return;
            default:
                System.out.println("Invalid command.");
                return;
        }

        System.out.println(result);
    }

    private void displayHelp() {
        System.out.println("""
                ---Help---
                Commands:
                u - move up
                d - move down
                l - move left
                r - move right
                s - save game
                q - quit game
                h - help
                
                Cells:
                P - player
                # - wall
                G - gold
                H - health potion
                T - trap
                M - melee mutant
                R - ranged mutant
                E - entry
                L - ladder
                
                Goal:
                Find the ladder in each level and keep progressing until you are done.
                """);
    }
    private void displayGameOver() {
        if (engine.isGameOver()){
            int deathType = engine.getDeathType();
            if (deathType == 0) {
                System.out.println("Game Over. You died.");
            } else if (deathType == 1) {
                System.out.println("Game Over. You walked your last step.");
            }
        }
    }

    private void displayHighScores() {
        String highScores = engine.getHighscores();
        if (!highScores.isEmpty()) {
            System.out.println("\n--High Scores--");
            System.out.println(highScores);
        }

        System.out.println("Thanks for playing! (until next time...)");
    }
}
