package dungeon.gui;

import dungeon.engine.*;
import dungeon.engine.Cell;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.Optional;

/**
 * Controller class for game GUI
 * Handles:
 * - Game GUI initialisation and config
 * - User input processing
 * - UI updates and state changes
 * - Map rendering and player visualisation
 * - Game status outputs
 * - Save/load functionality
 * - Game over conditions and display
 */
public class Controller {
    @FXML private GridPane gridPane;
    @FXML private Label hpLabel;
    @FXML private Label stepsLabel;
    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;
    @FXML private TextArea statusArea;
    @FXML private TextArea highscoreArea;
    @FXML private Button saveButton;
    @FXML @SuppressWarnings("unused") private Button helpButton;
    @FXML @SuppressWarnings("unused") private Button quitButton;
    @FXML private Button upButton;
    @FXML private Button downButton;
    @FXML private Button leftButton;
    @FXML private Button rightButton;

    private GameEngine engine;
    private final String savePath = "md_saves.dat";

    /**
     * Initalises the controller class
     * Preloads game assets, checks for save games, initialises UI and prepares game state
     */
    @FXML
    public void initialize() {
        // pre-loading images
        CellFX preloader = new CellFX(null);
        preloader.preload();

        // checking if load game exists
        if (new File(savePath).exists()) {
            askLoadGame();
        } else {
            // start a new game
            askDifficulty();
        }

        // stat labels
        hpLabel.setText("HP: 0");
        stepsLabel.setText("Steps: 0");
        scoreLabel.setText("Score: 0");
        levelLabel.setText("Level: 1");

        // highscore display
        updateHighScores();

        // gui init - runs after layout pass
        Platform.runLater(this::updateGui);
    }

