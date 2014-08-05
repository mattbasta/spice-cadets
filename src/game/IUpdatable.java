package game;

public interface IUpdatable {
	
	/**
	 * Method called on each iteration of the game loop.
	 * 
	 * @param ratio	The percent of a normal tick duration that the previous
	 * 				tick has consumed. I.e.: a tick taking (1000/60 + 1)ms will
	 * 				have a ratio value of ((1000/60 + 1)/(1000/60))ms. 
	 */
	public void tick(float ratio);
	
}
