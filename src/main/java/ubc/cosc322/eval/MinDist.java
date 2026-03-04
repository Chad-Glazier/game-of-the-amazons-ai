package ubc.cosc322.eval;

import ubc.cosc322.util.BitBoard;
import ubc.cosc322.util.Graph;

public class MinDist implements HeuristicMethod {
	/** The number of rows on the board. */
	private static final int N = 10;
	/** The number of columns on the board. */
	private static final int M = 10;
	/** The number of squares on the board. */
	private static final int SIZE = N * M;
	/** The number of queens per player. */
	private static final int QUEENS = 4;

	private static final int EMPTY = 0;
	private static final int WHITE_QUEEN = 1;
	private static final int BLACK_QUEEN = 2;

	/**
	 * Bitboard where empty squares should be flagged.
	 */
	private final long[] empty = BitBoard.create();
	/**
	 * Stores the indices of each white queen.
	 */
	private final int[] white = new int[QUEENS];
	/**
	 * Stores the indices of each black queen.
	 */
	private final int[] black = new int[QUEENS];

	public MinDist() {}

	public void setBoard(int[] board) {
		BitBoard.clear(empty);

		int whiteQueensMarked = 0;
		int blackQueensMarked = 0;

		for (int i = 0; i < SIZE; i++) {
			switch (board[i]) {
			case EMPTY:
				BitBoard.flag(empty, i);
				break;
			case WHITE_QUEEN:
				white[whiteQueensMarked] = i;
				whiteQueensMarked++;
				break;
			case BLACK_QUEEN:
				black[blackQueensMarked] = i;
				blackQueensMarked++;
				break;
			}
		}
	}

	public double evaluate(boolean playerIsWhite) {
		byte[] blackMinDistBoard = Graph.distance(empty, black[0]);
		byte[] whiteMinDistBoard = Graph.distance(empty, white[0]);

		for (int queen = 1; queen < QUEENS; queen++) {
			byte[] newBlackDistBoard = Graph.distance(empty, black[queen]);
			for (int i = 0; i < SIZE; i++) {
				if (newBlackDistBoard[i] < blackMinDistBoard[i]) {
					blackMinDistBoard[i] = newBlackDistBoard[i];
				}
			}

			byte[] newWhiteDistBoard = Graph.distance(empty, white[queen]);
			for (int i = 0; i < SIZE; i++) {
				if (newWhiteDistBoard[i] < whiteMinDistBoard[i]) {
					whiteMinDistBoard[i] = newWhiteDistBoard[i];
				}
			}
		}

		int blackSquares = 0;
		int whiteSquares = 0;
		int availableSquares = 100;
		for (int i = 0; i < SIZE; i++) {
			if (!BitBoard.flagged(empty, i)) {
				availableSquares--;
				continue;
			}
			if (blackMinDistBoard[i] < whiteMinDistBoard[i]) {
				blackSquares++;
				continue;
			}
			if (whiteMinDistBoard[i] < blackMinDistBoard[i]) {
				whiteSquares++;
				continue;
			}
		}

		if (playerIsWhite) {
			return ((double) (whiteSquares - blackSquares)) / ((double) 2 * availableSquares) + 0.50;
		} else {
			return ((double) (blackSquares - whiteSquares)) / ((double) 2 * availableSquares) + 0.50;
		}
	}

	public void visualize() {
		byte[] blackMinDistBoard = Graph.distance(empty, black[0]);
		byte[] whiteMinDistBoard = Graph.distance(empty, white[0]);

		for (int queen = 1; queen < QUEENS; queen++) {
			byte[] newBlackDistBoard = Graph.distance(empty, black[queen]);
			for (int i = 0; i < SIZE; i++) {
				if (newBlackDistBoard[i] < blackMinDistBoard[i]) {
					blackMinDistBoard[i] = newBlackDistBoard[i];
				}
			}

			byte[] newWhiteDistBoard = Graph.distance(empty, white[queen]);
			for (int i = 0; i < SIZE; i++) {
				if (newWhiteDistBoard[i] < whiteMinDistBoard[i]) {
					whiteMinDistBoard[i] = newWhiteDistBoard[i];
				}
			}
		}

		String[] boardDisplay = new String[SIZE];

		int blackSquares = 0;
		int whiteSquares = 0;
		int availableSquares = SIZE;
		int contestedSquares = 0;
		for (int i = 0; i < SIZE; i++) {
			if (!BitBoard.flagged(empty, i)) {
				availableSquares--;
				// boardDisplay[i] = "\u21A1";
				boardDisplay[i] = "X";
				continue;
			}
			if (blackMinDistBoard[i] < whiteMinDistBoard[i]) {
				blackSquares++;
				// boardDisplay[i] = "\u25A0";
				boardDisplay[i] = "b";
				continue;
			}
			if (whiteMinDistBoard[i] < blackMinDistBoard[i]) {
				whiteSquares++;
				// boardDisplay[i] = "\u25A1";
				boardDisplay[i] = "w";
				continue;
			}
			// boardDisplay[i] = "\u2591";
			boardDisplay[i] = "?";
			contestedSquares++;
		}

		for (int queen : white) {
			// boardDisplay[queen] = Character.toString(0x1FA0A);
			boardDisplay[queen] = "W";
		}

		for (int queen : black) {
			// boardDisplay[queen] = Character.toString(0x1FA3A);
			boardDisplay[queen] = "B";
		}

		for (int i = 0; i < SIZE; i++) {
			if (i % M == 0) {
				System.out.print("\n\t");
			}
			System.out.print(boardDisplay[i] + " ");
			if (i % M == M - 1) {
				switch (i / M) {
				case 0:
					System.out.print("\tLegend");
					break;
				case 1: 
					System.out.print("\t-----------------------");
					break;
				case 2:
					System.out.print("\tX: arrow-struck square");
					break;
				case 3:
					System.out.print("\tW: white queen");
					break;
				case 4:
					System.out.print("\tB: black queen");
					break;
				case 5:
					System.out.print("\tw: white territory");
					break;
				case 6:
					System.out.print("\tb: black territory");
					break;
				case 7:
					System.out.print("\tc: contested territory");
					break;
				}
			}
		}
		System.out.print("\n\n");
		System.out.printf("\tWhite squares:      %d (%.0f%%)\n", 
			whiteSquares,
			100 * (whiteSquares / (double) availableSquares)
		);
		System.out.printf("\tBlack squares:      %d (%.0f%%)\n", 
			blackSquares,
			100 * (blackSquares / (double) availableSquares)
		);
		System.out.printf("\tContested squares:  %d (%.0f%%)\n", 
			contestedSquares,
			100 * (contestedSquares / (double) availableSquares)
		);
		System.out.println();
	}
}
