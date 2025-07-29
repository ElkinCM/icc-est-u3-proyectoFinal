package solver.solverImpl;

import solver.MazeSolver;
import models.*;

import java.util.*;

public class MazeSolverRecursivoCompleto implements MazeSolver {

    private Set<Cell> visited;
    private List<Cell> path;
    private Cell[][] maze;
    private Cell end;

    @Override
    public SolveResults getPath(Cell[][] maze, Cell start, Cell end) {
        this.visited = new LinkedHashSet<>();
        this.path = new ArrayList<>();
        this.maze = maze;
        this.end = end;

        findPath(start.getRow(), start.getCol());

        Collections.reverse(path); 

        return new SolveResults(new ArrayList<>(path), new LinkedHashSet<>(visited));
    }

    private boolean findPath(int row, int col) {
        if (!isValid(row, col)) return false;

        Cell current = maze[row][col];
        if (visited.contains(current)) return false;

        visited.add(current);

        if (current.equals(end)) {
            path.add(current); 
            return true;
        }

        if (findPath(row + 1, col) ||
            findPath(row, col + 1) ||
            findPath(row - 1, col) ||
            findPath(row, col - 1)) {
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
