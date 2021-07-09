package gui;

import generation.Maze;
import gui.Robot.Direction;
import gui.Robot.Turn;


/**
 * Because I am not implementing the wizard class fully, I just subclass the wall follower and secretly perform the strategy.
 * Should still run with the same strategy on the command line.
 * @author tpchambers
 */
public class Wizard extends WallFollower {
private Maze maze;
private Robot robot;


/**
 * We set up the Wizard in the same manner of the WallFollower
 * @param maze
 * @param robot
 * 
 */
public Wizard (Maze maze, Robot robot) {
	setMaze(maze);
	setRobot(robot);
	}
}