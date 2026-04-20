package sim;


import static comp3170.Math.TAU;

import java.awt.event.KeyEvent;

import comp3170.GLBuffers;
import comp3170.InputManager;
import comp3170.SceneObject;
import sceneObjects.*;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;



public class Scene extends SceneObject{
	
	public static Scene theScene;
	private Camera camera;
	private Camera currentCamera;
	
	private Ant ants;
	private Circle food;

	private Grid grid;
	
	private final float MAIN_CAM_ZOOM = 200f;
	private final int GRID_SIZE = 50;
	private final float GRID_SPACING = 1f;
	private final float GRID_SCALE = 1;
	
	private final int ANT_COUNT = 100;
	private final float ANT_SCALE_MIN = 0.5f;
	private final float ANT_SCALE_MAX = 1f;
	private final float SCATTER_WIDTH = 2f;
	private final float SCATTER_HEIGHT = 2f;

	private Simulator sim;
			
	public Scene(Simulator s) {
		sim = s;
		screenWidth = sim.screenWidth;
		screenHeight = sim.screenHeight;
		theScene = this;
		
		grid = new Grid(GRID_SIZE, GRID_SIZE, GRID_SCALE, GRID_SPACING, this);
		grid.setParent(this);		
		camera = new Camera(MAIN_CAM_ZOOM);
		camera.setParent(theScene);
		currentCamera = camera;
		
		ants = new Ant(ANT_COUNT, ANT_SCALE_MAX, ANT_SCALE_MIN, SCATTER_WIDTH, SCATTER_HEIGHT, grid);
		ants.setParent(this);
		
		food = new Circle();
		food.setParent(this);
	}
	
	public static float randBetween(float min, float max) { //Awesome function that is used in other classes
		float scalar = (float) Math.random();
		return (scalar * (max-min)) + min;
	}
	
	public Camera getCamera() {
		return currentCamera; //Return the current active camera
	}
	
	private Vector2i mousePosition = new Vector2i();
	private Vector4f mousePos = new Vector4f();
	int screenWidth = 0;
	int screenHeight = 0;
	
	public Vector4f getMousePos() {
		return mousePos;
	}
	
	public void update(float deltaTime, InputManager input) {
		mousePos = getMousePosWorld(input);
		grid.update(deltaTime, input);
		if (input.isMouseDown()) {
			if (grid.getBrushMode() == 6) {
				ants.addAnt(mousePos);
			}
		}		
		ants.update(deltaTime, input);
		currentCamera.update(deltaTime, input);
	}
	
	public Vector4f getMousePosWorld(InputManager input) {
		input.getCursorPos(mousePosition);
		
		float x = (2f * ((float) mousePosition.x()/screenWidth) - 1f);
		float y = 1f - (2f * (((float) mousePosition.y()/screenHeight)));
		//System.out.println("Update function detecting mouse at: " + x + " " + y);
		
		
		Matrix4f mvpMatrix = new Matrix4f();
		Matrix4f viewMatrix = new Matrix4f();
		camera.getViewMatrix(viewMatrix);
		Matrix4f projectionMatrix = new Matrix4f();
		camera.getProjectionMatrix(projectionMatrix);

		mvpMatrix.set(viewMatrix.mul(projectionMatrix));
		mvpMatrix.invert();
		
		mousePos = new Vector4f(x, y, 0f, 1f);
		mousePos.mul(mvpMatrix);
		return mousePos;
	}

	
}