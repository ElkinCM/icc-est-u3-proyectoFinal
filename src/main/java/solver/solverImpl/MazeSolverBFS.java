package solver.solverImpl;

import solver.MazeSolver;
import models.*;

import java.util.*;


public class MazeSolverBFS implements MazeSolver {

    @Override
    public SolveResults getPath(Cell[][] maze, Cell start, Cell end) {
        int rows = maze.length;
        int cols = maze[0].length;

        boolean[][] visitedMatrix = new boolean[rows][cols];
        Map<Cell, Cell> parent = new HashMap<>();
        Queue<Cell> queue = new LinkedList<>();
        List<Cell> visitados = new ArrayList<>();

        Cell nodoInicio = maze[start.getRow()][start.getCol()];
        Cell nodoFin = maze[end.getRow()][end.getCol()];

        queue.offer(nodoInicio);
        visitedMatrix[nodoInicio.getRow()][nodoInicio.getCol()] = true;

        while (!queue.isEmpty()) {
            Cell actual = queue.poll();
            visitados.add(actual);

            if (actual.equals(nodoFin)) {
                break;
            }

            for (int[] dir : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                int nr = actual.getRow() + dir[0];
                int nc = actual.getCol() + dir[1];

                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                    Cell vecino = maze[nr][nc];
                    if (!visitedMatrix[nr][nc] && vecino.getState() != CellState.WALL) {
                        queue.offer(vecino);
                        visitedMatrix[nr][nc] = true;
                        parent.put(vecino, actual);
                    }
                }
            }
        }

        List<Cell> path = new ArrayList<>();
        Cell actual = nodoFin;

        while (parent.containsKey(actual)) {
            path.add(actual);
            actual = parent.get(actual);
        }

        if (actual.equals(nodoInicio)) {
            path.add(nodoInicio);
        } else {
            path.clear();
        }

        return new SolveResults(path, new LinkedHashSet<>(visitados));
    }
}