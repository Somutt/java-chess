package application;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.exceptions.ChessException;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ChessMatch chessMatch = new ChessMatch();
        List<ChessPiece> captured = new ArrayList<>();

        while (!chessMatch.getCheckmate()) {
            try {
                UI.clearScreen();
                UI.printMatch(chessMatch, captured);
                System.out.print("Source: ");
                ChessPosition source = UI.readChessPosition(scanner);
                boolean[][] possibleMoves = chessMatch.possibleMoves(source);
                UI.clearScreen();
                UI.printBoard(chessMatch.getPieces(), possibleMoves);
                System.out.println();

                System.out.print("Destination: ");
                ChessPosition destination = UI.readChessPosition(scanner);

                ChessPiece capturedPiece = chessMatch.performChessMove(source, destination);
                if (capturedPiece != null) {
                    captured.add(capturedPiece);
                }
            } catch (ChessException | InputMismatchException e) {
                System.out.println(e.getMessage());
                scanner.nextLine();
            }
        }
        UI.clearScreen();
        UI.printMatch(chessMatch, captured);
    }
}