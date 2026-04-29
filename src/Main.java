import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        int T = Integer.parseInt(in.readLine()); //test cases
        for (int i = 0; i < T; i++) {
            String gridDimensions = in.readLine();
            int nRows = Integer.parseInt(gridDimensions.split(" ")[0]); // rows
            int nColumns = Integer.parseInt(gridDimensions.split(" ")[1]); // columns
            String corridor = in.readLine();
            int nCorridorColumns = Integer.parseInt(corridor.split(" ")[0]); // n of chosen column
            int leftmostColumn = Integer.parseInt(corridor.split(" ")[1]); // the leftmost column
            int nBeams = Integer.parseInt(in.readLine()); // n of magic beams
            Solver solver = new Solver(nRows, nColumns, nCorridorColumns, leftmostColumn, nBeams);
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