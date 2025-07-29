package solver.solverImpl;

import models.*;
import solver.MazeSolver;

import java.util.*;


public class MazeSolverDFS implements MazeSolver {

    private Set<Cell> visitadas = new LinkedHashSet<>();
    private List<Cell> camino = new ArrayList<>();

    @Override
    public SolveResults getPath(Cell[][] laberinto, Cell inicio, Cell fin) {
        visitadas.clear();
        camino.clear();
        dfs(laberinto, inicio.getRow(), inicio.getCol(), fin);
        Collections.reverse(camino);
        return new SolveResults(camino, visitadas);
    }

    private boolean dfs(Cell[][] lab, int fila, int col, Cell fin) {
        if (!esValido(lab, fila, col)) return false;

        Cell actual = lab[fila][col];
        if (visitadas.contains(actual)) return false;

        visitadas.add(actual);

        if (actual.equals(fin)) {
            camino.add(actual);
            return true;
        }

        if (dfs(lab, fila + 1, col, fin) ||
                dfs(lab, fila - 1, col, fin) ||
                dfs(lab, fila, col + 1, fin) ||
                dfs(lab, fila, col - 1, fin)) {
            camino.add(actual);
            return true;
        }

        return false;
    }

    private boolean esValido(Cell[][] lab, int r, int c) {
        return r >= 0 && r < lab.length &&
                c >= 0 && c < lab[0].length &&
                lab[r][c].getState() != CellState.WALL;
    }
}