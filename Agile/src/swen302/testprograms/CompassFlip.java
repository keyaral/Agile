package swen302.testprograms;

public class CompassFlip {

	enum Direction
	{
		Up, Down
	}

	public static class Compass
	{
		private Direction direction;

		public Compass()
		{
			this.direction = Direction.Up;
		}

		public void flip() {
			if(direction == Direction.Up)
				direction = Direction.Down;
			else
				direction = Direction.Up;
		}

		public Direction getDirection() { return direction; }
	}

	public static void main(String[] args) throws Exception {
		Compass c = new Compass();

		c.flip();
		c.flip();
		c.flip();
		c.flip();
		c.flip();
		c.flip();
	}
}