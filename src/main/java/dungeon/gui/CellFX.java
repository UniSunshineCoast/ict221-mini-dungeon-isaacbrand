package dungeon.gui;

import dungeon.engine.*;
import dungeon.engine.cells.set.Empty;
import dungeon.engine.cells.set.Wall;
import dungeon.engine.cells.interactable.Entry;
import dungeon.engine.cells.interactable.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

/**
 * Visual cell representation for the JavaFX GUI
 * Handles:
 * - Cell rendering (with image and background colour)
 */
public class CellFX extends StackPane {
    private Cell cell;
    private final Map<String, Image> imageCache;
    private final double cellSize;
    private final String imagePath;
    private final Map<Class<?>, Color> colourMap;
    private final Map<Class<?>, String> imageMap;

    /**
     * Creates a rendered cell with default sizing
     *
     * @param cell to render
     */
    public CellFX(Cell cell) {
        this(cell, 30.0);
    }

    /**
     * Creates a rendered cell with a set size
     *
     * @param cell to render
     * @param cellSize size in pixels
     */
    public CellFX(Cell cell, double cellSize) {
        this.cell = cell;
        this.cellSize = cellSize;
        this.imageCache = new HashMap<>();
        this.imagePath = "/";
        this.colourMap = initColourMap();
        this.imageMap = initImageMap();

        setPrefSize(cellSize, cellSize);
        setMinSize(cellSize, cellSize);

        // border styling
        setStyle("-fx-border-color: #333333; -fx-border-width: 1px");

        updateCFX();
    }

    /**
     * Updates cell reference and refreshes
     *
     * @param cell new cell to render
     */
    public void setCell(Cell cell) {
        this.cell = cell;
        updateCFX();
    }

    /**
     * Refreshes visuals
     * Clears existing components and adds background colour / image if valid
     */
    private void updateCFX() {
        this.getChildren().clear();

        Rectangle background = createCellBackground();
        this.getChildren().add(background);

        ImageView imageView = createCellImage();
        if(imageView != null) {
            this.getChildren().add(imageView);
        }
    }

    /**
     * Renders a coloured background depending on cell type
     *
     * @return background colour of cell
     */
    private Rectangle createCellBackground() {
        Rectangle background = new Rectangle(cellSize, cellSize);

        // background based on cell typer
        Color backgroundColour = getCellColour();
        background.setFill(backgroundColour);

        return background;
    }

    /**
     * Determines cell background colour
     *
     * @return colour of cell type
     */
    private Color getCellColour() {
        if (cell == null) {
            return Color.WHITE;
        }

        Color colour = colourMap.get(cell.getClass());
        return colour != null ? colour : Color.WHITE;
    }

    /**
     * Assigns and image to a cell type and renders
     *
     * @return rendered image of cell type or null
     */
    private ImageView createCellImage() {
        if (cell == null || cell instanceof Empty) {
            return null;
        }

        String imageName = imageMap.get(cell.getClass());
        if (imageName == null) {
            return null;
        }

        Image image = getImage(imageName);
        if (image == null) {
            return null;
        }

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(cellSize * 0.9);
        imageView.setFitHeight(cellSize * 0.9);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        return imageView;
    }

    /**
     * Gets image from cache or loads image from resources
     *
     * @param name image file name (before .png)
     * @return the image, null if failed
     */
    private Image getImage(String name) {
        // check if image is cached
        if (imageCache.containsKey(name)) {
            return imageCache.get(name);
        }

        // load attempt
        try {
            String path = imagePath + name + ".png";
            var inputStream = getClass().getResourceAsStream(path);

            if (inputStream == null) {
                System.err.println("Resource not found: " + path);
                return null;
            }

            Image image = new Image(inputStream);
            imageCache.put(name, image);
            return image;
        } catch (Exception e) {
            System.err.println("Error loading image : " + name + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Adds player image overlay to cell (for player movmement overlapping)
     */
    public void playerOverlay() {
        Image playerImage = getImage("player");
        if (playerImage == null) {
            return; // image could not be loaded
        }
        ImageView playerView = new ImageView(playerImage);
        playerView.setFitWidth(cellSize * 0.9);
        playerView.setFitHeight(cellSize * 0.9);
        playerView.setPreserveRatio(true);
        this.getChildren().add(playerView);
    }

    /**
     * Returns the current rendered cell
     *
     * @return current cell
     */
    public Cell getCell() {
        return cell;
    }

    /**
     * Preloads game images into cache
     */
    public void preload() {
        String[] imageNames = {
                "wall"
                ,"entry"
                ,"ladder"
                ,"gold"
                ,"healthPotion"
                ,"trap"
                ,"meleeMutant"
                ,"rangedMutant"
                ,"player"
        };

        for (String name : imageNames) {
            getImage(name);
        }
    }

    /**
     * Colour mapping initilisation
     *
     * @return map associating cell class background colours
     */
    private Map<Class<?>, Color> initColourMap() {
        Map<Class<?>, Color> map = new HashMap<>();
        map.put(Wall.class, Color.BLACK);
        map.put(Entry.class, Color.LIGHTGREY);
        map.put(Ladder.class, Color.LIGHTGREEN);
        map.put(Gold.class, Color.GOLD);
        map.put(HealthPotion.class, Color.LIGHTPINK);
        map.put(Trap.class, Color.CORAL);
        map.put(MeleeMutant.class, Color.GREENYELLOW);
        map.put(RangedMutant.class, Color.PALEVIOLETRED);
        map.put(Empty.class, Color.WHITE);
        return map;
    }

    /**
     * Image mapping initilisation
     *
     * @return map associating cell class images
     */
    private Map<Class<?>, String> initImageMap() {
        Map<Class<?>, String> map = new HashMap<>();
        map.put(Wall.class, "wall");
        map.put(Entry.class, "entry");
        map.put(Ladder.class, "ladder");
        map.put(Gold.class, "gold");
        map.put(HealthPotion.class, "healthPotion");
        map.put(Trap.class, "trap");
        map.put(MeleeMutant.class, "meleeMutant");
        map.put(RangedMutant.class, "rangedMutant");
        return map;
    }
}
