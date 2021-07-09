package gui;

import generation.CardinalDirection;
import generation.Floorplan;
import generation.Maze;
import gui.Constants.UserInput;

public class BasicRobot implements Robot {
private Controller controller;
private int[] current_position;
private int[] curr_position;
private CardinalDirection current_direction;
private Floorplan floorplan;
private DistanceSensor right_sensor;
private DistanceSensor left_sensor;
private DistanceSensor forward_sensor;
private DistanceSensor backward_sensor;
private Maze maze;
private float battery;
private boolean hasStopped;
public BasicRobot robot;
private int odometer;
private int pathlength;


/**
 * Constructor for BasicRobot class. We receive a parameter of the controller to call setController.
 * This then calls the method which creates the maze configuration reference.
 * We originally set the odometer to zero, and all four sensors pointing to null.
 * @param controller
 */
public BasicRobot(Controller controller) {
	this.setController(controller);
	hasStopped = false;
	this.odometer = 0;
	this.right_sensor = null;
	this.left_sensor = null;
	this.forward_sensor = null;
	this.backward_sensor = null;
	this.floorplan = maze.getFloorplan();
	this.maze = controller.getMazeConfiguration();
	
}

public Maze get_maze() {
	return this.maze;
	
}

/**
 * Provides the robot with a reference to the controller. We use the controller to update the current position and current direction of the maze.
 */
	@Override
	public void setController(Controller controller) {
		// TODO Auto-generated method stub
		this.controller = controller; 
		maze = controller.getMazeConfiguration();
		this.current_position = controller.getCurrentPosition();
		this.current_direction = controller.getCurrentDirection();
	} 

	/**
	 * We add four different distance sensors to the robot. Each time this method is called, based on the direction desired we add a new instance of that sensor pointing in the mounted direction.
	 */
	@Override
	public void addDistanceSensor(DistanceSensor sensor, Direction mountedDirection) {
		if (mountedDirection == Direction.LEFT) {
			this.left_sensor= new BasicSensor(left_sensor, mountedDirection,this.maze,this.current_direction);
		}
		else if (mountedDirection == Direction.RIGHT) {
			this.right_sensor = new BasicSensor(right_sensor,mountedDirection,this.maze,this.current_direction);
		}
		else if (mountedDirection == Direction.FORWARD) {
			this.forward_sensor = new BasicSensor(forward_sensor,mountedDirection,this.maze,this.current_direction);
		}
		else if (mountedDirection == Direction.BACKWARD) {
			this.backward_sensor = new BasicSensor(backward_sensor,mountedDirection,this.maze,this.current_direction);
		}
	}
	

	@Override
	/**
	 * Returns the current position stored from the instance of the class.
	 */
	public int[] getCurrentPosition() throws Exception {
		// TODO Auto-generated method stub
		return this.current_position;
	}
	
	/**
	 * Returns the current direction stored from the instance of the class.
	 */
	@Override
	public CardinalDirection getCurrentDirection() {
		// TODO Auto-generated method stub
		return current_direction;
	}

	@Override
	public float getBatteryLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBatteryLevel(float level) {
		
	}

	@Override
	public float getEnergyForFullRotation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getEnergyForStepForward() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	/**
	 * Returns the current odometer reading that refers to the current path length traveled.
	 */
	public int getOdometerReading() {
		// TODO Auto-generated method stub
		return this.odometer;
	}
	
	/**
	 * We reset the odometer to zero.
	 */
	@Override
	public void resetOdometer() {
		this.odometer = 0;
	}
	
	/**
	 * Here we rotate using the controller to refer to the gui. For each direction passed, we first update the current direction based on the parameter, then
	 * we call keyDown, UserInput.(parameter) to make the robot move directly with the gui.
	 */
	@Override
	public void rotate(Turn turn) {
		if (turn == Turn.RIGHT) {
			this.current_direction = this.current_direction.rotateClockwise();
			this.controller.keyDown(UserInput.RIGHT,-1);
		}
		else if (turn == Turn.LEFT) {
			this.current_direction = this.current_direction.rotateleft();
			this.controller.keyDown(UserInput.LEFT,1);
		}
		else if (turn==Turn.AROUND) {
			this.current_direction = this.current_direction.oppositeDirection();
			this.controller.keyDown(UserInput.LEFT,1);
			this.controller.keyDown(UserInput.LEFT,1);
		}

	}
	/**
	 *We move forward a certain distance passed as long as the maze has no wall in the current position and as long as the current position is in bounds.
	 *We call controller.KeyDown(UserInput.Up,1) while the conditions are true. We decrement the distance to terminate the loop conditional, and increment the odometer for the distance traveled.
	 *After the loop terminates, we update the current position and set hasStopped to true.
	 */
	@Override
	public void move(int distance) {
		pathlength = 0;
		while (distance !=0 && !(maze.hasWall(current_position[0], current_position[1], current_direction)) && current_position[0] >=0 && current_position[1] >=0) {
			controller.keyDown(UserInput.UP, 1);
			distance--;
			odometer ++;
		}
		this.current_position[0] = controller.getCurrentPosition()[0];
		this.current_position[1] = controller.getCurrentPosition()[1];
		hasStopped = true;
		
	}
	
