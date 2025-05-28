package dungeon.engine.cells.interactable;

import dungeon.engine.Cell;
import dungeon.engine.cells.Interaction;
import dungeon.engine.Player;

// trap cell (interactable)
public class Trap extends Cell implements Interaction {
    public Trap() {
        super('T', true);
    }

    @Override
    public String interact(Player player) {
        return "You fell for a trap and lost 2 HP.";
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
        return 2;
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
