import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.PriorityQueue;

public class Solver {
    private int R;
    private int C;
    private int N;
    private int L;
    private int B;
    private int[][] grid;
    private MagicBeams[] beams;
    private Set<Integer> needed = new HashSet<>();
    private Set<Integer>[] graph;
    private int[] indegree;
    private final String FA = "False alarm";
    private final String D = "Disaster";

    public Solver(int nR, int nC, int n, int nL, int b, MagicBeams[] beams) {
        this.R = nR;
        this.C = nC;
        this.N = n;
        this.L = nL;
        this.B = b;
        this.beams = beams;

        grid = new int[R][C];

        putBeamsInGrid();

        graph = new HashSet[B + 1];
        indegree = new int[B + 1];

        for (int i = 1; i <= B; i++) {
            graph[i] = new HashSet<>();
        }
    }

    private int[] direction(char dir) {
        if (dir == 'N') return new int[]{-1, 0};
        if (dir == 'S') return new int[]{1, 0};
        if (dir == 'E') return new int[]{0, 1};
        return new int[]{0, -1}; // W
    }

    private void putBeamsInGrid() {
        for (int id = 1; id <= B; id++) {
            MagicBeams beam = beams[id];

            int r = beam.getR();
            int c = beam.getC();

            int[] move = direction(beam.getDir());
            int dr = move[0];
            int dc = move[1];

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
            int[] move = direction(beam.getDir());
            int dr = move[0];
            int dc = move[1];

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
                    break;
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
