package dungeon.engine.cells;

import dungeon.engine.Cell;
import dungeon.engine.Interaction;
import dungeon.engine.Player;

// abstract for gold + health potion collectable items
public abstract class Collectable extends Cell implements Interaction {
    // if item has been collected
    private boolean isCollected = false;

    public Collectable(char cellSymbol) {
        super(cellSymbol, true);
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        this.isCollected = collected;
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

    // subclasses will override both getHeal and getScore methods depending on needs, currently just placeholders for base implementation
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
