package view;

import javax.swing.*;
import java.awt.*;

public class PlayerFrame extends JFrame {
    private int WIDTH;
    private int HEIGTH;
    public PlayerFrame(int width, int height) {
        WIDTH = width;
        HEIGTH = height;
        setSize(WIDTH, HEIGTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        setLayout(new GridBagLayout());
    }

    private void addUserLabel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
    }
}
