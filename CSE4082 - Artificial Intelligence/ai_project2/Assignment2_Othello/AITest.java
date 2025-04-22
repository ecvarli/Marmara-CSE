package local.Assignment2_Othello;
import java.util.ArrayList;
import java.util.List;

public class AITest {

    public static void main(String[] args) {
        int[] plyValues = {4, 5, 6}; // Ply depths
        int[] heuristicTypes = {1, 2, 3}; // Heuristic types

        List<String[]> results = new ArrayList<>(); // All score tables

        for (int plyX : plyValues) {
            for (int plyO : plyValues) {
                for (int heuristicX : heuristicTypes) {
                    for (int heuristicO : heuristicTypes) {
                        System.out.println("\nTest");
                        System.out.println("AI X - ply: " + plyX + ", heuristic: " + heuristicX);
                        System.out.println("AI O - ply: " + plyO + ", heuristic: " + heuristicO);

                        Board board = new Board();
                        AIPlayer aiX = new AIPlayer('X', plyX, heuristicX);
                        AIPlayer aiO = new AIPlayer('O', plyO, heuristicO);

                        char currentPlayer = 'X';

                        // Variables to track total game time for each player
                        long totalTimeX = 0;
                        long totalTimeO = 0;

                        while (true) {
                            board.printBoard();
                            if (!board.hasValidMoves(currentPlayer)) {
                                System.out.println("There is no valid move for " + currentPlayer);
                                break;
                            }

                            AIPlayer currentAI = (currentPlayer == 'X') ? aiX : aiO;
                            long startTime = System.nanoTime(); // Start timing
                            int[] move = currentAI.getBestMove(board);
                            long endTime = System.nanoTime(); // End timing

                            if (move == null) {
                                System.out.println("There is no valid move for AI " + currentPlayer + ".");
                                break;
                            }

                            // Accumulate time for each player
                            if (currentPlayer == 'X') {
                                totalTimeX += (endTime - startTime);
                            } else {
                                totalTimeO += (endTime - startTime);
                            }

                            board.placeMove(move[0], move[1], currentPlayer);
                            System.out.println("AI " + currentPlayer + " plays: " + (char) ('a' + move[1]) + (move[0] + 1));

                            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                        }

                        int[] score = board.getScore();
                        System.out.println("\n\nGame Over");
                        System.out.println("Score - X: " + score[0] + " O: " + score[1]);
                        System.out.printf("Total time - X: %.2f ms, O: %.2f ms\n",
                                totalTimeX / 1_000_000.0, totalTimeO / 1_000_000.0);

                        // Add results to the table
                        results.add(new String[]{
                                "ply X: " + plyX,
                                "heuristic X: " + heuristicX,
                                "ply O: " + plyO,
                                "heuristic O: " + heuristicO,
                                "score X: " + score[0],
                                "score O: " + score[1],
                                String.format("time X: %.2f ms", totalTimeX / 1_000_000.0),
                                String.format("time O: %.2f ms", totalTimeO / 1_000_000.0)
                        });
                    }
                }
            }
        }

        // Print results as a table
        System.out.println("\nResults");
        System.out.printf("%-10s %-15s %-10s %-15s %-10s %-10s %-15s %-15s\n",
                "ply X", "heuristic X", "ply O", "heuristic O", "score X", "score O", "time X (ms)", "time O (ms)");

        for (String[] result : results) {
            System.out.printf("%-10s %-15s %-10s %-15s %-10s %-10s %-15s %-15s\n",
                    result[0], result[1], result[2], result[3], result[4], result[5], result[6], result[7]);
        }
    }
}
