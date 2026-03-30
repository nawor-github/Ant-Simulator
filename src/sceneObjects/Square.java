package sceneObjects;

import org.joml.Vector4f;


import comp3170.GLBuffers;
import comp3170.SceneObject;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import sim.Scene;

import org.joml.Matrix4f;
import org.joml.Vector2f;
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
	protected Vector3f colour = new Vector3f(0.1f,0.6f,0.3f); //Dark green
	protected Vector3f stripeColour = new Vector3f(0.05f,0.4f,0.3f); //Darker green, with a blue-ish tinge
	
	private Vector3f[] position;
	private int positionBuffer;
	
	private Vector2f[] scale;
	private int scaleBuffer;
	
	private int width, height, numSquares;
	
	
	
	public Square(int w, int h, float max_scale, float min_scale, float scatter_X, float scatter_Y) {
		width = w;
		height = h;
		numSquares = width * height;
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// Make one copy of the mesh
		makeMesh();
		
		position = new Vector3f[numSquares];
		scale = new Vector2f[numSquares];
		
		float min_X = -scatter_X/2f;
		float max_X = scatter_X/2f;
		float min_Y = -scatter_Y/2f;
		float max_Y = scatter_Y/2f;
		
		for (int i = 0; i < numSquares; i++) {
			float x = Scene.randBetween(min_X, max_X);
			float y = Scene.randBetween(min_Y, max_Y);
			position[i] = new Vector3f(x, y, 0f);
			float s = Scene.randBetween(min_scale, max_scale);
			scale[i] = new Vector2f(s, s);
		}
		positionBuffer = GLBuffers.createBuffer(position);
		scaleBuffer = GLBuffers.createBuffer(scale);
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
		shader.setAttribute("a_scale", scaleBuffer);
		glVertexAttribDivisor(shader.getAttribute("a_scale"), 1);
		
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
	    glDrawElementsInstanced(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0, numSquares);	
	    
	    // disable instance-based drawing (very important to a guy like me)
		glVertexAttribDivisor(shader.getAttribute("a_worldPos"), 0);
		glVertexAttribDivisor(shader.getAttribute("a_scale"), 0);


	}
}
