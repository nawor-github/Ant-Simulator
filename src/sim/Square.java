package sim;

import org.joml.Vector3f;

import comp3170.InputManager;

public class Square {
	float food, foodScent, homeScent;
	boolean isBlocker, isHome;
	int x, y, i;
	private Vector3f colour;
	
	private Vector3f clearColour = new Vector3f(0f, 0.1f, 0.2f); //Dark blue clear colour
	private Vector3f blockerColour = new Vector3f(0.9f, 0.9f, 0.8f); //Pale yellow blocker colour
	private Vector3f foodColour = new Vector3f(0.9f, 0.7f, 0.2f); //Red-orange food colour
	private Vector3f foodScentColour = new Vector3f(0.3f, 0.8f, 0.5f); //Dark green food scent colour
	private Vector3f homeScentColour = new Vector3f(0.8f, 0.1f, 0.6f); //Dark red home scent colour
	private Vector3f homeColour = new Vector3f(1f, 1f, 1f); //White home colour
	
	public Square(int X, int Y, int I) {
		x = X;
		y = Y;
		i = I;
		clear();
	}
	
	public Vector3f getColour() {
		return colour;
	}
	
	public void clear() {
		food = 0;
		foodScent = 0;
		homeScent = 0;
		isBlocker = false;
		isHome = false;
		colour = clearColour;
	}
	
	public void setBlocker() {
		clear();
		isBlocker = true;
	}
	
	public void setHome() {
		clear();
		isBlocker = false;
	}
	
	public void addFood(float f) {
		clear();
		food += f;
	}
	
	public void addFoodScent(float f) {
		clear();
		homeScent += f;
	}
	
	public void addHomeScent(float f) {
		clear();
		foodScent += f;
	}


	public void update(float deltaTime, InputManager input) {
		decay(deltaTime);
	}
	
	private final static float DECAY_SPEED = 0.5f;
	
	private void decay(float deltaTime) {
		foodScent = foodScent - DECAY_SPEED * deltaTime;
		homeScent = foodScent - DECAY_SPEED * deltaTime;
		if (foodScent < 0) {
			foodScent = 0;
		}
	}
	
}
