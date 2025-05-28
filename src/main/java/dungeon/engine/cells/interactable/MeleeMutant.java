package dungeon.engine.cells.interactable;

import dungeon.engine.Player;

// Melee mutant cell (mutant)
public class MeleeMutant extends Mutant {
    public MeleeMutant() {
        super('M', 2);
    }

    @Override
    public String interact(Player player) {
        setDefeated(true);
        return "You attacked a melee mutant! You lost 2 HP in your battle but you emerged victorious.";
    }
}
