package dungeon.engine;

// Interface for player interaction with cells
public interface Interaction {
    // handles player interaction
    String interact(Player player);

    // if cell should be removed on use
    boolean cellRemoveOnUse();

    // if cell can damage the player
    boolean canDamage();

    // damage total
    int getDamage();

    // value total (used for health potion)
    int getHeal();

    // score total
    int getScore();
}
