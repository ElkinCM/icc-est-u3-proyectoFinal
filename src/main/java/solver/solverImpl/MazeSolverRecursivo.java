package solver.solverImpl;

import models.Cell;
import models.CellState;
import models.SolveResults;
import solver.MazeSolver;

import java.util.*;

public class MazeSolverRecursivo implements MazeSolver {

    private Set<Cell> visited = new LinkedHashSet<>();
    private List<Cell> path = new ArrayList<>();

    private Cell[][] maze;
    private Cell end;

    @Override
public SolveResults getPath(Cell[][] maze, Cell start, Cell end) {
    this.visited.clear();
    this.path.clear();
    this.maze = maze;
    this.end = end;

    findPath(start.getRow(), start.getCol());

    Collections.reverse(path);

    return new SolveResults(new ArrayList<>(path), new LinkedHashSet<>(visited));
}


    private boolean findPath(int r, int c) {
    if (!isValid(r, c)) return false;

    Cell current = maze[r][c];
    if (visited.contains(current)) return false;

    visited.add(current);

    if (current.equals(end)) {
        path.add(current);
        return true;
    }

    if (findPath(r + 1, c) || findPath(r, c + 1)) {
        path.add(current); 
        return true;
    }

    return false;
}

    private boolean isValid(int r, int c) {
        return r >= 0 && r < maze.length &&
               c >= 0 && c < maze[0].length &&
               maze[r][c].getState() != CellState.WALL;
    }
}
