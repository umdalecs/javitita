package com.umdalecs.javitita.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ErrorArea extends JPanel {
    private final JTextPane textField;
    public ErrorArea() {
        super(new BorderLayout());

        textField = new JTextPane();
        textField.setEditable(false);

        textField.setFont(new Font("Hack", Font.PLAIN,42));
        var scrollPane = new JScrollPane(textField);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setText(String text) {
        textField.setText(text);
    }

}
