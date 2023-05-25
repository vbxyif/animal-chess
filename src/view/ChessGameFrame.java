package view;


import controller.GameController;
import model.Chessboard;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * 这个类表示游戏过程中的整个游戏界面，是一切的载体
 */
public class ChessGameFrame extends JFrame {
    private static ChessboardComponent chessboardComponent;
    private final int WIDTH;
    private final int HEIGTH;
    private JDialog dialog;
    private MessageText roundText;

    public ChessGameFrame(int width, int height) {
        setTitle("2023 CS109 Project Animal Chess"); //设置标题
        this.WIDTH = width;
        this.HEIGTH = height;

        int ONE_CHESS_SIZE = (HEIGTH * 4 / 5) / 9;

        setSize(WIDTH, HEIGTH);
        setLocationRelativeTo(null); // Center the window.
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setChessboardComponent(new ChessboardComponent(ONE_CHESS_SIZE));
        setLayout(new FlowLayout());

        addChessboard();
        addBackground();

        addAgainButton();
        addRuleButton();
        addSaveButton();
        addLoadButton();
        addExitButton();
        addUndoButton();
    }

    public static ChessboardComponent getChessboardComponent() {
        return chessboardComponent;
    }

    public void setChessboardComponent(ChessboardComponent chessboardComponent) {
        ChessGameFrame.chessboardComponent = chessboardComponent;
    }
    /**在面板中添加背景
     * */
    private void addBackground() {
        ImageIcon bgp = new ImageIcon("src/animals/background.jpg");
        JLabel bgl = new JLabel(bgp);
        bgl.setSize(WIDTH, HEIGTH);
        this.getLayeredPane().add(bgl, JLayeredPane.DEFAULT_LAYER);
    }

    /**
     * 在游戏面板中添加棋盘
     */
    private void addChessboard() {
        chessboardComponent.setLocation(HEIGTH / 5, HEIGTH / 10);
        this.getLayeredPane().add(chessboardComponent, JLayeredPane.MODAL_LAYER);
    }

    /**
     * 在游戏面板中添加标签
     */
    public void addRoundText(MessageText roundText) {
        roundText.setOpaque(false);
        roundText.setBorder(null);
        roundText.setText(roundText.getText());
        roundText.setSize(300, 100);
        roundText.setFont(new Font("Black", Font.BOLD, 100));
        roundText.setLocation(HEIGTH, HEIGTH / 10);
        roundText.setEditable(false);
        this.roundText = roundText;
        this.getLayeredPane().add(this.roundText, JLayeredPane.MODAL_LAYER);
    }

    private void addSaveText() {
        dialog = new JDialog();
        dialog.setTitle("命名存档");
        dialog.setSize(WIDTH / 3, HEIGTH / 10);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        JTextField textField = new JTextField();
        textField.setFont(new Font("Black", Font.BOLD, 15));
        textField.setSize(WIDTH / 4, HEIGTH / 14);
        dialog.add(textField, BorderLayout.CENTER);
        JButton saveButton = new JButton("保存");
        saveButton.setFont(new Font("Black", Font.BOLD, 15));
        saveButton.setSize(WIDTH / 10, HEIGTH / 14);
        saveButton.addActionListener(e -> {
            try {
                if (chessboardComponent.getGameController().save(textField.getText())) {
                    dialog.dispose();
                    JOptionPane.showMessageDialog(null, "保存成功");
                } else {
                    JOptionPane.showMessageDialog(null, "保存失败，请重新命名");
                    textField.setText("");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "保存失败");
                throw new RuntimeException(ex);
            }
        });
        dialog.add(saveButton, BorderLayout.EAST);
    }

