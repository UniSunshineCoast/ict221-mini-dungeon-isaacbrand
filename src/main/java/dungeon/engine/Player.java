package dungeon.engine;

import dungeon.engine.cells.Interaction;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents the player
 * Handles player attributes, status, movement and player/cell interactions
 */
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

    /**
     * Creates a new player with health and step limits
     *
     * @param maxHp maximum health points
     * @param maxSteps maximum steps
     */
    public Player(int maxHp, int maxSteps) {
        this.hp = maxHp;
        this.maxHp = maxHp;
        this.score = 0;
        this.position = new Position(0, 0); // position init
        this.steps = 0;
        this.maxSteps = maxSteps;
    }

    /**
     * Copies values from a seperate player object for save loading
     *
     * @param other player to copy from
     */
    public void copyFrom(Player other) {
        this.hp = other.hp;
        this.score = other.score;
        this.position = new Position(other.position);
        this.steps = other.steps;
    }

    /**
     * Sets player start position coordinates
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void startPos(int x, int y) {
        this.position = new Position(x, y);
    }

    //-------------------------------------------------------------------------------------- MOVEMENT
    // Moves player up one cell and increments steps
    public void moveUp() {
        position.setY(position.getY()-1);
        steps++;
    }

    // Moves player down one cell and increments steps
    public void moveDown() {
        position.setY(position.getY()+1);
        steps++;
    }

    // Moves player left one cell and increments steps
    public void moveLeft() {
        position.setX(position.getX()-1);
        steps++;
    }

    // Moves player right one cell and increments steps
    public void moveRight() {
        position.setX(position.getX()+1);
        steps++;
    }

    //-------------------------------------------------------------------------------------- INTERACTION

    /**
     * Handles player/cell interactions
     * Processes damage, healing and score values
     *
     * @param interaction cell being interacted with
     * @return description of the interaction result
     */
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

    /**
     * Reduces player health
     *
     * @param amount of hp to remove
     */
    public void hurt(int amount) {
        setHp(hp - amount);
    }

    /**
     * Increases player health (to a maximum value of 10)
     * @param amount of hp to add
     */
    public void heal(int amount) {
        setHp(hp + amount);
    }

    /**
     * Increases player score
     * @param amount of score to add
     */
    public void addScore(int amount) {
        score += amount;
    }

    /**
     * Checks if the player has reached the step limit
     * @return true if max steps reached
     */
    public boolean checkSteps() {
        return steps >= maxSteps;
    }

    /**
     * Checks if the player is alive
     * @return true if hp > 0
     */
    public boolean isAlive() {
        return hp > 0;
    }

    //------------------------------------------------------------------------------------- GETTERS / SETTERS

    /**
     * Gets current hp
     * @return current hp
     */
    public int getHp() {
        return hp;
    }

    /**
     * Sets hp with bound checking to ensure hp remains in bounds (0-10)
     * @param hp new hp value
     */
    public void setHp(int hp) {
        this.hp = Math.min(maxHp, Math.max(0, hp));
    }

    /**
     * Gets the current score
     * @return current score
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets score to a specified value
     * @param score new score value
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Gets current position of cell
     * @return current cell position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the current step count
     * @return steps taken (total)
     */
    public int getSteps() {
        return steps;
    }
}
