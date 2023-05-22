package controller;


import listener.GameListener;
import model.Chessboard;
import model.ChessboardPoint;
import model.Constant;
import model.PlayerColor;
import view.CellComponent;
import view.ChessComponent;
import view.ChessboardComponent;
import view.MessageText;
import java.io.*;
import java.awt.*;


/**
 * Controller is the connection between model and view,
 * when a Controller receive a request from a view, the Controller
 * analyzes and then hands over to the model for processing
 * [in this demo the request methods are onPlayerClickCell() and onPlayerClickChessPiece()]
 */
public class GameController implements GameListener {


    private final Chessboard model;
    private final ChessboardComponent view;
    private final MessageText roundText;
    private PlayerColor currentPlayer;

    // Record whether there is a selected piece before
    private ChessboardPoint selectedPoint;
    private double round;
    private static int count;
    public static SavesFileWriter savesFileWriter;
    private static final StringBuilder stringWriter = new StringBuilder();


    public GameController(ChessboardComponent view, Chessboard model, MessageText roundText) throws IOException {
        this.view = view;
        this.model = model;
        this.roundText = roundText;
        this.currentPlayer = PlayerColor.BLUE;

        view.registerController(this);
        //initialize();
        view.initiateChessComponent(model);
        view.repaint();
        round = 1;
    }

    public static void save() throws IOException {
        File[] list = new File("src/saves").listFiles();
        if (list != null) {
            count = list.length;
        }
        File newGameFile = new File(String.format("src/saves/save%d.txt", count + 1));
        savesFileWriter = new SavesFileWriter(newGameFile);
        savesFileWriter.write(stringWriter.toString());
        savesFileWriter.close();
        count ++;
    }

    private void initialize() {
        for (int i = 0; i < Constant.CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < Constant.CHESSBOARD_COL_SIZE.getNum(); j++) {

            }
        }
    }

    // after a valid move swap the player
    private void swapColor() {
        currentPlayer = currentPlayer == PlayerColor.BLUE ? PlayerColor.RED : PlayerColor.BLUE;
        round += 0.5;
        roundText.setText(String.valueOf((int) round));
        roundText.setForeground(currentPlayer.getColor());
    }

    public MessageText getRoundText() {
        return roundText;
    }

    private boolean win(ChessboardPoint point) {
        // TODO: Check the board if there is a winner--Done
        return point.equals(ChessboardComponent.getDenRed()) || point.equals(ChessboardComponent.getDenBlue())
                || model.getBlue().isEmpty() || model.getRed().isEmpty();
    }

    private void afterWin() {
        System.out.println("游戏结束！");
        //JOptionPane.showMessageDialog(null,"游戏结束！");
        roundText.setFont(new Font("Black", Font.BOLD, 100));
        roundText.setText("Win!");
        view.disableEvents();
    }

    private void writeOperate(ChessboardPoint src, ChessboardPoint dest) throws IOException {
        ChessComponent chess = view.removeChessComponentAtGrid(src);
        if (model.hasChessPiece(dest)) {
            ChessComponent target = view.removeChessComponentAtGrid(dest);
            model.captureChessPiece(src, dest);
            view.setChessComponentAtGrid(dest, chess);
            stringWriter.append(String.format("%s%s capture %s%s from %s to %s\n", chess.getOwner(), chess.getName(), target.getOwner(), target.getName(), src, dest));
        } else {
            model.moveChessPiece(src, dest);
            view.setChessComponentAtGrid(dest, chess);
            stringWriter.append(String.format("%s%s move from %s to %s\n", chess.getOwner(), chess.getName(), src, dest));
        }
    }

    // click an empty cell
    @Override
    public void onPlayerClickCell(ChessboardPoint point, CellComponent component) {
        if (selectedPoint != null && model.isValidMove(selectedPoint, point)) {
            try {
                writeOperate(selectedPoint, point);
            } catch (IOException e) {
                System.out.println("bugs");
                throw new RuntimeException(e);
            }
            selectedPoint = null;
            for (CellComponent[] cells : view.getGridComponents()) {
                for (CellComponent cell : cells) {
                    cell.setValidMove(false);
                }
            }
            view.repaint();
            if (win(point)) {
                afterWin();
            } else {
                swapColor();
            }
            // TODO: if the chess enter Dens or Traps and so on
        }
    }


    // click a cell with a chess
    @Override
    public void onPlayerClickChessPiece(ChessboardPoint point, ChessComponent component) {
        if (selectedPoint == null) {
            if (model.getChessPieceOwner(point).equals(currentPlayer)) {
                selectedPoint = point;
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 7; j++) {
                        ChessboardPoint point1 = new ChessboardPoint(i, j);
                        if (model.isValidMove(point, point1) && model.isValidCapture(point, point1)) {
                            view.getGridComponentAt(point1).setValidMove(true);
                        }
                    }
                }
                component.setSelected(true);
                view.repaint();
            }
        } else if (selectedPoint.equals(point)) {
            selectedPoint = null;
            for (CellComponent[] cells : view.getGridComponents()) {
                for (CellComponent cell : cells) {
                    cell.setValidMove(false);
                }
            }
            component.setSelected(false);
            view.repaint();
        } else if (!model.getChessPieceOwner(point).equals(currentPlayer)) {
            if (model.isValidCapture(selectedPoint, point)) {

                try {
                    writeOperate(selectedPoint, point);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                selectedPoint = null;
                for (CellComponent[] cells : view.getGridComponents()) {
                    for (CellComponent cell : cells) {
                        cell.setValidMove(false);
                    }
                }

                view.repaint();
                if (win(point)) {
                    afterWin();
                } else {
                    swapColor();
                }
            }
        }
        // TODO: Implement capture function--Done
    }
}
