package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.exceptions.ChessException;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
    private final Board board;

    public ChessMatch() {
        this.board = new Board(8, 8);
        initialSetup();
    }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] chessPieces = new ChessPiece[board.getRows()][board.getColumns()];

        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                chessPieces[i][j] = (ChessPiece) board.piece(i, j);
            }
        }

        return chessPieces;
    }

    private void initialSetup() {
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('e', 8, new King(board, Color.BLACK));
        placeNewPiece('e', 1, new King(board, Color.WHITE));
    }

    public ChessPiece performChessMove(ChessPosition origin, ChessPosition destination) {
        Position originPosition = origin.toPosition();
        Position targetPosition = destination.toPosition();
        validateOrigin(originPosition);
        validateTarget(originPosition, targetPosition);
        Piece capturedPiece = makeMove(originPosition, targetPosition);
        return (ChessPiece) capturedPiece;
    }

    private void validateOrigin(Position origin) {
        if (!board.thereIsAPiece(origin)) {
            throw new ChessException("There is no piece at source position");
        }
        if (!board.piece(origin).isThereAnyPossibleMove()) {
            throw new ChessException("There is no possible move for selected piece");
        }
    }

    private void validateTarget(Position origin, Position destination) {
        if (!board.piece(origin).possibleMove(destination)) {
            throw new ChessException("The selected piece cannot be moved to target position");
        }
    }

    private Piece makeMove(Position origin, Position destination) {
        Piece piece = board.removePiece(origin);
        Piece capturedPiece = board.removePiece(destination);
        board.placePiece(piece, destination);
        return capturedPiece;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
    }
}
