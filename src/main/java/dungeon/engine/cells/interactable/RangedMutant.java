package dungeon.engine.cells.interactable;

import dungeon.engine.Player;
import dungeon.engine.Position;

import java.util.Random;

// Ranged mutant cell (mutant)
public class RangedMutant extends Mutant {
    private final Random random = new Random();
    private final Position position;

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

    public int tryRangedAttack(Player player) {
        // checking if player is within range (2)
        if (position.isInRange(player.getPosition(), 2)) {
            // 50% chance to deal 2 damage with random boolean
            if (random.nextBoolean()) {
                return 2;
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
}
