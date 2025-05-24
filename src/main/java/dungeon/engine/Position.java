package dungeon.engine;

import java.io.Serial;
import java.io.Serializable;

// handles player positioning for ranged mutant attack and movement calculations
public class Position implements Serializable {
    private int x;
    private int y;

    // serial version UID
    @Serial
    private static final long serialVersionUID = 0L;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // duplicate position for calculations
    public Position(Position position) {
        this.x = position.x;
        this.y = position.y;
    }

    public int distance(Position target) {
        return Math.abs(x - target.x) + Math.abs(y - target.y); // distance between two points
    }

    public boolean isInRange(Position target, int range) {
        return  distance(target) <= range;
    }

    //----------------------------------------- Getters and Setters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

}
