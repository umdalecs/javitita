package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SymbolArea extends JPanel {
    private final DefaultTableModel symbolModel;

    public SymbolArea() {
        super(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Tabla de Símbolos"));

        String[] columnas = {"Identificador", "Tipo", "Valor", "Posición"};
        Object[][] datos = {};

        symbolModel = new DefaultTableModel(datos, columnas);
        var simbolosTable = new JTable(symbolModel);
        var scrollPane = new JScrollPane(simbolosTable);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void addRow(Object[] row) {
        this.symbolModel.addRow(row);
    }

    public void reset() {
        this.symbolModel.setRowCount(0);
    }
}
