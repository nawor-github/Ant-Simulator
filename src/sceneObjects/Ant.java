package sceneObjects;

import org.joml.Vector4f;


import comp3170.GLBuffers;
import comp3170.InputManager;
import comp3170.SceneObject;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import static comp3170.Math.TAU;

import sim.Scene;
import sim.Square;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;


public class Ant extends SceneObject {
	final private String VERTEX_SHADER = "instanced_vertex.glsl";
	final private String FRAGMENT_SHADER = "instanced_fragment.glsl";
	private Shader shader;
	
	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	protected Vector3f colour = new Vector3f(0.1f,0.2f,0.2f); //Dark colour
	protected Vector3f stripeColour = new Vector3f(0.05f, 0.1f, 0.2f); //Old clear colour now ant colour
	
	private Vector3f[] position;
	private int positionBuffer;
	
	private Vector3f[] scale;
	private int scaleBuffer;
	
	private Vector3f[] rotation, heading, Lheading, Rheading;
	private int rotationBuffer;
	
	private int N_Ants;
	
	private float max_scale, min_scale;
	
	private Grid grid;
	
	private final float MOVE_SPEED = 4f;
	private final float TURN_SPEED = 5f;
	
	private final float TRAIL_DEPOSIT_STRENGTH = 100f;

	private final float FOOD_CAPACITY = 20f;
	private final float FOOD_TAKE_SPEED = 10f;
	
	private static float decayMult = 0.1f;

	private static float RANDOM_WIGGLE = 1f;
	
	private static float ANTENNAE_ROTATION = TAU / 8f;
	
	ArrayList<Integer> foraging = new ArrayList<Integer>(); //1 for following food, 0 for following home
	ArrayList<Float> foodAmount = new ArrayList<Float>();
	ArrayList<Float> timeSinceTarget = new ArrayList<Float>(); //Tracks time since was last at target
	
	
	public Ant(int nAnts, float max_Scale, float min_Scale, float scatter_X, float scatter_Y, Grid g) {
		min_scale = min_Scale;
		max_scale = max_Scale;
		N_Ants = nAnts;
		grid = g;
		
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// Make one copy of the mesh
		makeMesh();
		
		position = new Vector3f[N_Ants];
		scale = new Vector3f[N_Ants];
		rotation = new Vector3f[N_Ants];
		heading = new Vector3f[N_Ants];
				
		Lheading = new Vector3f[N_Ants]; //Headings either sideo of the main heading
		Rheading = new Vector3f[N_Ants];
				
		float min_X = -scatter_X/2f;
		float max_X = scatter_X/2f;
		float min_Y = -scatter_Y/2f;
		float max_Y = scatter_Y/2f;
		
		for (int i = 0; i < N_Ants; i++) {
			float x = Scene.randBetween(min_X, max_X);
			float y = Scene.randBetween(min_Y, max_Y);
			position[i] = new Vector3f(x, y, 0f);
			float s = min_scale;
			scale[i] = new Vector3f(s, s, s);
			float randomRotation = Scene.randBetween(0,TAU);
			rotation[i] = new Vector3f(randomRotation, 0, 0);
			heading[i] = calcHeading(rotation[i].x);
			timeSinceTarget.add(99999f);
			//Antennae calcs
			Lheading[i] = calcHeading(rotation[i].x + ANTENNAE_ROTATION);
			Rheading[i] = calcHeading(rotation[i].x - ANTENNAE_ROTATION);
			foraging.add(1);
			foodAmount.add(0f);
		}
		positionBuffer = GLBuffers.createBuffer(position);
		scaleBuffer = GLBuffers.createBuffer(scale);
		rotationBuffer = GLBuffers.createBuffer(rotation); 
	}
	
