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
    private double count = 1;

    public GameController(ChessboardComponent view, Chessboard model, MessageText roundText) {
        this.view = view;
        this.model = model;
        this.roundText = roundText;
        this.currentPlayer = PlayerColor.BLUE;

        view.registerController(this);
        //initialize();
        view.initiateChessComponent(model);
        view.repaint();
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
        count += 0.5;
        roundText.setText((int) count + "");
        roundText.setForeground(currentPlayer.getColor());
    }

    public MessageText getRoundText() {
        return roundText;
    }

    private boolean win(boolean value) {
        // TODO: Check the board if there is a winner--Done
        if (value) {
            afterWin();
        }
        return value;
    }

    private void afterWin() {
        System.out.println("游戏结束！");
        //JOptionPane.showMessageDialog(null,"游戏结束！");
        roundText.setFont(new Font("Black", Font.BOLD, 100));
        roundText.setText("Win!");
        view.disableEvents();
    }

    // click an empty cell
    @Override
    public void onPlayerClickCell(ChessboardPoint point, CellComponent component) {
        if (selectedPoint != null && model.isValidMove(selectedPoint, point)) {
            model.moveChessPiece(selectedPoint, point);
            view.setChessComponentAtGrid(point, view.removeChessComponentAtGrid(selectedPoint));
            selectedPoint = null;
            for (CellComponent[] cells : view.getGridComponents()) {
                for (CellComponent cell : cells) {
                    cell.setValidMove(false);
                }
            }
            view.repaint();
            boolean value = point.equals(ChessboardComponent.getDenRed()) || point.equals(ChessboardComponent.getDenBlue());
            if (!win(value)) {
                swapColor();
            } else {
                win(true);
            }
            // TODO: if the chess enter Dens or Traps and so on--Done
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
                        if (model.isValidMove(point, point1) || model.isValidCapture(point, point1)) {
                            view.getGridComponentAt(point1).setValidMove(true);
                            //view.getGridComponentAt(point1).repaint();
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
                model.captureChessPiece(selectedPoint, point);
                view.removeChessComponentAtGrid(point);
                view.setChessComponentAtGrid(point, view.removeChessComponentAtGrid(selectedPoint));
                selectedPoint = null;
                for (CellComponent[] cells : view.getGridComponents()) {
                    for (CellComponent cell : cells) {
                        cell.setValidMove(false);
                    }
                }
                view.repaint();
                boolean value = point.equals(ChessboardComponent.getDenRed()) || point.equals(ChessboardComponent.getDenBlue());
                if (!win(value)) {
                    swapColor();
                } else {
                    win(true);
                }
            }
        }
        // TODO: Implement capture function--Done
    }
}
