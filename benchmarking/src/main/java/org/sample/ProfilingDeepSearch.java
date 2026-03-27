package org.sample;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.StackProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import ubc.team09.eval.KMinDist;
import ubc.team09.eval.QMinDist;
// import ubc.team09.eval.X;
import ubc.team09.player.Util;
import ubc.team09.search.AlphaBeta;
import ubc.team09.state.State;

public class ProfilingDeepSearch {

	private static final int BOARDS_PER_SPARSITY = 5;
	private static final int DEPTH = 6;

	private static final State[] veryDense = new State[BOARDS_PER_SPARSITY];
	private static final State[] veryVeryDense = new State[BOARDS_PER_SPARSITY];
	
	@org.openjdk.jmh.annotations.State(Scope.Thread)
	@Warmup(iterations = 3, batchSize = 1)
	@Measurement(iterations = 1, batchSize = 1)
	@BenchmarkMode(Mode.SingleShotTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Timeout(time = 1, timeUnit = TimeUnit.DAYS)
	@OperationsPerInvocation(1)
	@Fork(1)
	public static class AB {

		private static final KMinDist kmindist = new KMinDist();
		private static final QMinDist qmindist = new QMinDist();
		// private static final X x = new X();

		@Setup(Level.Trial)
		public void setup() {
			for (int i = 0; i < BOARDS_PER_SPARSITY; i++) {
				veryDense[i] = Util.randomBoard(0.60);
				veryVeryDense[i] = Util.randomBoard(0.75);
			}
		}

		@Benchmark
		public void KDepth6VeryDenseBoard(Blackhole bh) {
			for (State s : veryDense) {
				AlphaBeta ab = new AlphaBeta();
				ab.setHeuristic(kmindist);
				ab.setTimeLimit(10000);
				ab.setBoard(s);
				ab.setColor(s.activePlayer);
				ab.setMaxDepth(DEPTH);
				bh.consume(ab.search());				
			}
		}

		@Benchmark
		public void KDepth6VeryVeryDenseBoard(Blackhole bh) {
			for (State s : veryVeryDense) {
				AlphaBeta ab = new AlphaBeta();
				ab.setHeuristic(kmindist);
				ab.setTimeLimit(10000);
				ab.setBoard(s);
				ab.setColor(s.activePlayer);
				ab.setMaxDepth(DEPTH);
				bh.consume(ab.search());				
			}
		}

		@Benchmark
		public void QDepth6VeryDenseBoard(Blackhole bh) {
			for (State s : veryDense) {
				AlphaBeta ab = new AlphaBeta();
				ab.setHeuristic(qmindist);
				ab.setTimeLimit(10000);
				ab.setBoard(s);
				ab.setColor(s.activePlayer);
				ab.setMaxDepth(DEPTH);
				bh.consume(ab.search());				
			}
		}

		@Benchmark
		public void QDepth6VeryVeryDenseBoard(Blackhole bh) {
			for (State s : veryVeryDense) {
				AlphaBeta ab = new AlphaBeta();
				ab.setHeuristic(qmindist);
				ab.setTimeLimit(10000);
				ab.setBoard(s);
				ab.setColor(s.activePlayer);
				ab.setMaxDepth(DEPTH);
				bh.consume(ab.search());				
			}
		}

		public static void main(String[] args) throws RunnerException {
			Options opt = new OptionsBuilder()
				.include(ProfilingShallowSearch.AB.class.getSimpleName())
				.addProfiler(StackProfiler.class)
				// .addProfiler(GCProfiler.class)
				.build();

			new Runner(opt).run();
		}
	}
}
