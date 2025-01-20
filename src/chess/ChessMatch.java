package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.exceptions.ChessException;
import chess.pieces.King;
import chess.pieces.Rook;

import java.util.ArrayList;
import java.util.List;

public class ChessMatch {
    private final Board board;
    private Color currentPlayer;
    private int turn;

    private final List<Piece> piecesOnBoard = new ArrayList<>();
    private final List<Piece> captured = new ArrayList<>();

    public ChessMatch() {
        this.board = new Board(8, 8);
        turn = 1;
        currentPlayer = Color.WHITE;
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

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    private void initialSetup() {
        placeNewPiece('d', 1, new King(board, Color.WHITE));
        placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 8, new King(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnBoard.add(piece);
    }

    public boolean[][] possibleMoves(ChessPosition origin) {
        Position position = origin.toPosition();
        validateOrigin(position);

        return board.piece(position).possibleMoves();
    }

    public ChessPiece performChessMove(ChessPosition origin, ChessPosition destination) {
        Position originPosition = origin.toPosition();
        Position targetPosition = destination.toPosition();
        validateOrigin(originPosition);
        validateTarget(originPosition, targetPosition);

        Piece capturedPiece = makeMove(originPosition, targetPosition);
        nextTurn();
        return (ChessPiece) capturedPiece;
    }

    private void validateOrigin(Position origin) {
        if (!board.thereIsAPiece(origin)) {
            throw new ChessException("There is no piece at source position");
        }
        if (currentPlayer != ((ChessPiece) board.piece(origin)).getColor()) {
            throw new ChessException("Opposing player piece selected for move");
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
        if (capturedPiece != null) {
            piecesOnBoard.remove(capturedPiece);
            captured.add(capturedPiece);
        }

        return capturedPiece;
    }

    private void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }
}
