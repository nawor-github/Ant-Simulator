package sceneObjects;

import org.joml.Vector4f;


import comp3170.GLBuffers;
import comp3170.InputManager;
import comp3170.SceneObject;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import sim.Scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;


public class InstancedObject extends SceneObject {
	final protected String VERTEX_SHADER = "instanced_circle_vertex.glsl";
	final protected String FRAGMENT_SHADER = "instanced_circle_fragment.glsl";
	protected Shader shader;
	protected int N_Objects;
	
	protected Vector4f[] vertices;
	protected int vertexBuffer, indexBuffer;
	protected int[] indices;
	
	protected Vector3f[] position, scale, colour;
	protected int positionBuffer, scaleBuffer, colourBuffer;
	
	protected float max_scale, min_scale, scatter_X, scatter_Y;;
	
	protected static Vector3f defaultColour = new Vector3f(0,1,0);
	
	protected int index = 0;
	
	protected void setShader(String vertex, String fragment) {
		shader = ShaderLibrary.instance.compileShader(vertex, fragment);
	}
	
	public InstancedObject() {
		max_scale = 1;
		min_scale = 1;
		scatter_X = 0;
		scatter_Y = 0;
		N_Objects = 0;
		setShader(VERTEX_SHADER, FRAGMENT_SHADER);

		makeMesh(); //Generates mesh and VERTEX AND INDEX BUFFERS
		makeEmptyArrays(); //Generates all empty arrays and buffers
		
		for (int i = 0; i < N_Objects; i++) {
			addDefaultObject();
		}
		assignBuffers(); //Assigns all buffers used by GLSL
	}
	
	public InstancedObject(int n, float max_s, float min_s, float spread_X, float spread_Y) {
		max_scale = max_s;
		min_scale = min_s;
		scatter_X = spread_X;
		scatter_Y = spread_Y;
		N_Objects = n;
		setShader(VERTEX_SHADER, FRAGMENT_SHADER);

		makeMesh(); //Generates mesh and VERTEX AND INDEX BUFFERS
		makeEmptyArrays(); //Generates all empty arrays and buffers
		
		for (int i = 0; i < N_Objects; i++) {
			addDefaultObject();
		}
		assignBuffers(); //Assigns all buffers used by GLSL
	}
	
	public void makeEmptyArrays() {
		System.out.println("Making empty arrays :D");
		position = new Vector3f[0];
		scale = new Vector3f[0];
		colour = new Vector3f[0];
	}
	
	public void assignBuffers() {
		//System.out.println("Assigning buffers :D");
		positionBuffer = GLBuffers.createBuffer(position);
		scaleBuffer = GLBuffers.createBuffer(scale);
		colourBuffer = GLBuffers.createBuffer(colour); 
	}
	
	public void addDefaultObject() {
		addObject(genPosition(), genColour(), genScale());
		System.out.println("Generating a new default object at index: " + index);
	}
	
	public void addObject(Vector3f new_pos, Vector3f new_colour, Vector3f new_scale) {		
		position = addToVector3fArray(position, new_pos);
		scale = addToVector3fArray(scale, new_scale);
		colour = addToVector3fArray(colour, new_colour);
		index++;
	}
	
	protected Vector3f genPosition() {
		System.out.println("Initializing ant position");

		float min_X = -scatter_X/2f;
		float max_X = scatter_X/2f;
		float min_Y = -scatter_Y/2f;
		float max_Y = scatter_Y/2f;
		float x = Scene.randBetween(min_X, max_X);
		float y = Scene.randBetween(min_Y, max_Y);
		return new Vector3f(x,y,0);
	}
	
	protected Vector3f genScale() {
		System.out.println("Initializing default scale");
		float s = Scene.randBetween(min_scale, max_scale);
		return new Vector3f(s,0,0);
	}
	
	protected Vector3f genColour() {
		System.out.println("Initializing default colour");
		return new Vector3f(0,1,0);
		//return defaultColour;
	}
	
	protected void makeMesh() {	 //Cactus is default mesh
		System.out.println("Generating default (cactus) mesh");
		vertices = new Vector4f[] {
			//Main body
			new Vector4f( 0, 0, 0, 1), //P0 body vertices start
			new Vector4f( 0.2f, 0, 0, 1), //P1
			new Vector4f( 0.2f, 0.7f, 0, 1), //P2
			new Vector4f( 0.15f, 0.9f, 0, 1), //P3
			new Vector4f( 0.1f, 1, 0, 1), //P4
			new Vector4f( 0.05f, 1, 0, 1), //P5
			new Vector4f( 0f, 0.8f, 0, 1), //P6 body vertices end
			new Vector4f( 0f, 0.4f, 0, 1), //P7 left arm vertices start
			new Vector4f( 0f, 0.6f, 0, 1), //P8
			new Vector4f( -0.1f, 0.6f, 0, 1), //P9
			new Vector4f( -0.1f, 0.7f, 0, 1), //P10
			new Vector4f( -0.2f, 0.7f, 0, 1), //P11
			new Vector4f( -0.2f, 0.5f, 0, 1), //P12 left arm vertices end
			new Vector4f( 0.2f, 0.2f, 0, 1), //P13 right arm vertices start
			new Vector4f( 0.4f, 0.4f, 0, 1), //P14
			new Vector4f( 0.4f, 0.6f, 0, 1), //P15
			new Vector4f( 0.3f, 0.6f, 0, 1), //P16
			new Vector4f( 0.3f, 0.5f, 0, 1), //P17
			new Vector4f( 0.2f, 0.4f, 0, 1), //P18 right arm vertices end
			new Vector4f( -0.15f, 0.75f, 0, 1), //P19 left arm tip
			new Vector4f( 0.35f, 0.65f, 0, 1), //P20 right arm tip
		};
		vertexBuffer = GLBuffers.createBuffer(vertices);

		indices = new int[] {  
			0,1,2,
			0,2,6,
			2,3,6,
			3,4,5,
			3,5,6,
			7,8,9,
			7,9,12,
			9,10,11,
			9,11,12,
			13,14,18,
			14,17,18,
			14,15,17,
			15,16,17,
			15,16,20,
			10,11,19,
		};
		indexBuffer = GLBuffers.createIndexBuffer(indices);
	}
	
	public void update(float deltaTime, InputManager input) {
		for (int i = 0; i < N_Objects; i++) {
		}
		positionBuffer = GLBuffers.createBuffer(position);
		scaleBuffer = GLBuffers.createBuffer(scale);
		colourBuffer = GLBuffers.createBuffer(colour);
	}
	
	protected Vector3f[] addToVector3fArray(Vector3f[] base, Vector3f addition) {
		Vector3f[] result = new Vector3f[base.length + 1];
		for (int i = 0; i < base.length; i++) {
			result[i] = base[i];
		}
		result[base.length] = addition;
		return result;
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
		shader.setAttribute("a_colour", colourBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_colour"), 1);
	    
	    
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
	}
	
	
}
