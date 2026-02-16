package com.umdalecs.javitita.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class CodeArea extends JPanel {
    private final JTextPane codeField;
    public CodeArea() {
        super(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Programa:"));



        codeField = new JTextPane();
        codeField.setText("""
                class Test{
                  void main(){
                    int x;
                    x = 5;
                    while (x<10){
                      println(x);
                      x = x + 1;
                    }
                  }
                }
                """);

        codeField.setFont(new Font("Fira Code", Font.PLAIN,42));
        var scrollPane = new JScrollPane(codeField);
        add(scrollPane, BorderLayout.CENTER);
    }

    public String getText() {
        return codeField.getText();
    }

    public void underlineText(int absolutePos, int length) {
        StyledDocument doc = codeField.getStyledDocument();

        Style style = codeField.addStyle("UnderlineStyle", null);
        StyleConstants.setUnderline(style, true);
        StyleConstants.setForeground(style, Color.RED);

        doc.setCharacterAttributes(absolutePos, length, style, false);
    }
}
