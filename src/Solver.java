import java.util.*;

public class Solver {
    private final int nRows;
    private final int nColumns;
    private final int nCorridorColumns;
    private final int leftmostColumn;
    private final int nBeams;
    private final int[][] grid;
    private final Set<Integer> neededToFree = new HashSet<>();
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
                if (grid[r][c] != 0) {
                    neededToFree.add(grid[r][c]);
                }
            }
        }
    }

    private void buildGraph() {
        Queue<Integer> queue = new LinkedList<>(neededToFree);
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

        while (r >= 0 && r < nRows && c >= 0 && c < nColumns) {
            int blockerId = grid[r][c];

            if (blockerId != 0 && blockerId != beamId) {
                if (!graph[blockerId].contains(beamId)) {
                    graph[blockerId].add(beamId);
                    inDegree[beamId]++;
                }

                if (neededToFree.add(blockerId)) {
                    queue.add(blockerId);
                }
            }

            r += dR;
            c += dC;
        }
    }

    private String topologicalSort() {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int id : neededToFree) {
            if (inDegree[id] == 0) {
                pq.add(id);
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
        if (removedCount != neededToFree.size()) {
            return D;
        }
        return result.toString();
    }

    public String solve() {
        findBeams();
        if (neededToFree.isEmpty()) return FA;
        buildGraph();

        return topologicalSort();
    }
}
