public class Beam {
    private final int id;
    private final int row;
    private final int column;
    private final int length;
    private final char dir;

    public Beam(int id, int row, int column, int length, char dir) {
        this.id = id;
        this.row = row;
        this.column = column;
        this.length = length;
        this.dir = dir;
    }

    public int getId() {
        return id;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getLength() {
        return length;
    }

    public char getDir() {
        return dir;
    }
}
