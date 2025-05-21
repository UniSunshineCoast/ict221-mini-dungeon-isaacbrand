package dungeon.engine.cells;

import dungeon.engine.Player;

// health potion cell (collectable)
public class HealthPotion extends Collectable {
    private static final int HEAL_VALUE = 4;

    public HealthPotion() {
        super('H');
    }

    @Override
    public String interact(Player player) {
        return "You found a health potion! You recovered " + HEAL_VALUE + " HP";
    }

    @Override
    public int getHeal() {
        return HEAL_VALUE;
    }
}
