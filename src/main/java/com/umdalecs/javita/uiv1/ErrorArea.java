package com.umdalecs.javita.uiv1;

import javax.swing.*;
import java.awt.*;

public class ErrorArea extends JPanel {
    private final JTextArea errorArea;

    public ErrorArea() {
        super(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Zona de Errores Léxicos"));

        errorArea = new JTextArea();
        errorArea.setEditable(false);
        var scrollPane = new JScrollPane(errorArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void reset() {
        errorArea.setText("");
    }

    public void addText(String newText){
        errorArea.setText(errorArea.getText() + newText + "\n");
    }
}
