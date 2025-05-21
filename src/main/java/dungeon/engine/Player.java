package dungeon.engine;

public class Player {
    private int hp;
    private int score;
    private Position position;
    private int steps;
    private final int maxSteps;
    private int level;

    private static final int MAX_HP = 10;
    private static final int MAX_STEPS = 100;

    public Player() {
        this.hp = MAX_HP;
        this.score = 0;
        this.position = new Position(0, 0); // placeholder position, will change with wall implementation
        this.steps = 0;
        this.maxSteps = MAX_STEPS;
        this.level = 1;
    }

    public void startPos(int x, int y) {
        this.position = new Position(x, y);
    }

    //-------------------------------------------------------------------------------------- MOVEMENT
    public boolean moveUp() {
        position.setY(position.getY()-1);
        steps++;
        return true;
    }

    public boolean moveDown() {
        position.setY(position.getY()+1);
        steps++;
        return true;
    }

    public boolean moveLeft() {
        position.setX(position.getX()-1);
        steps++;
        return true;
    }

    public boolean moveRight() {
        position.setX(position.getX()+1);
        steps++;
        return true;
    }

    //-------------------------------------------------------------------------------------- INTERACTION
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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

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
