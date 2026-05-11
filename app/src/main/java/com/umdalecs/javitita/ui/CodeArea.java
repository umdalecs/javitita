package com.umdalecs.javitita.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.umdalecs.javitita.compiler.Token;

public class CodeArea extends JPanel {
    private final JTextPane codeField;
    private final JTextArea lineNumbers;

    public CodeArea() {
        super(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Programa:"));

        var mainFont = new Font("Hack", Font.PLAIN, 24);
        lineNumbers = new JTextArea("1");
        lineNumbers.setBackground(new Color(230, 230, 230));
        lineNumbers.setForeground(Color.GRAY);
        lineNumbers.setEditable(false);
        lineNumbers.setFont(mainFont);
        lineNumbers.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        codeField = new JTextPane();
        codeField.setText("""
                fn main() {
                  int x;
                  x = 5;
                  while (x<10) {
                    int y;
                    y = x;
                    while(y<10) {
                        println(y);
                        y = y + 1;
                    }
                    x = x + 1;
                  }
                }
                """);

        codeField.setFont(mainFont);

        codeField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateLineNumbers() {
                int lines = codeField.getDocument().getDefaultRootElement().getElementCount();

                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= lines; i++) {
                    sb.append(i).append("\n");
                }
                lineNumbers.setText(sb.toString());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateLineNumbers();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateLineNumbers();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateLineNumbers();
            }
        });

        updateInitialLineNumbers();

        var scrollPane = new JScrollPane(codeField);
        scrollPane.setRowHeaderView(lineNumbers);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void updateInitialLineNumbers() {
        int lines = codeField.getDocument().getDefaultRootElement().getElementCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lines; i++) {
            sb.append(i).append("\n");
        }
        lineNumbers.setText(sb.toString());
    }

    public String getText() {
        return codeField.getText();
    }

    public void setText(String text) {
        codeField.setText(text);
    }

    public void markError(Token token) {
        StyledDocument doc = codeField.getStyledDocument();

        Style style = codeField.addStyle("ErrorStyle", null);
        StyleConstants.setUnderline(style, true);
        StyleConstants.setForeground(style, Color.RED);

        doc.setCharacterAttributes(token.absolutePos(), token.literal().length(), style, false);
    }

    public void markMissing(int pos) {
        StyledDocument doc = codeField.getStyledDocument();

        Style style = codeField.addStyle("MissingStyle", null);
        StyleConstants.setUnderline(style, true);
        StyleConstants.setForeground(style, Color.RED);

        doc.setCharacterAttributes(pos, 1, style, false);
    }

    public void markKeyword(Token token) {
        StyledDocument doc = codeField.getStyledDocument();

        Style style = codeField.addStyle("KeywordStyle", null);
        StyleConstants.setForeground(style, new Color(215, 58, 73));

        doc.setCharacterAttributes(token.absolutePos(), token.literal().length(), style, false);
    }

    public void markInteger(Token token) {
        StyledDocument doc = codeField.getStyledDocument();

        Style style = codeField.addStyle("IntegerStyle", null);
        StyleConstants.setForeground(style, new Color(0, 92, 197));

        doc.setCharacterAttributes(token.absolutePos(), token.literal().length(), style, false);
    }

    public void markIdent(Token token) {
        StyledDocument doc = codeField.getStyledDocument();

        Style style = codeField.addStyle("IdentStyle", null);
        StyleConstants.setForeground(style, new Color(111, 66, 193));

        doc.setCharacterAttributes(token.absolutePos(), token.literal().length(), style, false);
    }
}
