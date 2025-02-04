package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Bishop extends ChessPiece {

    public Bishop(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString() {
        return "B";
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] possibleMoves = new boolean[getBoard().getRows()][getBoard().getColumns()];
        Position aux = new Position(0, 0);

        //northwest
        aux.setValues(position.getRow() - 1 , position.getColumn() - 1);
        while (getBoard().positionExists(aux) && !getBoard().thereIsAPiece(aux)) {
            possibleMoves[aux.getRow()][aux.getColumn()] = true;
            aux.setValues(aux.getRow() - 1, aux.getColumn() - 1);
        }
        if (getBoard().positionExists(aux) && isThereOpponentPiece(aux)) {
            possibleMoves[aux.getRow()][aux.getColumn()] = true;
        }

        //northeast
        aux.setValues(position.getRow() - 1 , position.getColumn() + 1);
        while (getBoard().positionExists(aux) && !getBoard().thereIsAPiece(aux)) {
            possibleMoves[aux.getRow()][aux.getColumn()] = true;
            aux.setValues(aux.getRow() - 1, aux.getColumn() + 1);
        }
        if (getBoard().positionExists(aux) && isThereOpponentPiece(aux)) {
            possibleMoves[aux.getRow()][aux.getColumn()] = true;
        }

        //southeast
        aux.setValues(position.getRow() + 1 , position.getColumn() + 1);
        while (getBoard().positionExists(aux) && !getBoard().thereIsAPiece(aux)) {
            possibleMoves[aux.getRow()][aux.getColumn()] = true;
            aux.setValues(aux.getRow() + 1, aux.getColumn() + 1);
        }
        if (getBoard().positionExists(aux) && isThereOpponentPiece(aux)) {
            possibleMoves[aux.getRow()][aux.getColumn()] = true;
        }

        //southwest
        aux.setValues(position.getRow() + 1 , position.getColumn() - 1);
        while (getBoard().positionExists(aux) && !getBoard().thereIsAPiece(aux)) {
            possibleMoves[aux.getRow()][aux.getColumn()] = true;
            aux.setValues(aux.getRow() + 1, aux.getColumn() - 1);
        }
        if (getBoard().positionExists(aux) && isThereOpponentPiece(aux)) {
            possibleMoves[aux.getRow()][aux.getColumn()] = true;
        }

        return possibleMoves;
    }
}
