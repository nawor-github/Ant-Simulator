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


public class Ant extends SceneObject {
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
	
	private Vector3f[] scale;
	private int scaleBuffer;
	
	private int N_Ants;
	
	private float max_scale, min_scale;
	
	public Ant(int nCacti, float max_Scale, float min_Scale, float scatter_X, float scatter_Y) {
		min_scale = min_Scale;
		max_scale = max_Scale;
		N_Ants = nCacti;
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// Make one copy of the mesh
		makeMesh();
		
		position = new Vector3f[N_Ants];
		scale = new Vector3f[N_Ants];
		
		float min_X = -scatter_X/2f;
		float max_X = scatter_X/2f;
		float min_Y = -scatter_Y/2f;
		float max_Y = scatter_Y/2f;
		
		for (int i = 0; i < N_Ants; i++) {
			float x = Scene.randBetween(min_X, max_X);
			float y = Scene.randBetween(min_Y, max_Y);
			position[i] = new Vector3f(x, y, 0f);
			float s = Scene.randBetween(min_scale, max_scale);
			scale[i] = new Vector3f(s, s, s);
		}
		positionBuffer = GLBuffers.createBuffer(position);
		scaleBuffer = GLBuffers.createBuffer(scale);
	}
	
	public void addAnt(Vector4f pos) {
		Vector3f p = new Vector3f(pos.x, pos.y, pos.z);
		N_Ants = N_Ants + 1;
		position = addToVector3fArray(position, p);
		float s = Scene.randBetween(min_scale, max_scale);
		scale = addToVector3fArray(scale, new Vector3f(s,s,s));
		positionBuffer = GLBuffers.createBuffer(position);
		scaleBuffer = GLBuffers.createBuffer(scale);
		System.out.println("Ant created at: " + pos.x + ", " + pos.y);
	}
	
	public void addAnt(Vector3f pos) {
		N_Ants = N_Ants + 1;
		position = addToVector3fArray(position, pos);
		float s = Scene.randBetween(min_scale, max_scale);
		scale = addToVector3fArray(scale, new Vector3f(s,s,s));
		positionBuffer = GLBuffers.createBuffer(position);
		scaleBuffer = GLBuffers.createBuffer(scale);
	}
	
	private Vector3f[] addToVector3fArray(Vector3f[] base, Vector3f addition) {
		Vector3f[] result = new Vector3f[base.length + 1];
		for (int i = 0; i < base.length; i++) {
			result[i] = base[i];
		}
		result[base.length] = addition;
		return result;
	}
	
	private void makeMesh() {	
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
	    glDrawElementsInstanced(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0, N_Ants);	
	    
	    // disable instance-based drawing (very important to a guy like me)
		glVertexAttribDivisor(shader.getAttribute("a_worldPos"), 0);
		glVertexAttribDivisor(shader.getAttribute("a_scale"), 0);


	}
}
