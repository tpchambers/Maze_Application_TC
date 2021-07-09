package generation;
import gui.BasicRobot;

public class StubOrder implements Order {
		private int seed;
		private int skillLevel; 
	    private Builder builder;
	    private boolean perfect;
	    private int percentdone;     
	    private Maze maze_configuration;
	    private BasicRobot robot;
	    private Maze maze;
	    
	    // The factory is used to calculate a new maze configuration
	    // The maze is computed in a separate thread which makes 
	    // communication with the factory slightly more complicated.
	    // Check the factory interface for details.
	    public StubOrder () { //default constructor
	    }
	    
	//set skill level for dimensions 
	public int setSkillLevel(int skillLevel) {
		return this.skillLevel=skillLevel;
	}
	
	// perfect == true: no loops, i.e. no rooms
    // perfect == false: maze can support rooms
	//set rooms , isperfect == true for yes, otherwise no
	public boolean setRoomOption (boolean perfect) {
		return this.perfect=perfect;
	}
	
	//set build option
	public Builder setBuildOption (Builder builder) {
		return this.builder = builder;
	}
	
	public int set_seed(int seed) {
		return this.seed=seed;
	}
	
	public BasicRobot get_robot() {
		return this.robot;
	}
	@Override
	public void deliver(Maze mazeConfig) {
		// TODO Auto-generated method stub
		this.maze_configuration=mazeConfig;
	}
	
	public Maze getMaze() {
		return this.maze_configuration;
	}
	
	@Override
	public int getSkillLevel() {
		// TODO Auto-generated method stub
		return skillLevel;
	}

	@Override
	public Builder getBuilder() {
		// TODO Auto-generated method stub
		return builder;
	}

	@Override
	public boolean isPerfect() {
		// TODO Auto-generated method stub
		return perfect;
	}

	@Override
	public int getSeed() {
		// TODO Auto-generated method stub
		return seed;
	}

	@Override
	public void updateProgress(int percentage) {
		this.percentdone = percentage;

	}
	

}
