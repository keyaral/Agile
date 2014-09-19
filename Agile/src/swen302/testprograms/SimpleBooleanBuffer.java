package swen302.testprograms;

public class SimpleBooleanBuffer {

	public static class BooleanBuffer
	{
		
		private boolean[] buffer;
		
		private int size;
		
		public BooleanBuffer()
		{
			this.buffer = new boolean[3];
			this.size = 0;
		}
		
		public boolean pop()
		{
			return buffer[--size];	
		}
		
		public void push(boolean b)
		{
			buffer[size++] = b;
		}
	}

	public static void main(String[] args) throws Exception {
		BooleanBuffer bb = new BooleanBuffer();
		
		bb.push(false);
		bb.push(false);
		bb.push(false);
		bb.pop();
		bb.push(true);
		bb.pop();
		bb.pop();
		bb.pop();
		bb.push(true);
		bb.push(true);
		bb.push(true);
		bb.pop();
		bb.push(false);
		bb.pop();
		bb.push(false);
		bb.pop();
		bb.push(true);
		
	}
}