package local.Assignment2_Othello;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
    private char[][] board; 
    private static final int board_size = 8;

    public Board() {
        board = new char[board_size][board_size];
        initializeBoard();
    }

    // create the initial board
    private void initializeBoard() {
        for (int i = 0; i < board_size; i++) {
            Arrays.fill(board[i], '.'); 
        }
        board[3][3] = 'O';
        board[3][4] = 'X';
        board[4][3] = 'X';
        board[4][4] = 'O';
    }

    // print board
    public void printBoard() {
        
        System.out.println("\n\n\n  a b c d e f g h");
        for (int i = 0; i < board_size; i++) {
       
            System.out.print((i + 1) + " ");
        for (int j = 0; j < board_size; j++) {
            System.out.print(board[i][j] + " ");
        }
        System.out.println();
    }
    }

    // check is htere any valid move
    public boolean isValidMove(int row, int col, char player) {
        if (row < 0 || row >= board_size || col < 0 || col >= board_size || board[row][col] != '.') {
            return false;
        }

        char opponent = (player == 'X') ? 'O' : 'X';
       
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int direction = 0; direction < 8; direction++) {
       
       
       
            int x = row + dx[direction];
        int y = col + dy[direction];
        boolean hasOpponentBetween = false;

        while (x >= 0 && x < board_size && y >= 0 && y < board_size && board[x][y] == opponent) {
            hasOpponentBetween = true;
            x += dx[direction];
            y += dy[direction];
        }



        if (hasOpponentBetween && x >= 0 && x < board_size && y >= 0 && y < board_size && board[x][y] == player) {
            return true;
            }
        }
       
       
       
       
        return false;
    }

         //  place then flip discs
    public boolean placeMove(int row, int col, char player) {
      
        if (!isValidMove(row, col, player)) {
            return false;
        }

        board[row][col] = player;
       
        char opponent = (player == 'X') ? 'O' : 'X';
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int direction = 0; direction < 8; direction++) {
       
       
            int x = row + dx[direction];
            int y = col + dy[direction];
            List<int[]> toFlip = new ArrayList<>();

          
          
            while (x >= 0 && x < board_size && y >= 0 && y < board_size && board[x][y] == opponent) {
                toFlip.add(new int[]{x, y});
              
                x += dx[direction];
               
                y += dy[direction];
            }

            if (x >= 0 && x < board_size && y >= 0 && y < board_size && board[x][y] == player) {
            
                for (int[] pos : toFlip) {
                    board[pos[0]][pos[1]] = player;
                }
            }
        }
        return true;
    }

    // check the move is valid or not
    public boolean hasValidMoves(char player) {
        for (int i = 0; i < board_size; i++) {
      
            for (int j = 0; j < board_size; j++) {
        
            if (isValidMove(i, j, player)) {
                return true;
            }
        }
    }
        return false;
    }

    // calculate score
    public int[] getScore() {
        int black = 0, white = 0;
        for (int i = 0; i < board_size; i++) {
      
            for (int j = 0; j < board_size; j++) {
      
                if (board[i][j] == 'X') {
      
            black++;
        } else if (board[i][j] == 'O') {
            white++;
            }
        }
        }
        return new int[]{black, white};
    }

    public char[][] getBoard() {
        return board;
    }
}