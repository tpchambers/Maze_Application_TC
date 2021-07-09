/**
 * 
 */
package gui;

import generation.Order;

import java.awt.event.KeyListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.JFrame;


/**
 * This class is a wrapper class to startup the Maze game as a Java application
 * 
 * This code is refactored code from Maze.java by Paul Falstad, www.falstad.com, Copyright (C) 1998, all rights reserved
 * Paul Falstad granted permission to modify and use code for teaching purposes.
 * Refactored by Peter Kemper
 * 
 * TODO: use logger for output instead of Sys.out
 * 
 * CRC Notes:
 * 
 * Maze application init calls createController with the corresponding string parameter
 * The string parameter sets the maze generation algorithm
 * The controller then starts, initializing the first state of the game - state Title
 * Thus, the transitions go from A -> B -> C -> D - > A for one round of the game
 * In state A, the controller responds to user keyboard input corresponding to a skill level
 * Once the key is pressed, Controller.keydown() is called, which calls controller.switchtitletogenerating
 * The current state is then B. Once the generation is completed, State C begins when controller.switchfromgeneratingtoplaying
 * State D then occurs once the game has ended.
 * 
 */
public class MazeApplication extends JFrame {

	// not used, just to make the compiler, static code checker happy
	private static final long serialVersionUID = 1L;
	String driver;
	String builder; 

	// developments vs production version
	// for development it is more convenient if we produce the same maze over an over again
	// by setting the following constant to false, the maze will only vary with skill level and algorithm
	// but not on its own
	// for production version it is desirable that we never play the same maze 
	// so even if the algorithm and skill level are the same, the generated maze should look different
	// which is achieved with some random initialization
	private static final boolean DEVELOPMENT_VERSION_WITH_DETERMINISTIC_MAZE_GENERATION = true; //change for testing

	/**
	 * Constructor
	 */
	public MazeApplication() {
		init(null);
	}

	/**
	 * Constructor that loads a maze from a given file or uses a particular method to generate a maze
	 * @param parameter can identify a generation method (Prim, Kruskal, Eller)
     * or a filename that stores an already generated maze that is then loaded, or can be null
	 */
	public MazeApplication(String parameter) {
		init(parameter);
	}
	
	/**
	 * Biggest addition to main method. We iterate over the arguments, and select the appropriate builders and drivers from the command line.
	 * If there is nothing, we init(null) which will create a random maze.
	 * Otherwise, we init(driver,builder) at the end, which will create the appropriate maze.
	 * For the last case, we try to see if we can read the file for the particular argument. If so, we check again if the user wants a certain builder or robot.
	 * We then do what the init(driver,builder) class does, directly in the else conditional.
	 * The loop will properly identify the important pieces, and disregard the -g or -d commands.
	 * 
	 * @param args
	 */
	