    /**Create a button named saveButton*/
    private void addSaveButton() {
        //*Create a button named saveButton*//
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            addSaveText();
            dialog.setVisible(true);
        });
        saveButton.setLocation(HEIGTH, HEIGTH / 10 + 260);
        saveButton.setSize(200, 60);
        saveButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        this.getLayeredPane().add(saveButton, JLayeredPane.MODAL_LAYER);
    }

    private void addLoadDialog() {
        /*Create a dialog to read a save file
         */
        dialog = new JDialog();
        dialog.setTitle("读取存档");
        dialog.setSize(WIDTH / 3, HEIGTH / 2);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        JList<String> list = new JList<>();
        DefaultListModel<String> model = new DefaultListModel<>();
        File[] files = new File("src/saves").listFiles();
        String[] fileNames;
        if (files != null) {
            fileNames = new String[files.length];
        } else {
            fileNames = new String[0];
        }
        int i = 0;
        if (files != null) {
            for (File file : files) {
                if (!file.getName().endsWith(".txt")) {
                    continue;
                }
                model.addElement(file.getName());
                fileNames[i++] = file.getName();
            }
        }
        list.setModel(model);
        list.setBorder(BorderFactory.createTitledBorder("选择存档"));
        list.setFont(new Font("Black", Font.BOLD, 18));
        final String[] str = {""};
        list.addListSelectionListener(e -> str[0] = fileNames[list.getSelectedIndex()]);
        JScrollPane scrollPane = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setSize(WIDTH / 3, HEIGTH / 3);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout());
        JButton okButton = new JButton("确定");
        okButton.setSize(WIDTH / 10, HEIGTH / 6);
        okButton.addActionListener(e -> {
            try {
                GameController gameController = againController();
                gameController.load("src/saves/" + str[0]);
                dialog.dispose();
                JOptionPane.showMessageDialog(null, "加载成功");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "加载失败");
                throw new RuntimeException(ex);
            }
        });
        buttonPane.add(okButton);
        JButton deleteButton = new JButton("删除");
        deleteButton.setSize(WIDTH / 10, HEIGTH / 6);
        deleteButton.addActionListener(e -> {
            try {
                GameController gameController = againController();
                if (gameController.delete("src/saves/" + str[0])) {
                    dialog.dispose();
                    JOptionPane.showMessageDialog(null, "删除成功");
                } else {
                    JOptionPane.showMessageDialog(null, "删除失败");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "删除失败");
                throw new RuntimeException(ex);
            }
        });
        buttonPane.add(deleteButton);
        dialog.add(buttonPane, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    /**create a button named loadButton
     * click the button to load
     * */
    private void addLoadButton() {
        JButton loadButton = new JButton("加载");
        loadButton.addActionListener(e -> addLoadDialog());
        loadButton.setLocation(HEIGTH, HEIGTH / 10 + 330);
        loadButton.setSize(200, 60);
        loadButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        this.getLayeredPane().add(loadButton, JLayeredPane.MODAL_LAYER);
    }
    /**create a button named undoButton
     * click the button to undo
     * */
    private void addUndoButton() {
        JButton undoButton = new JButton("悔棋");
        undoButton.addActionListener(e -> {
            if (chessboardComponent.getGameController().canUndo()) {
                GameController gameController;
                try {
                    gameController = againController();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                gameController.undo();
            } else {
                JOptionPane.showMessageDialog(null, "悔棋失败");
            }
        });
        undoButton.setLocation(HEIGTH, HEIGTH / 10 + 480);
        undoButton.setSize(200, 60);
        undoButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        this.getLayeredPane().add(undoButton, JLayeredPane.MODAL_LAYER);
    }

    /**create a button named ruleButton
     * click the button to open the rule
     * */
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
                }
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
        ruleButton.setLocation(HEIGTH, HEIGTH / 10 + 190);
        ruleButton.setSize(200, 60);
        ruleButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        this.getLayeredPane().add(ruleButton, JLayeredPane.MODAL_LAYER);
    }

    /**
     * 在游戏面板中增加一个按钮，如果按下的话就会重置游戏
     */

    private void addAgainButton() {
        JButton againButton = new JButton("重新开始");
        againButton.addActionListener((e) -> {
            try {
                againController();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        againButton.setLocation(HEIGTH, HEIGTH / 10 + 120);
        againButton.setSize(200, 60);
        againButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        this.getLayeredPane().add(againButton, JLayeredPane.MODAL_LAYER);
    }

    /**
     * 在游戏面板中增加一个按钮，如果按下的话就退出
     * */
    private void addExitButton() {
        JButton exitButton = new JButton("退出");
        exitButton.addActionListener((e) -> System.exit(0));
        exitButton.setLocation(HEIGTH, HEIGTH / 10 + 550);
        exitButton.setSize(200, 60);
        exitButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        this.getLayeredPane().add(exitButton, JLayeredPane.MODAL_LAYER);
    }

    /**
     * 重置游戏
     */

    public GameController againController() throws IOException {
        getLayeredPane().remove(chessboardComponent);
        chessboardComponent.reset(new Chessboard());
        getLayeredPane().add(chessboardComponent, JLayeredPane.MODAL_LAYER);
        GameController gameController = chessboardComponent.getGameController();
        getLayeredPane().remove(roundText);
        addRoundText(gameController.getRoundText());
        return chessboardComponent.getGameController();
    }

}
