package application;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ChessMatch chessMatch = new ChessMatch();

        while (true) {
            UI.printBoard(chessMatch.getPieces());
            System.out.println();
            System.out.print("Source: ");
            ChessPosition source = UI.readChessPosition(scanner);

            System.out.println();
            System.out.print("Destination: ");
            ChessPosition destination = UI.readChessPosition(scanner);

            ChessPiece capturedPiece = chessMatch.performChessMove(source, destination);
        }
    }
}