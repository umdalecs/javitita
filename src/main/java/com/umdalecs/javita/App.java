package com.umdalecs.javita;

import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;
import com.umdalecs.javita.uiv2.Window;

public class App {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            var gui = new Window();
            gui.setVisible(true);
        });
    }
}
