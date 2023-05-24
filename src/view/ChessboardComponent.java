package view;


import controller.GameController;
import model.Cell;
import model.ChessPiece;
import model.Chessboard;
import model.ChessboardPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import static model.Constant.CHESSBOARD_COL_SIZE;
import static model.Constant.CHESSBOARD_ROW_SIZE;

/**
 * This class represents the checkerboard component object on the panel
 */
public class ChessboardComponent extends JComponent {
    private static final ChessboardPoint denRed = new ChessboardPoint(0, 3);
    private static final ChessboardPoint denBlue = new ChessboardPoint(8, 3);
    private final CellComponent[][] gridComponents = new CellComponent[CHESSBOARD_ROW_SIZE.getNum()][CHESSBOARD_COL_SIZE.getNum()];
    private final int CHESS_SIZE;
    private final Set<ChessboardPoint> riverCell = new HashSet<>();
    private final Set<ChessboardPoint> riversideCell = new HashSet<>();
    private final Set<ChessboardPoint> trapRed = new HashSet<>();
    private final Set<ChessboardPoint> trapBlue = new HashSet<>();
    private GameController gameController;

    public ChessboardComponent(int chessSize) {
        CHESS_SIZE = chessSize;
        int width = CHESS_SIZE * 7;
        int height = CHESS_SIZE * 9;
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);// Allow mouse events to occur
        setLayout(null); // Use absolute layout.
        setSize(width, height);
        System.out.printf("chessboard width, height = [%d : %d], chess size = %d\n", width, height, CHESS_SIZE);

