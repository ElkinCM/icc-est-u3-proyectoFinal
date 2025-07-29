package views;

import controllers.MazeController;
import models.AlgorithmResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ResultadosDialog extends JDialog {

    private final MazeController controller;
    private JTable tabla;
    private DefaultTableModel tableModel;
    private List<AlgorithmResult> resultados;

    public ResultadosDialog(JFrame parent, MazeController controller) {
        super(parent, "Resultados de Algoritmos", true);
        this.controller = controller;

        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        initComponents();
        pack(); 
    }

    private void initComponents() {
        // Tabla
        tableModel = new DefaultTableModel(new String[]{"Algoritmo", "Tiempo (ms)", "Longitud del camino"}, 0);
        tabla = new JTable(tableModel);
        tabla.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setPreferredSize(new Dimension(700, 200));
        add(scrollPane, BorderLayout.CENTER);

        
        JButton btnLimpiar = new JButton("Limpiar Resultados");
        JButton btnMostrarGrafica = new JButton("Mostrar Gráfica");
        btnMostrarGrafica.setBackground(Color.ORANGE); 

        btnLimpiar.addActionListener(e -> {
            controller.limpiarResultados();
            actualizar();
        });

        btnMostrarGrafica.addActionListener(e -> mostrarVentanaGrafica());

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        southPanel.add(btnLimpiar);
        southPanel.add(btnMostrarGrafica);
        add(southPanel, BorderLayout.SOUTH);

        actualizar();
    }

    private void actualizar() {
        resultados = controller.listarResultados();
        tableModel.setRowCount(0);

        for (AlgorithmResult r : resultados) {
            tableModel.addRow(new Object[]{
                    r.getAlgorithmName(),
                    r.getExecutionTimeNanos(),
                    r.getPathLength()
            });
        }
    }

    private void mostrarVentanaGrafica() {
        if (resultados == null || resultados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos para graficar.");
            return;
        }

        JDialog graficaDialog = new JDialog(this, "Gráfico de Tiempos", true);
        graficaDialog.setSize(700, 400);
        graficaDialog.setLocationRelativeTo(this);

        JPanel panelGrafico = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGrafico(g, getWidth(), getHeight());
            }
        };

        graficaDialog.add(panelGrafico);
        graficaDialog.setVisible(true);
    }

    private void dibujarGrafico(Graphics g, int width, int height) {

    if (resultados == null || resultados.isEmpty()) {
    g.setColor(Color.BLACK);
    g.setFont(new Font("Arial", Font.BOLD, 16));
    g.drawString("No hay datos para mostrar", 100, 100);
    return;
}

    int marginLeft = 80;
    int marginTop = 60;
    int marginBottom = 60;
    int marginRight = 40;

    int usableWidth = width - marginLeft - marginRight;
    int usableHeight = height - marginTop - marginBottom;
    int baseY = height - marginBottom;

    long maxTime = resultados.stream()
            .mapToLong(AlgorithmResult::getExecutionTimeNanos)
            .max()
            .orElse(1);

    int numPoints = resultados.size();
    int stepX = usableWidth / (numPoints + 1);

    // Fondo
    g.setColor(new Color(230, 230, 230));
    g.fillRect(marginLeft, marginTop, usableWidth, usableHeight);

    // Líneas guía y escala Y
    g.setFont(new Font("Arial", Font.PLAIN, 12));
    int lines = 10;
    g.setColor(Color.GRAY);
    for (int i = 0; i <= lines; i++) {
        int y = marginTop + i * (usableHeight / lines);
        g.drawLine(marginLeft, y, marginLeft + usableWidth, y);

        long labelValue = (long) ((1 - i / (double) lines) * maxTime);
        String label = String.format("%,d", labelValue);
        g.setColor(Color.BLACK);
        g.drawString(label, marginLeft - 60, y + 5);
        g.setColor(Color.GRAY);
    }


    g.setColor(Color.BLACK);
    g.drawLine(marginLeft, baseY, marginLeft + usableWidth, baseY); 
    g.drawLine(marginLeft, marginTop, marginLeft, baseY); 


    g.setFont(new Font("Arial", Font.BOLD, 16));
    g.drawString("Tiempos de Ejecución por Algoritmo", marginLeft + 100, 40);

    
    g.setFont(new Font("Arial", Font.PLAIN, 12));
    g.drawString("Algoritmo", marginLeft + usableWidth / 2 - 30, height - 20);
    g.drawString("Tiempo (ns)", 10, marginTop - 10);

    int[] puntosX = new int[numPoints];
    int[] puntosY = new int[numPoints];

    for (int i = 0; i < numPoints; i++) {
        AlgorithmResult r = resultados.get(i);
        puntosX[i] = marginLeft + (i + 1) * stepX;
        puntosY[i] = (int) (baseY - (r.getExecutionTimeNanos() * 1.0 / maxTime) * usableHeight);
    }


    g.setColor(Color.RED);
    for (int i = 0; i < numPoints - 1; i++) {
        g.drawLine(puntosX[i], puntosY[i], puntosX[i + 1], puntosY[i + 1]);
    }

    for (int i = 0; i < numPoints; i++) {
        AlgorithmResult r = resultados.get(i);
        int x = puntosX[i];
        int y = puntosY[i];

      
        g.fillOval(x - 3, y - 3, 6, 6);

       
        String nombre = r.getAlgorithmName();
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.BLACK);
        g.drawString(nombre, x - fm.stringWidth(nombre) / 2, baseY + 15);

        
        String valor = r.getExecutionTimeNanos() + " ns";
        g.drawString(valor, x - fm.stringWidth(valor) / 2, y - 10);
    }

    
    g.setColor(Color.RED);
    g.drawLine(marginLeft, baseY + 30, marginLeft + 20, baseY + 30);
    g.setColor(Color.BLACK);
    g.drawString("Tiempo(ns)", marginLeft + 25, baseY + 35);
}


}
