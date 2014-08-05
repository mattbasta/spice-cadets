package game.entities;

import game.level.LevelManager;
import game.models.DrawableFont;

public class NPC extends Person {

	//private final DrawableFont font = new DrawableFont("Helvetica");
	
	public NPC(LevelManager levelManager, EntityManager entityManager) {
		super(levelManager, entityManager);
	}
	
	
	@Override
	public void doRender() {
		//font.draw("foobar", 0, 0);
		super.doRender();
	}
	

}
