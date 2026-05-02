import java.util.*;

public class Solver {
    private final int nRows;
    private final int nColumns;
    private final int nCorridorColumns;
    private final int leftmostColumn;
    private final int nBeams;
    private final int[][] grid;
    private final boolean[] neededToFree;
    private int neededCount;
    private final List<Integer>[] graph;
    private final Beam[] beams;
    private final int[] inDegree;
    private final String FA = "False alarm";
    private final String D = "Disaster";

    public Solver(int nRows, int nColumns, int nCorridorColumns, int leftmostColumn, int nBeams) {
        this.nRows = nRows;
        this.nColumns = nColumns;
        this.nCorridorColumns = nCorridorColumns;
        this.leftmostColumn = leftmostColumn;
        this.nBeams = nBeams;
        this.beams = new Beam[nBeams + 1];
        this.neededToFree = new boolean[nBeams + 1];
        neededCount = 0;

        grid = new int[nRows][nColumns];
        graph = new List[nBeams + 1];
        inDegree = new int[nBeams + 1];

        // initializeTheGraph function
        for (int i = 1; i <= nBeams; i++) {
            graph[i] = new LinkedList<>();
        }
    }

    private int getDRow(char dir) {
        return switch (dir) {
            case 'N' -> -1;
            case 'S' -> 1;
            default -> 0;
        };
    }

    private int getDColumn(char dir) {
        return switch (dir) {
            case 'E' -> 1;
            case 'W' -> -1;
            default -> 0;
        };
    }

    public void addBeam(int id, int row, int column, int length, char dir) {
        int dRow = getDRow(dir);
        int dColumn = getDColumn(dir);
        for (int i = 0; i < length; i++) {
            grid[row + dRow * i][column + dColumn * i] = id;
        }
        beams[id] = new Beam(id, row, column, length, dir);
    }

    private void findBeams() {
        for (int r = 0; r < nRows; r++) {
            int rightmostColumn = leftmostColumn + nCorridorColumns;
            for (int c = leftmostColumn; c < rightmostColumn; c++) {
                int beamId = grid[r][c];

                if (beamId != 0 && !neededToFree[beamId]){
                    neededToFree[beamId] = true;
                    neededCount++;
                }
            }
        }
    }

    private void buildGraph() {
        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 1; i < neededToFree.length; i++) {
            if (neededToFree[i]) {
                queue.add(i);
            }
        }
        boolean[] processed = new boolean[nBeams + 1];
        while (!queue.isEmpty()) {
            int beamId = queue.poll();
            if (processed[beamId]) {
                continue;
            }
            processed[beamId] = true;
            Beam beam = beams[beamId];
            findBlockers(beam, queue);
        }
    }

    private void findBlockers(Beam beam, Queue<Integer> queue) {
        int beamId = beam.getId();
        int dR = getDRow(beam.getDir());
        int dC = getDColumn(beam.getDir());

        int r = beam.getRow() + dR * beam.getLength();
        int c = beam.getColumn() + dC * beam.getLength();
        int lastBlocker = 0;

        while (r >= 0 && r < nRows && c >= 0 && c < nColumns) {
            int blockerId = grid[r][c];

            if (blockerId != 0 && blockerId != beamId && lastBlocker != blockerId) {
                graph[blockerId].add(beamId);
                inDegree[beamId]++;

                if (!neededToFree[blockerId]) {
                    queue.add(blockerId);
                    neededToFree[blockerId] = true;
                    neededCount++;
                }
                lastBlocker = blockerId;
            }

            r += dR;
            c += dC;
        }
    }

    private String topologicalSort() {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        // повтор кода
        for (int i = 1; i < neededToFree.length; i++) {
            if (neededToFree[i] && inDegree[i] == 0) {
                pq.add(i);
            }
        }
        StringBuilder result = new StringBuilder();
        int removedCount = 0;
        while (!pq.isEmpty()) {
            int current = pq.poll();
            if (!result.isEmpty()) {
                result.append(" ");
            }
            result.append(current);
            removedCount++;

            // current beam is removed, so it unlocks beams after it
            for (int next : graph[current]) {
                inDegree[next]--;

                if (inDegree[next] == 0) {
                    pq.add(next);
                }
            }
        }
        if (removedCount != neededCount) {
            return D;
        }
        return result.toString();
    }

    public String solve() {
        findBeams();
        if (neededCount == 0) return FA;
        buildGraph();

        return topologicalSort();
    }
}