	public MazeApplication(String [] args ) {
		if (args == null) {
			init(null);
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("Prim")) {
				builder = "Prim";
			}
			else if(args[i].equalsIgnoreCase("Kruskal")) {
				builder = "Kruskal";
			}
			
			else if(args[i].equalsIgnoreCase("Wallfollower")) {
				driver = "Wallfollower";
			}
			else if(args[i].equalsIgnoreCase("Wizard")) {
				driver = "Wizard";
			}
			else {
				        File f = new File(args[i]) ;
				        if (f.exists() && f.canRead())
				        {
				        	String msg = null;
				            msg = "MazeApplication: loading maze from file: " + args[i];
				            Controller result = new Controller() ;
				            result.setFileName(args[i]);
				            add(result.getPanel());
							KeyListener kl = new SimpleKeyListener(this, result) ;
							addKeyListener(kl) ;
							// set the frame to a fixed size for its width and height and put it on display
							setSize(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT+22) ;
							setVisible(true) ;
							// focus should be on the JFrame of the MazeApplication and not on the maze panel
							// such that the SimpleKeyListener kl is used
							setFocusable(true) ;
							// start the game, hand over control to the game controller
							//we can still have a driver, so we have two cases
							if ("Wallfollower".equalsIgnoreCase(driver)) {
									result.set_robot_option_WallFollower();
								}
								else if("Wizard".equalsIgnoreCase(driver)) {
									result.set_robot_option_Wizard();
								}
								result.start();
				        }
				
			}
		}
		init(driver,builder);
	}
	
	

	/**
	 * Instantiates a controller with settings according to the given parameter.
	 * @param parameter can identify a generation method (Prim, Kruskal, Eller)
	 * or a filename that contains a generated maze that is then loaded,
	 * or can be null
	 * @return the newly instantiated and configured controller
	 */
	 Controller createController(String parameter) {
	    // need to instantiate a controller to return as a result in any case
	    Controller result = new Controller() ;
	    // can decide if user repeatedly plays the same mazes or 
	    // if mazes are different each and every time
	    // set to true for testing purposes
	    // set to false for playing the game
	    if (DEVELOPMENT_VERSION_WITH_DETERMINISTIC_MAZE_GENERATION)
	    	result.setDeterministic(true);
	    else
	    	result.setDeterministic(false);
	    String msg = null; // message for feedback
	    // Case 1: no input
	    if (parameter == null) {
	        msg = "MazeApplication: maze will be generated with a randomized algorithm."; 
	    }
	    // Case 2: Prim
	    else if ("Prim".equalsIgnoreCase(parameter))
	    {
	        msg = "MazeApplication: generating random maze with Prim's algorithm.";
	        result.setBuilder(Order.Builder.Prim);
	    }
	    // Case 3 a and b: Eller, Kruskal or some other generation algorithm
	    else if ("Kruskal".equalsIgnoreCase(parameter))
	    {
	    	// TODO: for P2 assignment, please add code to set the builder accordingly
	    	 msg = "MazeApplication: generating random maze with Kruskal's algorithm.";
		     result.setBuilder(Order.Builder.Kruskal);
		     
	    }
	    else if ("DFS".equalsIgnoreCase(parameter))
	    {
	    	// TODO: for P2 assignment, please add code to set the builder accordingly
	    	 msg = "MazeApplication: generating random maze with DFS algorithm.";
		     result.setBuilder(Order.Builder.DFS);
	    }
	    else if ("Eller".equalsIgnoreCase(parameter))
	    {
	    	// TODO: for P2 assignment, please add code to set the builder accordingly
	        throw new RuntimeException("Don't know anybody named Eller ...");
	    }
	    // Case 4: a file
	    else {
	        File f = new File(parameter) ;
	        if (f.exists() && f.canRead())
	        {
	            msg = "MazeApplication: loading maze from file: " + parameter;
	            result.setFileName(parameter);
	            return result;
	        }
	        else {
	            // None of the predefined strings and not a filename either: 
	            msg = "MazeApplication: unknown parameter value: " + parameter + " ignored, operating in default mode.";
	        }
	    }
	    // controller instanted and attributes set according to given input parameter
	    // output message and return controller
	    System.out.println(msg);
	    return result;
	}
	 

	/**
	 * Initializes some internals and puts the game on display.
	 * @param parameter can identify a generation method (Prim, Kruskal, Eller)
     * or a filename that contains a generated maze that is then loaded, or can be null
	 */
	private void init(String parameter) {
	    // instantiate a game controller and add it to the JFrame
	    Controller controller = createController(parameter);
		add(controller.getPanel());
		// instantiate a key listener that feeds keyboard input into the controller
		// and add it to the JFrame
		KeyListener kl = new SimpleKeyListener(this, controller) ;
		addKeyListener(kl) ;
		// set the frame to a fixed size for its width and height and put it on display
		setSize(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT+22) ;
		setVisible(true) ;
		// focus should be on the JFrame of the MazeApplication and not on the maze panel
		// such that the SimpleKeyListener kl is used
		setFocusable(true) ;
		// start the game, hand over control to the game controller
		controller.start();
	}
	
	/**
	 * Here, we follow the same logic from the original init method, but we add a conditional to include for each driver case.
	 * For the two cases, we call controller.set_robot_option_(driver), which is a new method I wrote into controller to update a boolean indication of the driver we are using.
	 * This is similar to using controller.set_robot_driver, but I used this new method for clarity.
	 * @param driver
	 * @param builder
	 */
	private void init(String driver, String builder) {
			Controller controller = createController(builder);
			add(controller.getPanel());
			KeyListener kl = new SimpleKeyListener(this, controller) ;
			addKeyListener(kl) ;
			// set the frame to a fixed size for its width and height and put it on display
			setSize(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT+22) ;
			setVisible(true) ;
			// focus should be on the JFrame of the MazeApplication and not on the maze panel
			// such that the SimpleKeyListener kl is used
			setFocusable(true) ;
			// start the game, hand over control to the game controller
				
				// if wallfollower -> set wallfollower to true else we set robot to true
				if ("Wallfollower".equalsIgnoreCase(driver)) {
					controller.set_robot_option_WallFollower();
				}
				else if("Wizard".equalsIgnoreCase(driver)) {
					controller.set_robot_option_Wizard();
				}
				//either way, we will start the game
				controller.start();
			}
			
	
	
	/**
	 * Main method to launch Maze game as a java application.
	 * The application can be operated in three ways. 
	 * 1) The intended normal operation is to provide no parameters
	 * and the maze will be generated by a randomized DFS algorithm (default). 
	 * 2) If a filename is given that contains a maze stored in xml format. 
	 * The maze will be loaded from that file. 
	 * This option is useful during development to test with a particular maze.
	 * 3) A predefined constant string is given to select a maze
	 * generation algorithm, currently supported is "Prim".
	 * @param args is optional, first string can be a fixed constant like Prim or
	 * the name of a file that stores a maze in XML format
	 * 
	 * 
	 * Update:
	 * The main method now can receive any arguments. We pass the arguments into the constructor which does the majority of the work by identifying the builder,driver, and filename.
	 */
	public static void main(String[] args) {
	    JFrame app ; 
		app = new MazeApplication(args);
		app.repaint() ;
	}

}
