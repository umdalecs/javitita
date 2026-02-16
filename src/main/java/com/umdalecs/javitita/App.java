package com.umdalecs.javitita;

import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;
import com.umdalecs.javitita.ui.Window;

public class App {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            var gui = new Window();
            gui.setVisible(true);
        });
    }
}
