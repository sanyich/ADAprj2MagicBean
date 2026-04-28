public class MagicBeams {
    private int id;
    private int r, c;
    private int l;
    private char dir;
    public MagicBeams(int id, int r, int c, int l, char dir) {
        this.id = id;
        this.r = r;
        this.c = c;
        this.l = l;
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
        return l;
    }
}
