package com.umdalecs.javitita.ui;

import javax.swing.*;
import java.awt.*;

public class IntermediateCodeArea  extends JPanel {
    private final JTextPane textField;
    public IntermediateCodeArea() {
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