	/**
	 * Makes robot move in a forward direction even if there is a wall
	 * in front of it.
	 */
	@Override
	public void jump() {
		controller.keyDown(UserInput.JUMP, 1);
	}
	
	/**
	 * If the current floorplan is at the exit position at our current position, we return true, otherwise we return false.
	 */
	@Override
	public boolean isAtExit() {
		floorplan = this.maze.getFloorplan();
		if (floorplan.isExitPosition(current_position[0], current_position[1])) {
			return true;
		}
		else {return false;}
	}
	
	/**
	 * We check to inspect if the current position is inside a room. We use the current floorplan to check if this is the case. 
	 */
	@Override
	public boolean isInsideRoom() {
		// TODO Auto-generated method stub
		floorplan = this.maze.getFloorplan();
		if (floorplan.isInRoom(current_position[0], current_position[1])) {
			return true;
		}
		else {return false;}
	}

	@Override
	public boolean hasStopped() {
		// TODO Auto-generated method stub
		return hasStopped;
	}
	
	/**
	 *Here, we use the direction parameter to return how far away a wall is from the robots current position.
	 *We first assert if the robot does in fact, have a sensor in the direction desired. If not, it will throw an UnsupportedOperationException.
	 *Then, for each direction requested, we set count equal to the integer returned by calling distanceToObstacle from the current sensor reference.
	 *Thus, for a left sensor, we would say count = this.left_sensor.distanceToObstacle. The parameters would be the current_position, and the currentdirection updated to rotate left.
	 *In this way, the direction passed into the BasicSensor method will calculate the distance with respect to the robots current direction, without actually updating the robots current direction.
	 *We insert these return values in try and catch statements to catch exceptions and print their stack traces if applicable.
	 * @throws UnsupportedOperationException
	 */
	@Override
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		//current_direction = current_maze.getCurrentDirection();
		//current_position = current_maze.getCurrentPosition();
		int count = 0;
		if(!has_sensor(direction)) {
			throw new UnsupportedOperationException();
		}
			if (direction == Direction.LEFT) {
				try {
					count = this.left_sensor.distanceToObstacle(current_position, current_direction.rotateleft());
					return count;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			else if (direction == Direction.RIGHT) {
				try {
					count = this.right_sensor.distanceToObstacle(current_position, current_direction.rotateClockwise());
					return count;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(direction == Direction.BACKWARD) {
				try {
					count = this.backward_sensor.distanceToObstacle(current_position, current_direction.oppositeDirection());
					return count;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
			else {
				this.addDistanceSensor(forward_sensor, Direction.FORWARD);
				try {
					count = this.forward_sensor.distanceToObstacle(current_position, current_direction);
					return count;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return count;
	}
	/**
	 * Tells if a sensor can identify the exit in the given direction relative to its current sensor.
	 * @return true if the exit of the maze is visible in a straight line of sight
	 * @throws UnsupportedOperationException if robot has no sensor in this direction
	  */

	@Override
	public boolean canSeeThroughTheExitIntoEternity(Direction direction) throws UnsupportedOperationException {
		if (!(this.has_sensor(direction))) {
			throw new UnsupportedOperationException();
		}
		else if(distanceToObstacle(direction) != Integer.MAX_VALUE) {
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public void startFailureAndRepairProcess(Direction direction, int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopFailureAndRepairProcess(Direction direction) throws UnsupportedOperationException {
		// TODO Auto-generated method stub

	}
	
	/**
	 * We check if the current instance does, in fact, have a reference to the sensor in the appropriate direction. 
	 * This method is useful to ensure that when calling add sensor, the pointer is updated properly to add the new sensor reference in memory.
	 * Because we begin with a null pointer in the beginning when calling the constructor, we will know that we are in fact adding a sensor properly by using this boolean method.
	 * @param direction
	 * @return
	 */
	public boolean has_sensor(Direction direction) {
		
		if (direction==Direction.FORWARD) {
			if (this.forward_sensor != null) {
				return true;
			}
		}
		else if (direction == Direction.BACKWARD) {
			if (this.backward_sensor != null) {
				return true;
			}
		}
		else if (direction == Direction.RIGHT) {
			if (this.right_sensor != null) {
				return true;
				}
		}
		else if (direction == Direction.LEFT) {
			if (this.left_sensor != null) {
				return true;
				}
		}
		return false;
	}

}
