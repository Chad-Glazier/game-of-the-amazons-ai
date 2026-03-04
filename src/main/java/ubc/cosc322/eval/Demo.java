package ubc.cosc322.eval;

public class Demo {
	public static void main() {
		RandomBoard board = new RandomBoard(0.30);

		MinDist heuristic = new MinDist();
		heuristic.setBoard(board.arr);

		heuristic.visualize();
	}
}
