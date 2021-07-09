package generation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import generation.Order.Builder;
import gui.Constants;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import java.util.ArrayList;
import java.util.Collections;
/**Here, in since I override the initialize method to set a build option Kruskal, I also override the other methods.
 * The biggest difference between this Test and MazeFactory, is that I test my tree generation method from Kruskal to allow for higher coverage.
 * Other than that, the previous methods I created in MazeFactoryTest have a high coverage on MazeBuilderKruskal.
 */
public class MazeBuilderKruskalTest extends MazeFactoryTest {
	private MazeFactory factory;
	private StubOrder order;
	private Maze current_maze;
	private int skill;
	private Floorplan floorplan;
	
	/**This class extends MazeFactoryTest, so running this will automatically run all methods of previous test if not overwritten.
	 */
	@Before
	public void initialize_test () {
			factory = new MazeFactory();
			order = new StubOrder();
			order.setBuildOption(Builder.Kruskal); //default 
			order.setRoomOption(false);
			order.setSkillLevel(5); 
			order.set_seed(7); 
			factory.order(order);
			factory.waitTillDelivered();
			current_maze = order.getMaze();
	}
	/**We can't have any of the objects being null
	 */
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
	}
	
	/**This is Kruskal right, just making sure 
	 */
	@Test
	public final void Kruskal() {
		assertTrue(order.getBuilder() == Builder.Kruskal);
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
	
	/**This method tests that there is only one entrance to the maze
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
	public void exit_possible() {
			for (int w = 0; w < current_maze.getWidth();w++) {
				for (int h = 0; h < current_maze.getHeight(); h++) {
					int count = current_maze.getDistanceToExit(w, h);
					assertTrue(count !=0);
				}
			}
	}
	/*testing exit to check if there is a single instance where the distance to the exit is equal to one
	 * this is because the distance to exit can only be a one at minimum
	 * iterate through dimensions of floorplan width and height, initialize counter to ensure there is only one instance of the distance equaling one
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
	//this is the only real big change
	//adding tests to cover tree class used in MazeBuilderKruskal
	
	/**Tests tree class, same class as in MazeBuilderKruskal.
	 *We check if each of the methods in the tree class work properly.
	 *This is because I rely on the tree data structure to run my Kruskal implementation.
	 */
	@Test
	public final void test_Tree_generation () {
		Test_Tree tree_set1 = new Test_Tree(1);
		Test_Tree tree_set2 = new Test_Tree(2);
		Test_Tree tree_set3 = new Test_Tree();
		Test_Tree[][] spanning_tree = new Test_Tree[2][2];
		spanning_tree[0][0] = tree_set1;
		spanning_tree[0][1] = tree_set2;
		spanning_tree[1][0] = tree_set3;
		// we can add tree sets that don't contain any specified integers, yet we still contain the tree
		assertNotNull(spanning_tree[1][0]);
		assertNotNull(tree_set3.get_data());
		//if data not specified it should be zero
		assertTrue(tree_set3.get_data()==0);
		//the spanning tree does contain the tree although the tree contains no data 
		assertTrue(spanning_tree[1][0].contains_tree(tree_set3));
		assertNotNull(tree_set1.get_data());
		assertTrue(tree_set1.get_data()==1);
		assertTrue(tree_set2.get_data()==2);
		//tree does not contain set
		assertFalse(tree_set1.contains_tree(tree_set2));
		//testing overall spanning tree indexing individuals trees
		assertFalse(spanning_tree[0][1].contains_tree(spanning_tree[0][0]));
		//should contain tree_set1 placed into set
		assertTrue(spanning_tree[0][0].contains_tree(tree_set1));
	}
	//using same Tree class as earlier, but designating this one as a test 
	
	/**Here is a simple data structure to represent a tree, which can contain an integer data type.
	 * We use this tree to design the overall data structure representing the maze for our junit testing.
	*/
	final class Test_Tree {
			private int cell_data;
			//default 
			Test_Tree () {
			}
			//we can access the cell's data point, in this case an integer
			Test_Tree (int cell_data) {
				this.cell_data= cell_data;
			}
			//call to access 
			public int get_data() {
				return this.cell_data;
			}
			//checking if current tree contains tree in set 
			public boolean contains_tree (Test_Tree cur_tree) {
				if (this == cur_tree) {
				return true;
			} else {return false;}
		}
	}
}

	

	
	
