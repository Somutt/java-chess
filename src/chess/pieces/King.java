package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

    private ChessMatch chessMatch;

    public King(ChessMatch chessMatch, Board board, Color color) {
        super(board, color);
        this.chessMatch = chessMatch;
    }

    @Override
    public String toString() {
        return "K";
    }

    private boolean canMove(Position position) {
        ChessPiece piece = (ChessPiece) getBoard().piece(position);
        return piece == null || piece.getColor() != getColor();
    }

    private boolean testRookCastling(Position position) {
        ChessPiece piece = (ChessPiece) getBoard().piece(position);
        return piece instanceof Rook && piece.getColor() == getColor() && piece.getMoveCount() == 0;
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] possibleMoves = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position aux = new Position(0, 0);

        //above
        aux.setValues(position.getRow() - 1, position.getColumn());
        if (getBoard().positionExists(aux) && canMove(aux)) {
            possibleMoves[aux.getRow()][aux.getColumn()] = true;
        }

        //below
        aux.setValues(position.getRow() + 1, position.getColumn());
        if (getBoard().positionExists(aux) && canMove(aux)) {
            possibleMoves[aux.getRow()][aux.getColumn()] = true;
        }

        //left
        aux.setValues(position.getRow(), position.getColumn() - 1);
        if (getBoard().positionExists(aux) && canMove(aux)) {
            possibleMoves[aux.getRow()][aux.getColumn()] = true;
        }

        //right
        aux.setValues(position.getRow(), position.getColumn() + 1);
        if (getBoard().positionExists(aux) && canMove(aux)) {
            possibleMoves[aux.getRow()][aux.getColumn()] = true;
        }

        //northwest
        aux.setValues(position.getRow() - 1, position.getColumn() - 1);
        if (getBoard().positionExists(aux) && canMove(aux)) {
            possibleMoves[aux.getRow()][aux.getColumn()] = true;
        }

        //northeast
        aux.setValues(position.getRow() - 1, position.getColumn() + 1);
        if (getBoard().positionExists(aux) && canMove(aux)) {
            possibleMoves[aux.getRow()][aux.getColumn()] = true;
        }

        //southwest
        aux.setValues(position.getRow() + 1, position.getColumn() - 1);
        if (getBoard().positionExists(aux) && canMove(aux)) {
            possibleMoves[aux.getRow()][aux.getColumn()] = true;
        }

        //southeast
        aux.setValues(position.getRow() + 1, position.getColumn() + 1);
        if (getBoard().positionExists(aux) && canMove(aux)) {
            possibleMoves[aux.getRow()][aux.getColumn()] = true;
        }

        //special move castling
        if (getMoveCount() == 0 && !chessMatch.getCheck()) {
            //special move castling kins side
            Position kingsCastlingRook = new Position(position.getRow(), position.getColumn() + 3);
            if (testRookCastling(kingsCastlingRook)) {
                Position p1 = new Position(position.getRow(), position.getColumn() + 1);
                Position p2 = new Position(position.getRow(), position.getColumn() + 2);
                if (getBoard().piece(p1) == null && getBoard().piece(p2) == null) {
                    possibleMoves[position.getRow()][position.getColumn() + 2] = true;
                }
            }
            //special move castling queens side
            Position queensCastlingRook = new Position(position.getRow(), position.getColumn() - 4);
            if (testRookCastling(queensCastlingRook)) {
                Position p1 = new Position(position.getRow(), position.getColumn() - 1);
                Position p2 = new Position(position.getRow(), position.getColumn() - 2);
                Position p3 = new Position(position.getRow(), position.getColumn() - 3);
                if (getBoard().piece(p1) == null && getBoard().piece(p2) == null && getBoard().piece(p3) == null) {
                    possibleMoves[position.getRow()][position.getColumn() - 2] = true;
                }
            }
        }

        return possibleMoves;
    }
}
