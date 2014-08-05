package game.models;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import game.GLFactory;

/**
 * @author Matt
 * 
 * Class that represents a shape that will not change in size or relative
 * orientation.
 */
public abstract class FixedShape implements IRenderable {

	private final static Map<Long, Integer> shapeCount = new HashMap<Long, Integer>();
	private final static Map<Long, Integer> shapeList = new HashMap<Long, Integer>();

	protected boolean makeDisplayList;
	private int renderDisplayList;
	
	/**
	 * Initializes a new display list to store the shape.
	 */
	private void setupDisplayLists() {
		if(shapeList.containsKey(shapeHashCode())) return;
		renderDisplayList = GLFactory.getDisplayLists(1);
	}
	
	@Override
	public final void tryPrerender() {
		final long hashCode = shapeHashCode();
		if(shapeList.containsKey(hashCode)) return;  // If we've already prerendered, ignore. 
		
		setupDisplayLists();
		makeDisplayList = true;
		
		GL11.glNewList(renderDisplayList, GL11.GL_COMPILE);
		doRender();
		GL11.glEndList();
		
		makeDisplayList = false;
		
		// Flag that we've constructed the display list.
		bumpCount(1);
		shapeList.put(hashCode, renderDisplayList);
	}
	
	private int bumpCount(int amount) {
		final long hash = shapeHashCode();
		int newValue = amount;
		if(shapeCount.containsKey(hash)) {
			newValue += shapeCount.get(hash);
			shapeCount.put(hash, newValue);
		} else
			shapeCount.put(hash, amount);
		return newValue;
	}
	
	@Override
	public final void render() {
		final long hashCode = shapeHashCode();
		final int displayList = shapeList.containsKey(hashCode) ? shapeList.get(hashCode) : 0;
		if(displayList != 0) {
			GL11.glCallList(displayList);
			return;
		}
		if(makeDisplayList) {
			setupDisplayLists();
			GL11.glNewList(renderDisplayList, GL11.GL_COMPILE_AND_EXECUTE);
		}
		
		doRender();
		
		// If we should build a display list, finish that up.
		if(makeDisplayList) {
			GL11.glEndList();
			makeDisplayList = false;
		}
		
	}
	
	@Override
	public void discard() {
		final long hashCode = shapeHashCode();
		if(shapeList.containsKey(hashCode)) {
			bumpCount(-1);
			GLFactory.releaseDisplayLists(shapeList.get(hashCode));
		}
	}
	
	/**
	 * Method that is called when the shape needs to be drawn. 
	 */
	protected abstract void doRender();
	
	protected abstract long shapeHashCode();

}
