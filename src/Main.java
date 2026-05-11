import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

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
    private static final String FALSE_ALARM_OUTPUT = "False alarm";
    private static final String DISASTER_OUTPUT = "Disaster";

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
            // capacity for either a status message or a full list of beam ids.
            int stringCapacity = Math.max(FALSE_ALARM_OUTPUT.length(), nBeams * 6);
            StringBuilder output = new StringBuilder(stringCapacity);
            appendAnswer(solver.solve(), output);
            System.out.println(output);
        }
    }


    /**
     * Appends one test case answer to the given output builder.
     * -
     * The iterator contains either:
     * - Solver.FALSE_ALARM;
     * - Solver.DISASTER;
     * - or the sequence of beam identifiers to free.
     */
    private static void appendAnswer(Iterator<Integer> answer, StringBuilder output) {
        int first = answer.next();

        if (first == Solver.FALSE_ALARM) {
            output.append(FALSE_ALARM_OUTPUT);
            return;
        }

        if (first == Solver.DISASTER) {
            output.append(DISASTER_OUTPUT);
            return;
        }

        output.append(first);

        while (answer.hasNext()) {
            output.append(' ');
            output.append(answer.next());
        }
    }
}