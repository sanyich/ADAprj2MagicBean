import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.PriorityQueue;

public class Solver {
    private final int R, C, N, L, B;
    private final int[][] grid;
    private final MagicBeams[] beams;

    private final Set<Integer> needed = new HashSet<>();
    private Set<Integer>[] graph;
    private final int[] indegree;

    private static final String FA = "False alarm";
    private static final String D = "Disaster";

    public Solver(int nR, int nC, int n, int nL, int b, MagicBeams[] beams) {
        this.R = nR;
        this.C = nC;
        this.N = n;
        this.L = nL;
        this.B = b;
        this.beams = beams;
        this.grid = new int[R][C];
        this.indegree = new int[B + 1];

        putBeamsInGrid();
        createGraph();
    }

    @SuppressWarnings("unchecked")
    private void createGraph() {
        graph = new Set[B + 1];
        for (int i = 1; i <= B; i++) {
            graph[i] = new HashSet<>();
        }
    }

    private int getDr(char dir) {
        return switch (dir) {
            case 'N' -> -1;
            case 'S' -> 1;
            default -> 0;
        };
    }
    private int getDc(char dir) {
        return switch (dir) {
            case 'E' -> 1;
            case 'W' -> -1;
            default -> 0;
        };
    }


    private void putBeamsInGrid() {
        for (int id = 1; id <= B; id++) {
            MagicBeams beam = beams[id];

            int r = beam.getR();
            int c = beam.getC();

            int dr = getDr(beam.getDir());
            int dc = getDc(beam.getDir());

            for (int k = 0; k < beam.getL(); k++) {
                grid[r][c] = id;
                r += dr;
                c += dc;
            }
        }
    }

    private void findBeams() {
        for (int r = 0; r < R; r++) {
            for (int c = L; c < L + N; c++) {
                if (grid[r][c] != 0) {
                    needed.add(grid[r][c]);
                }
            }
        }
    }

    private void buildGraph() {
        Queue<Integer> queue = new LinkedList<>(needed);
        Set<Integer> processed = new HashSet<>();
        while (!queue.isEmpty()) {
            int currentId = queue.poll();
            if (processed.contains(currentId)) {
                continue;
            }
            processed.add(currentId);
            MagicBeams beam = beams[currentId];
            int r = beam.getR();
            int c = beam.getC();
            int dr = getDr(beam.getDir());
            int dc = getDc(beam.getDir());

            // cell after the last cell of beam
            r = r + dr * beam.getL();
            c = c + dc * beam.getL();

            // scan forward until the end of grid
            while (r >= 0 && r < R && c >= 0 && c < C) {
                int blockerId = grid[r][c];

                if (blockerId != 0 && blockerId != currentId) {
                    // blocker must be removed before current beam
                    if (!graph[blockerId].contains(currentId)) {
                        graph[blockerId].add(currentId);
                        indegree[currentId]++;
                    }
                    // blocker also becomes needed
                    if (!needed.contains(blockerId)) {
                        needed.add(blockerId);
                        queue.add(blockerId);
                    }
                }
                r += dr;
                c += dc;
            }
        }
    }

    private String topologicalSort() {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int id : needed) {
            if (indegree[id] == 0) {
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
                indegree[next]--;

                if (indegree[next] == 0) {
                    pq.add(next);
                }
            }
        }
        if (removedCount != needed.size()) {
            return D;
        }
        return result.toString();
    }

    public String solve() {
        findBeams();
        if (needed.isEmpty()) return FA;
        buildGraph();
        return topologicalSort();
    }
}
