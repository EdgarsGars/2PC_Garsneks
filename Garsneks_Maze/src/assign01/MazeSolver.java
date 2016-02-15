package assign01;

import com.sun.istack.internal.logging.Logger;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import org.w3c.dom.css.RGBColor;

/**
 * Assignment 01 - Implement parallel maze solving algorithm using JAVA
 * Concurrent API
 *
 * use maze.isNorth(x,y), maze.isSouth(x,y), etc to check if there is wall in
 * exact direction use maze.setVisited(x,y) and maze.isVisited(x,y) to check is
 * you already have been at particular cell in the maze
 *
 * @author Janis
 */
public class MazeSolver {

    Maze maze;

    //Current position
    int x;
    int y;

    public MazeSolver(Maze m) {
        this.maze = m;
    }

    /**
     * Solve maze starting from initial positions
     *
     * @param x starting position in x direction
     * @param y starting position in y direction
     */
    public List<Point2D> solveMaze(int x, int y) {
        //Set current position to initial position
        this.x = x;
        this.y = y;

        // TODO Your algorithm here. If necessary, create onother methods (for example, recursive solve method)
        // Use theses lines where necessary to draw movement of the solver
        ForkJoinPool pool = new ForkJoinPool(7);
        RecursiveSolver solver = new RecursiveSolver(maze, x, y);
        return pool.invoke(solver);
        
    }

}

class RecursiveSolver extends RecursiveTask<List<Point2D>> {

    Maze maze;
    int x, y;

    public RecursiveSolver(Maze maze, int x, int y) {
        this.maze = maze;
        this.x = x;
        this.y = y;
    }

    @Override
    protected List<Point2D> compute() {
        maze.setVisited(x, y);
        List<Point2D> p = null;

        if (x == maze.getDim() && y == maze.getDim()) {
            p = new ArrayList<>();
            p.add(new Point2D(x, y, Thread.currentThread().getName()));
            return p;
        }

        List<RecursiveSolver> solvers = new ArrayList<>();
        if (!maze.isEast(x, y) && !maze.isVisited(x + 1, y)) {
            solvers.add(new RecursiveSolver(maze, x + 1, y));
            solvers.get(solvers.size() - 1).fork();
        }
        if (!maze.isWest(x, y) && !maze.isVisited(x - 1, y)) {
            solvers.add(new RecursiveSolver(maze, x - 1, y));
            solvers.get(solvers.size() - 1).fork();
        }
        if (!maze.isSouth(x, y) && !maze.isVisited(x, y - 1)) {
            solvers.add(new RecursiveSolver(maze, x, y - 1));
            solvers.get(solvers.size() - 1).fork();
        }
        if (!maze.isNorth(x, y) && !maze.isVisited(x, y + 1)) {
            solvers.add(new RecursiveSolver(maze, x, y + 1));
            solvers.get(solvers.size() - 1).fork();
        }

        for (RecursiveSolver solver : solvers) {
            List<Point2D> l = solver.join();
            if (l != null) {
                l.add(new Point2D(x, y, Thread.currentThread().getName()));
                return l;
            }
        }

        return null;
    }

}
