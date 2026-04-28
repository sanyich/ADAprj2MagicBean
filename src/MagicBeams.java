public class MagicBeams {
    private final int id;
    private final int r;
    private final int c;
    private final int length;
    private final char dir;
    public MagicBeams(int id, int r, int c, int l, char dir) {
        this.id = id;
        this.r = r;
        this.c = c;
        this.length = l;
        this.dir = dir;
    }
    public int getId() {
        return id;
    }

    public int getR() {
        return r;
    }
    public int getC() {
        return c;
    }

    public char getDir() {
        return dir;
    }

    public int getL() {
        return length;
    }
}
