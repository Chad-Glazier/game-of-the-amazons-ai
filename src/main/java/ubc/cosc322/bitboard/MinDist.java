package ubc.cosc322.bitboard;

public class MinDist {
	private final BitState state;

	public MinDist(BitState state) {
		this.state = state;
	}

	public int evaluate() {
		
		// Store the territory belonging to each color.

		long[] whiteTerritory = BitBoard.create();
		long[] blackTerritory = BitBoard.create();

		// Track which squares have already been visited.

		long[] whiteVisited = BitBoard.create();
		long[] blackVisited = BitBoard.create();

		// Track the "frontier"; i.e., the squares that each territory is
		// expanding into and hasn't already visited.

		long[] whiteFrontier = BitBoard.create();
		long[] blackFrontier = BitBoard.create();

		// The frontiers each start simply with each side's queens. These are
		// the zero-distance squares. Since two queens can't sit on the same
		// square, we can also add these to their respective territories.
		
		for (int i = 0; i < 4; i++) {
			BitBoard.flag(whiteFrontier, state.queens[i]);
			BitBoard.copyTo(whiteFrontier, whiteVisited);
			BitBoard.copyTo(whiteFrontier, whiteTerritory);
		}

		for (int i = 4; i < 8; i++) {
			BitBoard.flag(blackFrontier, state.queens[i]);
			BitBoard.copyTo(blackFrontier, blackVisited);
			BitBoard.copyTo(blackFrontier, blackTerritory);
		}

		// Now, the frontiers are expanded one step at a time.

		while (true) {

			long[] nextWhite = BitGraph.neighbors(
				whiteFrontier, state.occupancy
			);
			long[] nextBlack = BitGraph.neighbors(
				blackFrontier, state.occupancy
			);

			// Add territory to white if the next thing is in white but not
			// black, and vice versa.
			// Add "next" to respective "visited".
			// Update the frontiers.

		}
	}
}
