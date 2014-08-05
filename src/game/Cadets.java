package game;

import game.entities.EntityManager;
import game.entities.Player;
import game.level.LevelManager;
import game.models.ParticleRenderer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;


public class Cadets {

	public static void main(String[] args) {
		Cadets c = new Cadets();
		c.start();
	}

	public final Player player;
	public final LevelManager levelManager;
	public final EntityManager entityManager = new EntityManager();
	public final ParticleRenderer particleRenderer = new ParticleRenderer();

	private long lastTick = System.nanoTime() / 1000000;
	private float zoom = 0, zoomTo = 0;

	public Cadets() {
		levelManager = new LevelManager(entityManager);
		// The entity manager doesn't need to know about the level mangaer. It
		// should only accept commands.
		
		player = new Player(levelManager, entityManager);
		zoomTo = zoom = 0;
	}

	/**
	 * Initialize the game and start the game loop. 
	 */
	public void start() {

		try {
			Display.setDisplayMode(new DisplayMode(1024, 768));
			Display.setVSyncEnabled(true);
			Display.setResizable(true);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		adjustWindowSize();

		// Clear setup
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glClearDepth(1.0f);

		// Depth testing
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// Turn on some lights.
		GL11.glShadeModel(GL11.GL_SMOOTH);
		setLights();

		// Set the camera's position.
		GLU.gluLookAt(0.0f, 10.0f, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

		// Load the world.
		levelManager.loadRegion("campus", 0, 0);

		while (!Display.isCloseRequested() &&
			   !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {

			Profiler.startCycle();
			
			tick();

			GL11.glPushMatrix();
			// GL11.glScalef(5.0f, 5.0f, 5.0f);
			render();
			GL11.glPopMatrix();
			
			Profiler.endCycle();

			Display.update();

			if (Display.wasResized())
				adjustWindowSize();

		}

		Display.destroy();

	}

	/**
	 * Called when the window size is updated. This should re-initialize
	 * anything that is directly affected by the size or shape of the window
	 * or the ratio of the height to the width of the window.
	 */
	public void adjustWindowSize() {
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(80.0f,
				(float) Display.getWidth() / (float) Display.getHeight(),
				0.01f, 100.0f);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	/**
	 * Called to initialize the lighting for the scene.
	 */
	public void setLights() {
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_LIGHT0);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);

		GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE);

		// Set up the lighting for the current level.
		levelManager.setLighting();
	}

	
	/**
	 * Called on every iteration of the game loop. Calculates the time since
	 * the last tick and calls the tick() function of the player and each of
	 * the loaded entities.
	 */
	public void tick() {
		Profiler.startActivity("Pre-tick Operations");
		
		final long now = System.nanoTime() / 1000000,
				   delta = now - lastTick,
			       ratio = delta / (1000 / 60);
		
		final int wheelDelta = Mouse.getDWheel();
		if(wheelDelta != 0)
			zoomTo = Math.min(Math.max(zoomTo + wheelDelta / 100, -5), 2);
		if(zoomTo != zoom)
			zoom = (zoomTo + zoom * 3) / 4;  // Smooth out the zoom process.

		Profiler.startActivity("Loading Chunks");
		levelManager.loadChunksAroundPoint((int)player.x, (int)player.y);
		Profiler.startActivity("Updating Entity Visibility");
		entityManager.updateVisibleEntities(50 * 4, (int)player.x, (int)player.y);

		Profiler.startActivity("Entity Manager Tick");
		entityManager.tick(ratio);

		lastTick = now;

		Profiler.startActivity("Post-tick Operations");
		if(player.x < -10 && !player.isDead())
			player.kill();
	}

	/**
	 * Called on every iteration of the game loop to render the scene. On each
	 * call, it will first clear the display, then render the player, the
	 * terrain, and each of the game entities. 
	 */
	public void render() {

		Profiler.startActivity("Rendering Prep");
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		final float zoomScale = (zoom > 0) ? zoom + 1 : -3 / (zoom - 3);
		GL11.glScalef(zoomScale, zoomScale, zoomScale);
		// Funky zoom tilting
		if(zoom > 0) {
			GL11.glRotatef(zoom / 5 * 60, -1.0f, 0.0f, 0.0f);
		} else if(zoom < -2) {
			GL11.glRotatef(-(zoom + 2) / 3 * -10, -1.0f, 0.0f, 0.0f);
		}
		
		GL11.glTranslated(-player.x, 0.0, -player.y);

		Profiler.startActivity("Rendering Terrain");
		levelManager.render();

		Profiler.startActivity("Rendering Entities");
		entityManager.render();

		Profiler.startActivity("Rendering Particles");
		particleRenderer.render();

	}

}
