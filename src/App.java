import ui.Window;

import javax.swing.*;

void main() {
    SwingUtilities.invokeLater(() -> {
        var gui = new Window();
        gui.setVisible(true);
    });
}
