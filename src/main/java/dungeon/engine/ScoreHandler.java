package dungeon.engine;

/**
 * Interface for handling game scoring
 * Provides methods for adding and formatting scores
 */
public interface ScoreHandler {
    /**
     * Adds a new score to the high score list if valid
     *
     * @param score value to add
     * @return true if score was added to list
     */
    boolean addScore(int score);

    /**
     * Formats highscores into a String
     * @return formatted string of highscores
     */
    String formatScores();
}
