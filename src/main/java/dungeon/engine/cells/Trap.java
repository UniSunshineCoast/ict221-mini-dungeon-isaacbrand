package dungeon.engine.cells;

import dungeon.engine.Cell;
import dungeon.engine.Interaction;
import dungeon.engine.Player;

// trap cell with player interaction
public class Trap extends Cell implements Interaction {
    private static final int DAMAGE = 2;

    public Trap() {
        super('T', true);
    }

    @Override
    public String interact(Player player) {
        return "You fell for a trap and lost " + DAMAGE + " HP.";
    }

    @Override
    public boolean cellRemoveOnUse() {
        return false;
    }

    @Override
    public boolean canDamage() {
        return true;
    }

    @Override public int getDamage() {
        return DAMAGE;
    }

    @Override
    public int getHeal() {
        return 0;
    }

    @Override
    public int getScore() {
        return 0;
    }
}
