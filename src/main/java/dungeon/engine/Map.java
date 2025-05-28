package dungeon.engine;

/**
 * Interface for map functionality
 */
public interface Map {
    /**
     * Gets cell at a specified position
     *
     * @param position position to check
     * @return cell at positon
     */
    Cell getCell(Position position);

    /**
     * Sets cell at a specified position
     *
     * @param position position to set
     * @param cell cell to place at position
     */
    void setCell(Position position, Cell cell);

    /**
     * Gets the entry position
     *
     * @return entry position
     */

    Position getEntryPos();

    /**
     * Gets the ladder position
     *
     * @return ladder positon
     */
    Position getLadderPos();
}
