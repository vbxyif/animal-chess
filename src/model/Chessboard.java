package model;

import view.ChessComponent.Name;
import view.ChessGameFrame;
import view.ChessboardComponent;

import java.util.HashSet;
import java.util.Set;


/**
 * This class store the real chess information.
 * The Chessboard has 9*7 cells, and each cell has a position for chess
 */
public class Chessboard {
    private final Cell[][] grid;
    private final Set<ChessPiece> red = new HashSet<>();
    private final Set<ChessPiece> blue = new HashSet<>();

    public Chessboard() {
        this.grid =
                new Cell[Constant.CHESSBOARD_ROW_SIZE.getNum()][Constant.CHESSBOARD_COL_SIZE.getNum()];//19X19

        initGrid();
        initPieces();
        initTeam();
    }

    private void initGrid() {
        for (int i = 0; i < Constant.CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < Constant.CHESSBOARD_COL_SIZE.getNum(); j++) {
                grid[i][j] = new Cell();
            }
        }
    }

    private void initPieces() {
        grid[0][0].setPiece(new ChessPiece(PlayerColor.RED, Name.狮, 7));
        grid[0][6].setPiece(new ChessPiece(PlayerColor.RED, Name.虎, 6));
        grid[1][1].setPiece(new ChessPiece(PlayerColor.RED, Name.狗, 3));
        grid[1][5].setPiece(new ChessPiece(PlayerColor.RED, Name.猫, 2));
        grid[2][0].setPiece(new ChessPiece(PlayerColor.RED, Name.鼠, 1));
        grid[2][2].setPiece(new ChessPiece(PlayerColor.RED, Name.豹, 5));
        grid[2][4].setPiece(new ChessPiece(PlayerColor.RED, Name.狼, 4));
        grid[2][6].setPiece(new ChessPiece(PlayerColor.RED, Name.象, 8));

        grid[6][0].setPiece(new ChessPiece(PlayerColor.BLUE, Name.象, 8));
        grid[6][2].setPiece(new ChessPiece(PlayerColor.BLUE, Name.狼, 4));
        grid[6][4].setPiece(new ChessPiece(PlayerColor.BLUE, Name.豹, 5));
        grid[6][6].setPiece(new ChessPiece(PlayerColor.BLUE, Name.鼠, 1));
        grid[7][1].setPiece(new ChessPiece(PlayerColor.BLUE, Name.猫, 2));
        grid[7][5].setPiece(new ChessPiece(PlayerColor.BLUE, Name.狗, 3));
        grid[8][0].setPiece(new ChessPiece(PlayerColor.BLUE, Name.虎, 6));
        grid[8][6].setPiece(new ChessPiece(PlayerColor.BLUE, Name.狮, 7));
    }

    private void initTeam() {
        for (int i = 0; i < Constant.CHESSBOARD_ROW_SIZE.getNum(); i++) {
            for (int j = 0; j < Constant.CHESSBOARD_COL_SIZE.getNum(); j++) {
                if (i < 3) {
                    red.add(getChessPieceAt(new ChessboardPoint(i, j)));
                } else if (i > 5) {
                    blue.add(getChessPieceAt(new ChessboardPoint(i, j)));
                }
            }
        }
    }

    private ChessPiece getChessPieceAt(ChessboardPoint point) {
        return getGridAt(point).getPiece();
    }

    private Cell getGridAt(ChessboardPoint point) {
        return grid[point.getRow()][point.getCol()];
    }

    public boolean isNoRiverHasRat(ChessboardPoint src, ChessboardPoint dest) {
        int rowDistance = dest.getRow() - src.getRow();
        int colDistance = dest.getCol() - src.getCol();
        int rowMuti = rowDistance / (Math.abs(rowDistance) != 0 ? Math.abs(rowDistance) : 1);
        int colMuti = colDistance / (Math.abs(colDistance) != 0 ? Math.abs(colDistance) : 1);
        for (int i = 0; i < Math.abs(rowDistance); i++) {
            for (int j = 0; j < Math.abs(colDistance); j++) {
                ChessboardPoint point = new ChessboardPoint(src.getRow() + rowMuti * i, src.getCol() + colMuti * j);
                if (getChessPieceAt(point) != null && getChessPieceAt(point).getName().equals(Name.鼠)) {
                    return false;
                }
            }
        }
        return true;
    }

    private double calculateDistance(ChessboardPoint src, ChessboardPoint dest) {
        return Math.sqrt(Math.pow(Math.abs(src.getRow() - dest.getRow()), 2) + Math.pow(Math.abs(src.getCol() - dest.getCol()), 2));
    }

    private ChessPiece removeChessPiece(ChessboardPoint point) {
        ChessPiece chessPiece = getChessPieceAt(point);
        getGridAt(point).removePiece();
        return chessPiece;
    }

    private void setChessPiece(ChessboardPoint point, ChessPiece chessPiece) {
        getGridAt(point).setPiece(chessPiece);
    }

    public void moveChessPiece(ChessboardPoint src, ChessboardPoint dest) {
        if (!isValidMove(src, dest)) {
            throw new IllegalArgumentException("Illegal chess move!");
        }
        setChessPiece(dest, removeChessPiece(src));
        getChessPieceAt(dest).fallInTrap(dest);
        getChessPieceAt(dest).inWater(dest);
    }

    public void captureChessPiece(ChessboardPoint src, ChessboardPoint dest) {
        if (!isValidCapture(src, dest)) {
            throw new IllegalArgumentException("Illegal chess capture!");
        }
        if (red.contains(getChessPieceAt(dest))) {
            red.remove(getChessPieceAt(dest));
        } else blue.remove(getChessPieceAt(dest));
        setChessPiece(dest, removeChessPiece(src));
        // TODO: Finish the method.--Done
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public PlayerColor getChessPieceOwner(ChessboardPoint point) {
        return getGridAt(point).getPiece().getOwner();
    }

    public boolean isValidMove(ChessboardPoint src, ChessboardPoint dest) {
        boolean isRiver = ChessGameFrame.getChessboardComponent().getRiverCell().contains(dest);
        boolean isRiver1side = ChessGameFrame.getChessboardComponent().getRiver1sideCell().contains(dest);
        boolean isRiver2side = ChessGameFrame.getChessboardComponent().getRiver2sideCell().contains(dest);
        boolean isSelfRiver1side = ChessGameFrame.getChessboardComponent().getRiver1sideCell().contains(src);
        boolean isSelfRiver2side = ChessGameFrame.getChessboardComponent().getRiver2sideCell().contains(src);
        boolean canCapture = getChessPieceAt(src) == null || getChessPieceAt(dest) == null || getChessPieceAt(src).canCapture(getChessPieceAt(dest));

        if (getChessPieceAt(src) == null) {
            return false;
        } else if (isRiver) {
            if (getChessPieceAt(src).getRank() != 1) {
                return false;
            }
        } else if (isRiver1side || isRiver2side) {
            if (getChessPieceAt(src).getRank() == 6 || getChessPieceAt(src).getRank() == 7) {
                if (isSelfRiver1side && isSelfRiver2side) {
                    return canCapture && isNoRiverHasRat(src, dest) && (calculateDistance(src, dest) == 3 || calculateDistance(src, dest) == 1);
                } else {
                    if (isSelfRiver1side && isRiver1side) {
                        return canCapture && isNoRiverHasRat(src, dest) && (calculateDistance(src, dest) == 3 || calculateDistance(src, dest) == 4 || calculateDistance(src, dest) == 1);
                    } else if (isSelfRiver2side && isRiver2side) {
                        return canCapture && isNoRiverHasRat(src, dest) && (calculateDistance(src, dest) == 3 || calculateDistance(src, dest) == 4 || calculateDistance(src, dest) == 1);
                    }
                }
            }
        }

        if (getChessPieceAt(dest) != null) {
            if (getChessPieceOwner(dest).equals(getChessPieceOwner(src))) {
                return false;
            }
        }

        if (getChessPieceAt(src).getOwner().equals(PlayerColor.RED) && dest.equals(ChessboardComponent.getDenRed())
                || getChessPieceAt(src).getOwner().equals(PlayerColor.BLUE) && dest.equals(ChessboardComponent.getDenBlue())) {
            return false;
        }
        return canCapture && calculateDistance(src, dest) == 1;
    }

    public boolean isValidCapture(ChessboardPoint src, ChessboardPoint dest) {
        // TODO:Fix this method--Done
        boolean isInRiver = ChessGameFrame.getChessboardComponent().getRiverCell().contains(src);
        return !isInRiver && isValidMove(src, dest) && getChessPieceAt(src).canCapture(getChessPieceAt(dest));
    }
}
