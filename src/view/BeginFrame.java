package view;

import controller.GameController;
import model.Chessboard;

import javax.swing.*;
import java.awt.*;

public class BeginFrame extends JFrame {
    public final int WIDTH;
    public final int HEIGHT;

    public BeginFrame(int WIDTH, int HEIGHT) {
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;

        setSize(this.WIDTH, this.HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(null);

        addTittleLabel();
        addBeginButton();
    }

    public void addTittleLabel() {
        JLabel tittleLabel = new JLabel(new ImageIcon("C:\\Users\\Admin\\Desktop\\笔记本\\Java作业\\CS109-2023-Sping-ChessDemo (1)\\resource\\logo.png"), JLabel.CENTER);
        tittleLabel.setSize(WIDTH, HEIGHT);
        add(tittleLabel);
    }

    public void addBeginButton() {
        JButton beginButton = new JButton("开始游戏");
        beginButton.addActionListener(e -> {
            this.dispose();
            ChessGameFrame mainFrame = new ChessGameFrame(1100, 810);
            GameController gameController = new GameController(ChessGameFrame.getChessboardComponent(), new Chessboard(), new MessageText("1", Color.BLUE));
            mainFrame.addRoundText(gameController.getRoundText());
            mainFrame.setVisible(true);
        });
        beginButton.setSize(200, 60);
        beginButton.setLocation((WIDTH - 200) / 2, HEIGHT / 2);
        add(beginButton);
    }
}
