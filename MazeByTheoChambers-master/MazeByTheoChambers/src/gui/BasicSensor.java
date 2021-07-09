package gui;

import generation.CardinalDirection;
import generation.Floorplan;
import generation.Maze;
import gui.Robot.Direction;

public class BasicSensor implements DistanceSensor {

private CardinalDirection current_direction;
private Maze maze; 
private Direction mountedDirection;
private Floorplan floorplan;
private DistanceSensor sensor;
private Controller controller;


public BasicSensor ( DistanceSensor sensor, Direction mountedDirection,Maze maze,CardinalDirection current_direction) {
	this.sensor = sensor;
	this.mountedDirection = mountedDirection;
	this.maze = maze;
	this.current_direction = current_direction;
}

public BasicSensor ( DistanceSensor sensor, Direction mountedDirection,Controller controller,CardinalDirection current_direction) {
	this.sensor = sensor;
	this.mountedDirection = mountedDirection;
	this.maze = controller.getMazeConfiguration();
	this.current_direction = current_direction;
}
/**
 * Tells the distance to an obstacle (a wallboard) that the sensor
 * measures. The sensor is assumed to be mounted in a particular
 * direction relative to the forward direction of the robot.
 * Distance is measured in the number of cells towards that obstacle, 
 * e.g. 0 if the current cell has a wallboard in this direction, 
 * 1 if it is one step in this direction before directly facing a wallboard,
 * Integer.MaxValue if one looks through the exit into eternity.
 * 
 * This method requires that the sensor has been given a reference
 * to the current maze and a mountedDirection by calling 
 * the corresponding set methods with a parameterized constructor.
 * 
 * @param currentPosition is the current location as (x,y) coordinates
 * @param currentDirection specifies the direction of the robot
 * @param powersupply is an array of length 1, whose content is modified 
 * to account for the power consumption for sensing
 * @return number of steps towards obstacle if obstacle is visible 
 * in a straight line of sight, Integer.MAX_VALUE otherwise.
 */
	@Override
	public int distanceToObstacle(int[] currentPosition, CardinalDirection currentDirection)
			throws Exception {
		
		
		//we need to keep track of the current position without actually changing the position 
		int distance_count = 0;
		int current_x = currentPosition[0];
		int current_y = currentPosition[1];
		
		//logic follows:
		// we need a loop so we initiate a while loop setting boolean to true, it will return a value anyways
		// we then check each direction
		//for each direction, if there is a wall in that direction called by the current direction which has been updated,
		//we return the distance count (incremented for each step)
		//otherwise, we increment the current position for the x and y value, without actually updating the values
		//this continues until we reach a wall and return
		//otherwise, we reach an exit and return integer max value 
		while (true) {
			if (current_x < 0 ||  current_y <0 || current_x >= maze.getWidth() ||current_y >= maze.getHeight()) {
				return Integer.MAX_VALUE;
			}
			else {
				if (currentDirection == CardinalDirection.North) {
					if(maze.hasWall(current_x, current_y, CardinalDirection.North)) {
						//return count cause we are done 
						return distance_count;
					}
					current_y--; //decrement y position 
				}
				else if (currentDirection == CardinalDirection.East) {
					if (maze.hasWall(current_x, current_y, CardinalDirection.East)) {
						return distance_count;
					}//decrement x for east 
					current_x++;
				}
				else if (currentDirection == CardinalDirection.South) {
					if (maze.hasWall(current_x,current_y,CardinalDirection.South)) {
						return distance_count;
					}//increment y since we are moving in south direction
					current_y++;
				}
				else if (currentDirection == CardinalDirection.West) { 
					if (maze.hasWall(current_x, current_y, CardinalDirection.West)) {
						return distance_count;
					} // decrement x 
					current_x--;
				}
				//increment count 
				distance_count ++;
				
			}
		}
	}

	@Override
	public void setMaze(Maze maze) {
		this.maze=maze;

	}
	
	/**
	 * Provides the angle, the relative direction at which this 
	 * sensor is mounted on the robot.
	 * If the direction is left, then the sensor is pointing
	 * towards the left hand side of the robot at a 90 degree
	 * angle from the forward direction. 
	 * @param mountedDirection is the sensor's relative direction
	 * @throws IllegalArgumentException if parameter is null
	 */
	@Override
	public void setSensorDirection(Direction mountedDirection) {
		this.mountedDirection = mountedDirection;
	}

	@Override
	public float getEnergyConsumptionForSensing() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void startFailureAndRepairProcess(int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopFailureAndRepairProcess() throws UnsupportedOperationException {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Return the current mounted direction stored with respect to the robot.
	 */
	public Direction getmountedDirection() {
		return this.mountedDirection;
	}

}
