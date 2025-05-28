package dungeon.engine.cells.interactable;

import dungeon.engine.Player;

// health potion cell (collectable)
public class HealthPotion extends Collectable {
    public HealthPotion() {
        super('H');
    }

    @Override
    public String interact(Player player) {
        return "You found a health potion! You recovered 4 HP.";
    }

    @Override
    public int getHeal() {
        return 4;
    }
}