    /**
     * Displays load request pending user input to create a new game or load existing game
     */
    private void askLoadGame() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Load Game");
        alert.setHeaderText("A saved game was found.");
        alert.setContentText("Would you like to load the saved game?");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {
            loadGame();
        } else {
            askDifficulty();
        }
    }

    /**
     * Displays difficulty request pending user input to create a game engine instance of difficulty selected
     */
    private void askDifficulty() {
        TextInputDialog dialog = new TextInputDialog("3");
        dialog.setTitle("Difficulty");
        dialog.setHeaderText("Difficulty Selection");
        dialog.setContentText("Please entire your desired difficulty from 0-10:");

        Optional<String> result = dialog.showAndWait();

        int difficulty = 3; // default
        if (result.isPresent()) {
            try {
                int input = Integer.parseInt(result.get());
                if (input >= 0 && input <= 10) {
                    difficulty = input;
                }
                } catch (NumberFormatException e) {
                    // using default
                }
            }

            engine = new GameEngine(difficulty, new Score("md_highscores.dat"), savePath);
            status("Welcome to the MiniDungeon! New game started with difficulty " + difficulty + ".");
        }

    /**
     * Attempts to load a saved game, fallbacks to new game creation
     */
    private void loadGame() {
        engine = new GameEngine(0, new dungeon.engine.Score("md_highscores.dat"), savePath);
        if (engine.loadGame()) {
            status("Game loaded!");
        } else {
            status("Failed to load game. Starting a new game...");
            askDifficulty();
        }
    }

    /**
     * Updates GUI, renders game map, updates player stats, checks game state
     * Handles dynamic sizing and cell placements
     */
    private void updateGui() {
        if (engine == null) return;

        // clearing old GUI grid pane
        gridPane.getChildren().clear();

        // get map and player pos
        Cell[][] map = engine.getMap();
        Position playerPos = engine.getPlayer().getPosition();

        // cell size
        double cellSize = 35.0;

        // player stat updates
        Player player = engine.getPlayer();
        hpLabel.setText("HP: " + player.getHp());
        stepsLabel.setText("Steps: " + player.getSteps());
        scoreLabel.setText("Score: " + player.getScore());
        levelLabel.setText("Level: " + engine.getLevel());

        // filling grid with cells
        for(int i = 0; i < engine.getSize(); i++) {
            for (int j = 0; j < engine.getSize(); j++) {
                Cell cell = map[i][j];

                // visual cells
                CellFX cellFX = new CellFX(cell, cellSize);

                // marking player pos
                if (i == playerPos.getY() && j == playerPos.getX()) {
                    cellFX.playerOverlay();
                }

                gridPane.add(cellFX, j, i);
            }
        }

        // making grid lines visisble (resets on each update instance)
        gridPane.setGridLinesVisible(false);
        gridPane.setGridLinesVisible(true);

        // checking if game is over
        if (engine.isGameOver()) {
            gameOver();
        }
    }

    /**
     * Adds auto-scrolling text to the status area
     * @param message text to input into status area
     */
    private void status(String message) {
        statusArea.appendText(message + "\n");
        // auto-scroll
        statusArea.setScrollTop(Double.MAX_VALUE);
    }

    /**
     * Updates UI highscore display
     */
    private void updateHighScores() {
        if (engine != null) {
            highscoreArea.setText(engine.getHighscores());
        }
    }

    /**
     * Handles game over display, disables controls and shows final score
     */
    private void gameOver() {
        String message;
        int deathType = engine.getDeathType();

        if (deathType == 0) {
            message = "Game over. You died.";
        } else if (deathType == 1) {
            message = "Game over. You walked your last step";
        } else {
            message = "You win!";
        }

        status(message);
        disableControls();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(message);
        alert.setContentText("Final score: " + engine.getPlayer().getScore());
        alert.showAndWait();

        updateHighScores();
    }

    /**
     * Disables controls on game over
     */
    private void disableControls() {
        upButton.setDisable(true);
        downButton.setDisable(true);
        leftButton.setDisable(true);
        rightButton.setDisable(true);
        saveButton.setDisable(true);
    }

    //-------------------------------------------------------------------------- EVENT HANDLERS
    // handles up button interaction
    @FXML
    private void handleUp() {
        // string of result
        String result = engine.moveUp();
        // adding result to status display
        status(result);
        // updating GUI
        updateGui();
    }

    // handles down button interaction
    @FXML
    private void handleDown() {
        String result = engine.moveDown();
        status(result);
        updateGui();
    }

    // handles left button interaction
    @FXML
    private void handleLeft() {
        String result = engine.moveLeft();
        status(result);
        updateGui();
    }

    // handles right button interaction
    @FXML
    private void handleRight() {
        String result = engine.moveRight();
        status(result);
        updateGui();
    }

    // handles save button interaction
    @FXML
    private void handleSave() {
        String result = engine.saveGame();
        status(result);
        updateGui();
    }

    // handles help button interaction
    @FXML
    private void handleHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("Dungeon Help");
        alert.setContentText("""
                Commands:
                up - move up
                down - move down
                left - move left
                right - move right
                save - save game
                quit - quit game
                help - help
                
                Cells:
                P - player (grey)
                # - wall (black)
                G - gold (gold)
                H - health potion (light pink)
                T - trap (orange)
                M - melee mutant (off-green)
                R - ranged mutant (dark pink)
                E - entry (dark grey)
                L - ladder (light green)
                
                Goal:
                Find the ladder in each level and keep progressing until you are done.
                """);
        alert.showAndWait();
    }

    // handles quit button interaction with confirmation and an option to save game and then quit
    @FXML
    private void handleQuit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit");
        alert.setHeaderText("Are you sure you want to quit the game?");
        alert.setContentText("Warning: Progress will be lost unless game is saved.");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        ButtonType buttonTypeSave = new ButtonType("Save & Quit");

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeSave);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == buttonTypeYes) {
                Platform.exit();
            } else if (result.get() == buttonTypeSave) {
                engine.saveGame();
                Platform.exit();
            }
        }
    }
}
