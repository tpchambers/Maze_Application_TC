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
 * Here, we test the implementation of the BasicSensor class.
 * This creates a BasicRobot to test a maze with gui implementation.
 * @author tpchambers
 *
 */
public class BasicSensorTest {
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

		@Before
		public void initialize_test () throws Exception {
				factory = new MazeFactory();
				order = new StubOrder();
				order.setBuildOption(Builder.Prim); //default 
				order.setRoomOption(false);
				order.setSkillLevel(2); 
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
		
		@Test 
		public void not_null () {
			assertNotNull(robot.get_maze());
			assertNotNull(robot);
			assertNotNull(controller.getMazeConfiguration());
		}
		public int set_sleeper(int sleeper) {
			return sleeper;
		}
		
		/**
		 * Method checks to see if the distance registered is correct in multiple locations.
		 * Method checks if the distance is being registered properly with the respective direction.
		 * @throws InterruptedException
		 */
		@Test //tested on skilllevel2
		public void sensors_functional() throws InterruptedException {
			int sleeper = set_sleeper(55); //set sleeper to change thread
			if (order.getSkillLevel()==2) {
				robot.move(1);
				Thread.sleep(sleeper);
				robot.addDistanceSensor(sensor, Direction.BACKWARD);
				robot.addDistanceSensor(sensor, Direction.FORWARD);
				robot.addDistanceSensor(sensor, Direction.RIGHT);
				robot.addDistanceSensor(sensor, Direction.LEFT);
				assertTrue(robot.distanceToObstacle(Direction.LEFT)==1);
				assertTrue(robot.distanceToObstacle(Direction.FORWARD)==0);
				assertTrue(robot.getCurrentDirection()==CardinalDirection.East);
				robot.rotate(Turn.LEFT);
				robot.move(1);
				Thread.sleep(sleeper);
				assertTrue(robot.distanceToObstacle(Direction.RIGHT)==1);
				assertTrue(robot.distanceToObstacle(Direction.BACKWARD)==1);
				robot.rotate(Turn.RIGHT);
				robot.move(1);
				Thread.sleep(sleeper);
				robot.rotate(Turn.LEFT);
				robot.move(1);
				Thread.sleep(sleeper);
				assertTrue(robot.distanceToObstacle(Direction.FORWARD)==0);
				robot.rotate(Turn.LEFT);
				robot.move(1);
				assertTrue(robot.distanceToObstacle(Direction.RIGHT)==3);
				assertTrue(robot.getOdometerReading()==5);
				
				
				
				
				
			}
		}
}
