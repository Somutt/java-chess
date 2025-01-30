package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.exceptions.ChessException;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Pawn;
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
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE));
        //blacks
        placeNewPiece('e', 8, new King(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK));
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnBoard.add(piece);
    }

    private Color opponent(Color color) {
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    public boolean[][] possibleMoves(ChessPosition origin) {
        Position position = origin.toPosition();
        validateOrigin(position);

        return board.piece(position).possibleMoves();
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateOrigin(source);
        validateTarget(source, target);
        Piece capturedPiece = makeMove(source, target);

        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece);
            throw new ChessException("You can't put yourself in check");
        }

        check = testCheck(opponent(currentPlayer));

        if (testCheckmate(opponent(currentPlayer))) {
            checkmate = true;
        }
        else {
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
        ChessPiece piece = (ChessPiece) board.removePiece(origin);
        piece.increaseMoveCount();
        Piece capturedPiece = board.removePiece(destination);
        board.placePiece(piece, destination);
        if (capturedPiece != null) {
            piecesOnBoard.remove(capturedPiece);
            captured.add(capturedPiece);
        }

        return capturedPiece;
    }

    private void undoMove(Position origin, Position destination, Piece capturedPiece) {
        ChessPiece piece = (ChessPiece) board.removePiece(destination);
        piece.decreaseMoveCount();
        board.placePiece(piece, origin);
        if (capturedPiece != null) {
            board.placePiece(capturedPiece, destination);
            captured.remove(capturedPiece);
            piecesOnBoard.add(capturedPiece);
        }
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
        List<Piece> list = piecesOnBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).toList();
        for (Piece p : list) {
            boolean[][] mat = p.possibleMoves();
            for (int i=0; i<board.getRows(); i++) {
                for (int j=0; j<board.getColumns(); j++) {
                    if (mat[i][j]) {
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturedPiece = makeMove(source, target);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, capturedPiece);
                        if (!testCheck) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
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

    private void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }
}
