package dungeon.engine.cells;

import dungeon.engine.Player;

// Melee mutant cell (mutant)
public class MeleeMutant extends Mutant {
    private static final int DAMAGE = 2;

    public MeleeMutant() {
        super('M', DAMAGE);
    }

    @Override
    public String interact(Player player) {
        setDefeated(true);
        return "You attacked a melee mutant! You lost " + DAMAGE + " HP in your battle but you emerged victorious.";
    }
}
