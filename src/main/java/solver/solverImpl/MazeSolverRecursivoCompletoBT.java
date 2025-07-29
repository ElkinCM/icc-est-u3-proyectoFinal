package solver.solverImpl;

import solver.MazeSolver;
import models.*;

import java.util.*;

/**
 * Algoritmo recursivo completo con backtracking real: explora todas las rutas posibles.
 */
public class MazeSolverRecursivoCompletoBT implements MazeSolver {

    private Set<Cell> visited;
    private List<Cell> path;

    @Override
    public SolveResults getPath(Cell[][] maze, Cell start, Cell end) {
        this.visited = new LinkedHashSet<>();
        this.path = new ArrayList<>();

        findPath(maze, start.getRow(), start.getCol(), end);

        Collections.reverse(this.path);

        List<Cell> regreso = new ArrayList<>(path);
        Collections.reverse(regreso);
        if (!regreso.isEmpty()) regreso.remove(0);

        path.addAll(regreso);

        return new SolveResults(new ArrayList<>(path), new LinkedHashSet<>(visited));
    }

    private boolean findPath(Cell[][] maze, int r, int c, Cell end) {
        if (!isValid(maze, r, c)) return false;

        Cell current = maze[r][c];
        if (visited.contains(current)) return false;

        visited.add(current);
        path.add(current);

        if (current.equals(end)) {
            return true;
        }

        if (!findPath(maze, r + 1, c, end) &&
                !findPath(maze, r, c + 1, end) &&
                !findPath(maze, r - 1, c, end) &&
                !findPath(maze, r, c - 1, end)) {
            path.remove(path.size() - 1);
            return false;
        }

        return true;
    }

    private boolean isValid(Cell[][] maze, int r, int c) {
        return r >= 0 && r < maze.length &&
                c >= 0 && c < maze[0].length &&
                maze[r][c].getState() != CellState.WALL;
    }
}