	public void update(float deltaTime, InputManager input) {
		for (int i = 0; i < N_Ants; i++) {
			Square current = getCurrentSquare(i);
			float time = timeSinceTarget.get(i);
			timeSinceTarget.set(i, time + deltaTime);
			if (current.isHome || current.getFood() > 0) { 
				timeSinceTarget.set(i, 0f);
			}
			pickUpFood(i, current);
			dropOffFood(i, current);
			depositTrail(i, current);
			float turnMult = turnDirection(i);
			rotation[i].x += (turnMult + (RANDOM_WIGGLE*Scene.randBetween(-1,1))) * TURN_SPEED * deltaTime;

			heading[i] = calcHeading(rotation[i].x);
			if (i == 0) {
				//System.out.println("Heading is: " + heading[i].x + "," + heading[i].y + " and rotation is: " + rotation[i].x);
			}
			position[i].x += heading[i].x * MOVE_SPEED * deltaTime;
			position[i].y += heading[i].y * MOVE_SPEED * deltaTime;
		}
		positionBuffer = GLBuffers.createBuffer(position);
		rotationBuffer = GLBuffers.createBuffer(rotation);
		scaleBuffer = GLBuffers.createBuffer(scale);

	}
	

	
	public void addAnt(Vector4f pos) {
		Vector3f p = new Vector3f(pos.x, pos.y, pos.z);
		N_Ants = N_Ants + 1;
		position = addToVector3fArray(position, p);
		float s = min_scale;
		scale = addToVector3fArray(scale, new Vector3f(s,s,s));
		float randomRotation = Scene.randBetween(0,TAU);
		rotation = addToVector3fArray(rotation, new Vector3f(randomRotation, 0, 0));
		heading = addToVector3fArray(heading, new Vector3f(0, rotation[N_Ants-1].x, 0));
		Lheading = addToVector3fArray(heading, new Vector3f(0, 1f + ANTENNAE_ROTATION, 0));
		Rheading = addToVector3fArray(heading, new Vector3f(0, 1f - ANTENNAE_ROTATION, 0));
		foraging.add(1);
		foodAmount.add(0f);
		timeSinceTarget.add(10000f);
		
		positionBuffer = GLBuffers.createBuffer(position);
		scaleBuffer = GLBuffers.createBuffer(scale);
		rotationBuffer = GLBuffers.createBuffer(rotation); 
		//System.out.println("Ant created at: " + pos.x + ", " + pos.y);
	}
	
	private Vector3f[] addToVector3fArray(Vector3f[] base, Vector3f addition) {
		Vector3f[] result = new Vector3f[base.length + 1];
		for (int i = 0; i < base.length; i++) {
			result[i] = base[i];
		}
		result[base.length] = addition;
		return result;
	}
	
	public Vector3f getAntennaeWorldPos(int antIndex, boolean leftAnntennae) {
		Vector3f antPos = position[antIndex];
		float antennae_rot = -1f * ANTENNAE_ROTATION;
		if (leftAnntennae) {
			antennae_rot = ANTENNAE_ROTATION;
		}
		Lheading[antIndex] = calcHeading(rotation[antIndex].x + antennae_rot);
		Vector3f anntennaePos = Lheading[antIndex];
		anntennaePos.x += antPos.x;
		anntennaePos.y += antPos.y;
		return anntennaePos;
	}

