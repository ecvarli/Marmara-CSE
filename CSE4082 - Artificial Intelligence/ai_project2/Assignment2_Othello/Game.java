package local.Assignment2_Othello;

import java.util.Scanner;

public class Game {
    private Board board;
    private Scanner scanner;

    public Game() {
        board = new Board();
        scanner = new Scanner(System.in);
    }


    //main menu
    public void play() {
        System.out.println("Choose mode :");
        System.out.println("1.human vs human");
        System.out.println("2.human vs AI");
        System.out.println("3.AI vs AI");
       
        int secim = scanner.nextInt();
        scanner.nextLine();

        switch (secim) {
            case 1:
                playHumanVsHuman();
                break;
            case 2:
                playHumanVsAI();
                break;
            case 3:
                playAIVsAI();
                break;
            default:
                System.out.println("invalid choose.");
        }
    }



    //HvsH game mode
    private void playHumanVsHuman() {
        char currentPlayer = 'X';
        while (true) {
            board.printBoard();
            if (!board.hasValidMoves(currentPlayer)) {
                System.out.println("there is not valid move for " + currentPlayer);
                break;
            }

            System.out.println("player " + currentPlayer + " enter your move (eg d3):");
            String move = scanner.nextLine();
            int row = move.charAt(1) - '1';
            int col = move.charAt(0) - 'a';

            if (!board.placeMove(row, col, currentPlayer)) {
                System.out.println("invalid move try again");
                continue;
            }

            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        }

        int[] score = board.getScore();
        System.out.println("game over");
        System.out.println("score - X: " + score[0] + " O: " + score[1]);
    }


    //HvsAI game mode
    private void playHumanVsAI() {
        char currentPlayer = 'X';
        AIPlayer ai = new AIPlayer('O', 3, 2);

        while (true) {
            board.printBoard();
            if (!board.hasValidMoves(currentPlayer)) {
                System.out.println("there is not valid move " + currentPlayer);
                break;
            }

            if (currentPlayer == 'X') {
                System.out.println("enter your move (eg d3)");
                String move = scanner.nextLine();
                int row = move.charAt(1) - '1';
                int col = move.charAt(0) - 'a';

                if (!board.placeMove(row, col, currentPlayer)) {
                    System.out.println("invalid move try again");
                    continue;
                }
            } else {
                int[] move = ai.getBestMove(board);
                if (move == null) {
                    System.out.println("there is not valid move for AI.");
                    break;
                }
                board.placeMove(move[0], move[1], currentPlayer);
                System.out.println("AI played: " + (char) ('a' + move[1]) + (move[0] + 1));
            }

            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        }

        int[] score = board.getScore();
        System.out.println("game over");
        System.out.println("score  X: " + score[0] + " O: " + score[1]);
    }

    //AIvsAI game mode
    private void playAIVsAI() {
        AIPlayer ai1 = new AIPlayer('X', 2, 1);
        AIPlayer ai2 = new AIPlayer('O', 2, 2);
        char currentPlayer = 'X';

        while (true) {
            board.printBoard();
            if (!board.hasValidMoves(currentPlayer)) {
                System.out.println("there is not valid move for " + currentPlayer);
                break;
            }

            AIPlayer currentAI = (currentPlayer == 'X') ? ai1 : ai2;
            int[] move = currentAI.getBestMove(board);
            if (move == null) {
                System.out.println("there is not valid move for " + currentPlayer + ".");
                break;
            }
            board.placeMove(move[0], move[1], currentPlayer);
            System.out.println("AI " + currentPlayer + " plays: " + (char) ('a' + move[1]) + (move[0] + 1));

            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        }

        int[] score = board.getScore();
        System.out.println("game over");
        System.out.println("score  X: " + score[0] + " O: " + score[1]);
    }
    public static void main(String[] args) {
        Game game = new Game();
        game.play();
    }
}