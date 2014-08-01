package swen302.testprograms;

public class CompassRotating {

	enum Point { North, East, South, West }
	enum Direction { Left, Right }

	public final static int LAST_POINT_ORDINAL = Point.values().length-1;


	public static class Compass
	{
		private Point point;
		//private int count = 0;

		public Compass()
		{
			this.point = Point.North;
		}

		public Point rotate90(Direction direction)
		{
			//count = (count+1)%6;
			Point[] values = Point.values();
			int ordinal = point.ordinal();

			if (direction == Direction.Left)
			{
				if (point.ordinal() == 0)
				{
					this.point = values[LAST_POINT_ORDINAL];
				}
				else
				{
					this.point = values[ordinal-1];
				}
			}
			else if (direction == Direction.Right)
			{
				if (point.ordinal() == LAST_POINT_ORDINAL)
				{
					this.point = values[0];
				}
				else
				{
					this.point = values[ordinal+1];
				}
			}



			return this.point;
		}
	}

	public static void main(String[] args) throws Exception {
		Compass c = new Compass();

		c.rotate90(Direction.Left);
		c.rotate90(Direction.Left);
		c.rotate90(Direction.Left);
		c.rotate90(Direction.Left);
		c.rotate90(Direction.Left);
		c.rotate90(Direction.Left);
		c.rotate90(Direction.Right);
		c.rotate90(Direction.Left);
		c.rotate90(Direction.Right);
		c.rotate90(Direction.Left);
		c.rotate90(Direction.Right);
		c.rotate90(Direction.Right);
		c.rotate90(Direction.Right);
		c.rotate90(Direction.Right);
		c.rotate90(Direction.Right);
		c.rotate90(Direction.Right);
		c.rotate90(Direction.Right);
		c.rotate90(Direction.Right);
		c.rotate90(Direction.Left);
		c.rotate90(Direction.Left);
		c.rotate90(Direction.Left);
		c.rotate90(Direction.Right);
	}
}