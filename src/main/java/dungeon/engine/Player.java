package dungeon.engine;

import java.io.Serial;
import java.io.Serializable;

public class Player implements Serializable {
    private int hp;
    private final int maxHp;
    private int score;
    private Position position;
    private int steps;
    private final int maxSteps;

    // serial version UID
    @Serial
    private static final long serialVersionUID = 0L;

    public Player(int maxHp, int maxSteps) {
        this.hp = maxHp;
        this.maxHp = maxHp;
        this.score = 0;
        this.position = new Position(0, 0); // placeholder position, will change with wall implementation
        this.steps = 0;
        this.maxSteps = maxSteps;
    }

    public void copyFrom(Player other) {
        this.hp = other.hp;
        this.score = other.score;
        this.position = new Position(other.position);
        this.steps = other.steps;
    }

    public void startPos(int x, int y) {
        this.position = new Position(x, y);
    }

    //-------------------------------------------------------------------------------------- MOVEMENT
    public void moveUp() {
        position.setY(position.getY()-1);
        steps++;
    }

    public void moveDown() {
        position.setY(position.getY()+1);
        steps++;
    }

    public void moveLeft() {
        position.setX(position.getX()-1);
        steps++;
    }

    public void moveRight() {
        position.setX(position.getX()+1);
        steps++;
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
        this.hp = Math.min(maxHp, Math.max(0, hp)); // hp bound check
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Position getPosition() {
        return position;
    }

    public int getSteps() {
        return steps;
    }
}
