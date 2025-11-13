import ui.LexerWindow;

import javax.swing.*;

void main() {
    SwingUtilities.invokeLater(() -> {
        var gui = new LexerWindow();
        gui.setVisible(true);
    });
}
