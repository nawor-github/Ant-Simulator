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


public class Circle extends InstancedObject {
	final private String VERTEX_SHADER = "instanced_vertex.glsl";
	final private String FRAGMENT_SHADER = "instanced_fragment.glsl";
	private Shader shader;
		
	public Circle() {
		super();
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);
	}	
	
	@Override
	public void addObject(Vector3f new_pos, Vector3f new_colour, Vector3f new_scale) {
		//System.out.println("Adding a new CIRCLE");
		
		super.addObject(new_pos, new_colour, new_scale);
	}
	
	
	@Override
	protected void makeMesh() {	
		//System.out.println("Generating squarer mesh");
		vertices = new Vector4f[] {
			//Main body
			new Vector4f( 0.5f, 0.5f, 0, 1), //P0 square for now
			new Vector4f( 0.5f, -0.5f, 0, 1), //P1
			new Vector4f( -0.5f, -0.5f, 0, 1), //P2
			new Vector4f( -0.5f, 0.5f, 0, 1), //P3
		};
		vertexBuffer = GLBuffers.createBuffer(vertices);

		indices = new int[] {  
			0,1,2, 
			0,2,3,
		};
		indexBuffer = GLBuffers.createIndexBuffer(indices);
	}
}
