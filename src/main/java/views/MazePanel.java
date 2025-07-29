package views;

import controllers.MazeController;
import models.Cell;
import models.CellState;
import models.SolveResults;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class MazePanel extends JPanel {

    private static final int SIZE = 40;
    private int filas = 10;
    private int columnas = 10;

    private Cell[][] cells;
    private JButton[][] buttons;
    private MazeController controller;

    public MazePanel() {
        solicitarTamanioLaberinto();
        inicializarCeldas();
        setLayout(new GridLayout(filas, columnas));
        setPreferredSize(new Dimension(columnas * SIZE, filas * SIZE));
        inicializarBotones();
    }

    private void solicitarTamanioLaberinto() {
        try {
            String inputFilas = JOptionPane.showInputDialog(null, "Ingrese número de filas:", "Tamaño del laberinto", JOptionPane.QUESTION_MESSAGE);
            String inputCols = JOptionPane.showInputDialog(null, "Ingrese número de columnas:", "Tamaño del laberinto", JOptionPane.QUESTION_MESSAGE);
            filas = Math.max(5, Integer.parseInt(inputFilas.trim()));
            columnas = Math.max(5, Integer.parseInt(inputCols.trim()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Entrada inválida. Usando tamaño por defecto 10x10.");
            filas = columnas = 10;
        }
    }

    private void inicializarCeldas() {
        cells = new Cell[filas][columnas];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                cells[i][j] = new Cell(i, j);
                cells[i][j].setState(CellState.EMPTY);
            }
        }
    }

    private void inicializarBotones() {
        buttons = new JButton[filas][columnas];

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                JButton btn = new JButton();
                btn.setBackground(Color.WHITE);
                btn.setOpaque(true);
                btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                final int fila = i;
                final int col = j;

                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (controller != null) {
                            controller.onCellClicked(fila, col);
                            actualizarVisual();
                        }
                    }
                });

                buttons[i][j] = btn;
                add(btn);
            }
        }
    }

    public void setController(MazeController controller) {
        this.controller = controller;
        controller.setMatriz(cells);
    }

    public Cell[][] getCells() {
        return cells;
    }

    public JButton getButton(int fila, int col) {
        return buttons[fila][col];
    }

    public void limpiarCeldasVisitadas() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                Cell cell = cells[i][j];
                if (cell.getState() == CellState.VISITED || cell.getState() == CellState.PATH) {
                    cell.setState(CellState.EMPTY);
                    buttons[i][j].setBackground(Color.WHITE);
                }
            }
        }
    }

    public MazePanel(Cell[][] cells, MazeController controller) {
        this.filas = cells.length;
        this.columnas = cells[0].length;
        this.cells = cells;
        this.controller = controller;

        this.buttons = new JButton[filas][columnas];
        setLayout(new GridLayout(filas, columnas));
        setPreferredSize(new Dimension(columnas * SIZE, filas * SIZE));

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                JButton btn = new JButton();
                btn.setBackground(Color.WHITE);
                btn.setOpaque(true);
                btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                final int fila = i;
                final int col = j;

                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (controller != null) {
                            controller.onCellClicked(fila, col);
                            actualizarVisual();
                        }
                    }
                });

                buttons[i][j] = btn;
                add(btn);
            }
        }

        controller.setMatriz(cells);
        actualizarVisual();
    }



    public void mostrarResultado(SolveResults resultado) {
        limpiarCeldasVisitadas();

        for (Cell cell : resultado.getVisited()) {
            if (cell != null && !cell.equals(controller.getStartCell()) && !cell.equals(controller.getEndCell())) {
                cells[cell.getRow()][cell.getCol()].setState(CellState.VISITED);
            }
        }

        for (Cell cell : resultado.getPath()) {
            if (cell != null && !cell.equals(controller.getStartCell()) && !cell.equals(controller.getEndCell())) {
                cells[cell.getRow()][cell.getCol()].setState(CellState.PATH);
            }
        }

        if (controller.getStartCell() != null) {
            Cell s = controller.getStartCell();
            cells[s.getRow()][s.getCol()].setState(CellState.START);
        }

        if (controller.getEndCell() != null) {
            Cell e = controller.getEndCell();
            cells[e.getRow()][e.getCol()].setState(CellState.END);
        }

        actualizarVisual();
    }


    public void resetear() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (cells[i][j].getState() != CellState.WALL) {
                    cells[i][j].setState(CellState.EMPTY);
                }
            }
        }
        actualizarVisual();
    }


    public void actualizarVisual() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                Cell cell = cells[i][j];
                JButton button = buttons[i][j];

                switch (cell.getState()) {
                    case EMPTY -> button.setBackground(Color.WHITE);
                    case WALL -> button.setBackground(Color.BLACK);
                    case START -> button.setBackground(Color.GREEN);
                    case END -> button.setBackground(Color.RED);
                    case VISITED -> button.setBackground(Color.CYAN);
                    case PATH -> button.setBackground(Color.BLUE);
                    default -> button.setBackground(Color.white);
                }
            }
        }
    }



    private void actualizarInicioYFin() {
        if (controller.getStartCell() != null) {
            Cell s = controller.getStartCell();
            buttons[s.getRow()][s.getCol()].setBackground(Color.GREEN);
        }
        if (controller.getEndCell() != null) {
            Cell e = controller.getEndCell();
            buttons[e.getRow()][e.getCol()].setBackground(Color.RED);
        }
    }

    public void generarNuevoLaberinto(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.cells = new Cell[filas][columnas];
        this.buttons = new JButton[filas][columnas];

        removeAll();
        setLayout(new GridLayout(filas, columnas));
        setPreferredSize(new Dimension(columnas * SIZE, filas * SIZE));

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                cells[i][j] = new Cell(i, j);
                cells[i][j].setState(CellState.EMPTY);

                JButton btn = new JButton();
                btn.setBackground(Color.WHITE);
                btn.setOpaque(true);
                btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                final int fila = i;
                final int col = j;

                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (controller != null) {
                            controller.onCellClicked(fila, col);
                            actualizarVisual();
                        }
                    }
                });

                buttons[i][j] = btn;
                add(btn);
            }
        }

        if (controller != null) {
            controller.setMatriz(cells);
        }

        revalidate();
        repaint();
    }
}