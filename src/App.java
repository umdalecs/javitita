import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatLightLaf;

import ui.LexerWindow;

public class App {
    public static void main(String[] args) throws Exception {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            var gui = new LexerWindow();
            gui.setVisible(true);
        });
    }
}
