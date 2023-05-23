package model;


import view.ChessComponent;
import view.ChessGameFrame;


public class ChessPiece {
    // the owner of the chess
    private final PlayerColor owner;

    // Elephant? Cat? Dog? ...
    private final ChessComponent.Name name;
    private final int rank;
    private boolean isCatch;
    private boolean inWater;

    public ChessPiece(PlayerColor owner, ChessComponent.Name name, int rank) {
        this.owner = owner;
        this.name = name;
        this.rank = rank;
        this.isCatch = false;
        this.inWater = false;
    }

    public void fallInTrap(ChessboardPoint point) {
        if (this.getOwner().equals(PlayerColor.RED) && ChessGameFrame.getChessboardComponent().getTrapBlue().contains(point)) {
            this.isCatch = true;
        } else
            this.isCatch = this.getOwner().equals(PlayerColor.BLUE) && ChessGameFrame.getChessboardComponent().getTrapRed().contains(point);
    }

    public void inWater(ChessboardPoint point) {
        this.inWater = ChessGameFrame.getChessboardComponent().getRiverCell().contains(point);
    }

    public boolean canCapture(ChessPiece target) {
        // TODO: Finish this method!--Done
        int thisRank = this.rank;
        int targetRank = target.rank;
        if (target.isCatch) {
            targetRank = 0;
        }
        if (this.isCatch) {
            thisRank = 0;
        }
        if (this.inWater) {
            thisRank = 0;
        }
        if (targetRank == 0 || (thisRank >= targetRank && !(thisRank == 8 && targetRank == 1)) || thisRank == 1 && targetRank == 8) {
            return !this.getOwner().equals(target.getOwner());
        }
        return false;
    }

    public ChessComponent.Name getName() {
        return name;
    }

    public PlayerColor getOwner() {
        return owner;
    }

    public int getRank() {
        return rank;
    }
}
