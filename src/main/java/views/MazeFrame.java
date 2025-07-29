package views;

import controllers.MazeController;
import controllers.MazeController.Mode;
import models.Cell;
import models.SolveResults;
import models.CellState;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


public class MazeFrame extends JFrame {

    private MazeController controller;
    private MazePanel mazePanel;

    private JPanel mainPanel;
    private JComboBox<String> comboBox;
    private JButton resolverButton;
    private JButton pasoAPasoButton;
    private JButton limpiarButton;
    private JButton setStartButton;
    private JButton setEndButton;
    private JButton toggleWallButton;

    private List<Cell> pasoVisitados;
    private List<Cell> pasoCamino;
    private int pasoIndex = 0;

    public MazeFrame(MazeController controller, MazePanel mazePanel) {
        this.controller = controller;
        this.mazePanel = mazePanel;

        initComponents(controller.obtenerNombresAlgoritmos());
    }

    private void initComponents(Iterable<String> algoritmosDisponibles) {

        setTitle("Maze Creator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        agregarBarraMenu();

        mainPanel = new JPanel(new BorderLayout());

        
        JPanel topPanel = new JPanel();
        setStartButton = new JButton("Set Start");
        setEndButton = new JButton("Set End");
        toggleWallButton = new JButton("Toggle Wall");

        setStartButton.addActionListener(e -> controller.setMode(MazeController.Mode.START));
        setEndButton.addActionListener(e -> controller.setMode(MazeController.Mode.END));
        toggleWallButton.addActionListener(e -> controller.setMode(MazeController.Mode.WALL));

        topPanel.add(setStartButton);
        topPanel.add(setEndButton);
        topPanel.add(toggleWallButton);

        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Algoritmo:"));
        comboBox = new JComboBox<>();
        for (String alg : algoritmosDisponibles) {
            comboBox.addItem(alg);
        }
        resolverButton = new JButton("Resolver");
        pasoAPasoButton = new JButton("Paso a paso");
        limpiarButton = new JButton("Limpiar");

        resolverButton.addActionListener(this::resolver);
        pasoAPasoButton.addActionListener(this::pasoAPaso);
        limpiarButton.addActionListener(e -> {
            mazePanel.resetear();
            limpiarPasoAPaso();
        });

        bottomPanel.add(comboBox);
        bottomPanel.add(resolverButton);
        bottomPanel.add(pasoAPasoButton);
        bottomPanel.add(limpiarButton);

        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        mainPanel.add(mazePanel, BorderLayout.CENTER);

        this.setContentPane(mainPanel);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }


    private void agregarBarraMenu() {
    JMenuBar menuBar = new JMenuBar();

    
    JMenu archivoMenu = new JMenu("Archivo");
    JMenuItem nuevoItem = new JMenuItem("Nuevo Laberinto");
    JMenuItem resultadosItem = new JMenuItem("Ver Resultados");

   nuevoItem.addActionListener(e -> {
    try {
        String filasStr = JOptionPane.showInputDialog(this, "Número de filas:");
        String columnasStr = JOptionPane.showInputDialog(this, "Número de columnas:");

        if (filasStr == null || columnasStr == null) return;

        int filas = Integer.parseInt(filasStr);
        int columnas = Integer.parseInt(columnasStr);

        Cell[][] nuevasCeldas = new Cell[filas][columnas];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                nuevasCeldas[i][j] = new Cell(i, j);
            }
        }


        controller.setMatriz(nuevasCeldas);

        mainPanel.remove(mazePanel);

        
        mazePanel = new MazePanel(nuevasCeldas, controller);
        mainPanel.add(mazePanel, BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();

        limpiarPasoAPaso();
        controller.limpiarResultados();

        JOptionPane.showMessageDialog(this, "Nuevo laberinto creado y resultados limpiados.");

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Ingrese números válidos.");
    }
});



    resultadosItem.addActionListener(e -> {
    new ResultadosDialog(this, controller).setVisible(true);
});


    archivoMenu.add(nuevoItem);
    archivoMenu.add(resultadosItem);

   
    JMenu ayudaMenu = new JMenu("Ayuda");
    JMenuItem acercaDeItem = new JMenuItem("Acerca de");

    acercaDeItem.addActionListener(e -> {
        JOptionPane.showMessageDialog(this,
                "Sistema de Resolución de Laberintos\nVersión 1.0\nAutor: Sebastian Ceron-Mateo Morejon-Elkin Chamba",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    });

    ayudaMenu.add(acercaDeItem);

    menuBar.add(archivoMenu);
    menuBar.add(ayudaMenu);
    setJMenuBar(menuBar);
}


    private void resolver(ActionEvent e) {
    SolveResults resultado = ejecutarAlgoritmo();
    if (resultado != null) {
        for (int i = 0; i < mazePanel.getCells().length; i++) {
    for (int j = 0; j < mazePanel.getCells()[0].length; j++) {
        Cell c = mazePanel.getCells()[i][j];
        CellState estado = c.getState();

        if (estado != CellState.START && estado != CellState.END && estado != CellState.WALL) {
            c.setState(CellState.EMPTY);
        }
    }
}
mazePanel.actualizarVisual();
        pasoVisitados = resultado.getVisited().stream().toList();
        pasoCamino = resultado.getPath();
        pasoIndex = 0;

   Timer timer = new Timer(50, null);
timer.addActionListener(ev -> {
    if (pasoIndex < pasoVisitados.size()) {
        Cell c = pasoVisitados.get(pasoIndex++);
        CellState estado = mazePanel.getCells()[c.getRow()][c.getCol()].getState();
        if (estado != CellState.START && estado != CellState.END) {
            mazePanel.getCells()[c.getRow()][c.getCol()].setState(CellState.VISITED);
        }
    } else if (pasoIndex - pasoVisitados.size() < pasoCamino.size()) {
    int idx = pasoIndex - pasoVisitados.size();
    int reverseIdx = pasoCamino.size() - 1 - idx;
    Cell c = pasoCamino.get(reverseIdx);
    CellState estado = mazePanel.getCells()[c.getRow()][c.getCol()].getState();
    if (estado != CellState.START && estado != CellState.END) {
        mazePanel.getCells()[c.getRow()][c.getCol()].setState(CellState.PATH);
    }
    pasoIndex++;
    } else {
        timer.stop();
        limpiarPasoAPaso();
    }
    mazePanel.actualizarVisual();
});
timer.start();
    }
}


    private SolveResults ejecutarAlgoritmo() {
        String algoritmo = (String) comboBox.getSelectedItem();
        boolean[][] matriz = controller.getMatriz();
        Cell inicio = controller.getStartCell();
        Cell fin = controller.getEndCell();

        if (inicio == null || fin == null) {
            JOptionPane.showMessageDialog(this, "Debe establecer los puntos de inicio y fin.");
            return null;
        }

        return controller.ejecutar(algoritmo, matriz, inicio, fin);
    }

private void pasoAPaso(ActionEvent e) {
    if (pasoCamino == null) {
        SolveResults resultado = ejecutarAlgoritmo();
        if (resultado != null) {
            
            for (int i = 0; i < mazePanel.getCells().length; i++) {
                for (int j = 0; j < mazePanel.getCells()[0].length; j++) {
                    Cell c = mazePanel.getCells()[i][j];
                    CellState estado = c.getState();

                    if (estado != CellState.START && estado != CellState.END && estado != CellState.WALL) {
                        c.setState(CellState.EMPTY);
                    }
                }
            }

            mazePanel.actualizarVisual();

            pasoVisitados = resultado.getVisited().stream().toList();
            pasoCamino = new ArrayList<>(resultado.getPath());
Collections.reverse(pasoCamino);
            pasoIndex = 0;
        }
    } else {
        
        if (pasoIndex < pasoVisitados.size()) {
            Cell c = pasoVisitados.get(pasoIndex++);
            CellState estado = mazePanel.getCells()[c.getRow()][c.getCol()].getState();

            if (estado != CellState.START && estado != CellState.END) {
                mazePanel.getCells()[c.getRow()][c.getCol()].setState(CellState.VISITED);
            }

            mazePanel.actualizarVisual();
        }
        
        else if (pasoIndex < pasoVisitados.size() + pasoCamino.size()) {
            int idx = pasoIndex - pasoVisitados.size();
            Cell c = pasoCamino.get(idx);
            pasoIndex++;

            CellState estado = mazePanel.getCells()[c.getRow()][c.getCol()].getState();
            if (estado != CellState.START && estado != CellState.END) {
                mazePanel.getCells()[c.getRow()][c.getCol()].setState(CellState.PATH);
            }

            mazePanel.actualizarVisual();
        }
    
        else {
            JOptionPane.showMessageDialog(this, "Paso a paso finalizado.");
            limpiarPasoAPaso();
        }
    }
}


    private void limpiarPasoAPaso() {
        pasoVisitados = null;
        pasoCamino = null;
        pasoIndex = 0;
    }
}