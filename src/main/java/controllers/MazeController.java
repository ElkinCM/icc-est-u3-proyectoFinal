package controllers;

import dao.AlgorithmResultDAO;
import models.AlgorithmResult;
import models.Cell;
import models.CellState;
import models.SolveResults;
import solver.MazeSolver;

import java.util.*;

public class MazeController {

    public enum Mode {
        START, END, WALL
    }

    private Mode modoActual = Mode.WALL;
    private Cell[][] matriz;
    private Cell startCell;
    private Cell endCell;

    private final Map<String, MazeSolver> algoritmos = new LinkedHashMap<>();
    private final AlgorithmResultDAO resultDAO;

    public MazeController(AlgorithmResultDAO resultDAO) {
        this.resultDAO = resultDAO;
    }

    public void setMode(Mode modo) {
        this.modoActual = modo;
    }

    public Mode getMode() {
        return modoActual;
    }

    public void setMatriz(Cell[][] matriz) {
        this.matriz = matriz;
    }

    public boolean[][] getMatriz() {
        boolean[][] m = new boolean[matriz.length][matriz[0].length];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[0].length; j++) {
                m[i][j] = matriz[i][j].getState() != CellState.WALL;
            }
        }
        return m;
    }

    public Cell getStartCell() {
        return startCell;
    }

    public Cell getEndCell() {
        return endCell;
    }

    public void onCellClicked(int fila, int columna) {
        Cell cell = matriz[fila][columna];
        switch (modoActual) {
            case START -> {
                if (startCell != null) {
                    matriz[startCell.getRow()][startCell.getCol()].setState(CellState.EMPTY);
                }
                cell.setState(CellState.START);
                startCell = cell;
            }
            case END -> {
                if (endCell != null) {
                    matriz[endCell.getRow()][endCell.getCol()].setState(CellState.EMPTY);
                }
                cell.setState(CellState.END);
                endCell = cell;
            }
            case WALL -> {
                if (cell.getState() == CellState.WALL) {
                    cell.setState(CellState.EMPTY);
                } else {
                    cell.setState(CellState.WALL);
                }
            }
        }
    }

    public void registrarAlgoritmo(String nombre, MazeSolver solver) {
        algoritmos.put(nombre, solver);
    }

   public SolveResults ejecutar(String nombreAlgoritmo, boolean[][] grid, Cell inicio, Cell fin) {
    MazeSolver solver = algoritmos.get(nombreAlgoritmo);
    if (solver == null) {
        throw new IllegalArgumentException("Algoritmo no registrado: " + nombreAlgoritmo);
    }

    long t0 = System.nanoTime();
SolveResults resultado = solver.getPath(convertToCells(grid), inicio, fin);
long tf = System.nanoTime();
long duracionNs = tf - t0;

resultDAO.guardarResultado(new AlgorithmResult(nombreAlgoritmo, resultado.getPath().size(), duracionNs));


    return resultado;
}

    public List<AlgorithmResult> listarResultados() {
        return resultDAO.obtenerResultados();
    }

    public void limpiarResultados() {
        resultDAO.limpiarResultados();
    }

    public Set<String> obtenerNombresAlgoritmos() {
        return algoritmos.keySet();
    }
    private Cell[][] convertToCells(boolean[][] grid) {
        int filas = grid.length;
        int cols = grid[0].length;
        Cell[][] cells = new Cell[filas][cols];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Cell(i, j);
                cells[i][j].setState(grid[i][j] ? CellState.EMPTY : CellState.WALL);
            }
        }
        return cells;
    }

}

