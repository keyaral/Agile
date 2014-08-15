package swen302.tracer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureTraceConsumer implements Future<Trace>, RealtimeTraceConsumer {

	private Trace t = new Trace();
	private boolean done = false;
	private Throwable exc;

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public Trace get() throws InterruptedException, ExecutionException {
		synchronized(this) {
			while(!done)
				wait();
			if(exc != null)
				throw new ExecutionException(exc);
			return t;
		}
	}

	@Override
	public Trace get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		long end = System.nanoTime() - unit.convert(timeout, TimeUnit.NANOSECONDS);
		synchronized(this) {
			while(!done) {
				long remaining = end - System.nanoTime();
				if(remaining <= 0)
					throw new TimeoutException();
				wait(remaining/1000000, (int)(remaining%1000000));
			}
			return get();
		}
	}

	@Override
	public boolean isCancelled() {
		return false;
	}
	@Override
	public boolean isDone() {
		return false;
	}


	@Override
	public void onTraceLine(String line) {
		t.lines.add(line);
	}

	@Override
	public void onTraceFinish() {
		synchronized(this) {
			done = true;
			notifyAll();
		}
	}

	@Override
	public void onTracerCrash(Throwable t) {
		exc = t;
	}
}
