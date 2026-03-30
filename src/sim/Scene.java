package sim;


import static comp3170.Math.TAU;

import comp3170.InputManager;
import comp3170.SceneObject;
import sceneObjects.*;

import org.joml.Matrix4f;
import org.joml.Vector4f;



public class Scene extends SceneObject{
	
	public static Scene theScene;
	private Camera camera;
	private Camera currentCamera;
	
	private Ant cacti;


	private Square grid;
	
	private final float MAIN_CAM_ZOOM = 200f;
	private final int GRID_SIZE = 30;
	private final float GRID_SPACING = 1f;
	private final float GRID_SCALE = 1;
	
	private final int CACTUS_COUNT = 10000;
	private final float CACTUS_SCALE_MIN = 1f;
	private final float CACTUS_SCALE_MAX = 2f;
	private final float SCATTER_WIDTH = 200f;
	private final float SCATTER_HEIGHT = 500f;
			
	public Scene() {
		
		
		theScene = this;
		
		
		
		grid = new Square(GRID_SIZE, GRID_SIZE, GRID_SCALE, GRID_SPACING);
		grid.setParent(this);		
		camera = new Camera(MAIN_CAM_ZOOM);
		camera.setParent(theScene);
		currentCamera = camera;
		
		cacti = new Ant(CACTUS_COUNT, CACTUS_SCALE_MAX, CACTUS_SCALE_MIN, SCATTER_WIDTH, SCATTER_HEIGHT);
		cacti.setParent(this);
	}
	
	public static float randBetween(float min, float max) { //Awesome function that is used in other classes
		float scalar = (float) Math.random();
		return (scalar * (max-min)) + min;
	}
	
	public Camera getCamera() {
		return currentCamera; //Return the current active camera
	}
	
	public void update(float deltaTime, InputManager input) {
		grid.update(deltaTime, input);
	}

	
}