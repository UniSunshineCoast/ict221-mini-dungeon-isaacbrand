package dungeon.engine;

public interface Map {
    Cell getCell(Position position);
    void setCell(Position position, Cell cell);
    Position getEntryPos();
    Position getLadderPos();
    boolean isLadder(Position position);
    Cell[][] getMap();
    int getSize();
}
