package sceneObjects;

import org.joml.Vector4f;


import comp3170.GLBuffers;
import comp3170.InputManager;
import comp3170.SceneObject;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import sim.Scene;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;


public class Square extends SceneObject {
	final private String VERTEX_SHADER = "instanced_vertex.glsl";
	final private String FRAGMENT_SHADER = "instanced_fragment.glsl";
	private Shader shader;
	
	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	
	private Vector3f[] position;
	private int positionBuffer;
	
	private Vector3f[] colour;
	private int colourBuffer;
	
	private int count_x, count_y, numSquares;
	private float scale;
	private Vector3f edgeColour = new Vector3f(0,0,0);
	
	
	
	public Square(int w, int h, float s, float spacing) {
		count_x = w;
		count_y = h;
		scale = s;
		numSquares = count_x * count_y;
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// Make one copy of the mesh
		makeMesh();
		
		position = new Vector3f[numSquares];
		colour = new Vector3f[numSquares];
		
		float widthRadius = count_x * spacing / 2;
		float heighRadius = count_y * spacing / 2;
		float squareRadius = 0;
		
		for (int x = 0; x < count_x; x++) {
			for (int y = 0; y < count_y; y++) {
				int index = (x*count_x)+y;
				float xCoord = (x*spacing) - squareRadius - widthRadius;
				float yCoord = (y*spacing) - squareRadius - heighRadius;
				position[index] = new Vector3f(xCoord, yCoord, 0f);
				colour[index] = new Vector3f(0.5f, 0.8f, 0.5f);
			}
		}
		positionBuffer = GLBuffers.createBuffer(position);
		colourBuffer = GLBuffers.createBuffer(colour);
	}
	
	private void makeMesh() {	
		vertices = new Vector4f[] {
			//Main body
			new Vector4f( 0, 0, 0, 1), //P0 body vertices start
			new Vector4f( 1f, 0, 0, 1), //P1
			new Vector4f( 1f, 1f, 0, 1), //P2
			new Vector4f( 0f, 1f, 0, 1), //P3
		};
		vertexBuffer = GLBuffers.createBuffer(vertices);

		indices = new int[] {  
			0,1,2,
			0,2,3
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
		shader.setAttribute("a_colour", colourBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_colour"), 1);
		
		//This one isn't set as an attribute divisor as it is the same for all cacti (they use the same mesh)
	    shader.setAttribute("a_position", vertexBuffer);
	    
	    shader.setUniform("u_scale", scale);
	    shader.setUniform("u_edgeColour", edgeColour);
	    	    
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
	    glDrawElementsInstanced(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0, numSquares);	
	    
	    // disable instance-based drawing (very important to a guy like me)
		glVertexAttribDivisor(shader.getAttribute("a_worldPos"), 0);
		glVertexAttribDivisor(shader.getAttribute("a_colour"), 0);


	}
	
	private Vector2i mousePosition = new Vector2i();
	int screenWidth = 600;
	int screenHeight = 600;

	public void update(float deltaTime, InputManager input) {
		if (input.isMouseDown()) {
			input.getCursorPos(mousePosition);
			float x = ((float) mousePosition.x()/screenWidth) - 1f;
			float y = ((((float) mousePosition.y()/screenHeight) ) - 1f) * -1f;
			System.out.println(x + " " + y);
			Vector3f pos = new Vector3f(x, y, 0f);
			
		}
		// TODO Auto-generated method stub
		
	}
}
