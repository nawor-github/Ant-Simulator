package sim;

	import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;


	import static org.lwjgl.glfw.GLFW.*;

	import static org.lwjgl.opengl.GL11.glClear;
	import static org.lwjgl.opengl.GL11.glClearColor;
	import static org.lwjgl.opengl.GL11.glViewport;

	import java.io.File;
	import org.joml.Matrix4f;

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
		private int screenWidth = 600; 
		private int screenHeight = 600; 
		private Scene scene;
		
		private long oldTime;
		private InputManager input;

		private Matrix4f viewMatrix = new Matrix4f();
		private Matrix4f projectionMatrix = new Matrix4f();
		private Matrix4f mvpMatrix = new Matrix4f();
		
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
			scene = new Scene();

			glClearColor(0.96f, 0.85f, 0.65f, 1.0f); // SANDY DESERT
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
		
		static final private int KEYBIND_NUMBER = 10; // Number of keys to be bound and tracked
		
		private void update() {
			long time = System.currentTimeMillis();
			float deltaTime = (time - oldTime) / 1000f;
			oldTime = time;
			boolean[] directions = new boolean[KEYBIND_NUMBER]; //Is this the best way to do this? No, but it works and this project took a long time
			if (input.isKeyDown(GLFW_KEY_W)) { //W press, tank go forward
				directions[3] = true;
				//Left and right movement only possible when going forward (as per spec)
				if (input.isKeyDown(GLFW_KEY_A)) { //A press, tank rotate left
					directions[0] = true;
				}
				if (input.isKeyDown(GLFW_KEY_D)) { //D press, tank rotate right
					directions[1] = true;
				}
			}
			if (input.isKeyDown(GLFW_KEY_S)) { //S press, do nothing
				directions[2] = true;
			}
			if (input.isKeyDown(GLFW_KEY_LEFT)) { //Left arrow press
				directions[4] = true;
			}
			if (input.isKeyDown(GLFW_KEY_UP)) { //Up arrow press
				directions[5] = true;
			}
			if (input.isKeyDown(GLFW_KEY_DOWN)) { //Down arrow press
				directions[6] = true;
			}
			if (input.isKeyDown(GLFW_KEY_RIGHT)) { //Right arrow press
				directions[7] = true;
			}
			if (input.isKeyDown(GLFW_KEY_1)) { //One is pressed
				directions[8] = true;
			}
			if (input.isKeyDown(GLFW_KEY_2)) { //Two is pressed
				directions[9] = true;
			}
			
			scene.update(directions, deltaTime);
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
