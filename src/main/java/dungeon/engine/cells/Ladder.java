package dungeon.engine.cells;

import dungeon.engine.Cell;
import dungeon.engine.Interaction;
import dungeon.engine.Player;

// ladder cell
public class Ladder extends Cell implements Interaction {
    public Ladder() {
        super('L', true);
    }

    @Override
    public String interact(Player player) {
        return "You found a ladder!";
    }

    @Override
    public boolean cellRemoveOnUse() {
        return false;
    }

    @Override
    public boolean canDamage() {
        return false;
    }

    @Override public int getDamage() {
        return 0;
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
