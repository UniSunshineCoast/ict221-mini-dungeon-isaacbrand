package dungeon.engine.cells.interactable;

import dungeon.engine.Cell;
import dungeon.engine.cells.Interaction;
import dungeon.engine.Player;

// abstract class for gold + health potion cells (interactable)
public abstract class Collectable extends Cell implements Interaction {

    /**
     * Creates a new collectable cell
     *
     * @param cellSymbol cell character symbol
     */
    public Collectable(char cellSymbol) {
        super(cellSymbol, true);
    }

    //-----------------------------------------------------------------INTERFACE OVERRIDES
    @Override
    public boolean cellRemoveOnUse() {
        return true;
    }

    @Override
    public boolean canDamage() {
        return false;
    }

    @Override
    public int getDamage() {
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

    @Override
    public abstract String interact(Player player);
}
