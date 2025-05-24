package dungeon.engine;

import javafx.scene.layout.StackPane;

import java.io.Serial;
import java.io.Serializable;

public abstract class Cell implements Serializable {
    // grid position (x, y)
    private int x;
    private int y;

    // Cell text symbol
    private final char cellSymbol;

    // serial version UID
    @Serial
    private static final long serialVersionUID = 0L;

    // Need to add image view

    // If player can walk onto the cell
    private final boolean canWalk;

    public Cell(char cellSymbol, boolean canWalk) {
        this.cellSymbol = cellSymbol;
        this.canWalk = canWalk;

        // could include basic GUI styling
    }

    // sets cell position (x, y)
    public void cellSetPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // grabs associated cell symbol
    public char cellGetSymbol() {
        return cellSymbol;
    }

    // checks cell player movement conditions
    public boolean cellCanWalk() {
        return canWalk;
    }
}
