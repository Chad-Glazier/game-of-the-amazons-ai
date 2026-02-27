package ubc.cosc322;

import java.util.ArrayList;

public class BoardState {
    
    // Internal 2D Array to represent the board for AI calculations
    // Size 10x10 is used as requested by team to simplify evaluation indices.
    public int[][] board = new int[10][10];

    /**
     * Converts the 1D ArrayList from the server into a 10x10 2D array.
     * 0 = Empty, 1 = White Queen, 2 = Black Queen, 3 = Arrow
     */
    public void setupLocalBoard(ArrayList<Integer> state) {
        for (int r = 1; r <= 10; r++) {
            for (int c = 1; c <= 10; c++) {
                int serverIndex = r * 11 + c;
                this.board[r - 1][c - 1] = state.get(serverIndex);
            }
        }
        System.out.println("Internal 10x10 board initialized in BoardState.");
    }
    
    /**
     * Updates the internal 10x10 array when a move is made by any player.
     */
    public void updateLocalBoard(ArrayList<Integer> qCurr, ArrayList<Integer> qNext, ArrayList<Integer> arrow) {
        int rCurr = qCurr.get(0) - 1;
        int cCurr = qCurr.get(1) - 1;
        
        int rNext = qNext.get(0) - 1;
        int cNext = qNext.get(1) - 1;
        
        int rArrow = arrow.get(0) - 1;
        int cArrow = arrow.get(1) - 1;
        
        int movingQueen = this.board[rCurr][cCurr];
        
        this.board[rCurr][cCurr] = 0;
        this.board[rNext][cNext] = movingQueen;
        this.board[rArrow][cArrow] = 3;
        
        System.out.println("Internal 10x10 board updated with recent move.");
    }

    /**
     * Finds all legal moves for a given color on a given board.
     * @param color The player's color (1 for White, 2 for Black)
     * @param currentBoard The 10x10 board to evaluate
     * @return A list of all possible valid moves.
     */
    public ArrayList<AmazonMove> generateLegalMoves(int color, int[][] currentBoard) {
        ArrayList<AmazonMove> legalMoves = new ArrayList<>();
        ArrayList<int[]> myQueens = new ArrayList<>();

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                if (currentBoard[r][c] == color) {
                    myQueens.add(new int[]{r, c});
                }
            }
        }

        for (int[] queenStart : myQueens) {
            int startR = queenStart[0];
            int startC = queenStart[1];

            ArrayList<int[]> possibleQueenEnds = getAvailableSquares(startR, startC, currentBoard);

            for (int[] queenEnd : possibleQueenEnds) {
                int endR = queenEnd[0];
                int endC = queenEnd[1];

                currentBoard[startR][startC] = 0; 
                currentBoard[endR][endC] = color; 

                ArrayList<int[]> possibleArrowEnds = getAvailableSquares(endR, endC, currentBoard);

                currentBoard[startR][startC] = color;
                currentBoard[endR][endC] = 0;

                for (int[] arrowEnd : possibleArrowEnds) {
                    legalMoves.add(new AmazonMove(queenStart, queenEnd, arrowEnd));
                }
            }
        }
        return legalMoves;
    }

    /**
     * Helper Method: Raycasts in 8 directions to find all reachable empty squares.
     */
    private ArrayList<int[]> getAvailableSquares(int startR, int startC, int[][] tempBoard) {
        ArrayList<int[]> squares = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};

        for (int[] d : directions) {
            int r = startR + d[0];
            int c = startC + d[1];
            while (r >= 0 && r < 10 && c >= 0 && c < 10 && tempBoard[r][c] == 0) {
                squares.add(new int[]{r, c});
                r += d[0];
                c += d[1];
            }
        }
        return squares;
    }
}