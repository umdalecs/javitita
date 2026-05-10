package com.umdalecs.javitita.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ObjectCodeArea extends JPanel {

    private final JTextArea textField;
    private final JTextArea lineNumbers;

    public ObjectCodeArea() {
        super(new BorderLayout());

        textField = new JTextArea();
        textField.setEditable(false);
        textField.setLineWrap(false);

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

        var headerPanel = new JPanel(new java.awt.GridLayout(1, 2));
        headerPanel.setBackground(new Color(230, 230, 230));

        var offsetLabel = new JLabel("OFFSET");
        offsetLabel.setFont(mainFont);
        offsetLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        var valueLabel = new JLabel("VALUE");
        valueLabel.setFont(mainFont);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        headerPanel.add(offsetLabel);
        headerPanel.add(valueLabel);

        var headerViewport = new javax.swing.JViewport() {
            @Override
            public void setViewPosition(java.awt.Point p) {
                super.setViewPosition(new java.awt.Point(0, p.y));
            }
        };
        headerViewport.setView(headerPanel);
        scrollPane.setColumnHeader(headerViewport);

        var corner = new JPanel();
        corner.setBackground(new Color(230, 230, 230));
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, corner);
        
        scrollPane.getViewport().setBackground(textField.getBackground());

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

}
