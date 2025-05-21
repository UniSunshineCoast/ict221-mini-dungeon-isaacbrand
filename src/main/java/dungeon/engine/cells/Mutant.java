package dungeon.engine.cells;

import dungeon.engine.Cell;
import dungeon.engine.Interaction;
import dungeon.engine.Player;

// abstract for melee and ranged mutant enemies
public abstract class Mutant extends Cell implements Interaction {
    private static final int SCORE_VALUE = 2;

    protected boolean defeated = false;
    protected final int damage;

    public Mutant(char cellSymbol, int damage) {
        super(cellSymbol, true);
        this.damage = damage;
    }

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
        return SCORE_VALUE;
    }

    @Override
    public abstract String interact(Player player);
}
