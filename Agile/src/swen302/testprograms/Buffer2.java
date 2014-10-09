package swen302.testprograms;

public class Buffer2 {

	static class Buffer {
		boolean a, b;
		void in() {
			if(a) throw new RuntimeException();

			a = true;
		}
		void move() {
			if(!a || b) throw new RuntimeException();

			a = false;
			b = true;
		}
		void out() {
			if(!b) throw new RuntimeException();

			b = false;
		}
	}

	public static void main(String[] args) {
		Buffer b = new Buffer();

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
