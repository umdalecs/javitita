import com.formdev.flatlaf.FlatLightLaf;
import ui.LexerWindow;

import javax.swing.*;

void main() {
    FlatLightLaf.setup();

    SwingUtilities.invokeLater(() -> {
        var gui = new LexerWindow();
        gui.setVisible(true);
    });
}
