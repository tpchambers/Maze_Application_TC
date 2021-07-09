package gui;

import generation.Maze;
import generation.Distance;
import generation.Order;

import java.util.Arrays;

import generation.CardinalDirection;
import gui.Robot.Direction;
import gui.Robot.Turn;
import generation.Floorplan;

/**
 * WallFollower is the main Robot Driver class implementing RobotDriver. We initiate a WallFollower with reference to the current robot and maze.
 * We call set Robot to point to the current BasicRobot, and establish the current maze which will be passed from the controller.
 * The main method, drive2exit, follows a simple WallFollower algorithm. We check if we are at the exit, if not, we check if we can go left.
 * If so, we turn left. Else, if we can move forward we do so. Otherwise, we have to rotate right. This will allow the robot to drive throughout most mazes without fear of a cycle.
 * @author tpchambers
 *
 */
public class WallFollower implements RobotDriver {

private Maze maze;
private Robot robot;
private Floorplan floorplan;
private int[] current_position;
int count=0;

/**
 * Constructor initializing maze and robot.
 */
public WallFollower (Maze maze,Robot robot) {
	setMaze(maze);
	setRobot(robot);
}

public WallFollower() {
	
}

	@Override
	public void setRobot(Robot robot) {
		// TODO Auto-generated method stub
		this.robot = robot;
	}

	@Override
	public void setMaze(Maze maze) {
		this.maze = maze;

	}
	/**
	 * Main driving method as explained above. We drive to the exit following our solution strategy.
	 * 
	 * Drives the robot towards the exit following
	 * its solution strategy and given the exit exists and  
	 * given the robot's energy supply lasts long enough. 
	 * When the robot reached the exit position and its forward
	 * direction points to the exit the search terminates and 
	 * the method returns true.
	 * If the robot failed due to lack of energy or crashed, the method
	 * throws an exception.
	 * If the method determines that it is not capable of finding the
	 * exit it returns false, for instance, if it determines it runs
	 * in a cycle and can't resolve this.
	 * @return true if driver successfully reaches the exit, false otherwise
	 * @throws Exception thrown if robot stopped due to some problem, e.g. lack of energy
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		while (!robot.isAtExit()) {
			//there is no left wall
				if (robot.distanceToObstacle(Direction.LEFT) > 0) {
					robot.rotate(Turn.LEFT);
					robot.move(1);
					count++;
					Thread.sleep(100);
				}
				//no front wall
				else {
					if (robot.distanceToObstacle(Direction.FORWARD) > 0) {
					robot.move(1);
					count++;
					Thread.sleep(100);
					}
					//rotate right 
					else {
						robot.rotate(Turn.RIGHT);
					}
				//System.out.println(Arrays.toString(robot.getCurrentPosition()));
				}
		}
		//we want forward direction to be pointing towards exit 
		while (robot.distanceToObstacle(Direction.FORWARD) != Integer.MAX_VALUE ) {
			robot.rotate(Turn.LEFT);
		}
		return true;
		//System.out.println(robot.getCurrentDirection());
		}
			
	/**
	 * Drives the robot one step towards the exit following
	 * its solution strategy.
	 * It returns true if the driver successfully moved
	 * the robot from its current location to an adjacent
	 * location.
	 * At the exit position, it rotates the robot 
	 * such that it faces the exit in its forward direction
	 * and returns false. 
	 * If the robot failed due to lack of energy or crashed, the method
	 * throws an exception. 
	 * Logic follows:
	 * 
	 * We follow the same strategy as the Wallfollower, except terminate the robots movement once the robot has moved to an adjacent cell.
	 * This is equivalent to returning true, once we call the move method.
	 * If we move, then we know we are going towards the exit following the same strategy above.
	 * Thus, if this is called, we return true. Otherwise, false is returned at the end as it did not work as planned.
	 * We add a condition to check if there is space available to the right, if so, we move in that direction.
	 * 
	 * @return true if it moved the robot to an adjacent cell, false otherwise
	 */
	@Override
	public boolean drive1Step2Exit() throws Exception {
		//if robot is at exit, we rotate until facing forward direction and return false
		if(robot.isAtExit()) {
			while (robot.distanceToObstacle(Direction.FORWARD) != Integer.MAX_VALUE ) {
				robot.rotate(Turn.LEFT);
				return false;
		}
		}
		//if we can move left move left
		if (robot.distanceToObstacle(Direction.LEFT) > 0) {
			robot.rotate(Turn.LEFT);
			robot.move(1);
			count++;
			Thread.sleep(100);
			return true;
		}
		//no front wall
		else {
			//move forward if possible
			if (robot.distanceToObstacle(Direction.FORWARD) > 0) {
			robot.move(1);
			count++;
			Thread.sleep(100);
			return true;
			}
			//move right if possible
			if (robot.distanceToObstacle(Direction.RIGHT) > 0) {
				robot.rotate(Turn.RIGHT);
				robot.move(1);
				count++;
				Thread.sleep(100);
				return true;
			}
			//instead of rotating right, we have not moved properly so we return false
			//this mean that we encountered a wall when trying to move, or we did not move to an adjacent cell
			else {
				return false;
			}
		}
	}

	@Override
	public float getEnergyConsumption() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	/**
	 * Here we return the path length. This should be the distance traveled by the OdometerReading stored in our robot reference class.
	 */
	public int getPathLength() {
		// TODO Auto-generated method stub
		assert(count == robot.getOdometerReading());
		return robot.getOdometerReading();
	}

}
