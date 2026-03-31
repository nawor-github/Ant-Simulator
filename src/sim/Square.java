package sim;

import org.joml.Vector3f;

import comp3170.InputManager;

public class Square {
	float food, foodScent, homeScent;
	public boolean isBlocker, isHome;
	public int x, y, i;
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
	
	public Vector3f calculateColour() {
		if (isBlocker) {
			colour = blockerColour;
			return colour;
		} else if (isHome) {
			colour = homeColour;
			return colour;
		} else {
			colour = clearColour;
		} 
		if (food > 0) {
			colour = foodColour;
		}
		if (foodScent > 0) {
			float blendFactor = foodScent / 50f;
			if (foodScent > 50) {
				blendFactor = 1;
			}
			colour = blendBetween(foodScentColour, clearColour, blendFactor);
			//colour = foodScentColour; //Make this blend later
		}
		if (homeScent > 0) {
			float blendFactor = homeScent / 50f;
			if (homeScent > 50) {
				blendFactor = 1;
			}
			colour = blendBetween(homeScentColour, clearColour, blendFactor);
			//colour = homeScentColour; //Make this blend later
		}
		return colour;
	}
	
	private Vector3f blendBetween(Vector3f a, Vector3f b, float t) { //Stub to be repalced with a lerp later on
		// a * t + y * (1-t)
		float R = (a.x * t) + (b.x*(1-t));
		float G = (a.y * t) + (b.y*(1-t));
		float B = (a.z * t) + (b.z*(1-t));
		return new Vector3f(R,G,B);
	}
	
	public void clear() {
		food = 0;
		foodScent = 0;
		homeScent = 0;
		isBlocker = false;
		isHome = false;
		colour = clearColour;
		calculateColour();
	}
	
	public void setBlocker() {
		clear();
		isBlocker = true;
		calculateColour();
	}
	
	public void setHome() {
		clear();
		isBlocker = false;
		calculateColour();
	}
	
	public void addFood(float f) {
		food += f;
		System.out.println("Adding " + f + " food to " + x + "," + y + ". Food is now: " + food);
		calculateColour();
	}
	
	public float takeFood(float f) {
		if (f >= food) {
			float value = food;
			food = 0;
			return value;
		}
		food -= f;
		calculateColour();
		return f;
	}
	
	public void addFoodScent(float f) {
		if (homeScent > 0) { //Make the two scents mutually exclusive/decay each other
			homeScent -= f;
			if (homeScent < 0) {
				homeScent = 0;
			}
		} else {
			foodScent += f;
		}
		System.out.println("Adding " + f + " food scent to " + x + "," + y + ". Food scent is now: " + foodScent);

		calculateColour();
	}
	
	public void addHomeScent(float f) {
		if (foodScent > 0) { //Make the two scents mutually exclusive/decay each other
			foodScent -= f;
			if (foodScent < 0) {
				foodScent = 0;
			}
		} else {
			homeScent += f;
		}
		calculateColour();
	}


	public void update(float deltaTime, InputManager input) {
		decay(deltaTime);
		calculateColour();
	}
	
	private final static float DECAY_SPEED = 2f;
	
	private void decay(float deltaTime) {
		foodScent -= DECAY_SPEED * deltaTime;
		homeScent -= DECAY_SPEED * deltaTime;
		if (foodScent < 0) {
			foodScent = 0;
		}
		if (homeScent < 0) {
			homeScent = 0;
		}
	}
	
	public Vector3f getColour() {
		return colour;
	}
	
	public float getFoodScent() {
		return foodScent;
	}
	
	public float getHomeScent() {
		return homeScent;
	}
	
}
