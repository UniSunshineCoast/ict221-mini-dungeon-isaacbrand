package dungeon.engine;

import javafx.scene.layout.StackPane;

public class CellFX extends StackPane {
    private Cell cell;

    public CellFX(Cell cell) {
        this.cell = cell;
        // cell styling
        updateCFX();
    }

    public void setCell(Cell cell) {
        this.cell = cell;
        updateCFX();
    }

    private void updateCFX() {
        // handles cell styling through JavaFX

        // placeholder, using cell symbols as text
        this.getChildren().clear();
        this.getChildren().add(new javafx.scene.text.Text(String.valueOf(cell.cellGetSymbol())));
    }

    public Cell getCell() {
        return cell;
    }
}
