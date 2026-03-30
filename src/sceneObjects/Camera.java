package sceneObjects;

import static comp3170.Math.TAU;

import org.joml.Matrix4f;
import static org.lwjgl.glfw.GLFW.*;

import comp3170.InputManager;
import comp3170.SceneObject;

public class Camera extends SceneObject {
		
	private Matrix4f viewMatrix;
	private Matrix4f projectionMatrix;
	private int width;
	private int height;
	private float zoom;
	private static float WIDTH_PROPORTION = 0.066f; //Relates the zoom to the width to control the scaling of the camera
	
	public Camera(float z) {	
		zoom = z;
		viewMatrix = new Matrix4f();
		projectionMatrix = new Matrix4f();
	}
	
	public Matrix4f getViewMatrix(Matrix4f dest) {
		getModelToWorldMatrix(viewMatrix);
		viewMatrix.invert(dest);
		return dest;
	}
	
	public Matrix4f getProjectionMatrix(Matrix4f dest) {
		projectionMatrix.identity();
		
		projectionMatrix.scaleXY(width/zoom, height/zoom);
		
		projectionMatrix.invert(dest);
		return dest;
	}
	
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		this.zoom = width * WIDTH_PROPORTION; //Scales the zoom to be proportional to the desired width proportion
	}
	
	private final float MOVE_SPEED = 20f;
	
	public void update(float deltaTime, InputManager input) {
		if (input.isKeyDown(GLFW_KEY_UP)) { //A press, tank rotate left
			this.getMatrix().translate(0f*deltaTime, MOVE_SPEED*deltaTime, 0*deltaTime);
		}
		if (input.isKeyDown(GLFW_KEY_LEFT)) { //A press, tank rotate left
			this.getMatrix().translate(-MOVE_SPEED*deltaTime, 0f*deltaTime, 0*deltaTime);
		}
		if (input.isKeyDown(GLFW_KEY_RIGHT)) { //A press, tank rotate left
			this.getMatrix().translate(MOVE_SPEED*deltaTime, 0f*deltaTime, 0*deltaTime);
		}
		if (input.isKeyDown(GLFW_KEY_DOWN)) { //A press, tank rotate left
			this.getMatrix().translate(0f*deltaTime, -MOVE_SPEED*deltaTime, 0*deltaTime);
		}
	}
}
