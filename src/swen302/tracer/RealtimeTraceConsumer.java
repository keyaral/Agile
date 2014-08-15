package swen302.tracer;

public interface RealtimeTraceConsumer {
	/**
	 * Called when a trace line is produced.
	 * TODO refactor into onMethodCalled, onMethodReturn, etc?
	 */
	public void onTraceLine(String line);

	/**
	 * Called when the tracer finishes.
	 * You may assume this is always called under normal circumstances (including an unexpected exception)
	 */
	public void onTraceFinish();

	/**
	 * Called if the tracer encounters an unexpected exception, before onTraceFinish.
	 */
	public void onTracerCrash(Throwable t);
}
