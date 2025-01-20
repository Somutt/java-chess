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
    private boolean check;
    private boolean checkmate;

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

    public boolean getCheck() {
        return check;
    }

    public boolean getCheckmate() {
        return checkmate;
    }

    private void initialSetup() {
        //whites
        placeNewPiece('e', 1, new King(board, Color.WHITE));
        placeNewPiece('d', 1, new Rook(board, Color.WHITE));
        placeNewPiece('h', 7, new Rook(board, Color.WHITE));
        //blacks
        placeNewPiece('a', 8, new King(board, Color.BLACK));
        placeNewPiece('b', 8, new Rook(board, Color.BLACK));
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnBoard.add(piece);
    }

    private Color opponent(Color color) {
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king(Color color) {
        List<Piece> aux = piecesOnBoard.stream().filter(p -> ((ChessPiece)p).getColor() == color).toList();
        for (Piece p : aux) {
            if (p instanceof King) {
                return (ChessPiece) p;
            }
        }
        throw new IllegalStateException("There is no " + color + " king on the board");
    }

    private boolean testCheck(Color color) {
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = piecesOnBoard.stream().filter(p -> ((ChessPiece)p).getColor() == opponent(color)).toList();

        for (Piece p : opponentPieces) {
            boolean[][] aux = p.possibleMoves();
            if (aux[kingPosition.getRow()][kingPosition.getColumn()]) {
                return true;
            }
        }
        return false;
    }

    private boolean testCheckmate(Color color) {
        if (!testCheck(color)) {
            return false;
        }
        List<Piece> pieces = piecesOnBoard.stream().filter(p -> ((ChessPiece)p).getColor() == color).toList();

        for (Piece p : pieces) {
            boolean[][] aux = p.possibleMoves();
            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getColumns(); j++) {
                    if (aux[i][j]) {
                        Position origin = ((ChessPiece) p).getChessPosition().toPosition();
                        Position destination = new Position(i, j);
                        Piece capturedPiece = makeMove(origin, destination);

                        boolean testCheck = testCheck(color);
                        undoMove(origin, destination, capturedPiece);
                        if (!testCheck) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
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

        if (testCheck(currentPlayer)) {
            undoMove(originPosition, targetPosition, capturedPiece);
            throw new ChessException("Cannot put yourself on Check");
        }
        check = testCheck(opponent(currentPlayer));

        if (testCheckmate(opponent(currentPlayer))) {
            checkmate = true;
        } else {
            nextTurn();
        }
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

    private void undoMove(Position origin, Position destination, Piece capturedPiece) {
        Piece piece = board.removePiece(destination);
        board.placePiece(piece, origin);
        if (capturedPiece != null) {
            board.placePiece(capturedPiece, destination);
            captured.remove(capturedPiece);
            piecesOnBoard.add(capturedPiece);
        }
    }

    private void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }
}
