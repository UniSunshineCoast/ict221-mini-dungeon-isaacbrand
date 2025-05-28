package dungeon.engine;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Manages score and highscore functionality
 * Handles:
 * - Score storage
 * - Score loading
 * - Score format
 * - Serialization for persistent storage between sessions
 * - Implements ScoreHandler interface for standardised score management
 */
public class Score implements ScoreHandler, Serializable {
    /**
     * Inner class representing a single highscore entry
     * Stores score value and date
     */
    public static class ScoreEntry implements Serializable, Comparable<ScoreEntry> {
        private final int score;
        private final String date;

        /**
         * Creates a score entry with the current date
         *
         * @param score final score value
         */
        public ScoreEntry(int score) {
            this.score = score;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            this.date = dateFormat.format(new Date());
        }

        /**
         * Gets the score value
         *
         * @return score value
         */
        public int getScore() {
            return score;
        }

        /**
         * Gets the date of when score was achieved
         *
         * @return formatted date string
         */
        public String getDate() {
            return date;
        }

        /**
         * Compares score entries and sorts in descending order)
         * @param other score entry to compare with
         * @return comparison result
         */
        @Override
        public int compareTo(ScoreEntry other) {
            // sorting by desc order
            return Integer.compare(other.score, this.score);
        }

        /**
         * Converts core entry to a string
         *
         * @return score and date as a string
         */
        @Override
        public String toString() {
            return score + " " + date;
        }
    }

    //------------------------------------------------------------------------------ MAIN CLASS
    private final int maxStore; // max amount of scores to track
    private final String scoreFile; // filename
    private final List<ScoreEntry> highScores;

    /**
     * Creates a Score instance with set file path and max scores to be saved
     *
     * @param scoreFile save file path
     * @param maxStore max amount of saved scores
     */
    public Score(String scoreFile, int maxStore) {
        this.scoreFile = scoreFile;
        this.maxStore = maxStore;
        this.highScores = new ArrayList<>();
        loadScores();
    }

    /**
     * Score instance with a max storage value of 5
     *
     * @param scoreFile save file path
     */
    public Score(String scoreFile) {
        this(scoreFile, 5);
    }

    /**
     * Adds a new high score to the persisted list if qualifiable
     * Sorted in descending order and trimmed to maxStore entries, negative/0 scores not entered
     *
     * @param score value to add
     * @return true if score was added
     */
    public boolean addScore(int score) {
        if (score <= 0) {
            return false; // prevents losing scores from saving
        }

        ScoreEntry newEntry = new ScoreEntry(score);
        highScores.add(newEntry);
        Collections.sort(highScores);

        // checking if new score is in the highscores
        boolean isHighScore = checkHighScore(newEntry);

        // trimming list using max score limit if required
        if (highScores.size() > maxStore) {
            highScores.subList(maxStore, highScores.size()).clear();
        }

        // saving changes
        saveScores();

        return isHighScore;
    }

    /**
     * Checks if new score entry is a high score
     *
     * @param entry score to check
     * @return true if score is within high score range
     */
    public boolean checkHighScore(ScoreEntry entry) {
        return highScores.indexOf(entry) < maxStore;
    }

    /**
     * Saves the current highscore list to file with serialization
     */
    public void saveScores() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(scoreFile)))) {
            out.writeObject(highScores);
        } catch (IOException e) {
            System.err.println("Error saving scores: " + e.getMessage());
        }
    }

    /**
     * Loads the current highscore list with graceful error handling
     */
    @SuppressWarnings("unchecked")
    private void loadScores() {
        File file = new File(scoreFile);
        if (!file.exists()) {
            return; // no score file
        }

        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(scoreFile)))) {
            Object obj = in.readObject();
            if (obj instanceof List) {
                List<ScoreEntry> loadedScores = (List<ScoreEntry>) obj;
                highScores.addAll(loadedScores);

                // sorting
                Collections.sort(highScores);

                // trimming
                if (highScores.size() > maxStore) {
                    highScores.subList(maxStore, highScores.size()).clear();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading score: " + e.getMessage());
        }
    }

    /**
     * Formats highscore list to a string with rank, value and date
     *
     * @return formatted string of highscores
     */
    public String formatScores() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < highScores.size(); i++) {
            ScoreEntry entry = highScores.get(i);
            sb.append("#").append(i + 1).append(" Score: ")
                    .append(entry.getScore()).append(" Date: ")
                    .append(entry.getDate());

            if (i < highScores.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
