import view.BeginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BeginFrame beginFrame = new BeginFrame(1100, 800);
            beginFrame.setVisible(true);
        });
    }
}
