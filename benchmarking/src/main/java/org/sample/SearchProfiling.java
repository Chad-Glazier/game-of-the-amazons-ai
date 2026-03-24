package org.sample;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
// import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import ubc.team09.eval.KMinDist;
import ubc.team09.eval.QMinDist;
import ubc.team09.eval.X;
import ubc.team09.player.Util;
import ubc.team09.search.AlphaBeta;
import ubc.team09.state.State;

public class SearchProfiling {
	
	@org.openjdk.jmh.annotations.State(Scope.Thread)
	@Warmup(iterations = 2, batchSize = 1)
	@Measurement(iterations = 2, batchSize = 1)
	@BenchmarkMode(Mode.SingleShotTime)
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	@OperationsPerInvocation(1)
	@Fork(1)
	public static class ABwithKMinDist {

		private static final State initial = Util.initialBoard();
		private static final State sparse = Util.randomBoard(0.10);
		private static final State dense = Util.randomBoard(0.40);

		private static final KMinDist kmindist = new KMinDist();

		@Benchmark
		@Timeout(time = 1, timeUnit = TimeUnit.DAYS)
		public void Depth3InitialBoard(Blackhole bh) {
			AlphaBeta ab = new AlphaBeta(
				initial,
				kmindist,
				initial.activePlayer
			);
			ab.setMaxDepth(3);
			ab.setTimeLimit(1000);

			bh.consume(ab.search());
		}

		@Benchmark
		@Timeout(time = 1, timeUnit = TimeUnit.DAYS)
		public void Depth3SparseBoard(Blackhole bh) {
			AlphaBeta ab = new AlphaBeta(
				sparse,
				kmindist,
				sparse.activePlayer
			);
			ab.setMaxDepth(3);
			ab.setTimeLimit(1000);

			bh.consume(ab.search());
		}

		@Benchmark
		@Timeout(time = 1, timeUnit = TimeUnit.DAYS)
		public void Depth5DenseBoard(Blackhole bh) {
			AlphaBeta ab = new AlphaBeta(
				dense,
				kmindist,
				dense.activePlayer
			);
			ab.setMaxDepth(5);
			ab.setTimeLimit(1000);

			bh.consume(ab.search());
		}

		public static void main(String[] args) throws RunnerException {
			Options opt = new OptionsBuilder()
					.include(SearchProfiling.ABwithKMinDist.class.getSimpleName())
					.addProfiler(StackProfiler.class)
					// .addProfiler(GCProfiler.class)
					.build();

			new Runner(opt).run();
		}
	}

	@org.openjdk.jmh.annotations.State(Scope.Thread)
	@Warmup(iterations = 2, batchSize = 1)
	@Measurement(iterations = 2, batchSize = 1)
	@BenchmarkMode(Mode.SingleShotTime)
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	@OperationsPerInvocation(1)
	@Fork(1)
	public static class ABwithQMinDist {

		private static final State initial = Util.initialBoard();
		private static final State sparse = Util.randomBoard(0.10);
		private static final State dense = Util.randomBoard(0.40);

		private static final QMinDist qmindist = new QMinDist();

		@Benchmark
		@Timeout(time = 1, timeUnit = TimeUnit.DAYS)
		public void Depth3InitialBoard(Blackhole bh) {
			AlphaBeta ab = new AlphaBeta(
				initial,
				qmindist,
				initial.activePlayer
			);
			ab.setMaxDepth(3);
			ab.setTimeLimit(1000);

			bh.consume(ab.search());
		}

		@Benchmark
		@Timeout(time = 1, timeUnit = TimeUnit.DAYS)
		public void Depth3SparseBoard(Blackhole bh) {
			AlphaBeta ab = new AlphaBeta(
				sparse,
				qmindist,
				sparse.activePlayer
			);
			ab.setMaxDepth(3);
			ab.setTimeLimit(1000);

			bh.consume(ab.search());
		}

		@Benchmark
		@Timeout(time = 1, timeUnit = TimeUnit.DAYS)
		public void Depth5DenseBoard(Blackhole bh) {
			AlphaBeta ab = new AlphaBeta(
				dense,
				qmindist,
				dense.activePlayer
			);
			ab.setMaxDepth(5);
			ab.setTimeLimit(1000);

			bh.consume(ab.search());
		}

		public static void main(String[] args) throws RunnerException {
			Options opt = new OptionsBuilder()
					.include(SearchProfiling.ABwithQMinDist.class.getSimpleName())
					.addProfiler(StackProfiler.class)
					// .addProfiler(GCProfiler.class)
					.build();

			new Runner(opt).run();
		}
	}


	@org.openjdk.jmh.annotations.State(Scope.Thread)
	@Warmup(iterations = 2, batchSize = 1)
	@Measurement(iterations = 2, batchSize = 1)
	@BenchmarkMode(Mode.SingleShotTime)
	@OutputTimeUnit(TimeUnit.NANOSECONDS)
	@OperationsPerInvocation(1)
	@Fork(1)
	public static class ABwithX {

		private static final State initial = Util.initialBoard();
		private static final State sparse = Util.randomBoard(0.10);
		private static final State dense = Util.randomBoard(0.40);

		private static final X x = new X();

		@Benchmark
		@Timeout(time = 1, timeUnit = TimeUnit.DAYS)
		public void Depth3InitialBoard(Blackhole bh) {
			AlphaBeta ab = new AlphaBeta(
				initial,
				x,
				initial.activePlayer
			);
			ab.setMaxDepth(3);
			ab.setTimeLimit(1000);

			bh.consume(ab.search());
		}

		@Benchmark
		@Timeout(time = 1, timeUnit = TimeUnit.DAYS)
		public void Depth3SparseBoard(Blackhole bh) {
			AlphaBeta ab = new AlphaBeta(
				sparse,
				x,
				sparse.activePlayer
			);
			ab.setMaxDepth(3);
			ab.setTimeLimit(1000);

			bh.consume(ab.search());
		}

		@Benchmark
		@Timeout(time = 1, timeUnit = TimeUnit.DAYS)
		public void Depth5DenseBoard(Blackhole bh) {
			AlphaBeta ab = new AlphaBeta(
				dense,
				x,
				dense.activePlayer
			);
			ab.setMaxDepth(5);
			ab.setTimeLimit(1000);

			bh.consume(ab.search());
		}

		public static void main(String[] args) throws RunnerException {
			Options opt = new OptionsBuilder()
					.include(SearchProfiling.ABwithX.class.getSimpleName())
					.addProfiler(StackProfiler.class)
					// .addProfiler(GCProfiler.class)
					.build();

			new Runner(opt).run();
		}
	}
}
