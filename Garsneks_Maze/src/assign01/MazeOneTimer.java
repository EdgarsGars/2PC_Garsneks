/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assign01;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 *
 * @author Edgar_000
 */
public class MazeOneTimer {

    public static void main(String[] args) {
        int N = 100;
        Maze maze = new Maze(N);
        MazeSolver s = new MazeSolver(maze);
        maze.setSolver(s);
        ForkJoinPool pool = new ForkJoinPool(7);
        long start = System.nanoTime();
        List<Point2D> p = maze.solve(pool);
        long end = System.nanoTime();
        System.out.println("Time to solve " + (end - start) / 1000000.0f + "ms");
        pool.shutdown();
        Maze.drawMazeWithSolution(maze, p);
    }
}
