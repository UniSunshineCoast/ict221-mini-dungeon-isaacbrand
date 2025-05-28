package dungeon.engine;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a position in the level
 * Handles:
 * - Position tracking
 * - Distance calculations
 * - Range checks
 */
public class Position implements Serializable {
    private int x;
    private int y;

    // serial version UID
    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Creates a new position instance at coordinates (x, y)
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a position instance copy for calculations
     *
     * @param position position to copy (x, y)
     */
    public Position(Position position) {
        this.x = position.x;
        this.y = position.y;
    }

    /**
     * Calculates the distance between two points for attack range
     *
     * @param target target position
     * @return distance from current position to target position
     */
    public int distance(Position target) {
        return Math.abs(x - target.x) + Math.abs(y - target.y);
    }

    public boolean isInRange(Position target, int range) {
        return  distance(target) <= range;
    }

    //--------------------------------------------------------------------------- GETTERS AND SETTERS

    /**
     * Gets the x-coordinate
     * @return x-coordinate (column)
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate
     * @return y-coordinate (row)
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the x-coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

}
