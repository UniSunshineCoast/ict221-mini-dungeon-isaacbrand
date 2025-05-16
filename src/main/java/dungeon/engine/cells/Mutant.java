package dungeon.engine.cells;

import dungeon.engine.Cell;
import dungeon.engine.Interaction;
import dungeon.engine.Player;

// abstract for melee and ranged mutant enemies
public abstract class Mutant extends Cell implements Interaction {
    private int score = 2;

    private boolean mutantDefeated = false;

    private int damage;

    public Mutant(char cellSymbol, int damage) {
        super(cellSymbol, true);
        this.damage = damage;
    }

    public boolean mutantIsDefeated() {
        return mutantDefeated;
    }

    public void mutantSetDefeated(boolean defeated) {
        this.mutantDefeated = defeated;
    }

    //-----------------------------------------------------------------INTERFACE OVERRIDES
    @Override
    public boolean cellRemoveOnUse() {
        return mutantDefeated;
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
        return score;
    }

    @Override
    public abstract String interact(Player player);


}
