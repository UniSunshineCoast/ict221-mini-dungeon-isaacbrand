import dungeon.engine.*;
import dungeon.engine.cells.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameEngine {
    private GameEngine engine;
    private GameEngine controlEngine;
    private final String testSavePath = "test_md_save.dat";

    @BeforeEach
    void setUp() {
        // regular engine with difficulty 3 before each test
        engine = new GameEngine(3);

        // creating a specialised engine
        controlEngine = new GameEngine(0);
        createControl(controlEngine);

        // deleting save file if it exists
        File testSave = new File(testSavePath);
        if (testSave.exists()) {
            testSave.delete();
        }
    }

    // creating a controlled GameEngine instance
    private void createControl(GameEngine engine) {
        Level level = engine.getCurrentLevel();
        Cell[][] map = engine.getMap();

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (x == 0 || x == 9 || y == 0 || y == 9) {
                    map[y][x] = new Wall();
                    map[y][x].cellSetPos(x, y);
                } else {
                    map[y][x] = new Empty();
                    map[y][x].cellSetPos(x, y);
                }
            }
        }

        // placing cells at known locations
        map[8][1] = new Entry();// Entry at bottom left
        map[8][1].cellSetPos(1, 8);

        map[7][1] = new Gold(); // Gold above entry
        map[7][1].cellSetPos(1, 7);

        map[6][1] = new Trap(); // Trap above gold
        map[6][1].cellSetPos(1, 6);

        map[5][1] = new MeleeMutant(); // Melee Mutant above trap
        map[5][1].cellSetPos(1, 5);

        map[5][2] = new HealthPotion(); // Health potion to the right of the melee mutant
        map[5][2].cellSetPos(2, 5);

        map[5][5] = new RangedMutant(); // Ranged Mutant three cells right of the health potion
        map[5][5].cellSetPos(5, 5);
        // pos for attack calculations
        if (map[5][5] instanceof RangedMutant rangedMutant) {
            rangedMutant.setPosition(5, 5);
        }

        map[5][6] = new Ladder(); // Ladder to the right of the ranged mutant
        map[5][6].cellSetPos(6, 5);

        // resetting player pos to entry
        engine.getPlayer().startPos(1, 8);

        // setting control entry and ladder pos
        level.setEntryPos(new Position(1, 8));
        level.setLadderPos(new Position(6, 5));
    }

    //---------------------------------------------------------------------------------------- BASIC TESTING
    @Test
    void testControlMap() {
        // testing initialisation
        Cell[][] map = controlEngine.getMap();

        // checking boundary walls
        for (int i = 0; i < 10; i++) {
            assertInstanceOf(Wall.class, map[0][i], "Wall missing at " + i);    // top
            assertInstanceOf(Wall.class, map[9][i], "Wall missing at " + i);    // bottom
            assertInstanceOf(Wall.class, map[i][0], "Wall missing at " + i);    // left
            assertInstanceOf(Wall.class, map[i][9], "Wall missing at " + i);    // right
        }

        // checking interactable cells
        assertInstanceOf(Entry.class, map[8][1], "Entry not found");
        assertInstanceOf(Gold.class, map[7][1], "Gold not found");
        assertInstanceOf(Trap.class, map[6][1], "Trap not found");
        assertInstanceOf(MeleeMutant.class, map[5][1], "Melee Mutant not found");
        assertInstanceOf(HealthPotion.class, map[5][2], "Health Potion not found");
        assertInstanceOf(RangedMutant.class, map[5][5], "Ranged Mutant not found");
        assertInstanceOf(Ladder.class, map[5][6], "Ladder not found");

        // checking player pos
        Position position = controlEngine.getPlayer().getPosition();
        assertEquals(1, position.getX(), "Player X incorrect");
        assertEquals(8, position.getY(), "Player Y incorrect");

    }

    @Test
    void testGetSize() {
        assertEquals(10, engine.getSize());
    }

    @Test
    void testInitialState() {
        assertFalse(engine.isGameOver());
        assertEquals(-1, engine.getDeathType());
        assertNotNull(engine.getPlayer());
        assertNotNull(engine.getMap());
    }

    //------------------------------------------------------------------------------- MOVEMENT TESTING
    @Test
    void testMovement() {
        // storing start pos
        Position oldPos = new Position(engine.getPlayer().getPosition());

        // getting new pos based on move up
        engine.moveUp();
        Position newPos = new Position(engine.getPlayer().getPosition());

        // check
        assertEquals(oldPos.getX(), newPos.getX());
        assertEquals((oldPos.getY() - 1), newPos.getY());

        // updating old pos
        oldPos = newPos;

        // getting new pos based on move right
        engine.moveRight();
        newPos = new Position(engine.getPlayer().getPosition());

        // check
        assertEquals((oldPos.getX() + 1), newPos.getX());
        assertEquals(oldPos.getY(), newPos.getY());

        // updating old pos
        oldPos = newPos;

        // getting new pos based on move down
        engine.moveDown();
        newPos = new Position(engine.getPlayer().getPosition());

        // check
        assertEquals(oldPos.getX(), newPos.getX());
        assertEquals((oldPos.getY() + 1), newPos.getY());

        // updating old pos
        oldPos = newPos;

        // getting new pos based on move left
        engine.moveLeft();
        newPos = new Position(engine.getPlayer().getPosition());

        // check
        assertEquals((oldPos.getX() - 1), newPos.getX());
        assertEquals(oldPos.getY(), newPos.getY());
    }

    @Test
    void testStepTrack() {
        // getting start steps
        int startSteps = engine.getPlayer().getSteps();

        // check
        engine.moveRight();
        assertEquals(startSteps + 1, engine.getPlayer().getSteps());
    }

    @Test
    void testInvalidMove() {
        // moving to find a boundary
        boolean hitWall = false;
        for (int i = 0; i < 10; i++) {
            String result = engine.moveUp();
            if (result.contains("hit a wall")) {
                hitWall = true;
                break;
            }
        }

        assertTrue(hitWall, "Hit a wall while moving toward boundary.");
    }

    //------------------------------------------------------------------------------------------ PLAYER/CELL INTERACTION TESTING

    @Test
    void testInteraction() {
        // covering map until interaction occurs
        boolean foundInteraction = false;
        String result;

        // loop counter for spiral scan pattern
        int loop = engine.getSize() - 1;

        while (loop > 0) {
            // up
            for (int i = 0; i < loop; i++) {
                result = engine.moveUp();
                if (result.length() > "Moved up one step.".length() && !result.contains("hit a wall")) {
                    foundInteraction = true;
                    break;
                }
            }
            if (foundInteraction) break;

            // right
            for (int i = 0; i < loop; i++) {
                result = engine.moveRight();
                if (result.length() > "Moved right one step.".length() && !result.contains("hit a wall")) {
                    foundInteraction = true;
                    break;
                }
            }
            if (foundInteraction) break;

            // down
            for (int i = 0; i < loop; i++) {
                result = engine.moveDown();
                if (result.length() > "Moved down one step.".length() && !result.contains("hit a wall")) {
                    foundInteraction = true;
                    break;
                }
            }
            if (foundInteraction) break;

            // reducing steps to make loop one cell less
            loop--;
            if (loop <= 0) break;

            // left
            for (int i = 0; i < loop; i++) {
                result = engine.moveLeft();
                if (result.length() > "Moved left one step.".length() && !result.contains("hit a wall")) {
                    foundInteraction = true;
                    break;
                }
            }
            if (foundInteraction) break;
        }

        // states that an interaction was found and was successful
        assertTrue(foundInteraction);
    }

    @Test
    void testGold() {
        // initial score
        int startScore = controlEngine.getPlayer().getScore();

        // moving to gold
        controlEngine.moveUp();

        // checking score increased by 2
        assertEquals(startScore + 2, controlEngine.getPlayer().getScore(), "Gold should increase score by 2");

        // checking cell is removed
        Cell checkCell = controlEngine.getMap()[7][1];
        assertInstanceOf(Empty.class, checkCell, "Gold cell should disappear after interaction");
    }

    @Test
    void testTrap() {
        // setting player pos to under the trap
        controlEngine.getPlayer().startPos(1, 7);

        // initial hp
        int startHp = controlEngine.getPlayer().getHp();

        // moving to trap
        controlEngine.moveUp();

        // checking hp decreased by 2
        assertEquals(startHp - 2, controlEngine.getPlayer().getHp(), "Trap should damage player by 2");

        // checking trap exists after interaction
        Cell checkCell = controlEngine.getMap()[6][1];
        assertInstanceOf(Trap.class, checkCell, "Trap should stay after an interaction");

        // verifying trap retriggers
        controlEngine.moveDown();
        int trapHP = controlEngine.getPlayer().getHp();
        controlEngine.moveUp();
        assertEquals(trapHP - 2, controlEngine.getPlayer().getHp(), "Trap should damage player by 2 on repeat interactions");
    }

    @Test
    void testMeleeMutant() {
        // setting player pos to under the melee mutant
        controlEngine.getPlayer().startPos(1, 6);

        // initial hp and score
        int startHp = controlEngine.getPlayer().getHp();
        int startScore = controlEngine.getPlayer().getScore();

        // moving to melee mutant
        controlEngine.moveUp();

        // checking hp decreased by 2
        assertEquals(startHp - 2, controlEngine.getPlayer().getHp(), "Melee Mutant should damage player by 2");

        // checking score increased by 2
        assertEquals(startScore + 2, controlEngine.getPlayer().getScore(), "Melee Mutant should increase score by 2 on interaction");

        // checking cell is removed
        Cell checkCell = controlEngine.getMap()[5][1];
        assertInstanceOf(Empty.class, checkCell, "Melee Mutant cell should disappear after interaction");
    }

    @Test
    void testHealthPotion() {
        // setting player pos to beside health potion
        controlEngine.getPlayer().startPos(1, 5);

        // reducing player hp
        controlEngine.getPlayer().hurt(4); // reducing hp by 4
        int startHp = controlEngine.getPlayer().getHp();

        // moving to health potion
        controlEngine.moveRight();

        // checking hp increase
        assertEquals(Math.min(10, startHp + 4), controlEngine.getPlayer().getHp(), "Health potion should increase player HP by 4");

        // checking cell is removed
        Cell checkCell = controlEngine.getMap()[5][2];
        assertInstanceOf(Empty.class, checkCell, "Health Potion cell should disappear after interaction");

        // testing max hp cap
        controlEngine.getPlayer().setHp(8);
        controlEngine.moveLeft(); // moving away from cell

        controlEngine.getMap()[5][2] = new HealthPotion(); // placing health potion
        controlEngine.getMap()[5][2].cellSetPos(2, 5);

        controlEngine.moveRight(); // moving to health potion
        assertEquals(10, controlEngine.getPlayer().getHp(), "HP should be capped at 10");

    }

    @Test
    void testRangedMutant() {
        // setting player pos to 2 steps away from ranged mutant
        controlEngine.getPlayer().startPos(3, 5);

        // initial hp and score
        int startHp = controlEngine.getPlayer().getHp();
        int startScore = controlEngine.getPlayer().getScore();

        // move player into attack range
        String result = controlEngine.moveRight();

        // check if attack was successful or occured by analysing result message
        if (startHp > controlEngine.getPlayer().getHp()) {
            assertEquals(startHp - 2, controlEngine.getPlayer().getHp(), "Ranged Mutant should damage player by 2 on successful attack");
            assertTrue(result.contains("arrows"), "Movement result should mention attack");
        }

        // moving onto ranged mutant
        controlEngine.moveRight();

        // check score increased by 2
        assertEquals(startScore + 2, controlEngine.getPlayer().getScore(), "Ranged Mutant should increase score by 2 on defeat");

        // checking hp didn't decrease after interaction (only if a ranged attack occured)
        if (startHp > controlEngine.getPlayer().getHp()) {
            assertEquals(startHp - 2, controlEngine.getPlayer().getHp(), "Ranged Mutant interaction should not deal damage");
        }


        // checking cell is removed
        Cell checkCell = controlEngine.getMap()[5][5];
        assertInstanceOf(Empty.class, checkCell, "Melee Mutant cell should disappear after interaction");
    }

    //-------------------------------------------------------------------------------------------------------------- GAME PROGRESSION TESTING
    @Test
    void testLadder() {
        // setting play pos to beside ladder
        controlEngine.getPlayer().startPos(5, 5);

        // getting level
        int level = controlEngine.getLevel();

        // moving player to ladder
        String result = controlEngine.moveRight();

        // check if level increased
        assertEquals(level + 1, controlEngine.getLevel(), "Level should increase after ladder interaction");

        // check movement result contains a result reffering to moving levels
        assertTrue(result.contains("level 2"), "Result should mention the new level");

        // checking difficulty increase
        int newDiff = controlEngine.getCurrentLevel().getDifficulty();
        assertEquals(2, newDiff, "Difficulty should increase by 2 on new level");
    }

    @Test
    void testGameComplete() {
        // creating an engine for level 2
        GameEngine lvl2Engine = new GameEngine(0);
        createControl(lvl2Engine); // for controlled item placements
        lvl2Engine.setLevel(2); // setting level count to 2

        // moving player to ladder
        lvl2Engine.getPlayer().startPos(5, 5);

        // triggering win condition
        String result = lvl2Engine.moveRight();

        // checking game over
        assertTrue(lvl2Engine.isGameOver(), "Game should be over after reaching ladder on level 2");

        // checking win
        assertTrue(result.contains("win"), "Should show win message");
    }

    // game completion test

    //------------------------------------------------------------------------------------------ GAME MANAGEMENT TESTING
    @Test
    void testGameOver() {
        // setting hp to 0 to test player death
        engine.getPlayer().hurt(engine.getPlayer().getHp());

        // check trigger
        engine.moveUp();

        assertTrue(engine.isGameOver());
        assertEquals(0, engine.getDeathType()); // death to hp is 0
    }

    @Test
    void testMaxSteps() {
        GameEngine stepsEngine = new GameEngine(0);
        createWalkMap(stepsEngine);

        // centering player
        stepsEngine.getPlayer().startPos(5, 5);

        // moving until 98 steps
        for (int i = 0; i < 49; i++) {
            stepsEngine.moveLeft();
            stepsEngine.moveRight();
        }

        // checking steps are 98
        assertEquals(98, stepsEngine.getPlayer().getSteps(), "Player should have 98 steps");

        // reaching max steps
        stepsEngine.moveLeft();
        stepsEngine.moveRight();

        // checking game over
        assertTrue(stepsEngine.isGameOver(), "Game should be over on max steps");
        assertEquals(1, stepsEngine.getDeathType(), "Death type should be 1 (corresponding value for max steps reached)");
        assertEquals(-1, stepsEngine.getPlayer().getScore(), "Score should be -1 on gameover due to loss");


    }

    //------------------------------------------------------------------------------------- SCORE TESTING
    @Test
    void testPersistentScores() {
        // dedicated test file
        String testScorePath = "test_md_highscores.dat";
        File testScoreFile = new File(testScorePath);

        try {
            // deleteing file if exists
            if (testScoreFile.exists()) {
                testScoreFile.delete();
            }

            // creating a score instance with 3 scores
            Score testScore = new Score(testScorePath, 3); // only using 3 saved scores for testing

            // adding temp scores
            assertTrue(testScore.addScore(10), "First score should be a high score");
            assertTrue(testScore.addScore(15), "Higher score should be a high score");
            assertTrue(testScore.addScore(7), "Lower score should be a high score when total scores is less then max");

            // another score to test sorting and trimming
            assertFalse(testScore.addScore(4), "Lower score should not be a highscore when list is full");

            // verifying string formatting
            String highScores = testScore.formatScores();
            assertNotNull(highScores, "High scores should be not null");
            assertTrue(highScores.contains("#1 Score: 15"), "First entry should be highest score with correct format");
            assertTrue(highScores.contains("#2 Score: 10"), "Second entry should be the second highest score with correct format");
            assertTrue(highScores.contains("#3 Score: 7"), "Third entry should be the lowest high score with correct format");
            assertFalse(highScores.contains("4"), "Fourth score should not appear in list");

            // testing score loading with a new instance
            Score loadScore = new Score(testScorePath,3);
            String loadHighScores = loadScore.formatScores();

            // verfying loaded scores match current scores
            assertEquals(highScores, loadHighScores, "Loaded scores should match original scores");

            // adding a new highscore to the load instance
            assertTrue(loadScore.addScore(20), "New highest score should be 20");

            // checking updates to list
            String newHighScores = loadScore.formatScores();
            assertTrue(newHighScores.contains("#1 Score: 20"), "New score should be first entry with correct format");
            assertTrue(newHighScores.contains("#2 Score: 15"), "Previous high score should be second with correct format");
            assertTrue(newHighScores.contains("#3 Score: 10"), "Previous second should be third with correct format");
            assertFalse(newHighScores.contains("7"), "Previous third should be exluced");

        } finally {
            // cleaning up test file
            if (testScoreFile.exists()) {
                testScoreFile.delete();
            }
        }
    }
    //------------------------------------------------------------------------------------- MAP GENERATION TESTING
    @Test
    void testMapGen() {
        // new engine for map gen testing
        GameEngine genEngine = new GameEngine(3); // default difficulty
        Cell[][] map = genEngine.getMap();

        // checking boundaries
        for (int i = 0; i < 10; i++) {
            assertInstanceOf(Wall.class, map[0][i], "Missing top wall");    // top
            assertInstanceOf(Wall.class, map[9][i], "Missing bottom wall ");    // bottom
            assertInstanceOf(Wall.class, map[i][0], "Missing left wall");    // left
            assertInstanceOf(Wall.class, map[i][9], "Missing right wall");    // right
        }

        // initialising item count verification
        int goldCount = 0;
        int healthCount = 0;
        int trapCount = 0;
        int mMutantCount = 0;
        int rMutantCount = 0;
        int ladderCount = 0;
        int entryCount = 0;

        // tracking positions to check for overlaps
        Set<String> posTaken = new HashSet<>();

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Cell cell = map[y][x];
                String pos = x + ", " + y;

                // skipping walls as overlaps can occur on corners
                if (!(cell instanceof Wall)) {
                    assertFalse(posTaken.contains(pos), "Items should not overlap at " + pos);
                    posTaken.add(pos);
                }

                if (cell instanceof Gold) goldCount++;
                else if (cell instanceof HealthPotion) healthCount++;
                else if (cell instanceof Trap) trapCount++;
                else if (cell instanceof MeleeMutant) mMutantCount++;
                else if (cell instanceof RangedMutant) rMutantCount++;
                else if (cell instanceof Ladder) ladderCount++;
                else if (cell instanceof Entry) entryCount++;
                }
            }

        // item count checks
        assertEquals(5, goldCount, "Should be 5 gold cells");
        assertEquals(2, healthCount, "Should be 2 health potion cells");
        assertEquals(5, trapCount, "Should be 5 trap cells");
        assertEquals(3, mMutantCount, "Should be 3 melee mutant cells");
        assertEquals(3, rMutantCount, "Should be 3 ranged mutant cells");
        assertEquals(1, ladderCount, "Should be 1 ladder cell");
        assertEquals(1, entryCount, "Should be 1 entry cell");

        // checking player pos at entry
        Position position = genEngine.getPlayer().getPosition();
        Position entryPos = genEngine.getCurrentLevel().getEntryPos();
        assertEquals(entryPos.getX(), position.getX(), "Player X incorrect, should be equal to entry X");
        assertEquals(entryPos.getY(), position.getY(), "Player Y incorrect, should be equal to entry Y");
    }


    //------------------------------------------------------------------------------------- SAVE/LOAD TESTING
    // test save/load
    @Test
    void testSaveLoad() {
        // dedicated save engine with test save path
        GameEngine saveEngine = new GameEngine(3, new Score("test_md_scores.dat"), testSavePath);

        // game state changes to test saving/loading capabilities
        saveEngine.getPlayer().hurt(2);
        saveEngine.getPlayer().addScore(10);
        saveEngine.moveRight();
        int baseHP = saveEngine.getPlayer().getHp();
        int baseScore = saveEngine.getPlayer().getScore();
        Position basePos = saveEngine.getPlayer().getPosition();

        // saving
        saveEngine.saveGame();

        // verify save file now exists
        assertTrue(saveEngine.saveExists(), "Save file should exist after creation");

        // dedicated load engine
        GameEngine loadEngine = new GameEngine(0, new Score("test_md_scores.dat"), testSavePath);

        // loading game
        boolean loadTrue = loadEngine.loadGame();
        assertTrue(loadTrue, "Game should load successfully");

        // verifying game state
        assertEquals(baseHP, loadEngine.getPlayer().getHp(), "HP should be preserved on load");
        assertEquals(baseScore, loadEngine.getPlayer().getScore(), "Score should be preserved on load");
        assertEquals(basePos.getX(), loadEngine.getPlayer().getPosition().getX(), "Player X position should be preserved on load");
        assertEquals(basePos.getY(), loadEngine.getPlayer().getPosition().getY(), "Player Y position should be preserved on load");
    }

    @Test
    void testInvalidLoad() {
        // creating fake file path
        String invalidPath = "md_bait.dat";
        File invalidFile = new File(invalidPath);

        // ensuring file doesn't exists
        if (invalidFile.exists()) {
            invalidFile.delete();
        }

        // game engine instance with invalid path
        GameEngine invalidEngine = new GameEngine(3, new Score("test_md_scores.dat"), invalidPath);

        // attempting to load
        boolean loadResult = invalidEngine.loadGame();

        // verifying graceful failure
        assertFalse(loadResult, "Loading a file that doesn't exist should fail");
    }

    //------------------------------------------------------------------------------------- HELPERS
    // helper for testing max steps
    private void createWalkMap(GameEngine engine) {
        Level level = engine.getCurrentLevel();
        Cell[][] map = level.getMap();

        // creating an open map with boundary walls
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (x == 0 || x == 9 || y == 0 || y == 9) {
                    map[y][x] = new Wall();
                    map[y][x].cellSetPos(x, y);
                } else {
                    map[y][x] = new Empty();
                    map[y][x].cellSetPos(x, y);
                }
            }
        }
    }
}

