package ui;

import javax.swing.*;
import java.awt.*;

public class CodeArea extends JPanel {
    private final JTextArea codeField;
    public CodeArea() {
        super(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Editor de Código (Zona de Entrada)"));

        codeField = new JTextArea();
        codeField.setText("""
                class Test {
                  void main() {
                    int x ;
                    x = 5 ;
                      while ( x < 10 ) {
                        println( x ) ;
                        x = x + 1 ;
                      }
                  }
                }
                """);
        var scrollPane = new JScrollPane(codeField);
        add(scrollPane, BorderLayout.CENTER);
    }

    public String getText() {
        return codeField.getText();
    }
}
