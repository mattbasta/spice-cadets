package game.entities;

import game.IUpdatable;
import game.models.IRenderable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Set;

public class EntityManager implements IRenderable, IUpdatable {

	private final List<Entity> entities = new ArrayList<Entity>();
	private final Queue<Entity> prerenderQueue = new LinkedList<Entity>();

	private final List<Entity> visibleEntities = new ArrayList<Entity>();
	private final Set<Entity> visibleEntitiesMap = new HashSet<Entity>();

	@Override
	public void tryPrerender() {
		final ListIterator<Entity> itr = entities.listIterator();
		while(itr.hasNext())
			itr.next().tryPrerender();
	}

	/**
	 * Renders all entities that are capable of being rendered and are
	 * currently visible to the player. Uses the entities' `draw()` method
	 * rather than `render()`, as `draw()` also handles positioning and matrix
	 * cleanup.
	 * 
	 * If there are any entities which have not been prerendered, they will be
	 * prerendered before any drawing takes place.
	 */
	@Override
	public void render() {
		while(!prerenderQueue.isEmpty())
			prerenderQueue.remove().tryPrerender();

		final int size = visibleEntities.size();
		for(int i = 0; i < size; i++)
			visibleEntities.get(i).render();
	}

	/**
	 * Cleans up all of the renderable entities.
	 */
	@Override
	public void discard() {
		while(!entities.isEmpty())
			entities.remove(0).discard();
		
		prerenderQueue.clear();
		visibleEntities.clear();
		visibleEntitiesMap.clear();
	}
	
	/**
	 * Adds an entity to the game.
	 * 
	 * @param entity The entity to add.
	 */
	public void add(Entity entity) {
		entities.add(entity);
		prerenderQueue.add(entity);
	}

	/**
	 * Returns an Iterable of entities which are near to `entity` (within
	 * `distance` units of `entity`). A fast algorithm is used which may
	 * exclude distant 
	 * 
	 * @param entity The entity to test against.
	 * @param distance The maximum distance of entities to return.
	 * @return An Iterable of "nearby" entities.
	 */
	public Iterable<Entity> nearby(Entity entity, double distance) {
		final LinkedList<Entity> retEntities = new LinkedList<Entity>();
		final ListIterator<Entity> itr = entities.listIterator();
		while(itr.hasNext()) {
			final Entity nextEntity = itr.next();
			
			// Don't return the entity we're testing against.
			if(nextEntity == entity)
				continue;
			
			// Roughly calculate the distance to the entity.
			// 0.7071 is the approximate ratio of the sum of the sides of a
			// right triangle to its hypotenuse.
			final double roughDist = (Math.abs(entity.x - nextEntity.x) + Math.abs(entity.y + nextEntity.y)) * 0.7071;
			if(roughDist < distance)
				retEntities.add(nextEntity);
		}
		return retEntities;
	}

	@Override
	public void tick(float ratio) {
		final ListIterator<Entity> itr = entities.listIterator();
		while(itr.hasNext())
			itr.next().tick(ratio);
		
		if(visibleEntities.size() > 1) {
			Collections.sort(visibleEntities, new Comparator<Entity>() {
				@Override
				public int compare(Entity o1, Entity o2) {
					return o1.y > o2.y ? 1 : -1;
				}
			});
		}
	}
	
	public void updateVisibleEntities(int maxDist, int x, int y) {
		final ListIterator<Entity> itr = entities.listIterator();
		final int minX = x - maxDist,
				  maxX = x + maxDist,
				  minY = y - maxDist,
				  maxY = y + maxDist;
		
		while(itr.hasNext()) {
			final Entity entity = itr.next();
			final boolean visible = entity.x > minX || entity.x < maxX ||
									entity.y > minY || entity.y < maxY;
			
			if(!visible) {
				if(visibleEntitiesMap.contains(entity)) {
					visibleEntities.remove(entity);
					visibleEntitiesMap.remove(entity);
				}
			} else if(visible) {
				if(!visibleEntitiesMap.contains(entity)) {
					visibleEntities.add(entity);
					visibleEntitiesMap.add(entity);
				}
			}
		}
	}
	
}
