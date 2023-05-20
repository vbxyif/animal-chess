package view;

import javax.swing.*;
import java.awt.*;

/**
 * This is the equivalent of the Cell class,
 * but this class only cares how to draw Cells on ChessboardComponent
 */

public class CellComponent extends JPanel {
    private final Color background;
    private final String name;
    private boolean validMove;

    public CellComponent(Color background, Point location, int size, String name) {
        setLayout(new GridLayout(1, 1));
        setLocation(location);
        setSize(size, size);
        this.background = background;
        this.name = name;
    }

    public boolean isValidMove() {
        return validMove;
    }

    public void setValidMove(boolean validMove) {
        this.validMove = validMove;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        g.setColor(background);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.lightGray);
        g.drawRect(0, 0, this.getWidth(), this.getHeight());
        Image image = new ImageIcon(String.format("C:/Users/Admin/Desktop/笔记本/Java作业/CS109-2023-Sping-ChessDemo (1)/src/animals/%s.png", name)).getImage();

        if (isValidMove()) {
            g.setColor(new Color(134, 231, 90));
            g.fillOval(0, 0, getWidth(), getHeight());
        }

        g.drawImage(image, getWidth() / 6, getHeight() / 6, this);
    }
}
