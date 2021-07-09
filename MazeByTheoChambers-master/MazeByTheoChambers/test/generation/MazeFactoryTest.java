package generation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import generation.Order.Builder;
import gui.Constants;
import java.util.ArrayList;
import java.util.Collections;
import generation.MazeBuilderKruskal;

//Pseudocode for test cases written in comments 
/**We begin by creating the steps necessary to create an order.
 * As an order is processed through a factory, and the mazefactory operates a background thread, we need to make the test wait for termination of mazebuilder thread.
 * We will begin by setting up the test with the appropriate variables, a factory, order, the current maze configuration, and the floorplan. 
 */
public class MazeFactoryTest {
	private MazeFactory factory;
	private StubOrder order;
	private Maze current_maze;
	private int skill;
	private Floorplan floorplan;
	
	//initial methods to use for testing mazebuilder 
	//we make initial public to allow MazeBuilderKruskal to override the method
	
	/**
	 * We setup the chronology required to check properties of the generated maze.
	 * This method creates a new instance of factory, and a new instance of StubOrder.
	 * We then set the seed, the build option, perfect option (to ensure rooms are created), and the skill level.
	 * Once complete, the method calls an order on factory, with the order created above for testing.
	 *We then call  the method wait Till Delivered to make test wait for termination of the mazebuilder thread.
	 *Lastly, we return the current maze configuration object.
	 */
	@Before
	public void initialize_test () {
			factory = new MazeFactory();
			order = new StubOrder();
			order.setBuildOption(Builder.Prim); //default 
			order.setRoomOption(false);
			order.setSkillLevel(5); 
			order.set_seed(7); 
			factory.order(order);
			factory.waitTillDelivered();
			current_maze = order.getMaze();
	}
	// we check to make sure none of the objects created are null 
	@Test 
	public void not_null () {
	
		assertNotNull(factory);
		assertNotNull(order);
		assertNotNull(current_maze);
		assertNotNull(current_maze.getFloorplan());
	}
	
	/** Make sure that calling method is_perfect on our Order object is not true, as I am usually testing with a maze with multiple rooms.
	*Change to == true if the maze is perfect.
	 */
	@Test
	public void order_not_perfect () {
		assertTrue (order.isPerfect() != true);
		//assertTrue(order.getBuilder()!=Builder.Kruskal); run to make sure we are testing kruskal builder method
	}
	/**This method tests the inputs to ensure that the current maze is being constructed properly.
	 * We first check that the inputs are correctly matched up to the floorplan of the current maze configuration.
	 * We then check that the width of floorplan is same as currentmaze width.
	 * Finally, we check that the skill generated width properly, as Constants.skill_X/Y decides the properties of the mazes including the configuration of the walls.
	 */
	@Test
	public void test_inputs () {
		int skill = order.getSkillLevel();
		floorplan = current_maze.getFloorplan();
		// Skill-level 
		// The user picks a skill level between 0 - 9, a-f 
		// The following arrays transform this into corresponding dimensions (x,y)
		// for the resulting maze as well as the number of rooms and parts
		// Example: level 3 is a 20 x 15 maze with at most 3 randomly positioned rooms
		assertEquals(current_maze.getWidth(),Constants.SKILL_X[skill]);
		assertEquals(current_maze.getHeight(),Constants.SKILL_Y[skill]);
		assertEquals(floorplan.getWidth(),Constants.SKILL_X[skill]);
		assertEquals(floorplan.getHeight(),Constants.SKILL_Y[skill]);
		assertEquals(floorplan.getWidth(),current_maze.getWidth());
	}
	
	/**This method tests that there is only one entrance to the maze.
	 * To test this, we create a data structure to maintain list of distances for each cell.
	 * We ensure that there is only a single maximum distance to the exit, since the maximum distance is the entrance.
	 * If this is false, test fails as this means there are two entrances.
	 * This method uses a helper function to check for max in array - Collections.max for this function.
	 * We use an ArrayList for the data structure.
	 */
	@Test
	public void test_entrance () {
		floorplan = current_maze.getFloorplan();
		ArrayList<Integer> distance_list = new ArrayList<Integer>();
		int [][] curr_dimensions = new int[floorplan.getWidth()][floorplan.getHeight()];
		for (int w = 0; w< curr_dimensions[0].length;w++) {
			for (int h=0;h < curr_dimensions[1].length;h++) {
				 {
					distance_list.add(current_maze.getDistanceToExit(w, h));
				}
			}
	
		}
		assertTrue(get_count(distance_list) == 1);
	}
	//helper function for entrance test
		public int get_count (ArrayList<Integer> array) {
			// call collections to use static method max
			int max = Collections.max(array);
			int count = 0;
			if (array.contains(max)) { //if array has maximum number, count increments
				count ++;
			}
			//should be one
			return count;
		}
	/**Testing if there is a distance to exit from each point.
	* If this is true for each point, then we know each cell has a path to exit.
	* We get the distance to exit from each point, and it should be greater than 0 for each point.
	*/
	@Test
	public void exit_possible() 
		{
			for (int w = 0; w < current_maze.getWidth();w++) {
				for (int h = 0; h < current_maze.getHeight(); h++) {
					int count = current_maze.getDistanceToExit(w, h);
					assertTrue(count !=0);
				}
			}
	}
	
	/**Testing exit to check if there is a single instance where the distance to the exit is equal to one.
	 * This is because the distance to exit can only be a one at minimum.
	 * We iterate through dimensions of floorplan width and height, initialize a counter to ensure there is only one instance of the distance equaling one.
	 */
	@Test
	public void test_exit() {
		floorplan = current_maze.getFloorplan();
		int [][] curr_dimensions = new int[floorplan.getWidth()][floorplan.getHeight()];
		int counter = 0;
		for (int w = 0; w< curr_dimensions[0].length;w++) {
			for (int h=0;h < curr_dimensions[1].length;h++) {
				if (current_maze.getDistanceToExit(w, h) == 1) {
					counter ++;
				}
				//else {System.out.println(current_maze.getDistanceToExit(w,h));} - was ensuring getDistanceToExit computing properly
			}
		}
		assertTrue(counter == 1);
	}
	
	/**Because I am testing with a maze that is not perfect, we iterate through floorplan cells and check that if floorplan.isInRoom is true, that there are multiple circumstances of this being called.
	 * If this is not being called at all then we know it is a perfect maze.
	 * The developer can change the assertTrue statement to assertFalse for when the maze is a perfect maze.
	 */
	@Test
	public void test_room_generation() {
		floorplan=current_maze.getFloorplan();
		int count = 0;
		for (int w = 0; w<current_maze.getWidth();w++) {
			for (int h=0;h < current_maze.getHeight();h++) {
				if (floorplan.isInRoom(w, h) == true) {
					count ++;
				}
			}
		}
		//change as needed, we test if count == 0 for a perfect maze, otherwise count better be > 0 
		//count !=0 currently as I am testing for mazes generated with rooms 
	assertTrue(count!=0);
	}			
}
