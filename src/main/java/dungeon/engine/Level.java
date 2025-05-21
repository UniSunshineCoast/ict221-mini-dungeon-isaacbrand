package dungeon.engine;

import dungeon.engine.cells.*;

import java.util.Random;

// handles current level management + level map creation
public class Level {
    private static final int SIZE = 10;
    private final int currentLevel;
    private final int difficulty;
    private final Cell[][] map;
    private Position entryPos;
    private Position ladderPos;
    private final Random random = new Random();

    public Level(int currentLevel, int difficulty) {
        this.currentLevel = currentLevel;
        this.difficulty = difficulty;
        this.map = new Cell[SIZE][SIZE];
        createMap();
    }

    //---------------------------------------------------------------- MAP GEN
    private void createMap() {
        // making all cells empty
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                // using [y][x] due to array indexing, first index is rows (up/down) and second index is columns (left/right) meaning [y][x] translates to (x, y)
                map[y][x] = new Empty();
                map[y][x].cellSetPos(x, y);
            }
        }

        // map boundary
        createWalls();

        // entry
        placeEntry();

        // ladder
        placeLadder();

        // interactable
        placeItems();
    }

    private void createWalls() {
        for (int i = 0; i < SIZE; i++) {
            // top + bottom
            map[0][i] = new Wall();
            map[0][i].cellSetPos(i, 0);
            map[SIZE - 1][i] = new Wall();
            map[SIZE - 1][i].cellSetPos(SIZE - 1, i);

            // left + right
            map[i][0] = new Wall();
            map[i][0].cellSetPos(0 , i);
            map[i][SIZE - 1] = new Wall();
            map[i][SIZE - 1].cellSetPos(SIZE - 1, i);
        }

        // could create internal wall algorithm to make dungeon more like a maze
    }

    private void placeEntry() {
        if (currentLevel == 1) {
            // placing entry at bottom left
            int x = 1;
            int y = SIZE - 2;
            entryPos = new Position(x, y);
            map[y][x] = new Entry();
            map[y][x].cellSetPos(x, y);
        } else {
            // should be placed below previous levels ladder
            // planning to handle through the game engine
        }
    }

    private void placeLadder() {
        int x, y;
        do {
            x = random.nextInt(SIZE - 2) + 1;
            y = random.nextInt(SIZE - 2) + 1;
        } while (!(map[y][x] instanceof Empty));

        ladderPos = new Position(x, y);
        map[y][x] = new Ladder();
        map[y][x].cellSetPos(x, y);
    }

    private void placeItems() {
        // gold
        placeItems(5, Gold.class);

        // health potion
        placeItems(2, HealthPotion.class);

        // trap
        placeItems(5, Trap.class);

        // melee mutant
        placeItems(3, MeleeMutant.class);

        // ranged mutant, increased spawns with difficulty
        placeItems(difficulty, RangedMutant.class);
    }

    // helper for item placement
    private void placeItems(int total, Class<? extends Cell> cellType) {
        for (int i = 0; i < total; i++) {
            int x, y;
            do {
                // ensuring placement is inside wall perimeter
                x = random.nextInt(SIZE - 2) + 1;
                y = random.nextInt(SIZE - 2) + 1;
            } while (!(map[y][x] instanceof Empty));

            try {
                Cell cell = cellType.getDeclaredConstructor().newInstance(); // grabs class constructors and creates a new instance
                map[y][x] = cell;
                map[y][x].cellSetPos(x, y);

                // setting position for ranged mutant attack calculations
                if (cell instanceof RangedMutant rangedMutant) {
                    rangedMutant.setPosition(x, y);
                }
            } catch (Exception e) {
                System.err.println("Error during cell creation: " + e.getMessage());
            }
        }
    }

    //--------------------------------------------------------------------------------------- INTERACTIONS
    // method for levels 1 >, called in game engine once implemented
    public void setEntryPos(Position position) {
        // ensuring placement is inside wall perimeter
        this.entryPos = new Position(position);
        int x = position.getX();
        int y = position.getY();

        map[y][x] = new Entry();
        map[y][x].cellSetPos(x, y);
    }

    // check and execute for ranged mutant attacks
    public int checkRange(Player player) {
        int rangedDamage = 0;

        // checking cells for ranged mutants
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Cell cell = map[y][x];
                if (cell instanceof RangedMutant mutant) {
                    rangedDamage += mutant.tryRangedAttack(player);
                }
            }
        }

        return rangedDamage;
    }

    //-------------------------------------------------------------------------- UTILS
    public Cell getCell(Position position) {
        int x = position.getX();
        int y = position.getY();

        return map[y][x];
    }

    public Cell setCell(Position position, Cell cell) {
        int x = position.getX();
        int y = position.getY();

        cell.cellSetPos(x, y);
        map[y][x] = cell;
        return cell;
    }

    public boolean isLadder(Position position) {
        return ladderPos != null
                && position.getX() == ladderPos.getX()
                && position.getY() == ladderPos.getY();

    }

    //-------------------------------------------------------------------------- GETTERS AND SETTERS
    public Cell[][] getMap() {
        return map;
    }

    public Position getEntryPos() {
        return new Position(entryPos);
    }

    public Position getLadderPos() {
        return new Position(ladderPos);
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public static int getSize() {
        return SIZE;
    }
}
