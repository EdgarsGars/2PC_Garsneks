package assign01;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * Generates a perfect N-by-N maze.
 *
 * @author Janis
 */
public class Maze {

    private int N;                 // dimension of maze

    private boolean[][] north;     // is there a wall to north of cell i, j
    private boolean[][] east;      // and so forth...
    private boolean[][] south;
    private boolean[][] west;

    private boolean[][] visited;    // Use this array to register/query already visited cells

    private MazeSolver solver;

    public Maze(int N) {
        this.N = N;
        StdDraw.setXscale(0, N + 2);
        StdDraw.setYscale(0, N + 2);
        init();
        generate();
    }

    private void init() {
        // initialize border cells as already visited
        visited = new boolean[N + 2][N + 2];
        for (int x = 0; x < N + 2; x++) {
            visited[x][0] = visited[x][N + 1] = true;
        }
        for (int y = 0; y < N + 2; y++) {
            visited[0][y] = visited[N + 1][y] = true;
        }

        // initialze all walls as present
        north = new boolean[N + 2][N + 2];
        east = new boolean[N + 2][N + 2];
        south = new boolean[N + 2][N + 2];
        west = new boolean[N + 2][N + 2];
        for (int x = 0; x < N + 2; x++) {
            for (int y = 0; y < N + 2; y++) {
                north[x][y] = east[x][y] = south[x][y] = west[x][y] = true;
            }
        }
    }

    // generate the maze
    private void generate(int x, int y) {
        visited[x][y] = true;

        // while there is an unvisited neighbor
        while (!visited[x][y + 1] || !visited[x + 1][y] || !visited[x][y - 1] || !visited[x - 1][y]) {

            // pick random neighbor
            while (true) {
                double r = Math.random();
                if (r < 0.25 && !visited[x][y + 1]) {
                    north[x][y] = south[x][y + 1] = false;
                    generate(x, y + 1);
                    break;
                } else if (r >= 0.25 && r < 0.50 && !visited[x + 1][y]) {
                    east[x][y] = west[x + 1][y] = false;
                    generate(x + 1, y);
                    break;
                } else if (r >= 0.5 && r < 0.75 && !visited[x][y - 1]) {
                    south[x][y] = north[x][y - 1] = false;
                    generate(x, y - 1);
                    break;
                } else if (r >= 0.75 && r < 1.00 && !visited[x - 1][y]) {
                    west[x][y] = east[x - 1][y] = false;
                    generate(x - 1, y);
                    break;
                }
            }
        }
    }

    // generate the maze starting from lower left
    private void generate() {
        generate(1, 1);
    }

    public void setSolver(MazeSolver s) {
        solver = s;
    }

    // solve the maze starting from the start state
    public List<Point2D> solve(ForkJoinPool pool) {
        for (int x = 1; x <= N; x++) {
            for (int y = 1; y <= N; y++) {
                visited[x][y] = false;
            }
        }

        //Call solver with your implementation of algorithm. Initial position x = 1, y = 1
        return solver.solveMaze(1, 1, pool);
    }

    // draw the maze
    public void draw() {
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.filledCircle(N + 0.5, N + 0.5, 0.375);
        StdDraw.filledCircle(1.5, 1.5, 0.375);

        StdDraw.setPenColor(StdDraw.BLACK);
        for (int x = 1; x <= N; x++) {
            for (int y = 1; y <= N; y++) {
                if (south[x][y]) {
                    StdDraw.line(x, y, x + 1, y);
                }
                if (north[x][y]) {
                    StdDraw.line(x, y + 1, x + 1, y + 1);
                }
                if (west[x][y]) {
                    StdDraw.line(x, y, x, y + 1);
                }
                if (east[x][y]) {
                    StdDraw.line(x + 1, y, x + 1, y + 1);
                }
            }
        }
        StdDraw.show(1000);
    }

    public boolean isSouth(int x, int y) {
        return south[x][y];
    }

    public boolean isNorth(int x, int y) {
        return north[x][y];
    }

    public boolean isWest(int x, int y) {
        return west[x][y];
    }

    public boolean isEast(int x, int y) {
        return east[x][y];
    }

    public boolean isVisited(int x, int y) {
        return visited[x][y];
    }

    public void setVisited(int x, int y) {
        visited[x][y] = true;
    }

    public int getDim() {
        return N;
    }

    // Client
    public static void main(String[] args) throws IOException {
        //int threadCount = 5;
        int[] ns = new int[]{30, 50, 100};
        for (int threadCount = 1; threadCount < 9; threadCount++) {

            ForkJoinPool pool = new ForkJoinPool(threadCount);
            for (int n = 0; n < ns.length; n++) {

                List<Point2D> solution = null;
                Maze maze = null;
                int N = ns[n];
                //  Vector v = new Vector();
                for (int s = 0; s < 10000; s++) {
                    StdDraw.clear();
                    maze = new Maze(N);
                    MazeSolver solver = new MazeSolver(maze);
                    maze.setSolver(solver);

                    //ForkJoinPool pool = new ForkJoinPool(threadCount);
                    long start = System.nanoTime();
                    solution = maze.solve(pool);
                    long end = System.nanoTime();
                    //pool.shutdown();
                    //  v.add((end - start) / 1000000.0f);
                }
                //System.out.println("Using " + threadCount + " thread/s");
                // System.out.println("Average solving time for N = " + N + " is " + v.mean() / 1000000.0f + "ns");
                //   System.out.println("Min solving time for N = " + N + " is " + v.minValue() / 1000000.0f + "ns");
                //   System.out.println("Max solving time for N = " + N + " is " + v.maxValue() / 1000000.0f + "ns");
                //  VectorUtils.saveToFile(v, "./bla/T" + threadCount + "N" + N + ".csv", ",");
                //  drawMazeWithSolution(maze, solution);

            }

            pool.shutdownNow();
        }

    }

    public static void drawMazeWithSolution(Maze maze, List<Point2D> solution) {
        StdDraw.show(0);
        maze.draw();
        for (int i = solution.size() - 1; i >= 0; i--) {
            StdDraw.setPenColor(Color.blue);
            StdDraw.filledCircle(solution.get(i).x + 0.5, solution.get(i).y + 0.5, 0.25);

            // for 30 ms
            StdDraw.show(1);
        }
    }

}
