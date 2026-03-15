package com.umdalecs.javitita.ui;

import com.umdalecs.javitita.compiler.lexer.Token;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class CodeArea extends JPanel {
    private final JTextPane codeField;
    public CodeArea() {
        super(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Programa:"));

        codeField = new JTextPane();
        codeField.setText("""
                fn main() {
                  int x;
                  x = 5;
                  while (x<10) {
                    println(x);
                    x = x + 1;
                  }
                }
                """);

        codeField.setFont(new Font("Hack", Font.PLAIN,42));
        var scrollPane = new JScrollPane(codeField);
        add(scrollPane, BorderLayout.CENTER);
    }

    public String getText() {
        return codeField.getText();
    }

//    public void resetStyles() {
//        StyledDocument doc = codeField.getStyledDocument();
//        Style defaultStyle = StyleContext.getDefaultStyleContext()
//                .getStyle(StyleContext.DEFAULT_STYLE);
//
//        doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, false);
//    }

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
        StyleConstants.setForeground(style, Color.BLUE);

        doc.setCharacterAttributes(token.absolutePos(), token.literal().length(), style, false);
    }

    public void markInteger(Token token) {
        StyledDocument doc = codeField.getStyledDocument();

        Style style = codeField.addStyle("IntegerStyle", null);
        StyleConstants.setForeground(style, Color.ORANGE);

        doc.setCharacterAttributes(token.absolutePos(), token.literal().length(), style, false);
    }

    public void markIdent(Token token) {
        StyledDocument doc = codeField.getStyledDocument();

        Style style = codeField.addStyle("IdentStyle", null);
        StyleConstants.setForeground(style, Color.MAGENTA);

        doc.setCharacterAttributes(token.absolutePos(), token.literal().length(), style, false);
    }
}
