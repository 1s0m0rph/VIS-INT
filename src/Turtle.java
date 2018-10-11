public class Turtle
{
	public int direction;
	public int x, y;
	public Turtle()
	{
		direction = 0;
		x = 0;
		y = 0;
	}
	
	public Turtle(int x, int y)
	{
		direction = 0;
		this.x = x;
		this.y = y;
	}
	
	void turnRight()
	{
		direction = (direction + 1) % 4;
	}
	
	void turnLeft()
	{
		direction = (direction - 1) % 4;
		if(direction < 0)
			direction = 4 + direction;
	}
	
	void stepForward()
	{
		switch(direction)
		{
			case 0:
				//down
				y += 1;
				break;
			case 1:
				//left
				x -= 1;
				break;
			case 2:
				//up
				y -= 1;
				break;
			case 3:
				//right
				x += 1;
		}
	}
}
