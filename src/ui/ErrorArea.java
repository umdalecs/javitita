package ui;

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

    public String getText() {
        return errorArea.getText();
    }

    public void setText(String newText){
        errorArea.setText(newText);
    }
}
