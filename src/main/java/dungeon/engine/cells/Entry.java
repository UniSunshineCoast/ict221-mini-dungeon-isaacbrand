package dungeon.engine.cells;

import dungeon.engine.Cell;
import dungeon.engine.Interaction;
import dungeon.engine.Player;

// entry cell
public class Entry extends Cell implements Interaction {
    public Entry() {
        super('E', true);
    }

    @Override
    public String interact(Player player) {
        return "You arrive at the entry to a new level";
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
