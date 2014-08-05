package game;

import java.util.HashMap;
import java.util.Map;

public class Profiler {
	
	private static String lastActivity;
	private static long lastTime = 0;
	
	private static final Map<String, Long> times = new HashMap<String, Long>();
	
	public static void startCycle() {
		lastTime = System.nanoTime();
	}
	
	private static void saveLastActivity() {
		final long now = System.nanoTime(),
				   duration = now - lastTime;
		if(times.containsKey(lastActivity))
			times.put(lastActivity, duration + times.get(lastActivity));
		else
			times.put(lastActivity, duration);
	}
	
	public static void startActivity(String name) {
		saveLastActivity();
		
		lastActivity = name;
		lastTime = System.nanoTime();
	}
	
	public static void endCycle() {
		saveLastActivity();
		
	}
	
}
