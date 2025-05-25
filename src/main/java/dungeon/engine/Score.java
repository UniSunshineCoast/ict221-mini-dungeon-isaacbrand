package dungeon.engine;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Score implements ScoreHandler, Serializable {
    // inner class for representing a single high score entry
    public static class ScoreEntry implements Serializable, Comparable<ScoreEntry> {
        private final int score;
        private final String date;

        // new score entry with date
        public ScoreEntry(int score) {
            this.score = score;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            this.date = dateFormat.format(new Date());
        }

        public int getScore() {
            return score;
        }

        public String getDate() {
            return date;
        }

        // compares score entries (used for sorting)
        @Override
        public int compareTo(ScoreEntry other) {
            // sorting by desc order
            return Integer.compare(other.score, this.score);
        }

        @Override
        public String toString() {
            return score + " " + date;
        }
    }

    //------------------------------------------------------------------------------ MAIN CLASS
    private final int maxStore; // max amount of scores to track
    private final String scoreFile; // filename
    private final List<ScoreEntry> highScores;

    public Score(String scoreFile, int maxStore) {
        this.scoreFile = scoreFile;
        this.maxStore = maxStore;
        this.highScores = new ArrayList<>();
        loadScores();
    }

    // default constructor
    public Score(String scoreFile) {
        this(scoreFile, 5);
    }

    // adds a new score to the list if conditions are met
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

    // check if new entry is in the high score list
    public boolean checkHighScore(ScoreEntry entry) {
        return highScores.indexOf(entry) < maxStore;
    }

    // stores current high scores to disk
    public void saveScores() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(scoreFile)))) {
            out.writeObject(highScores);
        } catch (IOException e) {
            System.err.println("Error saving scores: " + e.getMessage());
        }
    }

    // loading scores from disk
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

    // formatting scores to string
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
