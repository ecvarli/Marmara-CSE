package local.Assignment2_Othello;

public class AIPlayer {
    private char player;
    private char opponent;
    private int ply;
    private int heuristicType;





    public AIPlayer(char player, int ply, int heuristicType) {
        this.player = player;
        this.opponent = (player == 'X') ? 'O' : 'X';
        this.ply = ply;
        this.heuristicType = heuristicType;
    }



    //calculate bestmove
    public int[] getBestMove(Board board) {
        long startTime = System.nanoTime();
        int[] bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

            if (board.isValidMove(i, j, player)) {
              
                Board tempBoard = cloneBoard(board);
                
                tempBoard.placeMove(i, j, player);
                
                int value = minimax(tempBoard, ply - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);

                if (value > bestValue) {
                    bestValue = value;
                    bestMove = new int[]{i, j};
                }
                }
            }
        }
        long endTime = System.nanoTime();
        
        System.out.printf("ply: %d time : %.2f ms%n", ply, (endTime - startTime) / 1_000_000.0);
       
        return bestMove;
    }

    // MAx
    private int Max(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || !board.hasValidMoves(player) && !board.hasValidMoves(opponent)) {
            return evaluate(board);
        }

        int maxEval = Integer.MIN_VALUE;
        for (int i = 0; i < 8; i++) {
            
            for (int j = 0; j < 8; j++) {
               
                if (board.isValidMove(i, j, player)) {
                   
                   
                    Board tempBoard = cloneBoard(board);
                    
                    tempBoard.placeMove(i, j, player);
                    
                    int eval = Min(tempBoard, depth - 1, alpha, beta);
                   
                    maxEval = Math.max(maxEval, eval);
                    
                    alpha = Math.max(alpha, eval);
                    
                    
                    if (beta <= alpha) {
                        return maxEval; 
                    }
                }
            }
        }
        return maxEval;
    }

    // Min
    private int Min(Board board, int depth, int alpha, int beta) {
        
        if (depth == 0 || !board.hasValidMoves(player) && !board.hasValidMoves(opponent)) {
            return evaluate(board);
        }

        int minEval = Integer.MAX_VALUE;
        
        
        for (int i = 0; i < 8; i++) {
           
           
            for (int j = 0; j < 8; j++) {
                
                if (board.isValidMove(i, j, opponent)) {
                   
                   
                    Board tempBoard = cloneBoard(board);
                   
                    tempBoard.placeMove(i, j, opponent);
                   
                    int eval = Max(tempBoard, depth - 1, alpha, beta);
                   
                    minEval = Math.min(minEval, eval);
                   
                    beta = Math.min(beta, eval);
                   
                   
                    if (beta <= alpha) {
                        return minEval; 
                    }
                }
            }
        }
        return minEval;
    }

        //minimax 
        private int minimax(Board board, int depth, boolean isMaximizing, int alpha, int beta) {
            if (isMaximizing) {
                return Max(board, depth, alpha, beta);
            } else {
                return Min(board, depth, alpha, beta);
            }
        }
        

    private int evaluate(Board board) {
        switch (heuristicType) {
            case 1:
                return evaluateH1(board);
            case 2:
                return evaluateH2(board);
            case 3:
                return evaluateH3(board);
            default:
                return 0;
        }
    }
    //evaluate with h1 method
    private int evaluateH1(Board board) {
      
        int[] score = board.getScore();
        return (player == 'X') ? score[0] - score[1] : score[1] - score[0];
    }

     //evalate with h3 method
     private int evaluateH3(Board board) {
        int[][] cornerAdjacents = {
            {0,1},{1,0},{1,1}, {0,6},{1,6},{1,7},{6,0},{6,1},{7,1},{6,6},{6,7}, {7,6} 
        };
    
        char[][] boardState = board.getBoard();
        int score = 0;
    
        for (int[] pos : cornerAdjacents) {
            int row = pos[0];
            int col = pos[1];
    
            if (boardState[row][col] == player) {
                score -= 1; // risky move for player
            } else if (boardState[row][col] == opponent) {
                score += 1; // risky move for opponent
            }
        }
    
        return score;
    }
 //evalate with h2 method

private int evaluateH2(Board board) {
        int[][] weights = {
            {4,-3,2,2,2,2, -3,4},
            {-3,-4,-1, -1,-1,-1,-4,-3},
            {2,-1,1,0,0,1,-1,2},
            {2,-1,0,1, 1,0,-1,2},
            {2,-1, 0,1,1,0, -1,2},
            {2,-1,1 ,0,0,1,-1,2},
            {-3,-4,-1,- 1,-1, -1,-4,-3},
            { 4,-3,2,2,2,2,-3,4}
        };
    
        char[][] boardState = board.getBoard();
        int score = 0;
    
        for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
       
            if (boardState[i][j] == player) {
            score += weights[i][j];
       
        } else if (boardState[i][j] == opponent) {
            score -= weights[i][j];
       
        }
       
    
    
    }
       
}
    
        return score;
    }
    
    //clone borad for simulation
    private Board cloneBoard(Board original) {
       
        Board copy = new Board();
        char[][] originalBoard = original.getBoard();
        char[][] copyBoard = copy.getBoard();
       
       
        for (int i = 0; i < 8; i++) {
            System.arraycopy(originalBoard[i], 0, copyBoard[i], 0, 8);
        }
        return copy;
    }
}