	private float turnDirection(int antIndex) {
		Vector3f antPos = position[antIndex];
		//Vector3f projectedPos = heading[index].add(antPos).mul(grid.getScale()); //Project forward direction, corrected for changing grid scale from 1
		heading[antIndex] = calcHeading(rotation[antIndex].x);
		Vector3f projectedPos = heading[antIndex];
		projectedPos.x += antPos.x;
		projectedPos.y += antPos.y;
		
		Vector3f L_AnntennaePos = getAntennaeWorldPos(antIndex, true);
		Vector3f R_AnntennaePos = getAntennaeWorldPos(antIndex, false);
		
		//int currentIndex = grid.getCellAtWorldPos(new Vector4f(antPos.x, antPos.y, antPos.z, 1));
		int projectedIndex = grid.getCellAtWorldPos(new Vector4f(projectedPos.x, projectedPos.y, projectedPos.z, 1));
		if (projectedIndex == -1 || grid.getSquare(projectedIndex).isBlocker) { //Return a random turn direction if directly ahead is off-nap or a blocker
			if (antIndex % 2 == 0) { //Pick a side this ant will always turn towards
				return 1;
			} else {
				return -1;
			}
		}
		int leftIndex = grid.getCellAtWorldPos(new Vector4f(L_AnntennaePos.x, L_AnntennaePos.y, L_AnntennaePos.z, 1));
		int rightIndex = grid.getCellAtWorldPos(new Vector4f(R_AnntennaePos.x, R_AnntennaePos.y, R_AnntennaePos.z, 1));

		Square forwardSquare = grid.getSquare(projectedIndex);
		Square leftSquare = grid.getSquare(leftIndex);
		if (leftSquare.isBlocker || leftSquare.i == -1) {
			return -1;
		}
		Square rightSquare = grid.getSquare(rightIndex);
		if (rightSquare.isBlocker || rightSquare.i == -1) {
			return 1;
		}
		float value = 0; //Steady course
		if (foraging.get(antIndex) == 1) { // Following food
			if (forwardSquare.getFoodScent() < leftSquare.getFoodScent() && rightSquare.getFoodScent() < leftSquare.getFoodScent()) {
				value = 1; //Turn left
			}
			if (forwardSquare.getFoodScent() < rightSquare.getFoodScent()) {
				value = -1; //Turn right
			}
			return value;
		}
		if (forwardSquare.getHomeScent() < leftSquare.getHomeScent() && rightSquare.getHomeScent() < leftSquare.getHomeScent()) { //Following home
			value = 1; //Turn left
		}
		if (forwardSquare.getHomeScent() < rightSquare.getHomeScent()) {
			value = -1; //Turn right
		}
		return value;
	}
	
	private Square getCurrentSquare(int antIndex) {
		Vector3f antPos = position[antIndex];
		int currentIndex = grid.getCellAtWorldPos(new Vector4f(antPos.x, antPos.y, antPos.z, 1));
		return grid.getSquare(currentIndex);
	}
	
	private void pickUpFood(int antIndex, Square s) {
		float currentFood = foodAmount.get(antIndex);
		if (currentFood == FOOD_CAPACITY) {
			return;
		}
		float foodGrabbed = s.takeFood(FOOD_TAKE_SPEED);
		if (foodGrabbed == 0) {
			return;
		}
		currentFood += foodGrabbed;
		scale[antIndex] = new Vector3f(max_scale,max_scale,max_scale);
		if (currentFood > FOOD_CAPACITY) {
			s.addFood(currentFood-FOOD_CAPACITY);
			currentFood = FOOD_CAPACITY;
		}
		foodAmount.set(antIndex, currentFood);
		foraging.set(antIndex, 0); //1 for following food, 0 for following home
		timeSinceTarget.set(antIndex, 0f);//Reset foraging time
	}
	
	private void dropOffFood(int antIndex, Square s) {
		if (s.isHome) {
			float currentFood = foodAmount.get(antIndex);
			if (currentFood == 0) {
				return;
			}
			grid.foodStored += currentFood;
			foodAmount.set(antIndex, 0f);
			scale[antIndex] = new Vector3f(min_scale,min_scale,min_scale);
			
			foraging.set(antIndex, 1); //1 for following food, 0 for following home
			timeSinceTarget.set(antIndex, 0f);//Reset foraging time
		}
	}
	
	private void depositTrail(int antIndex, Square s) { //1 for following food, 0 for following home

		float pheremoneAmount = TRAIL_DEPOSIT_STRENGTH * (float) Math.pow((1f - decayMult),timeSinceTarget.get(antIndex));
		if (foraging.get(antIndex) == 0) {
			s.addFoodScent(pheremoneAmount);
			return;
		}
		s.addHomeScent(pheremoneAmount);
	}
	
	private Vector3f calcHeading(float r) { //The rotation as a number expressed in radians
		float x = (float) Math.cos(r);
		float y = (float) Math.sin(r);
		Vector3f result = new Vector3f(x, y, 0);
		return result.normalize();
	}
	
