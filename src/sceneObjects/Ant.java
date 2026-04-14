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


public class Ant extends InstancedObject {
	final protected String VERTEX_SHADER = "instanced_ant_vertex.glsl";
	final protected String FRAGMENT_SHADER = "instanced_ant_fragment.glsl";
	
	protected Vector3f antColour = new Vector3f(0.1f,0.2f,0.2f); //Dark colour
	protected Vector3f stripeColour = new Vector3f(0.05f, 0.1f, 0.2f); //Old clear colour now ant colour
	
	protected final static Vector3f homeAntColour = new Vector3f(0.5f, 0.1f, 0.3f); //Magenta home scent colour
	protected final static Vector3f foodAntColour = new Vector3f(0.1f, 0.5f, 0.4f); //Dark green food scent colour
	protected final static Vector3f debugColour = new Vector3f(0.1f, 1f, 0.4f); //Dark green food scent colour
	
	private Vector3f[] rotation, heading, Lheading, Rheading;
	private int rotationBuffer;
	
	private Grid grid;
	
	private final float MOVE_SPEED = 4f;
	private final float TURN_SPEED = 5f;
	
	private final float TRAIL_DEPOSIT_STRENGTH = 100f;

	private final float FOOD_CAPACITY = 20f;
	private final float FOOD_TAKE_SPEED = 10f;
	
	private static float decayMult = 0.1f;

	private static float RANDOM_WIGGLE = 1f;
	
	private static float ANTENNAE_ROTATION = TAU / 8f;
	
	ArrayList<Integer> foraging; //1 for following food, 0 for following home
	ArrayList<Float> foodAmount, timeSinceTarget; //Tracks food amount carried and time since was last at target
		
	public Ant(int nAnts, float max_Scale, float min_Scale, float s_X, float s_Y, Grid g) {
		super(nAnts, max_Scale, min_Scale, s_X, s_Y);
	
		grid = g;
		
		System.out.println("John the ant is ant number 0. His position is: " + position[0].x + ", " +  position[0].y);
		System.out.println("Scale: " + scale[0].x + ". Colour: " + colour[0].x + ", " +   colour[0].y + ", " +   colour[0].z);
	}
	
	@Override
	protected void setShader(String vertex, String fragment) {
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);
	}
	
	@Override
	public void makeEmptyArrays() {
		super.makeEmptyArrays();
		
		rotation = new Vector3f[0];
		heading = new Vector3f[0];
				
		Lheading = new Vector3f[0]; //Headings either sideo of the main heading
		Rheading = new Vector3f[0];
		
		foraging = new ArrayList<Integer>();
		foodAmount = new ArrayList<Float>();
		timeSinceTarget = new ArrayList<Float>();
	}
	
	@Override
	public void assignBuffers() {
		//System.out.println("Assigning ant-specific buffers (rotation) :D");
		super.assignBuffers();
		rotationBuffer = GLBuffers.createBuffer(rotation); 
	}
	
	@Override
	public void addObject(Vector3f new_pos, Vector3f new_colour, Vector3f new_scale) {
		System.out.println("Adding a new ant");
		
		super.addObject(new_pos, new_colour, new_scale);
		float randomRotation = Scene.randBetween(0,TAU);
		Vector3f newHeading = calcHeading(randomRotation);
		Vector3f newLheading = calcHeading(randomRotation + ANTENNAE_ROTATION);
		Vector3f newRheading = calcHeading(randomRotation - ANTENNAE_ROTATION);
		
		rotation = addToVector3fArray(rotation, new Vector3f(randomRotation, 0, 0));
		heading = addToVector3fArray(heading, newHeading);
		Lheading = addToVector3fArray(Lheading, newLheading);
		Rheading = addToVector3fArray(Rheading, newRheading);
		
		foraging.add(1);
		foodAmount.add(0f);
		timeSinceTarget.add(10000f);
		System.out.println("Time since target length is " + timeSinceTarget.size());
	}
	
	public void addAnt(Vector4f pos) {
		System.out.println("Adding new ant at pos: " + pos.x + ", " + pos.y);
		Vector3f p = new Vector3f(pos.x, pos.y, pos.z);
		addDefaultObject();
		addObject(p, genColour(), genScale());
		assignBuffers(); //Assigns all buffers used by GLSL
		System.out.println("Generating a new ant at index: " + index);
	}
	
	@Override
	protected Vector3f genScale() {
		System.out.println("Initializing ant scale");
		float s = max_scale;
		return new Vector3f(s, s, s);
	}
	
	@Override
	protected Vector3f genColour() {
		System.out.println("Initializing ant colour");
		return new Vector3f(foodAntColour.x, foodAntColour.y, foodAntColour.z);
		//float r = Scene.randBetween(0f, 1f);
		//float g = Scene.randBetween(0f, 1f);
		//float b = Scene.randBetween(0f, 1f);
		//return new Vector3f(r,g,b);
		//return antColour;
	}
	
	public void update(float deltaTime, InputManager input) {
		for (int i = 0; i < N_Objects; i++) {
			Square current = getCurrentSquare(i);
			//System.out.println("Time since target length is " + timeSinceTarget.size());
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
		assignBuffers();
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
		setForagingMode(antIndex, 0); //Set to follow home
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
			
			setForagingMode(antIndex, 1); //Set to follow food
		}
	}
	
	private void setForagingMode(int antIndex, int mode) { //1 for following food, 0 for following home
		switch (mode) {
			case 1:
				foraging.set(antIndex, 1); 
				setColour(antIndex, homeAntColour);
				break;
			case 0:
				foraging.set(antIndex, 0); 
				setColour(antIndex, foodAntColour);
				break;
			default:
				foraging.set(antIndex, -1); 
				setColour(antIndex, debugColour);
		}
		timeSinceTarget.set(antIndex, 0f);//Reset foraging time
	}
	
	private void setColour(int antIndex, Vector3f c) {
		colour[antIndex] = c;
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
	
	@Override
	protected void makeMesh() {	
		System.out.println("Generating ant mesh");
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
		//System.out.println("Drawing ants!");
		shader.enable();
		
		shader.setUniform("u_mvpMatrix", mvpMatrix);

		//Passing our instanced variables (yippee!)
		shader.setAttribute("a_worldPos", super.positionBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_worldPos"), 1);
		shader.setAttribute("a_scale", super.scaleBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_scale"), 1);
		shader.setAttribute("a_colour", super.colourBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_colour"), 1);
		
		shader.setAttribute("a_rotation", rotationBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_rotation"), 1);
		
		//This one isn't set as an attribute divisor as it is the same for all cacti (they use the same mesh)
	    shader.setAttribute("a_position", vertexBuffer);   
	    
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
	    glDrawElementsInstanced(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0, N_Objects);	
	    
	    // disable instance-based drawing (very important to a guy like me)
		glVertexAttribDivisor(shader.getAttribute("a_worldPos"), 0);
		glVertexAttribDivisor(shader.getAttribute("a_scale"), 0);
		glVertexAttribDivisor(shader.getAttribute("a_colour"), 0);
		glVertexAttribDivisor(shader.getAttribute("a_rotation"), 0);
	}
}
