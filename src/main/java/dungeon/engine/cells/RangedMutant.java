package dungeon.engine.cells;

import dungeon.engine.Player;
import dungeon.engine.Position;

import java.util.Random;

// Ranged mutant cell (mutant)
public class RangedMutant extends Mutant {
    private static final int DAMAGE = 2;
    private static final int RANGE = 2;
    private final Random random = new Random();
    private Position position;

    // defaults
    public RangedMutant() {
        super('R', 0); // 0 contact damage, damage occurs from ranged attacks
        this.position = new Position(0, 0);
    }

    // constructor with positioning
    public RangedMutant(int x, int y) {
        super('R', 0);
        this.position = new Position(x, y);
    }

    // updates mutant x and y coords
    public void setPosition(int x, int y) {
        this.position.setX(x);
        this.position.setY(y);

    }

    // sets position via position object for calculations
    public void setPosition(Position position) {
        this.position = new Position(position);
    }

    public int tryRangedAttack(Player player) {
        // checking if player is within range
        if (position.isInRange(player.getPosition(), RANGE)) {
            // 50% chance to damage with random boolean
            if (random.nextBoolean()) {
                System.out.println("You were hit by a ranged attack and lost " + DAMAGE + " HP, where did that come from?");
                return DAMAGE;
            }
        }
        return 0;
    }

    @Override
    public String interact(Player player) {
        setDefeated(true);
        return "Ranged mutant down!";
    }

    @Override
    public int getDamage() {
        return 0;
    }

    public Position getPosition() {
        return new Position(position);
    }
}
