package com.umdalecs.javitita.ui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class IntermediateCodeArea extends JPanel {
    private final JTextPane textField;

    public IntermediateCodeArea() {
        super(new BorderLayout());

        textField = new JTextPane();
        textField.setEditable(true);

        textField.setFont(new Font("Hack", Font.PLAIN, 24));
        var scrollPane = new JScrollPane(textField);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public String getText() {
        return textField.getText();
    }
}
