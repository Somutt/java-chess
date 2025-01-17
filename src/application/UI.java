package application;

import chess.ChessPiece;
import chess.ChessPosition;
import chess.Color;

import java.util.InputMismatchException;
import java.util.Scanner;

public class UI {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    //public static final String ANSI_WHITE = "\u001B[37m";

    public static ChessPosition readChessPosition(Scanner scanner) {
        try {
            String line = scanner.nextLine();
            char column = line.charAt(0);
            int row = Integer.parseInt(line.substring(1));

            return new ChessPosition(column, row);
        } catch (RuntimeException e) {
            throw new InputMismatchException("Error reading chess position. Valid values are a1 to h8");
        }

    }

    public static void printBoard(ChessPiece[][] pieces) {
        for (int i = 0; i < pieces.length; i++) {
            System.out.print((8 - i) + " ");
            for (int j = 0; j < pieces.length; j++) {
                printPiece(pieces[i][j]);
            }
            System.out.println();
        }
        System.out.print("  a b c d e f g h");
    }

    private static void printPiece(ChessPiece piece) {
        if (piece == null) {
            System.out.print("-");
        } else {
            if (piece.getColor() == Color.BLACK) {
                System.out.print(ANSI_YELLOW + piece + ANSI_RESET);
            } else {
                System.out.print(piece);
            }
        }

        System.out.print(" ");
    }
}
