package dungeon.gui;

import dungeon.engine.*;
import dungeon.engine.Cell;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.Optional;

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

    @FXML
    public void initialize() {
        // checking if load game exiists
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

        // gui init
        updateGui();
    }

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

    private void loadGame() {
        engine = new GameEngine(0, new dungeon.engine.Score("md_highscores.dat"), savePath);
        if (engine.loadGame()) {
            status("Game loaded!");
        } else {
            status("Failed to load game. Starting a new game...");
            askDifficulty();
        }
    }

    private void updateGui() {
        if (engine == null) return;

        // clearing old GUI grid pane
        gridPane.getChildren().clear();


        // setting grid lines
        gridPane.setGridLinesVisible(false);
        gridPane.setGridLinesVisible(true);

        // get map and player pos
        Cell[][] map = engine.getMap();
        Position playerPos = engine.getPlayer().getPosition();

        // grid calc
        double gridWidth = gridPane.getPrefWidth() * 0.95;
        double gridHeight = gridPane.getPrefHeight() * 0.95;

        // cell calc
        double cellSize = Math.min(
                gridHeight / engine.getSize(),
                gridWidth / engine.getSize()
        );

        // minimum cell size
        cellSize = Math.max(cellSize, 30);

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
                CellFX cellFX = new CellFX(cell);
                cellFX.setPrefSize(cellSize, cellSize);

                // marking player pos
                if (i == playerPos.getY() && j == playerPos.getX()) {
                    cellFX.setStyle("-fx-background-color: lightblue; -fx-border-color: blue");
                    cellFX.getChildren().clear();
                    cellFX.getChildren().add(new javafx.scene.text.Text(("P")));
                }

                gridPane.add(cellFX, j, i);
            }
        }
        // checking if game is over
        if (engine.isGameOver()) {
            gameOver();
        }
    }

    private void status(String message) {
        statusArea.appendText(message + "\n");
        // auto-scroll
        statusArea.setScrollTop(Double.MAX_VALUE);
    }

    private void updateHighScores() {
        if (engine != null) {
            highscoreArea.setText(engine.getHighscores());
        }
    }

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

    private void disableControls() {
        upButton.setDisable(true);
        downButton.setDisable(true);
        leftButton.setDisable(true);
        rightButton.setDisable(true);
        saveButton.setDisable(true);
    }

    // event handlers
    @FXML
    private void handleUp() {
        String result = engine.moveUp();
        status(result);
        updateGui();
    }

    @FXML
    private void handleDown() {
        String result = engine.moveDown();
        status(result);
        updateGui();
    }

    @FXML
    private void handleLeft() {
        String result = engine.moveLeft();
        status(result);
        updateGui();
    }

    @FXML
    private void handleRight() {
        String result = engine.moveRight();
        status(result);
        updateGui();
    }

    @FXML
    private void handleSave() {
        String result = engine.saveGame();
        status(result);
        updateGui();
    }

    @FXML
    private void handleHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("Dungeon Help");
        alert.setContentText("""
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
        alert.showAndWait();
    }

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
