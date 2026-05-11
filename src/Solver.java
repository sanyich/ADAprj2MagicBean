import java.util.*;

/**
 * Solves one test case of the Magic Beams problem.
 * Each beam is treated as a vertex of a directed graph.
 * If beam A blocks the path of beam B, then A must be freed before B,
 * so the graph contains the edge A -> B.
 * solver first finds all beams that occupy the chosen corridor.
 * Then it iteratively adds every beam that blocks one of the needed beams.
 * Finally, it applies Kahn's topological sorting algorithm with a priority
 * queue, so that when several beams are available, the smallest identifier
 * is chosen first.
 */
public class Solver {

    private final int nRows;
    private final int nColumns;
    private final int nCorridorColumns;
    private final int leftmostColumn;
    private final int nBeams;

    /*
     * grid[r][c] stores the id of the beam occupying cell (r, c),
     * or 0 if the cell is empty.
     *
     * short is enough because beam ids are at most 10060.
     */
    private final short[][] grid;

    /*
     * neededToFree[id] is true iff beam id must be freed either because
     * it is inside the corridor or because it blocks another needed beam.
     */
    private final boolean[] neededToFree;
    private int neededCount;

    /*
     * graph[u] contains all beams v such that u must be freed before v.
     * In other words, u blocks v, so the dependency edge is u -> v.
     */
    private final List<Integer>[] graph;

    /*
     * Primitive arrays are used instead of Beam objects to reduce memory
     * and object creation overhead.
     */
    private final int[] row;
    private final int[] column;
    private final int[] length;
    private final char[] dir;

    /*
     * inDegree[v] is the number of needed blockers that must be freed
     * before beam v can be freed.
     */
    private final int[] inDegree;

    // constants
    public final static int FALSE_ALARM = -1;
    public final static int DISASTER = -2;

    private final static char NORTH = 'N';
    private final static char SOUTH = 'S';
    private final static char WEST = 'W';
    private final static char EAST = 'E';

    /**
     * Creates a solver for one test case.
     *
     * @param nRows number of grid rows
     * @param nColumns number of grid columns
     * @param nCorridorColumns number of chosen corridor columns
     * @param leftmostColumn leftmost chosen corridor column
     * @param nBeams number of beams
     */
    public Solver(int nRows, int nColumns, int nCorridorColumns, int leftmostColumn, int nBeams) {
        this.nRows = nRows;
        this.nColumns = nColumns;
        this.nCorridorColumns = nCorridorColumns;
        this.leftmostColumn = leftmostColumn;
        this.nBeams = nBeams;

        this.row = new int[nBeams + 1];
        this.column = new int[nBeams + 1];
        this.length = new int[nBeams + 1];
        this.dir = new char[nBeams + 1];

        this.neededToFree = new boolean[nBeams + 1];
        neededCount = 0;

        grid = new short[nRows][nColumns];
        graph = initializeGraph(nBeams + 1);
        inDegree = new int[nBeams + 1];
    }

    /**
     * Initializes an adjacency-list representation of the dependency graph.
     * -
     * LinkedList is used because the number of outgoing edges of each beam
     * is not known in advance.
     */
    @SuppressWarnings("unchecked")
    private List<Integer>[] initializeGraph(int size) {
        List<Integer>[] result = new List[size];

        for (int i = 1; i < size; i++) {
            result[i] = new LinkedList<>();
        }

        return result;
    }

    private int getDRow(char dir) {
        if (dir == NORTH) return -1;
        if (dir == SOUTH) return 1;
        return 0;
    }

    private int getDColumn(char dir) {
        if (dir == EAST) return 1;
        if (dir == WEST) return -1;
        return 0;
    }

