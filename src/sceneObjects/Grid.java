package sceneObjects;

import org.joml.Vector4f;


import comp3170.GLBuffers;
import comp3170.InputManager;
import comp3170.SceneObject;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import sim.Scene;
import sim.Square;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.awt.event.KeyEvent;

import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;


public class Grid extends SceneObject {
	final private String VERTEX_SHADER = "instanced_square_vertex.glsl";
	final private String FRAGMENT_SHADER = "instanced_square_fragment.glsl";
	private Shader shader;
	
	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	
	private Vector3f[] position;
	private int positionBuffer;
	
	private Vector3f[] colour;
	private int colourBuffer;
	
	private Square[][] squares;
	
	private int count_x, count_y, numSquares;
	private float scale;
	private Vector3f edgeColour = new Vector3f(0,0,0);
	
	float foodStored = 0;
	Scene scene;
	
	int brushMode = 0;
	// 0 Clear
	// 1 Blocker
	// 2 Food
	// 3 Food Scent
	// 4 Home Scent
	// 5 Home
	// 6 Spawn new ants
	
	private final float FOOD_AMOUNT = 200000f; //Amount of food to paint
	private final float SCENT_AMOUNT = 200f; //Amount of scent to paint
	
	public Grid(int w, int h, float s, float spacing, Scene Scene) {
		scene = Scene;
		count_x = w;
		count_y = h;
		setScale(s);
		numSquares = count_x * count_y;
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// Make one copy of the mesh
		makeMesh();
		
		position = new Vector3f[numSquares];
		colour = new Vector3f[numSquares];
		squares = new Square[count_x][count_y];
		
		float widthRadius = count_x * spacing / 2;
		float heighRadius = count_y * spacing / 2;
		float squareRadius = 0;
		
		for (int x = 0; x < count_x; x++) {
			for (int y = 0; y < count_y; y++) {
				int index = (x*count_x)+y;
				float xCoord = (x*spacing) - squareRadius - widthRadius;
				float yCoord = (y*spacing) - squareRadius - heighRadius;
				position[index] = new Vector3f(xCoord, yCoord, 0f);
				colour[index] = new Vector3f(0.8f, 0.8f, 0.5f);
				squares[x][y] = new Square(x,y,index);
			}
		}
		positionBuffer = GLBuffers.createBuffer(position);
		colourBuffer = GLBuffers.createBuffer(colour);
	}

	
	public Square getSquare(int index) {
		if (index != -1) {
			for (int x = 0; x < count_x; x++) {
				for (int y = 0; y < count_y; y++) {
					if (squares[x][y].i == index) {
						return squares[x][y];
					}
				}
			}
		}
		return new Square(-1,-1,-1); //Returns a square with an impossible index and coords
	}
	
	public Square getSquare(int x, int y) {
		if (x < 0 || y < 0 || x > count_x || y > count_y) {
			return new Square(-1,-1,-1);
		}
		return squares[x][y];
	}

	public void update(float deltaTime, InputManager input) {
		for (int x = 0; x < count_x; x++) {
			for (int y = 0; y < count_y; y++) {
				//squares[x][y].calculateColour();
				colour[squares[x][y].i] = squares[x][y].getColour();
				squares[x][y].update(deltaTime, input);
			}
		}
		colourBuffer = GLBuffers.createBuffer(colour); // See if this can be removed??
		
		if (input.wasKeyPressed(KeyEvent.VK_0)){ //0 = clear
			brushMode = 0;
		}
		if (input.wasKeyPressed(KeyEvent.VK_1)){ //1 = black blocker squares
			brushMode = 1;
		}
		if (input.wasKeyPressed(KeyEvent.VK_2)){ //2 = food
			brushMode = 2;
		}
		if (input.wasKeyPressed(KeyEvent.VK_3)){ //3 = food scent
			brushMode = 3;
		}
		if (input.wasKeyPressed(KeyEvent.VK_4)){ //4 = home scent
			brushMode = 4;
		}
		if (input.wasKeyPressed(KeyEvent.VK_5)){ //5 = home
			brushMode = 5;
		}
		if (input.wasKeyPressed(KeyEvent.VK_6)){ //6 = paint ants
			brushMode = 6;
		}
		
		Vector4f mousePos = scene.getMousePos();
		if (input.isMouseDown()) {
			if (brushMode != 6) {
				Square s = getSquareAtWorldPos(mousePos);
				//int gridIndex = getCellAtWorldPos(mousePos);
				int gridIndex = s.i;
				//6System.out.println("Grid Index:" + gridIndex);
				if (gridIndex != -1) {
					switch(brushMode) {
						case 0: // clear
							s.clear();
							//grid.setColour(gridIndex, clearColour);
							break;
						case 1: // blocker
							s.setBlocker();
							//grid.setColour(gridIndex, blockerColour);
							break;
						case 2: // food
							s.addFood(FOOD_AMOUNT * deltaTime);
							//grid.setColour(gridIndex, foodColour);
							break;
						case 3: // food scent
							s.addFoodScent(SCENT_AMOUNT * deltaTime);
							//grid.setColour(gridIndex, foodScentColour);
							break;
						case 4: // home scent
							s.addHomeScent(SCENT_AMOUNT * deltaTime);
							//grid.setColour(gridIndex, homeScentColour);
							break;
						case 5: //home colour
							s.setHome();
							//grid.setColour(gridIndex, homeColour);
							break;
					}
				}
			}
		}
		
	}
	
	public void setColour(int i, Vector3f c) {
		colour[i] = c;
		colourBuffer = GLBuffers.createBuffer(colour); // See if this can be removed??
	}
	
	private boolean liesWithin(Vector4f mousePos, int index) {
		float x1 = position[index].x;
		float y1 = position[index].y;
		float x2 = x1 + getScale();
		float y2 = y1 + getScale();
		if (mousePos.x >= x1 && mousePos.x <= x2 && mousePos.y >= y1 && mousePos.y <= y2) {
			return true;
		}
		return false;
	}

	public int getCellIndexAtWorldPos(Vector4f worldPos) { //This can be optimized
		for (int i = 0; i < position.length; i++) {
			if (liesWithin(worldPos, i)) {
				return i;
			}
		}
		return -1;
	}
	
	public int getCellIndexAtWorldPos(Vector3f worldPos) { //This can be optimized
		Vector4f fixedPos = new Vector4f(worldPos.x, worldPos.y, worldPos.z, 1f);
		return getCellIndexAtWorldPos(fixedPos);
	}
	
	public Square getSquareAtWorldPos(Vector4f worldPos) { //This can be optimized
		for (int i = 0; i < position.length; i++) {
			if (liesWithin(worldPos, i)) {
				return getSquare(i);
			}
		}
		return new Square(-1,-1,-1);
	}
	
	public Square getSquareAtWorldPos(Vector3f worldPos) { //This can be optimized
		Vector4f fixedPos = new Vector4f(worldPos.x, worldPos.y, worldPos.z, 1f);
		return getSquareAtWorldPos(fixedPos);
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
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
	    
	    shader.setUniform("u_scale", getScale());
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
	
	
	public void setBrushMode(int mode) {
		brushMode = mode;
	}
	
	public int getBrushMode() {
		return brushMode;
	}
}
