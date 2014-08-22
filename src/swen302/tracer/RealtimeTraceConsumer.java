package swen302.tracer;

public interface RealtimeTraceConsumer {
	/**
	 * Called when a trace entry is produced.
	 * TODO refactor into onMethodCalled, onMethodReturn, etc?
	 */
	public void onTraceLine(TraceEntry line);

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
