package dungeon.engine.cells;

import dungeon.engine.Player;

// gold cell (collectable)
public class Gold extends Collectable {
    private static final int SCORE_VALUE = 2;

    public Gold() {
        super('G');
    }

    @Override
    public String interact(Player player) {
        return "You found some gold! Score increased by " + SCORE_VALUE;
    }

    @Override
    public int getScore() {
        return SCORE_VALUE;
    }
}
