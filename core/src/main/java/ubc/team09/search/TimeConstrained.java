package ubc.team09.search;

import java.util.concurrent.TimeoutException;

/**
 * This class should be extended if you want to impose a time constraint on 
 * some process. To use it, 
 * <ol>
 * 	<li> use {@link #setTimeLimit} to set the time limit.
 * 	<li> use {@link #startTimer} to start the countdown.
 * 	<li> whenever you need to check the time limit, use 
 * 	{@link #checkTimeOccasionally} or {@link #checkTime}. 
 * 	Either of these methods will throw an exception if the time limit is 
 * 	expired. The former will only actually check the time once every ~1000
 * 	calls.
 * </ol> 
 */
public class TimeConstrained {
	private long endTimeMs = 0;
	private long timeLimitMs = 10 * 1000;

	public void setTimeLimit(int seconds) {
		this.timeLimitMs = seconds * 1000;
	}

	public void setTimeLimitMs(long milliseconds) {
		this.timeLimitMs = milliseconds;
	}

	protected void startTimer() {
		this.endTimeMs = timeLimitMs + System.currentTimeMillis();
	}

	protected void checkTime() throws TimeoutException {
		if (System.currentTimeMillis() > endTimeMs) {
			throw new TimeoutException();
		}
	}

	private int callsSinceLastCheck = 0;

	protected void checkTimeOccasionally() throws TimeoutException {
		if ((++callsSinceLastCheck & 1023) == 0) {
			if (System.currentTimeMillis() > endTimeMs) {
				throw new TimeoutException();
			}
		}
	}
}
