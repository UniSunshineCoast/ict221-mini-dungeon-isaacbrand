package dungeon.engine.cells.interactable;

import dungeon.engine.Cell;
import dungeon.engine.cells.Interaction;
import dungeon.engine.Player;

// abstract class for melee and ranged mutant cells (interactable)
public abstract class Mutant extends Cell implements Interaction {
    protected boolean defeated = false;
    protected final int damage;

    /**
     * Creates a new mutant cell
     *
     * @param cellSymbol cell character symbol
     * @param damage amount of player / cell contact damage
     */
    public Mutant(char cellSymbol, int damage) {
        super(cellSymbol, true);
        this.damage = damage;
    }

    /**
     * Sets mutant status to defeated
     * @param defeated true if mutant has been defeated (player interacted with cell)
     */
    public void setDefeated(boolean defeated) {
        this.defeated = defeated;
    }

    //-----------------------------------------------------------------INTERFACE OVERRIDES
    @Override
    public boolean cellRemoveOnUse() {
        return defeated;
    }

    @Override
    public boolean canDamage() {
        return true;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public int getHeal() {
        return 0;
    }

    @Override
    public int getScore() {
        return 2;
    }

    @Override
    public abstract String interact(Player player);
}