        initiateGridComponents();
    }

    public static ChessboardPoint getDenRed() {
        return denRed;
    }

    public static ChessboardPoint getDenBlue() {
        return denBlue;
    }

    /**
     * This method represents how to initiate ChessComponent
     * according to Chessboard information
     */
    public void initiateChessComponent(Chessboard chessboard) {
        Cell[][] grid = chessboard.getGrid();
        for (int i = 0; i < CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < CHESSBOARD_COL_SIZE.getNum(); j++) {
                // TODO: Implement the initialization checkerboard —— Done

                if (grid[i][j].getPiece() != null) {
                    ChessPiece chessPiece = grid[i][j].getPiece();
                    System.out.println(chessPiece.getOwner().toString() + chessPiece.getName());
                    gridComponents[i][j].add(
                            new ChessComponent(chessPiece.getName(),
                                    chessPiece.getOwner(),
                                    CHESS_SIZE));
                }
            }
        }

    }

    public Set<ChessboardPoint> getRiversideCell() {
        return riversideCell;
    }

    public Set<ChessboardPoint> getTrapRed() {
        return trapRed;
    }

    public Set<ChessboardPoint> getTrapBlue() {
        return trapBlue;
    }

    public CellComponent[][] getGridComponents() {
        return gridComponents;
    }

    public void initiateGridComponents() {

        {
            trapBlue.add(new ChessboardPoint(8, 2));
            trapBlue.add(new ChessboardPoint(8, 4));
            trapBlue.add(new ChessboardPoint(7, 3));

            trapRed.add(new ChessboardPoint(0, 2));
            trapRed.add(new ChessboardPoint(0, 4));
            trapRed.add(new ChessboardPoint(1, 3));

            riverCell.add(new ChessboardPoint(3, 1));
            riverCell.add(new ChessboardPoint(3, 2));
            riverCell.add(new ChessboardPoint(4, 1));
            riverCell.add(new ChessboardPoint(4, 2));
            riverCell.add(new ChessboardPoint(5, 1));
            riverCell.add(new ChessboardPoint(5, 2));

            riversideCell.add(new ChessboardPoint(2, 1));
            riversideCell.add(new ChessboardPoint(2, 2));
            riversideCell.add(new ChessboardPoint(3, 0));
            riversideCell.add(new ChessboardPoint(3, 3));
            riversideCell.add(new ChessboardPoint(4, 0));
            riversideCell.add(new ChessboardPoint(4, 3));
            riversideCell.add(new ChessboardPoint(5, 0));
            riversideCell.add(new ChessboardPoint(5, 3));
            riversideCell.add(new ChessboardPoint(6, 1));
            riversideCell.add(new ChessboardPoint(6, 2));

            riverCell.add(new ChessboardPoint(3, 4));
            riverCell.add(new ChessboardPoint(3, 5));
            riverCell.add(new ChessboardPoint(4, 4));
            riverCell.add(new ChessboardPoint(4, 5));
            riverCell.add(new ChessboardPoint(5, 4));
            riverCell.add(new ChessboardPoint(5, 5));

            riversideCell.add(new ChessboardPoint(2, 4));
            riversideCell.add(new ChessboardPoint(2, 5));
            riversideCell.add(new ChessboardPoint(3, 3));
            riversideCell.add(new ChessboardPoint(3, 6));
            riversideCell.add(new ChessboardPoint(4, 3));
            riversideCell.add(new ChessboardPoint(4, 6));
            riversideCell.add(new ChessboardPoint(5, 3));
            riversideCell.add(new ChessboardPoint(5, 6));
            riversideCell.add(new ChessboardPoint(6, 4));
            riversideCell.add(new ChessboardPoint(6, 5));
        }//set river, traps and dens

        for (int i = 0; i < CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < CHESSBOARD_COL_SIZE.getNum(); j++) {
                ChessboardPoint temp = new ChessboardPoint(i, j);
                CellComponent cell;
                if (riverCell.contains(temp)) {
                    cell = new CellComponent(Color.CYAN, calculatePoint(i, j), CHESS_SIZE, "river");
                    this.add(cell);
                } else if (riversideCell.contains(temp)) {
                    cell = new CellComponent(new Color(46, 80, 44), calculatePoint(i, j), CHESS_SIZE, "riverside");
                    this.add(cell);
                } else if (trapRed.contains(temp) || trapBlue.contains(temp)) {
                    cell = new CellComponent(Color.GRAY, calculatePoint(i, j), CHESS_SIZE, "trap");
                    this.add(cell);
                } else if (temp.equals(denRed) || temp.equals(denBlue)) {
                    cell = new CellComponent(Color.ORANGE, calculatePoint(i, j), CHESS_SIZE, "den");
                    this.add(cell);
                } else {
                    cell = new CellComponent(new Color(16, 145, 4), calculatePoint(i, j), CHESS_SIZE, "grass");
                    this.add(cell);
                }
                gridComponents[i][j] = cell;
            }
        }
    }

    public void registerController(GameController gameController) {
        this.gameController = gameController;
    }

    public void setChessComponentAtGrid(ChessboardPoint point, ChessComponent chess) {
        getGridComponentAt(point).add(chess);
    }

    public ChessComponent removeChessComponentAtGrid(ChessboardPoint point) {
        // Note re-validation is required after remove / removeAll.
        ChessComponent chess = (ChessComponent) getGridComponentAt(point).getComponents()[0];
        getGridComponentAt(point).removeAll();
        getGridComponentAt(point).revalidate();
        chess.setSelected(false);
        return chess;
    }

    public Set<ChessboardPoint> getRiverCell() {
        return riverCell;
    }

    public CellComponent getGridComponentAt(ChessboardPoint point) {
        return gridComponents[point.row()][point.col()];
    }

    private ChessboardPoint getChessboardPoint(Point point) {
        System.out.println("[" + point.y / CHESS_SIZE + ", " + point.x / CHESS_SIZE + "] Clicked");
        return new ChessboardPoint(point.y / CHESS_SIZE, point.x / CHESS_SIZE);
    }

    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE, row * CHESS_SIZE);
    }

    public void disableEvents() {
        disableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            JComponent clickedComponent = (JComponent) getComponentAt(e.getX(), e.getY());
            if (clickedComponent.getComponentCount() == 0) {
                System.out.print("None chess here and ");
                gameController.onPlayerClickCell(getChessboardPoint(e.getPoint()), (CellComponent) clickedComponent);
            } else {
                System.out.print("One chess here and ");
                gameController.onPlayerClickChessPiece(getChessboardPoint(e.getPoint()), (ChessComponent) clickedComponent.getComponents()[0]);
            }
        }
    }

    public GameController getGameController() {
        return gameController;
    }
}
