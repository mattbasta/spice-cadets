package game.models;

public interface IRenderable {
	
	public void tryPrerender();
	public void render();
	
	public void discard();
	
}
