package dungeon.engine;

public class Player {
    private int hp;
    private int score;
    private Position position; // not yet implemented
    private int steps;
    private final int maxSteps;
    private int level;

    private static final int MAX_HP = 10;
    private static final int MAX_STEPS = 100;

    public Player() {
        this.hp = MAX_HP;
        this.score = 0;
        this.position = null; // temp for position implementation
        this.steps = 0;
        this.maxSteps = MAX_STEPS;
        this.level = 1;
    }

    // require position data for player start x, y
    // might include max steps info in this method or create two methods for handling

    // need to implement player move handling

    // will handle player interactions with cells through the Interaction interface
    public String interact(Interaction interaction) {
        String result = interaction.interact(this);

        if (interaction.canDamage()) {
            hurt(interaction.getDamage());
        }

        int healValue = interaction.getHeal();
        if (healValue > 0) {
            heal(healValue);
        }

        int scoreValue = interaction.getScore();
        if (scoreValue > 0) {
            addScore(scoreValue);
        }

        return result;
    }

    public void hurt(int amount) {
        setHp(hp - amount);
    }

    public void heal(int amount) {
        setHp(hp + amount);
    }

    public void addScore(int amount) {
        score += amount;
    }

    public void nextLevel() {
        level++;
    }

    public boolean checkSteps() {
        return steps >= maxSteps;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    //----------------------------------------------------------------------- Getters + Setters
    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = Math.min(MAX_HP, Math.max(0, hp)); // hp bound check
    }

    // score not yet implemented, placeholder

    // position not yet implemented, placeholder

    // ---------------------------
    public int getSteps() {
        return steps;
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    // level not implemented yet, placeholder


    // could potentially add debugging for checks
}