	private void makeMesh() {	
		vertices = new Vector4f[] {
			//Main body
			new Vector4f( 0, 0, 0, 1), //P0 body vertices start
			new Vector4f( 0.1f, -0.1f, 0, 1), //P1
			new Vector4f( 0, -0.2f, 0, 1), //P2
			new Vector4f( -0.1f, -0.1f, 0, 1), //P3
			new Vector4f( -0.3f, -0.3f, 0, 1), //P4
			new Vector4f( -0.4f, -0.4f, 0, 1), //P5
			new Vector4f( -0.4f, -0.6f, 0, 1), //P6
			new Vector4f( -0.1f, -1f, 0, 1), //P7
			new Vector4f( 0.1f, -1f, 0, 1), //P8
			new Vector4f( 0.4f, -0.6f, 0, 1), //P9
			new Vector4f( 0.4f, -0.4f, 0, 1), //P10
			new Vector4f( 0.3f, -0.3f, 0, 1), //P11
			new Vector4f( 0, -0.6f, 0, 1), //P12
			new Vector4f( 0.2f, 0.3f, 0, 1), //P13
			new Vector4f( 0, 0.6f, 0, 1), //P14
			new Vector4f( -0.2f, 0.3f, 0, 1), //P15
			new Vector4f( 0.2f, 0.5f, 0, 1), //P16
			new Vector4f( 0.3f, 0.6f, 0, 1), //P17
			new Vector4f( 0.3f, 0.7f, 0, 1), //P18
			new Vector4f( 0.1f, 0.9f, 0, 1), //P19
			new Vector4f( -0.1f, 0.9f, 0, 1), //P20
			new Vector4f( -0.3f, 0.7f, 0, 1), //P21
			new Vector4f( -0.3f, 0.6f, 0, 1), //P22
			new Vector4f( -0.2f, 0.5f, 0, 1), //P23
		};
		vertexBuffer = GLBuffers.createBuffer(vertices);

		indices = new int[] {  
			0,1,2, //Petiole tris
			0,2,3,
			12,2,11,//Gaster tris
			12,11,10,
			12,10,9,
			12,9,8,
			12,8,7,
			12,7,6,
			12,6,5,
			12,5,4,
			12,4,2,
			0,14,13, //Mesosoma tris
			0,14,15,
			14,17,16, //Head tris
			22,14,23,
			14,18,17,
			14,19,18,
			14,20,19,
			14,20,21,
			14,21,22,
		};
		indexBuffer = GLBuffers.createIndexBuffer(indices);
	}
	
	@Override
	public void drawSelf(Matrix4f mvpMatrix) {
		shader.enable();
		
		shader.setUniform("u_mvpMatrix", mvpMatrix);

		//Passing our instanced variables (yippee!)
		shader.setAttribute("a_worldPos", positionBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_worldPos"), 1);
		shader.setAttribute("a_scale", scaleBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_scale"), 1);
		shader.setAttribute("a_rotation", rotationBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_rotation"), 1);
		
		//This one isn't set as an attribute divisor as it is the same for all cacti (they use the same mesh)
	    shader.setAttribute("a_position", vertexBuffer);
	    
	    //Setting our awesome colour uniforms to be passed to the fragment shader
	    shader.setUniform("u_colour", colour);	 
	    shader.setUniform("u_stripeColour", stripeColour);	    

	    
	    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
	    /*
		        ________________
		   ()==(                (@==()
		        '_______________'|
		          |              |
		          | Draw call :) |
		        __)_____________ |
		   ()==(                (@==()
		        '---------------'
		*/
	    glDrawElementsInstanced(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0, N_Ants);	
	    
	    // disable instance-based drawing (very important to a guy like me)
		glVertexAttribDivisor(shader.getAttribute("a_worldPos"), 0);
		glVertexAttribDivisor(shader.getAttribute("a_scale"), 0);
	}
	
	
}
