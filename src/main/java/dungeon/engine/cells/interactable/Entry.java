package dungeon.engine.cells.interactable;

import dungeon.engine.Cell;
import dungeon.engine.cells.Interaction;
import dungeon.engine.Player;

// entry cell (interactable)
public class Entry extends Cell implements Interaction {
    public Entry() {
        super('E', true);
    }

    @Override
    public String interact(Player player) {
        return "You peer into the level above, didn't you just come down?";
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
