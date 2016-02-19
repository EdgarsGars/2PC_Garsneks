package assign01;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

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
    public List<Point2D> solveMaze(int x, int y, ForkJoinPool pool) {
        //Set current position to initial position
        this.x = x;
        this.y = y;

        // TODO Your algorithm here. If necessary, create onother methods (for example, recursive solve method)
        // Use theses lines where necessary to draw movement of the solver
        //ForkJoinPool pool = new ForkJoinPool(5);
        RecursiveSolver solver = new RecursiveSolver(maze, x, y);
        List<Point2D> p = pool.invoke(solver);
        //pool.shutdownNow();
        return p;
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
        if (!maze.isEast(x, y) && !maze.isVisited(x + 1, y) && !isDeadEnd(x + 1, y)) {
            solvers.add(new RecursiveSolver(maze, x + 1, y));
        }
        if (!maze.isWest(x, y) && !maze.isVisited(x - 1, y) && !isDeadEnd(x - 1, y)) {
            solvers.add(new RecursiveSolver(maze, x - 1, y));
        }
        if (!maze.isSouth(x, y) && !maze.isVisited(x, y - 1) && !isDeadEnd(x, y - 1)) {
            solvers.add(new RecursiveSolver(maze, x, y - 1));
        }
        if (!maze.isNorth(x, y) && !maze.isVisited(x, y + 1) && !isDeadEnd(x, y + 1)) {
            solvers.add(new RecursiveSolver(maze, x, y + 1));
        }

        if (solvers.size() > 1) {
            for (int i = 1; i < solvers.size(); i++) {
                solvers.get(i).fork();
            }
        }
        List<Point2D> l = null;
        for (int i = 0; i < solvers.size(); i++) {
            RecursiveSolver solver = solvers.get(i);
            
            if (solvers.size() > 1 && i != 0) {
                l = solver.join();
            } else {
                l = solver.compute();
            }
            if (l != null) {
                l.add(new Point2D(x, y, Thread.currentThread().getName()));
                return l;

            }
        }

        return null;

    }

    public boolean isDeadEnd(int x, int y) {
        // return false;/*
        if (x == maze.getDim() && y == maze.getDim()) {
            return false;
        }
        return (maze.isVisited(x, y + 1) && maze.isEast(x, y) && maze.isWest(x, y) && maze.isNorth(x, y))
                || (maze.isVisited(x, y - 1) && maze.isEast(x, y) && maze.isWest(x, y) && maze.isSouth(x, y))
                || (maze.isVisited(x + 1, y) && maze.isSouth(x, y) && maze.isWest(x, y) && maze.isNorth(x, y))
                || (maze.isVisited(x - 1, y) && maze.isEast(x, y) && maze.isSouth(x, y) && maze.isNorth(x, y));
//*/
    }

}
