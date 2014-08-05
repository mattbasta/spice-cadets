package game.entities;

import org.lwjgl.input.Keyboard;

import game.level.LevelManager;

public class Player extends Person {

	public Player(LevelManager levelManager, EntityManager entityManager) {
		super(levelManager, entityManager);
	}

	public long lastForwardTick = 0;
	private boolean wWasDown = false;
	
	@Override
	public void tick(float ratio) {
		if(isDead())
			return;
		
		float speed = !isSneaking ? 1.0f : 0.3f;
		
		// Deal with controls first.
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			rotation += Player.rotationPerTick * ratio * speed;
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			rotation -= Player.rotationPerTick * ratio * speed;
		
		isSneaking = Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) ||
					 Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		if(isSneaking && isRunning)
			isRunning = false;
		
		boolean wkey = Keyboard.isKeyDown(Keyboard.KEY_W);
		if (wkey) {
			if(isRunning)
				speed = 2.0f;
			else if(!isRunning && !isSneaking && !wWasDown) {
				final long now = System.nanoTime() / 1000000;
				isRunning = now - lastForwardTick < 500;
				lastForwardTick = now;
			}
				
			impulse(Player.movementPerTick * ratio * speed);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			impulse(-Player.movementPerTick * ratio * speed);
			isRunning = false;
		} else {
			isRunning = false;
		}
		
		wWasDown = wkey;
		
		super.tick(ratio);
	}

}
