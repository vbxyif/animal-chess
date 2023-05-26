package view;


import model.PlayerColor;

import javax.swing.*;
import java.awt.*;

/**
 * This is the equivalent of the ChessPiece class,
 * but this class only cares how to draw Chess on ChessboardComponent
 */
public class ChessComponent extends JComponent {
    private final Name name;
    private final PlayerColor owner;
    private boolean selected;

    public ChessComponent(Name name, PlayerColor owner, int size) {
        this.name = name;
        this.owner = owner;
        this.selected = false;
        setSize(size / 2, size / 2);
        setLocation(0, 0);
        setVisible(true);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public PlayerColor getOwner() {
        return owner;
    }

    public String getName() {
        return name.toString();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        String name = this.name.toString();
        String color = this.owner.toString();
        Image image = new ImageIcon(String.format("src/animals/%s%s.png", name, color)).getImage();
        g2.drawImage(image, getWidth() / 6, getHeight() / 6, this);
        // FIXME: Use library to find the correct offset.--Done
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }


    public enum Name {
        鼠, 猫, 狗, 狼, 豹, 虎, 狮, 象,
    }
}
