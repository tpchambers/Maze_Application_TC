package gui;
import static org.junit.Assert.assertEquals;
import gui.SimpleKeyListener;
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
import javax.swing.JFrame;


/**
 * Here we test the functionality of our BasicRobot class. This is a detailed test with high coverage.
 * Please change the Thread input to your desire.
 * Additionally, please run each test method separately, so as to watch the gui work. If one runs all the tests at once, multiple different j frames will be instantiated.
 * We setup the test similar to how we did before, but we create a Jframe object to replicate what the main in MazeApplication does.
 * @author tpchambers
 *
 */
public class BasicRobotTest {
private BasicRobot robot;
private RobotDriver driver;
private MazeFactory factory;
private StubOrder order;
private Maze current_maze;
private int skill;
private Floorplan floorplan;
private Maze order_maze;
private Controller controller;
private CardinalDirection current_direction;
private DistanceSensor sensor;
private JFrame test_app;
	/**
	 * I performed most of my tests at skilllevel5, for consistency.
	 * @throws Exception
	 */
	@Before
	public void initialize_test () throws Exception {
			factory = new MazeFactory();
			order = new StubOrder();
			order.setBuildOption(Builder.Prim); //default 
			order.setRoomOption(false);
			order.setSkillLevel(5); 
			order.set_seed(7); 
			factory.order(order);
			factory.waitTillDelivered();
			order_maze = order.getMaze();
			test_app = new JFrame();
			controller = new Controller();
			test_app.add(controller.getPanel());
			KeyListener kl = new SimpleKeyListener(test_app,controller) ;
			test_app.addKeyListener(kl);
			test_app.setSize(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT+22) ;
			test_app.setVisible(true) ;
			test_app.setFocusable(true);
			controller.start();
			controller.switchFromGeneratingToPlaying(order_maze);
			robot = new BasicRobot(controller);
			
	}
	
	public int set_sleeper(int sleeper) {
		return sleeper;
	}
	
	/**
	 * Checking if configurations make sense.
	 */
	@Test 
	public void not_null () {
		assertNotNull(robot.get_maze());
		assertNotNull(robot);
		assertNotNull(controller.getMazeConfiguration());
	}
	
	/**
	 * Checking if dimensions line up.
	 */
	@Test 
	public void dimensions() {
		current_maze = robot.get_maze();
		assertTrue(current_maze == order_maze);
	}
	/**
	 * Checking setup is grabbing the robots configurations properly.
	 * @throws Exception
	 */
	@Test
	public void test_setup() throws Exception {
		try {
			assertNotNull(robot.getCurrentDirection());
			assertNotNull(robot.getCurrentPosition());
		}
		catch (UnsupportedOperationException e) {
			System.out.println("Can't do that.");
		}
		
	}
	/**
	 * Initial movement test with gui.
	 */
	@Test
	public void movement() {
		current_maze = robot.get_maze();
		floorplan = current_maze.getFloorplan();
		try {
			assertTrue(Arrays.equals(robot.getCurrentPosition(),current_maze.getStartingPosition()));
			robot.move(1);
			robot.rotate(Turn.LEFT);
			robot.move(1);
			robot.rotate(Turn.RIGHT);
			robot.move(3);
			assertTrue(!(Arrays.equals(current_maze.getStartingPosition(), robot.getCurrentPosition())));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Checking odometer reading.
	 */
	@Test
	public void distance_traveled() {
		assertNotNull(robot.getOdometerReading());
		robot.move(2);
		robot.rotate(Turn.AROUND);
		assertTrue(robot.getCurrentDirection()==CardinalDirection.West);
		System.out.println(robot.getOdometerReading());
		//assertTrue(robot.getOdometerReading() == 2);
		assertTrue(robot.hasStopped()==true);
	}
	
	/**
	 * Checking an extensive sensor and robot movement test. 
	 * Checking if the sensors are picking up direction, and distance properly.
	 * Checking if robot is keeping track of current position and direction properly. 
	 * @throws Exception
	 */
	@Test
	//tested on skill Level 5, seed 7 
	public void extensive_playthrough() throws Exception {
		int sleeper = set_sleeper(25); //set sleeper to change thread
		//current_maze = robot.get_maze();
		if (order.getSkillLevel() == 5) {
		current_direction = robot.getCurrentDirection();
		assertTrue(robot.getCurrentPosition()[0]==0);
		assertTrue(robot.getCurrentPosition()[1]==0);
		robot.addDistanceSensor(sensor, Direction.FORWARD);
		robot.addDistanceSensor(sensor, Direction.BACKWARD);
		robot.addDistanceSensor(sensor, Direction.RIGHT);
		robot.addDistanceSensor(sensor, Direction.LEFT);
		assertTrue((robot.distanceToObstacle(Direction.FORWARD)==1));
		//position should not have changed since there is a wall
		robot.move(2);
		Thread.sleep(sleeper);
		robot.rotate(Turn.LEFT);
		Thread.sleep(sleeper);
		robot.rotate(Turn.RIGHT);
		Thread.sleep(sleeper);
		assertTrue(robot.distanceToObstacle(Direction.LEFT)==6);
		robot.rotate(Turn.LEFT);
		Thread.sleep(sleeper);
		assertTrue(robot.distanceToObstacle(Direction.FORWARD)==6);
		robot.move(6);
		Thread.sleep(sleeper);
		robot.rotate(Turn.RIGHT); // East [1,6]
		assertTrue(robot.getCurrentPosition()[1]==6);
		robot.move(3);
		Thread.sleep(sleeper);
		assertTrue(robot.distanceToObstacle(Direction.BACKWARD)==4);
		robot.rotate(Turn.LEFT);
		Thread.sleep(sleeper);
		assertTrue(robot.distanceToObstacle(Direction.FORWARD)==9);
		assertTrue(robot.distanceToObstacle(Direction.BACKWARD)==0);
		assertTrue(robot.distanceToObstacle(Direction.LEFT)==4);
		}
	}
	
	/**
	 * Testing at skill level 0, to see if calling isAtExit() is true.
	 */
	@Test
	//tested on skill level 0 
	public void test_exit () {
		if (order.getSkillLevel() == 0 ) {
			robot.rotate(Turn.LEFT);
			robot.addDistanceSensor(sensor, Direction.LEFT);
			assertTrue(robot.distanceToObstacle(Direction.LEFT)==Integer.MAX_VALUE);
			assertTrue(robot.canSeeThroughTheExitIntoEternity(Direction.LEFT));
			robot.rotate(Turn.LEFT);
			//we are now facing the exit 
			assertTrue(robot.isAtExit());
			
		}
	}
	
}
