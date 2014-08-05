package game.models;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public abstract class Model extends FixedShape {
	
	protected final List<IRenderable> renderables;
	
	public Model() {
		this(false);
	}
	
	public Model(boolean makeDisplayList) {
		renderables = new LinkedList<IRenderable>();
		this.makeDisplayList = makeDisplayList;
	}

	@Override
	protected void doRender() {
		// Iterate the renderable components and render them.
		ListIterator<IRenderable> itr = renderables.listIterator();
		while(itr.hasNext())
			itr.next().render();
	}
	
}
	