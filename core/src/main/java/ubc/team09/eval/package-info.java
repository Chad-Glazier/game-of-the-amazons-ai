/**
 * This package is where static evaluation functions are implemented.
 * 
 * <ul>
 * 	<li>{@link HeuristicMethod} defines the general interface of a heuristic 
 * 	method.</li>
 * 	<li>{@link KMinDist} is a heuristic method which partitions territory based
 * 	on the number of king moves it would take for queen to get to a given 
 * 	square.</li>
 * 	<li>{@link QMinDist} is a heuristic method identical to {@link KMinDist},
 * 	except that it evaluates territories based on queen moves instead of king
 * 	moves.</li>
 * 	<li>{@link X} is a heuristic method that is a little more involved than 
 * 	{@link QMinDist} and {@link KMinDist}, however we have found it 
 * 	unremarkable.
 * </ul>
 */
package ubc.team09.eval;
