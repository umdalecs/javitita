package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LexemArea extends JPanel {
    private final DefaultTableModel lexemModel;

    public LexemArea() {
        super(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Zona de Lexemas y Componentes Léxicos"));

        String[] columnas = {"Lexema", "Componente Léxico"};
        Object[][] datos = {};

        lexemModel = new DefaultTableModel(datos, columnas);
        var lexemasTable = new JTable(lexemModel);
        var scrollPane = new JScrollPane(lexemasTable);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void addRow(Object[] row) {
        this.lexemModel.addRow(row);
    }

    public void reset() {
        this.lexemModel.setRowCount(0);
    }
}


