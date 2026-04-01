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


public class Circle extends SceneObject {
	final private String VERTEX_SHADER = "instanced_circle_vertex.glsl";
	final private String FRAGMENT_SHADER = "instanced_circle_fragment.glsl";
	private Shader shader;
	private int N_Circles;
	
	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	
	private Vector3f[] position, scale, colour;
	private int positionBuffer, scaleBuffer, colourBuffer;
	
	private static Vector3f defaultColour = new Vector3f(0,1,0);
	
	public Circle(int n) {
		N_Circles = n;
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		indices = new int[0];
		indexBuffer = GLBuffers.createIndexBuffer(indices);
		vertexBuffer = GLBuffers.createBuffer(vertices);

		
		position = new Vector3f[N_Circles];
		scale = new Vector3f[N_Circles];
		colour = new Vector3f[N_Circles];
		
		positionBuffer = GLBuffers.createBuffer(position);
		scaleBuffer = GLBuffers.createBuffer(scale);
		colourBuffer = GLBuffers.createBuffer(colour); 
	}
	
	public void update(float deltaTime, InputManager input) {
		for (int i = 0; i < N_Circles; i++) {
		}
		positionBuffer = GLBuffers.createBuffer(position);
		scaleBuffer = GLBuffers.createBuffer(scale);
		colourBuffer = GLBuffers.createBuffer(colour);
	}
	

	
	public void addCircle(Vector4f pos, Vector3f c, Vector3f s) {
		Vector3f p = new Vector3f(pos.x, pos.y, pos.z);
		N_Circles++;
		position = addToVector3fArray(position, p);
		
		scale = addToVector3fArray(scale, s);
		colour = addToVector3fArray(colour, c);
		
		positionBuffer = GLBuffers.createBuffer(position);
		scaleBuffer = GLBuffers.createBuffer(scale);
		colourBuffer = GLBuffers.createBuffer(colour);
	}
	
	private Vector3f[] addToVector3fArray(Vector3f[] base, Vector3f addition) {
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
	    glDrawElementsInstanced(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0, N_Circles);	
	    
	    // disable instance-based drawing (very important to a guy like me)
		glVertexAttribDivisor(shader.getAttribute("a_worldPos"), 0);
		glVertexAttribDivisor(shader.getAttribute("a_scale"), 0);
	}
	
	
}