    /**
     * Adds a beam to the internal representation.
     * The beam is stored in primitive arrays and its occupied cells are
     * written into the grid.
     *
     * @param id beam identifier, from 1 to nBeams
     * @param row starting row of the beam
     * @param column starting column of the beam
     * @param length number of cells occupied by the beam
     * @param dir direction of the beam: N, S, E, or W
     */
    public void addBeam(int id, int row, int column, int length, char dir) {
        int dRow = getDRow(dir);
        int dColumn = getDColumn(dir);
        for (int i = 0; i < length; i++) {
            grid[row + dRow * i][column + dColumn * i] = (short) id;
        }
        this.row[id] = row;
        this.column[id] = column;
        this.length[id] = length;
        this.dir[id] = dir;
    }

    /**
     * Finds all beams that currently occupy at least one chosen corridor column.
     * Those beams must be freed, unless the corridor is already empty.
     */
    private void findBeams() {
        int rightmostColumn = leftmostColumn + nCorridorColumns;
        for (int r = 0; r < nRows; r++) {
            for (int c = leftmostColumn; c < rightmostColumn; c++) {
                int beamId = grid[r][c];

                if (beamId != 0) {
                    markNeeded(beamId);
                }
            }
        }
    }

    /**
     * Builds the dependency graph for all beams that are necessary to free.
     * The queue starts with beams inside the corridor. Whenever a blocker is
     * found, it also becomes necessary and is added to the queue.
     */
    private void buildGraph() {
        Queue<Integer> queue = new ArrayDeque<>(neededCount);
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
            findBlockers(beamId, queue);
        }
    }

    /**
     * Scans the path of a beam and adds all beams that block it.
     * If blockerId blocks beamId, then blockerId must be freed first.
     * Therefore, the dependency edge is blockerId -> beamId.
     *
     * @param beamId identifier of the beam whose path is scanned
     * @param queue queue of beams whose blockers still need to be processed
     */
    private void findBlockers(int beamId, Queue<Integer> queue) {
        int dR = getDRow(dir[beamId]);
        int dC = getDColumn(dir[beamId]);

        int r = row[beamId] + dR * length[beamId];
        int c = column[beamId] + dC * length[beamId];
        int lastBlocker = 0;

        while (r >= 0 && r < nRows && c >= 0 && c < nColumns) {
            int blockerId = grid[r][c];

            if (blockerId != 0 && blockerId != beamId && lastBlocker != blockerId) {
                graph[blockerId].add(beamId);
                inDegree[beamId]++;

                if (markNeeded(blockerId)) {
                    queue.add(blockerId);
                }
                lastBlocker = blockerId;
            }

            r += dR;
            c += dC;
        }
    }

    /**
     * Computes the freeing order using Kahn's topological sorting algorithm.
     * A priority queue is used instead of a normal queue because the problem
     * requires choosing the smallest available beam identifier.
     *
     * @return the freeing order, or null if the dependency graph has a cycle
     */
    private List<Integer> topologicalSort() {
        Queue<Integer> ready = new PriorityQueue<>(neededCount);
        for (int i = 1; i < neededToFree.length; i++) {
            if (neededToFree[i] && inDegree[i] == 0) {
                ready.add(i);
            }
        }
        List<Integer> order = new ArrayList<>(neededCount);

        while (!ready.isEmpty()) {
            int current = ready.poll();
            order.add(current);

            for (int next : graph[current]) {
                inDegree[next]--;

                if (inDegree[next] == 0) {
                    ready.add(next);
                }
            }
        }

        if (order.size() != neededCount) {
            return null;
        }

        return order;
    }

    /**
     * Marks a beam as necessary to free, if it was not marked before.
     *
     * @param beamId identifier of the beam
     * @return true if the beam was marked for the first time
     */
    private boolean markNeeded(int beamId) {
        if (neededToFree[beamId]) {
            return false;
        }

        neededToFree[beamId] = true;
        neededCount++;
        return true;
    }

    /**
     * Solves the current test case.
     *
     * @return iterator containing either a status code or the freeing order
     */
    public Iterator<Integer> solve() {
        findBeams();

        if (neededCount == 0) {
            return Collections.singletonList(FALSE_ALARM).iterator();
        }

        buildGraph();

        List<Integer> permutation = topologicalSort();

        if (permutation == null) {
            return Collections.singletonList(DISASTER).iterator();
        }

        return permutation.iterator();
    }
}
