package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.exceptions.ChessException;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.List;

public class ChessMatch {
    private final Board board;
    private Color currentPlayer;
    private int turn;
    private boolean check;
    private boolean checkmate;
    private ChessPiece enPassant;

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

    public ChessPiece getEnPassant() {
        return enPassant;
    }

    private void initialSetup() {
        //whites
        placeNewPiece('e', 1, new King(this, board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(this, board, Color.WHITE));
        placeNewPiece('b', 2, new Pawn(this, board, Color.WHITE));
        placeNewPiece('c', 2, new Pawn(this, board, Color.WHITE));
        placeNewPiece('d', 2, new Pawn(this, board, Color.WHITE));
        placeNewPiece('e', 2, new Pawn(this, board, Color.WHITE));
        placeNewPiece('f', 2, new Pawn(this, board, Color.WHITE));
        placeNewPiece('g', 2, new Pawn(this, board, Color.WHITE));
        placeNewPiece('h', 2, new Pawn(this, board, Color.WHITE));
        //blacks
        placeNewPiece('e', 8, new King(this, board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(this, board, Color.BLACK));
        placeNewPiece('b', 7, new Pawn(this, board, Color.BLACK));
        placeNewPiece('c', 7, new Pawn(this, board, Color.BLACK));
        placeNewPiece('d', 7, new Pawn(this, board, Color.BLACK));
        placeNewPiece('e', 7, new Pawn(this, board, Color.BLACK));
        placeNewPiece('f', 7, new Pawn(this, board, Color.BLACK));
        placeNewPiece('g', 7, new Pawn(this, board, Color.BLACK));
        placeNewPiece('h', 7, new Pawn(this, board, Color.BLACK));
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

        ChessPiece movedPiece = (ChessPiece) board.piece(target);
        check = testCheck(opponent(currentPlayer));

        if (testCheckmate(opponent(currentPlayer))) {
            checkmate = true;
        }
        else {
            nextTurn();
        }

        // special move En passant
        if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2 )) {
            enPassant = movedPiece;
        } else {
            enPassant = null;
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
        //special move Castling kings side
        if (piece instanceof King && destination.getColumn() == origin.getColumn() + 2) {
            Position originRook = new Position(origin.getRow(), origin.getColumn() + 3);
            Position destinationRook = new Position(origin.getRow(), origin.getColumn() + 1);
            ChessPiece rook = (ChessPiece) board.removePiece(originRook);
            board.placePiece(rook, destinationRook);
            rook.increaseMoveCount();
        }
        //special move Castling queens side
        if (piece instanceof King && destination.getColumn() == origin.getColumn() - 2) {
            Position originRook = new Position(origin.getRow(), origin.getColumn() - 4);
            Position destinationRook = new Position(origin.getRow(), origin.getColumn() - 1);
            ChessPiece rook = (ChessPiece) board.removePiece(originRook);
            board.placePiece(rook, destinationRook);
            rook.increaseMoveCount();
        }

        //special move En passant
        if (piece instanceof Pawn) {
            if (origin.getColumn() != destination.getColumn() && capturedPiece == null) {
                Position pawnPosition;
                if (piece.getColor() == Color.WHITE) {
                    pawnPosition = new Position(destination.getRow() + 1, destination.getColumn());
                } else {
                    pawnPosition = new Position(destination.getRow() - 1, destination.getColumn());
                }
                capturedPiece = board.removePiece(pawnPosition);
                captured.add(capturedPiece);
                piecesOnBoard.remove(capturedPiece);
            }
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

        //special move Castling kings side
        if (piece instanceof King && destination.getColumn() == origin.getColumn() + 2) {
            Position originRook = new Position(origin.getRow(), origin.getColumn() + 3);
            Position destinationRook = new Position(origin.getRow(), origin.getColumn() + 1);
            ChessPiece rook = (ChessPiece) board.removePiece(destinationRook);
            board.placePiece(rook, originRook);
            rook.decreaseMoveCount();
        }
        //special move Castling queens side
        if (piece instanceof King && destination.getColumn() == origin.getColumn() - 2) {
            Position originRook = new Position(origin.getRow(), origin.getColumn() - 4);
            Position destinationRook = new Position(origin.getRow(), origin.getColumn() - 1);
            ChessPiece rook = (ChessPiece) board.removePiece(destinationRook);
            board.placePiece(rook, originRook);
            rook.decreaseMoveCount();
        }

        //special move En passant
        if (piece instanceof Pawn) {
            if (origin.getColumn() != destination.getColumn() && capturedPiece == enPassant) {
                ChessPiece pawn = (ChessPiece) board.removePiece(destination);
                Position pawnPosition;
                if (piece.getColor() == Color.WHITE) {
                    pawnPosition = new Position(3, destination.getColumn());
                } else {
                    pawnPosition = new Position(4, destination.getColumn());
                }
                board.placePiece(pawn, pawnPosition);
            }
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
