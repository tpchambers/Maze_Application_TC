package gui;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import generation.CardinalDirection;
import org.junit.Before;
import org.junit.Test;

import java.awt.event.KeyListener;
import java.util.Arrays;

import generation.Floorplan;
import generation.Maze;
import generation.MazeFactory;
import generation.Order;
import generation.StubOrder;
import gui.Robot.Direction;
import gui.Robot.Turn;
import generation.Order.Builder;
import gui.WallFollower;
import javax.swing.JFrame;



/**
 * Here we test the functionality of our WallFollower
 * I tested on multiple mazes through skill level 9.
 * Each time, I tested with rooms, and the robot did not get caught in a cycle.
 * @author tpchambers
 *
 */
public class WallFollowerTest {
	private BasicRobot robot;
	private RobotDriver driver;
	private MazeFactory factory;
	private StubOrder order;
	private Maze maze;
	private int skill;
	private Floorplan floorplan;
	private Maze order_maze;
	private Controller controller;
	private CardinalDirection current_direction;
	private DistanceSensor sensor;
	private RobotDriver robot_driver; 
	private Maze current_maze;
	private RobotDriver wizard;
	private JFrame test_app;
	/**
	 * Setup works as follows:
	 * We follow the same procedure from last project, but create a new JFrame instance.
	 * This object then adds the controller, a key listener. We then hand the gameplay over to the controller, and add the sensors to the robot.
	 * @throws Exception
	 * 
	 */
	@Before
	public void initialize_test () throws Exception {
			factory = new MazeFactory();
			order = new StubOrder();
			order.setBuildOption(Builder.DFS); //default 
			order.setRoomOption(false);
			order.setSkillLevel(1); 
			order.set_seed(7); 
			factory.order(order);
			factory.waitTillDelivered();
			order_maze = order.getMaze();
			controller = new Controller();
			test_app = new JFrame();
			
			test_app.add(controller.getPanel());
			//controller.setSeed(7);
			KeyListener kl = new SimpleKeyListener(test_app,controller) ;
			test_app.addKeyListener(kl);
			test_app.setSize(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT+22) ;
			test_app.setVisible(true) ;
			test_app.setFocusable(true);
			controller.start();
			controller.switchFromGeneratingToPlaying(order_maze);
			robot = new BasicRobot(controller);
			robot.addDistanceSensor(sensor, Direction.LEFT);
			robot.addDistanceSensor(sensor, Direction.FORWARD);
			robot.addDistanceSensor(sensor, Direction.RIGHT);
			maze = controller.getMazeConfiguration();
			robot_driver = new WallFollower(maze,robot);
			//wizard = new Wizard(maze,robot);
			
	}
	
	/**
	 * Please don't be null.
	 */
	@Test 
	public void not_null () {
		assertNotNull(robot);
		assertNotNull(robot_driver);
	}
	/**
	 * Checking dimensions.
	 */
	@Test 
	public void dimensions() {
		assertTrue(maze == order_maze);
	}
	/**
	 * Checking main method, do we get to the exit properly?
	 * Tested through skill level 9, answer is yes.
	 * @throws Exception
	 */
	@Test
	public void test_driver() throws Exception {
		assertTrue(robot.getCurrentPosition()[0]==0);
		assertTrue(robot_driver.drive2Exit());
		assertTrue(robot.isAtExit());
		assertTrue(robot.distanceToObstacle(Direction.FORWARD)==Integer.MAX_VALUE);
		assertNotNull(robot.getOdometerReading());
	}
	
	/**
	 * This should return true, if the robot driver has moved to an adjacent cell.
	 * @throws Exception
	 */
	@Test
	public void test_driver_one_step() throws Exception {
		assertTrue(robot_driver.drive1Step2Exit());
	}
	
}
