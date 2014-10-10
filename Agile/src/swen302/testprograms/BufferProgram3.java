package swen302.testprograms;

public class BufferProgram3 {

	static class Buffer1 {
		boolean containsValue = false;
		boolean doesNotContainValue = true;

		void in() {
			if(containsValue) throw new RuntimeException();
			containsValue = true;
			doesNotContainValue = false;
		}

		void out() {
			if(!containsValue) throw new RuntimeException();
			containsValue = false;
			doesNotContainValue = true;
		}
	}

	static class Buffer2 {
		Buffer1 a = new Buffer1();
		Buffer1 b = new Buffer1();

		void in() {
			a.in();
		}
		void move() {
			a.out();
			b.in();
		}
		void out() {
			b.out();
		}
	}

	public static void main(String[] args) {
		Buffer2 b = new Buffer2();

		b.in();
		b.move();
		b.in();
		b.out();
		b.move();
		b.out();

		b.in();
		b.move();
		b.out();

	}
}
