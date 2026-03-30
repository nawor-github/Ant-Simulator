package sceneObjects;

import org.joml.Vector4f;

import comp3170.SceneObject;
import comp3170.Shader;

public class Square extends SceneObject {
	final private String VERTEX_SHADER = "instanced_vertex.glsl";
	final private String FRAGMENT_SHADER = "instanced_fragment.glsl";
	private Shader shader;
	
	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
}
