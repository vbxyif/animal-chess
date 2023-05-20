package view;


import controller.GameController;
import model.Chessboard;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 这个类表示游戏过程中的整个游戏界面，是一切的载体
 */
public class ChessGameFrame extends JFrame {
    private static ChessboardComponent chessboardComponent;
    //    public final Dimension FRAME_SIZE ;
    private final int WIDTH;
    private final int HEIGTH;
    private final int ONE_CHESS_SIZE;

    public ChessGameFrame(int width, int height) {
        setTitle("2023 CS109 Project Demo"); //设置标题
        this.WIDTH = width;
        this.HEIGTH = height;
        this.ONE_CHESS_SIZE = (HEIGTH * 4 / 5) / 9;

        setSize(WIDTH, HEIGTH);
        setLocationRelativeTo(null); // Center the window.
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);

        addChessboard();
        addBackground();
        addAgainButton();
        addRuleButton();
    }

    public static ChessboardComponent getChessboardComponent() {
        return chessboardComponent;
    }

    public void setChessboardComponent(ChessboardComponent chessboardComponent) {
        ChessGameFrame.chessboardComponent = chessboardComponent;
    }

    private void addBackground() {
        ImageIcon bgp = new ImageIcon("C:\\Users\\Admin\\Desktop\\笔记本\\Java作业\\CS109-2023-Sping-ChessDemo (1)\\resource\\background.jpg");
        JLabel bgl = new JLabel(bgp);
        bgl.setSize(WIDTH, HEIGTH);
        this.getLayeredPane().add(bgl, JLayeredPane.DEFAULT_LAYER);
    }

    /**
     * 在游戏面板中添加棋盘
     */
    private void addChessboard() {
        setChessboardComponent(new ChessboardComponent(ONE_CHESS_SIZE));
        chessboardComponent.setLocation(HEIGTH / 5, HEIGTH / 10);
        this.getLayeredPane().add(chessboardComponent, JLayeredPane.MODAL_LAYER);
    }

    /**
     * 在游戏面板中添加标签
     */
    /*private void addLabel() {
        JLabel statusLabel = new JLabel();
        statusLabel.setLocation(HEIGTH, HEIGTH / 10);
        statusLabel.setSize(200, 60);
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 40));
        this.getLayeredPane().add(statusLabel,JLayeredPane.MODAL_LAYER);
    }*/
    public void addRoundText(MessageText roundText) {
        roundText.setOpaque(false);
        roundText.setBorder(null);
        roundText.setText(roundText.getText());
        roundText.setSize(300, 100);
        roundText.setFont(new Font("Black", Font.BOLD, 100));
        roundText.setLocation(HEIGTH, HEIGTH / 10);
        roundText.setEditable(false);
        this.getLayeredPane().add(roundText, JLayeredPane.MODAL_LAYER);
    }

    private void addRuleButton() {
        JButton ruleButton = new JButton("规则");
        ruleButton.addActionListener(e -> {
            try {
                StringBuilder messageText = new StringBuilder();
                String filePath = "src/rule/Rules.txt";
                FileInputStream fin = new FileInputStream(filePath);
                InputStreamReader reader = new InputStreamReader(fin);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String strTmp;
                while ((strTmp = bufferedReader.readLine()) != null) {
                    messageText.append("\n").append(strTmp);
                    //System.out.println(strTmp);
                }
                //JOptionPane.showMessageDialog(null, messageText,"规则",JOptionPane.INFORMATION_MESSAGE);
                {
                    TextFrame ruleFrame = new TextFrame(600, 800);
                    ruleFrame.setTitle("规则");
                    ruleFrame.addText(messageText.toString(), ruleFrame.getWidth() - 20, ruleFrame.getHeight() - 20, Color.BLACK);
                    ruleFrame.setVisible(true);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        ruleButton.setLocation(HEIGTH, HEIGTH / 10 + 180);
        ruleButton.setSize(200, 60);
        ruleButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        this.getLayeredPane().add(ruleButton, JLayeredPane.MODAL_LAYER);
    }

    /**
     * 在游戏面板中增加一个按钮，如果按下的话就会显示Hello, world!
     */

    private void addAgainButton() {
        JButton button = new JButton("重新开始");
        button.addActionListener((e) -> {
            this.dispose();
            ChessGameFrame mainFrame = new ChessGameFrame(1100, 810);
            GameController gameController = new GameController(getChessboardComponent(), new Chessboard(), new MessageText("1", Color.BLUE));
            mainFrame.addRoundText(gameController.getRoundText());
            mainFrame.setVisible(true);
        });
        button.setLocation(HEIGTH, HEIGTH / 10 + 120);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        this.getLayeredPane().add(button, JLayeredPane.MODAL_LAYER);
    }
    //private void addLoadButton() {
    //JButton button = new JButton("Load");
    //button.setLocation(HEIGTH, HEIGTH / 10 + 240);
    //button.setSize(200, 60);
    //button.setFont(new Font("Rockwell", Font.BOLD, 20));
    //add(button);
    //
    //button.addActionListener(e -> {
    //System.out.println("Click load");
    // String path = JOptionPane.showInputDialog(this,"Input Path here");
    // gameController.loadGameFromFile(path);
    //  });
    //}


}
