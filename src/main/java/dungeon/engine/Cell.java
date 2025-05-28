package dungeon.engine;

import java.io.Serial;
import java.io.Serializable;

/**
 * Abstract class for cells in the game with core functionality
 * Handles:
 * - If player can move onto cell
 * - Cell symbol for display
 * - Cell positioning
 * Unique cell behaviours are handled through subclasses
 */
public abstract class Cell implements Serializable {
    // Cell text symbol
    private final char cellSymbol;

    // serial version UID
    @Serial
    private static final long serialVersionUID = 0L;

    // If player can walk onto the cell
    private final boolean canWalk;

    // Creates a new cell instance
    public Cell(char cellSymbol, boolean canWalk) {
        this.cellSymbol = cellSymbol;
        this.canWalk = canWalk;
    }

    // Sets cell position (x, y), values handled in Level class
    public void cellSetPos() {}

    // Grabs associated cell symbol
    public char cellGetSymbol() {
        return cellSymbol;
    }

    // Checks cell player movement conditions
    public boolean cellCanWalk() {
        return canWalk;
    }
}
