package view;

import controller.GameController;
import model.Chessboard;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class BeginFrame extends JFrame {
    public final int WIDTH;
    public final int HEIGHT;
    private final JLayeredPane layeredPane;

    public BeginFrame(int WIDTH, int HEIGHT) {
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;

        setSize(this.WIDTH, this.HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(null);

        layeredPane = new JLayeredPane();
        layeredPane.setSize(WIDTH, HEIGHT);
        layeredPane.setLayout(new GridBagLayout());

        JLabel tittleLabel = new JLabel(new ImageIcon("src/animals/logo.png"), JLabel.CENTER);
        tittleLabel.setSize(WIDTH, HEIGHT);


        addBeginButton();
        addExitButton();
        add(layeredPane);
        add(tittleLabel);
    }

    public void addBeginButton() {
        JButton beginButton = new JButton("开始游戏");

        beginButton.addActionListener(e -> {
            this.dispose();
            ChessGameFrame mainFrame = new ChessGameFrame(1100, 810);
            GameController gameController;
            try {
                gameController = new GameController(ChessGameFrame.getChessboardComponent(), new Chessboard(), new MessageText("1", Color.BLUE));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            mainFrame.addRoundText(gameController.getRoundText());
            mainFrame.setVisible(true);
        });

        beginButton.setFont(new Font("Black", Font.BOLD, 30));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        layeredPane.setLayer(beginButton, 0);
        layeredPane.add(beginButton, constraints);
    }

    private void addExitButton() {
        //add a button to exit the game:
        JButton exitButton = new JButton("退出游戏");

        exitButton.addActionListener(e -> this.dispose());

        exitButton.setFont(new Font("Rockwell", Font.BOLD, 30));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = new Insets(10, 10, 10, 10);
        layeredPane.setLayer(exitButton, 0);
        layeredPane.add(exitButton, constraints);
    }

}
