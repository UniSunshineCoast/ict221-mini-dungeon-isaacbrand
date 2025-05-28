package dungeon.engine.cells.interactable;

import dungeon.engine.Player;

// gold cell (collectable)
public class Gold extends Collectable {
    public Gold() {
        super('G');
    }

    @Override
    public String interact(Player player) {
        return "You found some gold! Score increased by 2.";
    }

    @Override
    public int getScore() {
        return 2;
    }
}
