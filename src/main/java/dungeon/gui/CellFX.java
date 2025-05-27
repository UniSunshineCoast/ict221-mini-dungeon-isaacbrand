package dungeon.gui;

import dungeon.engine.*;
import dungeon.engine.cells.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

public class CellFX extends StackPane {
    private Cell cell;
    private final Map<String, Image> imageCache;
    private final double cellSize;
    private final String imagePath;
    private final Map<Class<?>, Color> colourMap;
    private final Map<Class<?>, String> imageMap;

    public CellFX(Cell cell) {
        this(cell, 30.0);
    }

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

    public void setCell(Cell cell) {
        this.cell = cell;
        updateCFX();
    }

    private void updateCFX() {
        this.getChildren().clear();

        Rectangle background = createCellBackground();
        this.getChildren().add(background);

        ImageView imageView = createCellImage();
        if(imageView != null) {
            this.getChildren().add(imageView);
        }
    }

    private Rectangle createCellBackground() {
        Rectangle background = new Rectangle(cellSize, cellSize);

        // background based on cell typer
        Color backgroundColour = getCellColour();
        background.setFill(backgroundColour);

        return background;
    }

    private Color getCellColour() {
        if (cell == null) {
            return Color.WHITE;
        }

        Color colour = colourMap.get(cell.getClass());
        return colour != null ? colour : Color.WHITE;
    }

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


    public Cell getCell() {
        return cell;
    }

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
