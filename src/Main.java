import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Entry point of the program
 * Reads all test cases, builds a Solver for each one, and prints the answer.
 * The Main class is responsible only for input and output. The computation
 * itself is delegated to Solver.
 *
 * @author Ilia Taitsel 67258
 * @author Oleksandra Kozlova 68739
 */
class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        int nTestCases = Integer.parseInt(in.readLine());

        for (int i = 0; i < nTestCases; i++) {
            String[] gridDimensions = in.readLine().split(" ");
            int nRows = Integer.parseInt(gridDimensions[0]);
            int nColumns = Integer.parseInt(gridDimensions[1]);

            String[] corridor = in.readLine().split(" ");
            int nCorridorColumns = Integer.parseInt(corridor[0]);
            int leftmostColumn = Integer.parseInt(corridor[1]);

            int nBeams = Integer.parseInt(in.readLine());

            Solver solver = new Solver(nRows, nColumns, nCorridorColumns, leftmostColumn, nBeams);

            /*
             * Beam identifiers are assigned by input order:
             * the first beam has id 1, the second has id 2, and so on.
             */
            for (int id = 1; id <= nBeams; id++) {
                String[] parts =  in.readLine().trim().split(" ");

                int row = Integer.parseInt(parts[0]);
                int column = Integer.parseInt(parts[1]);
                int length = Integer.parseInt(parts[2]);
                char dir = parts[3].charAt(0);

                solver.addBeam(id, row, column, length, dir);
            }
            System.out.println(solver.solve());
        }
    }
}