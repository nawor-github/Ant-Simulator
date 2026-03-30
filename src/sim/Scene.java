package sim;


import static comp3170.Math.TAU;

import comp3170.SceneObject;
import sceneObjects.Camera;
import sceneObjects.Square;

import org.joml.Matrix4f;
import org.joml.Vector4f;



public class Scene extends SceneObject{
	
	public static Scene theScene;
	private Camera camera;
	private Camera currentCamera;

	private Square grid;
	
	private final float MAIN_CAM_ZOOM = 200f;
	private final int GRID_SIZE = 100;
	private final float GRID_SPACING = 1f;
	private final float GRID_SCALE = 1;
			
	public Scene() {
		
		
		theScene = this;
		
		grid = new Square(GRID_SIZE, GRID_SIZE, GRID_SCALE, GRID_SPACING);
		grid.setParent(this);		
		camera = new Camera(MAIN_CAM_ZOOM);
		camera.setParent(theScene);
		currentCamera = camera;
	}
	
	public static float randBetween(float min, float max) { //Awesome function that is used in other classes
		float scalar = (float) Math.random();
		return (scalar * (max-min)) + min;
	}
	
	public Camera getCamera() {
		return currentCamera; //Return the current active camera
	}
	
	public void update(boolean[] directions, float dt) { //LEFT RIGHT UP DOWN STRANGE CHARM
		
	}

	
}