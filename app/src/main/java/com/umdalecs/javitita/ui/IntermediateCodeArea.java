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

public class IntermediateCodeArea extends JPanel {
    private final JTextPane textField;
    private final JTextArea lineNumbers;


    public IntermediateCodeArea() {
        super(new BorderLayout());

        textField = new JTextPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return getParent() != null ? (getUI().getPreferredSize(this).width <= getParent().getSize().width) : true;
            }
        };
        textField.setEditable(true);

        var mainFont = new Font("Hack", Font.PLAIN, 24);

        textField.setFont(mainFont);

        lineNumbers = new JTextArea("1");
        lineNumbers.setBackground(new Color(230, 230, 230));
        lineNumbers.setForeground(Color.GRAY);
        lineNumbers.setEditable(false);
        lineNumbers.setFont(mainFont);
        lineNumbers.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));


        textField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateLineNumbers() {
                int lines = textField.getDocument().getDefaultRootElement().getElementCount();

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

        var scrollPane = new JScrollPane(textField);
        scrollPane.setRowHeaderView(lineNumbers);

        add(scrollPane, BorderLayout.CENTER);

    }

    private void updateInitialLineNumbers() {
        int lines = textField.getDocument().getDefaultRootElement().getElementCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lines; i++) {
            sb.append(i).append("\n");
        }
        lineNumbers.setText(sb.toString());
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public String getText() {
        return textField.getText();
    }
}
