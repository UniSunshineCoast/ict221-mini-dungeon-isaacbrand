import dungeon.engine.GameEngine;
import dungeon.engine.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameEngine {
    private GameEngine engine;

    @BeforeEach
    void setUp() {
        // new game engine with difficulty 3 before each test
        engine = new GameEngine(3);
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

}

