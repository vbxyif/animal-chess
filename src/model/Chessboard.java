package model;

import view.ChessComponent.Name;
import view.ChessboardComponent;

import java.util.HashSet;
import java.util.Set;

import static view.ChessGameFrame.getChessboardComponent;


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
                ChessboardPoint point = new ChessboardPoint(i, j);
                if (i < 3 && getChessPieceAt(point) != null) {
                    red.add(getChessPieceAt(point));
                } else if (i > 5 && getChessPieceAt(point) != null) {
                    blue.add(getChessPieceAt(point));
                }
            }
        }
    }

    public Set<ChessPiece> getRed() {
        return red;
    }

    public Set<ChessPiece> getBlue() {
        return blue;
    }

    private ChessPiece getChessPieceAt(ChessboardPoint point) {
        return getGridAt(point).getPiece();
    }

    private Cell getGridAt(ChessboardPoint point) {
        return grid[point.row()][point.col()];
    }

    private boolean isRiverNoRat(ChessboardPoint src, ChessboardPoint dest) {
        int x = dest.col() - src.col();
        int y = dest.row() - src.row();
        if (!(x != 0 && y != 0) && !(x == 0 && y == 0)) {
            int d = x != 0 ? x / Math.abs(x) : y / Math.abs(y);
            for (int i = 0; i < Math.abs(x); i++) {
                ChessboardPoint point = new ChessboardPoint(src.row(), src.col() + d * i);
                ChessPiece chess = getChessPieceAt(point);
                if (chess.getName().equals(Name.鼠)) {
                    return false;
                }
            }
            for (int i = 0; i < Math.abs(y); i++) {
                ChessboardPoint point = new ChessboardPoint(src.row(), src.col() + d * i);
                ChessPiece chess = getChessPieceAt(point);
                if (chess.getName().equals(Name.鼠)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isRiver(ChessboardPoint dest) {
        return getChessboardComponent().getRiverCell().contains(dest);
    }

    private boolean isNext(ChessboardPoint src, ChessboardPoint dest) {
        return calculateDistance(src, dest) == 1;
    }

    private boolean isMiddleRiver(ChessboardPoint src, ChessboardPoint dest) {
        int x = dest.col() - src.col();
        int y = dest.row() - src.row();
        if (!(x != 0 && y != 0) && !(x == 0 && y == 0)) {
            int d = x != 0 ? x / Math.abs(x) : y / Math.abs(y);
            for (int i = 0; i < Math.abs(x); i++) {
                ChessboardPoint point = new ChessboardPoint(src.row(), src.col() + d * i);
                if (!isRiver(point)) {
                    return false;
                }
            }
            for (int i = 0; i < Math.abs(y); i++) {
                ChessboardPoint point = new ChessboardPoint(src.row() + d * i, src.col());
                if (!isRiver(point)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean canIn(ChessboardPoint src, ChessboardPoint dest) {
        int rank = getChessPieceAt(src).getRank();
        return rank == 1 && isNext(src, dest) && isRiver(dest);
    }

    private boolean canJump(ChessboardPoint src, ChessboardPoint dest) {
        int rank = getChessPieceAt(src).getRank();
        return (rank == 6 || rank == 7) && isMiddleRiver(src, dest) && isRiverNoRat(src, dest);
    }

    private boolean isPartner(ChessboardPoint src, ChessboardPoint dest) {
        if (getChessPieceAt(src) != null && getChessPieceAt(dest) != null) {
            PlayerColor self = getChessPieceOwner(src);
            PlayerColor target = getChessPieceOwner(dest);
            return self.equals(target);
        }
        return false;
    }

    private boolean isHomeDen(ChessboardPoint src, ChessboardPoint dest) {
        PlayerColor self = getChessPieceOwner(src);
        if (self.equals(PlayerColor.RED) && dest.equals(ChessboardComponent.getDenRed())) {
            return true;
        } else return self.equals(PlayerColor.BLUE) && dest.equals(ChessboardComponent.getDenBlue());
    }

    private double calculateDistance(ChessboardPoint src, ChessboardPoint dest) {
        return Math.sqrt(Math.pow(Math.abs(src.row() - dest.row()), 2) + Math.pow(Math.abs(src.col() - dest.col()), 2));
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
        if (isPartner(src, dest) || isHomeDen(src, dest)) {
            return false;
        }
        if (isRiver(dest)) {
            return canIn(src, dest);
        }
        return canJump(src, dest) || isNext(src, dest);
    }

    public boolean isValidCapture(ChessboardPoint src, ChessboardPoint dest) {
        // TODO:Fix this method--Done
        if (getChessPieceAt(dest) == null) {
            return true;
        } else {
            return getChessPieceAt(src).canCapture(getChessPieceAt(dest));
        }
    }
}
