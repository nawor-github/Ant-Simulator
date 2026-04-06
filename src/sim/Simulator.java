package sim;

	import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;


	import static org.lwjgl.glfw.GLFW.*;

	import static org.lwjgl.opengl.GL11.glClear;
	import static org.lwjgl.opengl.GL11.glClearColor;
	import static org.lwjgl.opengl.GL11.glViewport;

	import java.io.File;
	import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.IWindowListener;
	import comp3170.InputManager;
	import comp3170.OpenGLException;
	import comp3170.ShaderLibrary;
	import comp3170.Window;
import sceneObjects.Camera;


	public class Simulator implements IWindowListener {
		
		final private File DIRECTORY = new File("src/shaders");
		
		public static Simulator instance;
		
		private Window window;
		public int screenWidth = 800; 
		public int screenHeight = 800; 
		private Scene scene;
		
		private long oldTime;
		private InputManager input;

		private Matrix4f viewMatrix = new Matrix4f();
		private Matrix4f projectionMatrix = new Matrix4f();
		private Matrix4f mvpMatrix = new Matrix4f();
		
		private final static float clearColour = 0f; //BLACK

		
		public Simulator() throws OpenGLException {
			instance = this;
			window = new Window("Assignment 1", screenWidth, screenHeight, this);
			window.setResizable(true);
			window.run();
		}

		@Override
		public void init() {
			
			new ShaderLibrary(DIRECTORY);
			
			input = new InputManager(window);
			scene = new Scene(this);

			glClearColor(clearColour, clearColour, clearColour, 1); // BLACK
			
			input = new InputManager(window);
			oldTime = System.currentTimeMillis();
		}

		private Camera cam;

		@Override
		public void draw() {	
			cam = scene.getCamera(); //Get the camera from the scene
			cam.resize(screenWidth, screenHeight);


			update();
			//glViewport(0, 0, screenWidth, screenHeight);
			glClear(GL_COLOR_BUFFER_BIT);
			
			mvpMatrix.identity();
			cam.getViewMatrix(viewMatrix);
			cam.getProjectionMatrix(projectionMatrix);
			mvpMatrix.set(projectionMatrix).mul(viewMatrix);
			scene.draw(mvpMatrix);
			String s = "Saa";
		}
				
		private void update() {
			long time = System.currentTimeMillis();
			float deltaTime = (time - oldTime) / 1000f;
			oldTime = time;
			
			// update the scene
			scene.update(deltaTime, input);
		
			// clear the input at the end of each update
			input.clear();
		}
		
		@Override
		public void resize(int width, int height) {
			screenWidth = width;
			screenHeight = height;
			glViewport(0, 0, screenWidth, screenHeight);
			
			cam = scene.getCamera();
			cam.resize(screenWidth, screenHeight);
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}
		
		public static void main(String[] args) throws OpenGLException {
			new Simulator();
		}
		
		

	}
