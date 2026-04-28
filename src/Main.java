import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        int T = Integer.parseInt(in.readLine()); //test cases
        for (int i = 0; i < T; i++) {
            String grind = in.readLine();
            int nR = Integer.parseInt(grind.split(" ")[0]); // rows
            int nC = Integer.parseInt(grind.split(" ")[1]); // columns
            String str = in.readLine();
            int N = Integer.parseInt(str.split(" ")[0]); // n of chosen column
            int nL = Integer.parseInt(str.split(" ")[1]); // the leftmost column
            int B = Integer.parseInt(in.readLine()); //n of magic beams
            MagicBeams[] beams = new MagicBeams[B+1];
            for (int id = 1; id <= B; id++) {
                String[] parts =  in.readLine().trim().split(" ");
                int r = Integer.parseInt(parts[0]);
                int c = Integer.parseInt(parts[1]);
                int l = Integer.parseInt(parts[2]);
                char dir = parts[3].charAt(0);
                beams[id] = new MagicBeams(id, r, c, l, dir);
            }
            Solver solver = new Solver(nR, nC, N, nL, B, beams);
            System.out.println(solver.solve());
        }
    }
}