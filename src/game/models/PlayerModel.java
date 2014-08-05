package game.models;

import org.lwjgl.util.Color;

public class PlayerModel extends Model {
	
	private final Box head, body;
	
	public PlayerModel() {
		body = new Box(0.0f, 0.75f, 0.0f,
					   1.0f, 1.5f, 1.0f,
					   (Color) new Color(255, 255, 255, 80));
		head = new Box(0.0f, 1.75f, 0.0f,
					   0.5f, 0.5f, 0.5f,
					   (Color) Color.BLUE);

		//renderables.add(body);
		renderables.add(head);
	}

	@Override
	protected long shapeHashCode() {
		return 1l;
	}
	
}
