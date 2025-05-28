package dungeon.engine;

import dungeon.engine.cells.set.Empty;
import dungeon.engine.cells.set.Wall;
import dungeon.engine.cells.interactable.Entry;
import dungeon.engine.cells.interactable.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;

/**
 * Main class for level functionality
 * Handles:
 * - Map creation and management
 * - Cell placement
 * - Level properties (difficulty, entry/ladder positions...)
 * - Ranged attack handling
 * - Player/cell interactions
 */
public class Level implements Map, Serializable {
    private final int size;
    private final int currentLevel;
    private final int difficulty;
    private final Cell[][] map;
    private Position entryPos;
    private Position ladderPos;
    private final Random random;

    // serial version UID
    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Creates a new level
     *
     * @param currentLevel current level number
     * @param difficulty diffculty between 0-10
     * @param size map size (width/height)
     */
    public Level(int currentLevel, int difficulty, int size) {
        this.currentLevel = currentLevel;
        this.difficulty = difficulty;
        this.size = size;
        this.map = new Cell[size][size];
        this.random = new Random();

        createMap();
    }

    /** Creates a new level with an injected random generator (used for testing)
     *
     * @param currentLevel current level number
     * @param difficulty diffculty between 0-10
     * @param size map size (width/height)
     * @param random random number generator
     */
    public Level(int currentLevel, int difficulty, int size, Random random) {
        this.currentLevel = currentLevel;
        this.difficulty = difficulty;
        this.size = size;
        this.map = new Cell[size][size];
        this.random = random;

        createMap();
    }

    //------------------------------------------------------------------------------------- MAP GEN

    /**
     * Map creation and cell placements
     * Fills map with empty cells, then adds walls, entry, ladder and item (interactable) cells
     */
    private void createMap() {
        // making all cells empty
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                // using [y][x] due to array indexing, translates to (x, y)
                // first index is rows (up/down) and second index is columns (left/right)
                map[y][x] = new Empty();
                map[y][x].cellSetPos();
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

    /**
     * Creates a perimeter of wall cells around the map
     */
    private void createWalls() {
        for (int i = 0; i < size; i++) {
            // top + bottom
            map[0][i] = new Wall();
            map[0][i].cellSetPos();
            map[size - 1][i] = new Wall();
            map[size - 1][i].cellSetPos();

            // left + right
            map[i][0] = new Wall();
            map[i][0].cellSetPos();
            map[i][size - 1] = new Wall();
            map[i][size - 1].cellSetPos();
        }
    }

    /**
     * Entry cell placement
     * Level 1: bottom left corner
     * Level 2+: set through setEntryPos method
     */
    private void placeEntry() {
        if (currentLevel == 1) {
            // placing entry at bottom left
            int x = 1;
            int y = size - 2;
            entryPos = new Position(x, y);
            map[y][x] = new Entry();
            map[y][x].cellSetPos();
        }
            // levels > 1 handled through game engine
    }

    /**
     * Ladder cell placement
     */
    private void placeLadder() {
        int x, y;
        do {
            x = random.nextInt(size - 2) + 1;
            y = random.nextInt(size - 2) + 1;
        } while (!(map[y][x] instanceof Empty));

        ladderPos = new Position(x, y);
        map[y][x] = new Ladder();
        map[y][x].cellSetPos();
    }

    /**
     * Interactable item placements
     */
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

    /**
     * Item placement helper method
     * Handles placement of a specific number of cells depending on type
     *
     * @param total number of cells to place
     * @param cellType cell class
     */
    private void placeItems(int total, Class<? extends Cell> cellType) {
        for (int i = 0; i < total; i++) {
            int x, y;
            do {
                // ensuring placement is inside wall perimeter
                x = random.nextInt(size - 2) + 1;
                y = random.nextInt(size - 2) + 1;
            } while (!(map[y][x] instanceof Empty));

            try {
                // grabbing class constructors and creating a new instance
                Cell cell = cellType.getDeclaredConstructor().newInstance();
                map[y][x] = cell;
                map[y][x].cellSetPos();

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

    /**
     * Sets entry positon for levels 2+
     * @param position entry placement positon
     */
    public void setEntryPos(Position position) {
        // ensuring placement is inside wall perimeter
        this.entryPos = new Position(position);
        int x = position.getX();
        int y = position.getY();

        map[y][x] = new Entry();
        map[y][x].cellSetPos();
    }

    /**
     * Checks for possible ranged mutant attacks
     * Calculates total damage done by all instances of attacks
     *
     * @param player player to check attacks against
     * @return total damage from attacks
     */
    public int checkRange(Player player) {
        int rangedDamage = 0;

        // checking cells for ranged mutants
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Cell cell = map[y][x];
                if (cell instanceof RangedMutant mutant) {
                    rangedDamage += mutant.tryRangedAttack(player);
                }
            }
        }

        return rangedDamage;
    }

    //-------------------------------------------------------------------------- UTIL METHODS

    /**
     * Gets cell at positon
     *
     * @param position positon to check
     * @return cell at positon
     */
    public Cell getCell(Position position) {
        int x = position.getX();
        int y = position.getY();

        return map[y][x];
    }

    /**
     * Sets cell at positon
     *
     * @param position positon to place cell
     */
    public void setCell(Position position, Cell cell) {
        int x = position.getX();
        int y = position.getY();

        cell.cellSetPos();
        map[y][x] = cell;
    }

    /**
     * Checks if position contains a ladder cell
     *
     * @param position position to check
     * @return true if position contains a ladder cell
     */
    public boolean isLadder(Position position) {
        return ladderPos != null
                && position.getX() == ladderPos.getX()
                && position.getY() == ladderPos.getY();

    }

    //-------------------------------------------------------------------------- GETTERS AND SETTERS

    /**
     * Gets the 2d map cell array
     *
     * @return 2d array of cells
     */
    public Cell[][] getMap() {
        return map;
    }

    /**
     * Gets the entry positon
     *
     * @return entry positon
     */
    public Position getEntryPos() {
        return new Position(entryPos);
    }

    /**
     * Gets the ladder positon
     *
     * @return ladder position
     */
    public Position getLadderPos() {
        return new Position(ladderPos);
    }

    /**
     * Gets the current difficulty
     *
     * @return difficulty (0-10)
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Gets the map size
     * @return the size of the map
     */
    public int getSize() {
        return size;
    }

    //-------------------------------------------------------------------------- TESTING UTILS

    /**
     * Sets the ladder position
     *
     * @param position position to place ladder
     */
    public void setLadderPos(Position position) {
        this.ladderPos = new Position(position);
        int x = position.getX();
        int y = position.getY();

        map[y][x] = new Ladder();
        map[y][x].cellSetPos();
    }
}
